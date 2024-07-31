/*
 * Copyright (C) 2009 Search Solution Corporation. All rights reserved by Search Solution.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 * - Redistributions of source code must retain the above copyright notice,
 *   this list of conditions and the following disclaimer.
 *
 * - Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * - Neither the name of the <ORGANIZATION> nor the names of its contributors
 *   may be used to endorse or promote products derived from this software without
 *   specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY
 * OF SUCH DAMAGE.
 *
 */
package com.cubrid.cubridmanager.ui.monitoring.editor.count;

import com.cubrid.common.core.util.Closer;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

/**
 * Implemention of CounterFile
 *
 * @author lcl
 * @version 1.1 - 2009-9-10 created by lcl
 */
public class BasicCounterFile implements CounterFile {

    private static final long EMPTY_TIME = 0x8000000000000000L;

    private static final int HEADER_LENGTH = 64;
    private static final byte[] MAGIC_FLAG = {(byte) 'c', (byte) 'd', (byte) 's', (byte) 't'};
    private static final int FILE_VERSION = 1;
    private static final String CHARSET = "UTF-8";
    private static final byte[] RESERVE_BYTES = new byte[12];
    private static final int OFFSET_TYPES_OFFSET = 28;
    private static final int ALIGNMENT_TYPES = 64;
    private static final int ALIGNMENT_PROPERTIES = 64;
    private static final int ALIGNMENT_SKIPSLOTS = 64;
    private static final int ALIGNMENT_DATA = 4096;
    private static final int PROPERTIES_SEGEMENT_LENGTH = 2048;
    private static final int OFFSET_ORIGIN_TIME_SECOND = 8;
    private static final int OFFSET_ROW_OFFSET = 24;
    private static final String OPEN_MODE = "rw";
    private static final int SKIP_SLOT_UNIT = 128;
    private static final byte[] ZERO_BYTES = new byte[4096];

    private final RandomAccessFile file;
    private final int interval;
    private final int mode;
    private final Map<String, CounterTypeInternal> types =
            new LinkedHashMap<String, CounterTypeInternal>();
    private final Map<String, Integer> typeIndexes = new HashMap<String, Integer>();
    private final Properties props;
    private final long offsetTypes;
    private final long offsetProperties;
    private final long offsetSkipSlots;
    private final long offsetData;
    private final int rowSize;
    private final int rowCapacity;
    private final boolean[] skipSlots;

    private int originTimeSecond; // begin from first storage data;
    private int rowOffset; // offset of row
    private int rowCount; // row count

    private volatile boolean closed = false;

    private final Object seekLock = new Object();
    private final Object propsLock = new Object();

    /**
     * Open existing counter file.
     *
     * @param fo the file will opened.
     * @param props a new properties that write immediately on opening file.
     * @throws IOException exception for IO operation
     */
    public BasicCounterFile(File fo, final Properties props) throws IOException {
        if (!fo.isFile()) {
            throw new IOException("the path is not a file, or file not exists");
        }

        if (fo.length() < HEADER_LENGTH) {
            throw new IOException("format error, file size too small");
        }

        file = new RandomAccessFile(fo, OPEN_MODE);

        byte[] magic = new byte[MAGIC_FLAG.length];
        file.read(magic);

        for (int i = 0; i < magic.length; i++) {
            if (MAGIC_FLAG[i] != magic[i]) {
                throw new IOException("format error, magic flag does not match");
            }
        }

        if (file.readInt() != FILE_VERSION) {
            throw new IOException("format error, version does not match");
        }

        originTimeSecond = file.readInt();
        rowCapacity = file.readInt();
        interval = file.readInt();
        mode = file.readInt();
        rowOffset = file.readInt();
        offsetTypes = file.readLong();
        offsetProperties = file.readLong();
        offsetSkipSlots = file.readLong();
        offsetData = file.readLong();

        if (offsetTypes > file.length()
                || offsetProperties > file.length()
                || offsetSkipSlots > file.length()
                || offsetData > file.length()) {
            throw new IOException("format error, offset out of file range");
        }

        file.seek(offsetTypes);

        int typeCount = file.readInt();
        int offsetInRow = 0;

        for (int i = 0; i < typeCount; i++) {

            CounterTypeInternal type = CounterTypeInternal.readFrom(file, offsetInRow);
            types.put(type.getName(), type);
            offsetInRow += type.getLength();
        }

        rowSize = offsetInRow;

        makeTypeIndexes();

        this.props = new Properties();
        file.seek(offsetProperties);

        while (true) {
            String line = readString(file);

            if (line == null) {
                break;
            }

            int pos = line.indexOf('=');

            if (pos < 0) {
                this.props.setProperty(line, "");
            } else {
                this.props.setProperty(line.substring(0, pos), line.substring(pos + 1));
            }
        }

        file.seek(offsetSkipSlots);
        int skipSlotCount = (rowCapacity + SKIP_SLOT_UNIT - 1) / SKIP_SLOT_UNIT;
        skipSlots = new boolean[skipSlotCount];

        byte[] slotData = new byte[skipSlotCount];
        int readCount = file.read(slotData);

        if (readCount < skipSlotCount) {
            throw new IOException();
        }

        for (int i = 0; i < skipSlotCount; i++) {
            skipSlots[i] = slotData[i] != 0;
        }

        //
        // update properties
        //
        boolean changed = false;

        if (props != null) {
            for (Object key : props.keySet()) {
                String value = props.getProperty((String) key);

                if (value == null) {
                    changed = true;
                    this.props.remove(key);
                } else {
                    String value2 = this.props.getProperty((String) key);

                    if (!value.equals(value2)) {
                        changed = true;
                        this.props.setProperty((String) key, value);
                    }
                }
            }
        }

        if (changed) {

            byte[] bytes = propertiesToBytes(this.props);

            if (bytes.length > PROPERTIES_SEGEMENT_LENGTH) {
                throw new IOException("Too much properties");
            }

            file.seek(offsetProperties);
            file.write(bytes);
        }

        file.seek(offsetData);
        rowCount = (int) ((file.length() - offsetData) / rowSize);
    }

    /**
     * Create new counter file
     *
     * @param fo File path
     * @param types Type of store counter data
     * @param maxCount Total row of counter data
     * @param interval Duration of between two row (in second unit)
     * @param mode Open mode (always zero)
     * @param props External properties for counter file. can be null.
     * @throws IOException
     */
    public BasicCounterFile(
            File fo, CounterType[] types, int maxCount, int interval, int mode, Properties props)
            throws IOException {

        if (types == null || types.length == 0) {
            throw new IllegalArgumentException("must specify least one type");
        }

        if (maxCount <= 0) {
            throw new IllegalArgumentException("maxCount must greater than zero");
        }

        if (interval <= 0) {
            throw new IllegalArgumentException("interval must greater than zero");
        }

        if (interval > 3600) {
            throw new IllegalArgumentException("interval cannot exceed 3600");
        }

        originTimeSecond = NULL_TIME;
        rowCapacity = maxCount;
        this.interval = interval;
        this.mode = mode;
        rowOffset = 0;
        this.props = props == null ? new Properties() : (Properties) props.clone();

        if (fo.exists()) {
            throw new IOException("file exists already");
        }

        file = new RandomAccessFile(fo, OPEN_MODE);

        //
        // write headers
        file.write(MAGIC_FLAG);
        file.writeInt(FILE_VERSION);
        file.writeInt(originTimeSecond);
        file.writeInt(rowCapacity);
        file.writeInt(interval);
        file.writeInt(mode);
        file.writeInt(rowOffset);
        file.writeLong(-1);
        file.writeLong(-1);
        file.writeLong(-1);
        file.writeLong(-1);
        file.write(RESERVE_BYTES);

        //
        // write types data
        offsetTypes = fillAlign(file, ALIGNMENT_TYPES);
        file.writeInt(types.length); // write type count

        int offsetInRow = 0;

        for (CounterType type : types) {
            CounterTypeInternal ti =
                    new CounterTypeInternal(
                            type.getName(), type.getMode(), file.getFilePointer(), offsetInRow);
            this.types.put(type.getName(), ti);
            ti.writeTo(file);
            offsetInRow += ti.getLength();
        }

        rowSize = offsetInRow;
        makeTypeIndexes();

        //
        // write properties data
        offsetProperties = fillAlign(file, ALIGNMENT_PROPERTIES);
        byte[] bytes = propertiesToBytes(this.props);

        if (bytes.length > PROPERTIES_SEGEMENT_LENGTH) {
            Closer.close(file);
            throw new IOException("too much properties");
        }

        file.write(bytes);
        fillZero(file, PROPERTIES_SEGEMENT_LENGTH - bytes.length);

        //
        // write skip slot data
        offsetSkipSlots = fillAlign(file, ALIGNMENT_SKIPSLOTS);
        int skipSlotCount = (rowCapacity + SKIP_SLOT_UNIT - 1) / SKIP_SLOT_UNIT;
        byte[] slotData = new byte[skipSlotCount];
        file.write(slotData);
        skipSlots = new boolean[skipSlotCount];

        //
        // fill zero bytes up to reach data region
        offsetData = fillAlign(file, ALIGNMENT_DATA);

        //
        // write back offset information to file header
        file.seek(OFFSET_TYPES_OFFSET);
        file.writeLong(offsetTypes);
        file.writeLong(offsetProperties);
        file.writeLong(offsetSkipSlots);
        file.writeLong(offsetData);

        //
        // flush file contents to persistent storage device
        file.getFD().sync();
    }

    /**
     * get information for counter file
     *
     * @param fo specified file
     * @param readTypes read or not read type information
     * @param readProps read or not read property information
     * @return counter file information object
     * @throws IOException exception for IO operation
     */
    public static CounterFileInfo getFileInfo(File fo, boolean readTypes, boolean readProps)
            throws IOException {
        if (!fo.isFile()) {
            throw new IOException("the path is not a file, or file not exists");
        }

        if (fo.length() < HEADER_LENGTH) {
            throw new IOException("format error, file size too small");
        }

        RandomAccessFile file = null;

        try {
            file = new RandomAccessFile(fo, "r");

            byte[] magic = new byte[MAGIC_FLAG.length];
            file.read(magic);

            for (int i = 0; i < magic.length; i++) {
                if (MAGIC_FLAG[i] != magic[i]) {
                    throw new IOException("format error, magic flag does not match");
                }
            }

            if (file.readInt() != FILE_VERSION) {
                throw new IOException("format error, version does not match");
            }

            CounterFileInfo info = new CounterFileInfo();
            int ot = file.readInt(); // originTimeSecond;
            info.maxCount = file.readInt();
            info.interval = file.readInt();
            info.mode = file.readInt();
            int rowOffset = file.readInt(); // rowOffset

            long offsetTypes = file.readLong();
            long offsetProperties = file.readLong();
            file.readLong(); // offsetSkipSlots
            long offsetData = file.readLong(); // offsetData

            if (offsetTypes > file.length() || offsetProperties > file.length()) {
                throw new IOException("format error, offset out of file range");
            }

            if (readTypes) {
                file.seek(offsetTypes);

                int typeCount = file.readInt();
                int offsetInRow = 0;

                for (int i = 0; i < typeCount; i++) {

                    CounterTypeInternal type = CounterTypeInternal.readFrom(file, offsetInRow);
                    info.types.put(type.getName(), type);
                    offsetInRow += type.getLength();
                }

                info.rowSize = offsetInRow;
            }

            if (readProps) {
                file.seek(offsetProperties);

                while (true) {
                    String line = readString(file);

                    if (line == null) {
                        break;
                    }

                    int pos = line.indexOf('=');

                    if (pos < 0) {
                        info.props.setProperty(line, "");
                    } else {
                        info.props.setProperty(line.substring(0, pos), line.substring(pos + 1));
                    }
                }

                info.rowCount = (int) ((file.length() - offsetData) / info.rowSize);
            }

            info.beginTime = ot == NULL_TIME ? NULL_TIME : (ot + rowOffset * info.interval) * 1000L;
            info.endTime =
                    ot == NULL_TIME
                            ? NULL_TIME
                            : (ot + (rowOffset + info.rowCount) * info.interval) * 1000L;
            return info;

        } finally {
            Closer.close(file);
        }
    }

    /**
     * get all properties
     *
     * @return properties object
     */
    public Properties getProperties() {
        synchronized (propsLock) {
            return (Properties) props.clone();
        }
    }

    /**
     * get property by key
     *
     * @param key key
     * @return value
     */
    public String getProperty(String key) {
        synchronized (propsLock) {
            return props.getProperty(key);
        }
    }

    /**
     * get property as int
     *
     * @param key key
     * @param def default value when convert error or value not found.
     * @return integer value
     */
    public int getPropertyInt(String key, int def) {

        String val;
        synchronized (propsLock) {
            val = props.getProperty(key);
        }

        try {
            return Integer.parseInt(val);
        } catch (RuntimeException e) {
            return def;
        }
    }

    /**
     * get property as long
     *
     * @param key key
     * @param def default value when convert error or value not found
     * @return long value
     */
    public long getPropertyLong(String key, long def) {
        String val;
        synchronized (propsLock) {
            val = props.getProperty(key);
        }

        try {
            return Long.parseLong(val);
        } catch (RuntimeException e) {
            return def;
        }
    }

    /**
     * get property as boolean
     *
     * @param key key
     * @param def default value when convert error or value not found
     * @return boolean value
     */
    public boolean getPropertyBool(String key, boolean def) {
        String val;
        synchronized (propsLock) {
            val = props.getProperty(key);
        }

        if (val == null) {
            return def;
        }

        try {
            long d1 = Long.parseLong(val);
            return d1 != 0;

        } catch (NumberFormatException e) {
            return Boolean.parseBoolean(val);
        }
    }

    /**
     * get property as double
     *
     * @param key key
     * @param def default value when convert error or value not found
     * @return double value
     */
    public double getPropertyDouble(String key, double def) {
        String val;
        synchronized (propsLock) {
            val = props.getProperty(key);
        }

        try {
            return Double.parseDouble(val);
        } catch (RuntimeException e) {
            return def;
        }
    }

    /**
     * set property
     *
     * @param key key
     * @param value value
     * @throws IOException exception for IO operation
     */
    public void setProperty(String key, String value) throws IOException {

        synchronized (propsLock) {
            boolean changed = internalSetProperty(key, value);

            if (changed) {
                synchronized (seekLock) {
                    writebackProperties();
                }
            }
        }
    }

    /**
     * set multi properties in one file operation
     *
     * @param pairs key-value pairs in Map object
     * @throws IOException exception for IO operation
     */
    public void setProperties(Map<String, String> pairs) throws IOException {
        synchronized (propsLock) {
            boolean changed = false;

            for (Map.Entry<String, String> entry : pairs.entrySet()) {
                changed |= internalSetProperty(entry.getKey(), entry.getValue());
            }

            if (changed) {
                synchronized (seekLock) {
                    writebackProperties();
                }
            }
        }
    }

    /**
     * @param key key
     * @param value value
     * @return true: value changed, false: value not changed.
     */
    private boolean internalSetProperty(String key, String value) {
        boolean changed = false;
        String old = props.getProperty(key);

        if (old == null) {
            if (value != null) {
                props.setProperty(key, value);
                changed = true;
            }
        } else {
            if (value == null) {
                props.remove(key);
                changed = true;
            } else {
                if (!old.equals(value)) {
                    props.setProperty(key, value);
                    changed = true;
                }
            }
        }

        return changed;
    }

    /**
     * write back properties to file
     *
     * @throws IOException exception for IO operation
     */
    private void writebackProperties() throws IOException {
        byte[] bytes = propertiesToBytes(this.props);

        if (bytes.length > PROPERTIES_SEGEMENT_LENGTH) {
            throw new IOException("Too much properties");
        }

        file.seek(offsetProperties);
        file.write(bytes);
    }

    /**
     * close file, release system resource
     *
     * @throws IOException exception for IO operation
     */
    public void close() throws IOException {
        if (!closed) {
            file.close();
            closed = true;
        }
    }

    /**
     * serialize properties as byte array
     *
     * @param props props
     * @return byte array will write to file
     * @throws IOException exception for IO operation
     */
    private static byte[] propertiesToBytes(Properties props) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream(PROPERTIES_SEGEMENT_LENGTH);

        for (Map.Entry<Object, Object> entry : props.entrySet()) {
            baos.write(stringToBytes(String.format("%s=%s", entry.getKey(), entry.getValue())));
        }

        baos.write(stringToBytes(""));

        return baos.toByteArray();
    }

    /**
     * fill zero bytes to file.
     *
     * @param file the file
     * @param length byte counted fill length
     * @return file pointer to next position
     * @throws IOException exception for IO operation
     */
    @SuppressWarnings("PMD")
    private static long fillZero(RandomAccessFile file, int length) throws IOException {

        while (length > 0) {
            int writeCount = length < ZERO_BYTES.length ? length : ZERO_BYTES.length;
            file.write(ZERO_BYTES, 0, writeCount);
            length -= writeCount;
        }

        return file.getFilePointer();
    }

    /**
     * fill zero and align file pointer
     *
     * @param file the file
     * @param alignSize align size.
     * @return file pointer to next position
     * @throws IOException exception for IO operation
     */
    private static long fillAlign(RandomAccessFile file, int alignSize) throws IOException {
        long pos = file.getFilePointer();
        int mod = (int) (pos % alignSize);

        if (mod == 0) {
            return pos;
        }

        int fillCount = alignSize - mod;

        byte[] zeroBytes = new byte[fillCount];
        file.write(zeroBytes);
        return pos + fillCount;
    }

    /**
     * read '\0' terminated string from current file pointer
     *
     * @param file the file
     * @return read value
     * @throws IOException exception for IO operation
     */
    private static String readString(RandomAccessFile file) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        while (true) {
            byte b1 = file.readByte();

            if (b1 == '\0') {
                break;
            }

            baos.write(b1);
        }

        byte[] bytes = baos.toByteArray();

        if (bytes.length == 0) {
            return null;
        }

        try {
            return new String(bytes, CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * convert to UTF-8 string that include '\0' character
     *
     * @param str str
     * @return byte array
     */
    private static byte[] stringToBytes(String str) {
        try {
            return (str + "\0").getBytes(CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * get begin time
     *
     * @return the difference, measured in milliseconds, between the current time and midnight,
     *     January 1, 1970 UTC
     */
    public long getBeginTime() {
        if (originTimeSecond == NULL_TIME) {
            return NULL_TIME;
        }

        return (originTimeSecond + rowOffset * interval) * 1000L;
    }

    public int getDuration() {
        return interval * rowCapacity;
    }

    /**
     * @return the difference, measured in milliseconds, between the current time and midnight,
     *     January 1, 1970 UTC
     */
    public long getEndTime() {
        if (originTimeSecond == NULL_TIME) {
            return NULL_TIME;
        }

        return (originTimeSecond + (rowOffset + rowCount) * interval) * 1000L;
    }

    public int getInterval() {
        return interval;
    }

    public int getMaxCount() {
        return rowCapacity;
    }

    public int getCount() {
        return rowCount;
    }

    public int getMode() {
        return mode;
    }

    /**
     * read data from counter file
     *
     * @param time time
     * @param typeArgs types
     * @return result object
     * @throws IOException exception for IOException
     */
    @SuppressWarnings("PMD")
    public Result readData(long time, String... typeArgs) throws IOException {

        if (typeArgs.length == 0) {
            typeArgs = types.keySet().toArray(new String[types.size()]);
        }

        CounterTypeInternal[] args = new CounterTypeInternal[typeArgs.length];

        for (int i = 0; i < typeArgs.length; i++) {
            String name = typeArgs[i];
            CounterTypeInternal type = types.get(name);

            if (type == null) {
                throw new IllegalArgumentException();
            }

            args[i] = type;
        }

        ResultImpl res = new ResultImpl(args);

        if (originTimeSecond == NULL_TIME
                || time < getBeginTime()
                || time >= getEndTime()
                || isSkipped(time)) {

            for (int i = 0; i < args.length; i++) {
                CounterTypeInternal type = args[i];

                if (type.isMulti()) {
                    res.setMulti(i, new ColumnData(), type.isCounter());
                } else {
                    res.setSingle(i, INVALID_VALUE, type.isCounter());
                }
            }

        } else {

            long pos = getRowOffset(time);

            synchronized (seekLock) {
                for (int i = 0; i < args.length; i++) {
                    CounterTypeInternal type = args[i];

                    if (type.isMulti()) {
                        ColumnData data = type.readMulti(file, pos);
                        res.setMulti(i, data, type.isCounter());
                    } else {
                        long data = type.readSingle(file, pos);
                        res.setSingle(i, data, type.isCounter());
                    }
                }
            }
        }

        return res;
    }

    /**
     * read data as long
     *
     * @param time time
     * @param typeArgs types
     * @return long value array, mapped to types @@throws IOException exception for IO operation
     */
    @SuppressWarnings("PMD")
    public long[] readDataAsLong(long time, String... typeArgs) throws IOException {

        if (typeArgs.length == 0) {
            typeArgs = types.keySet().toArray(new String[types.size()]);
        }

        CounterTypeInternal[] args = new CounterTypeInternal[typeArgs.length];

        for (int i = 0; i < typeArgs.length; i++) {
            String name = typeArgs[i];
            CounterTypeInternal type = types.get(name);

            if (type == null) {
                throw new IllegalArgumentException();
            }

            args[i] = type;
        }

        if (originTimeSecond == NULL_TIME
                || time < getBeginTime()
                || time >= getEndTime()
                || isSkipped(time)) {

            long[] res = new long[typeArgs.length];

            for (int i = 0; i < res.length; i++) {
                res[i] = INVALID_VALUE;
            }

            return res;
        }

        long[] items = new long[typeArgs.length];
        long pos = getRowOffset(time);

        synchronized (seekLock) {
            for (int i = 0; i < args.length; i++) {
                CounterTypeInternal type = args[i];

                if (type.isMulti()) {
                    ColumnData data = type.readMulti(file, pos);

                    if (data.getCount() == 0) {
                        items[i] = INVALID_VALUE;
                    } else {
                        if (type.isCounter()) {
                            items[i] = data.getSum();
                        } else {
                            items[i] = data.getSum() / data.getCount();
                        }
                    }
                } else {
                    items[i] = type.readSingle(file, pos);
                }
            }
        }

        return items;
    }

    /**
     * mark skip slots flag to false, and fill zero bytes to mapped storage space
     *
     * @param no the slot number
     * @throws IOException exception for IO operation
     */
    private void makeSlotNonEmpty(int no) throws IOException {
        int i = no / SKIP_SLOT_UNIT;

        if (!skipSlots[i]) {
            throw new IllegalStateException();
        }

        skipSlots[i] = false;
        file.seek(offsetData + i * SKIP_SLOT_UNIT * rowSize);
        fillZero(file, SKIP_SLOT_UNIT * rowSize);
    }

    /**
     * @param time time
     * @param name name
     * @param value value
     * @throws IOException exception for IO operatation
     */
    public void updateData(long time, String name, long value) throws IOException {

        if (time < getBeginTime()) {
            return;
        }

        CounterTypeInternal type = types.get(name);

        if (type == null) {
            throw new IllegalArgumentException();
        }

        if (value < 0 && !type.isCounter()) {
            return;
        }

        synchronized (seekLock) {
            boolean isNew = prepareRowSpace(time);

            if (isSkipped(time)) {
                makeSlotNonEmpty(getRowNoFromOrigin(time));
            }

            final long pos = getRowOffset(time);

            if (type.isMulti()) {

                ColumnData data = isNew ? new ColumnData() : type.readMulti(file, pos);

                if (type.isCounter()) {

                    long[] lu = type.readLastUpdate(file);
                    long prevValue = lu[0];
                    long prevTime = lu[1];

                    if (prevTime == EMPTY_TIME) {
                        // do not save first data. first data cannot take diff value
                        type.setAndFlushLastUpdate(file, value, time);
                    } else {
                        if (prevTime >= time) {
                            return; // nothing to do
                        }

                        type.setAndFlushLastUpdate(file, value, time);
                        long diffValue = value - prevValue;

                        if (diffValue < 0) {
                            diffValue = 0;
                        }

                        long delta = diffValue * 1000L * 100L / (time - prevTime);

                        if (isNew || data.count == 0) {
                            data.count = 1;
                            data.min = delta;
                            data.max = delta;
                            data.last = delta;
                            data.sum = diffValue;
                        } else {
                            data.count++;
                            data.last = delta;
                            data.sum += diffValue;

                            if (data.min > delta) {
                                data.min = delta;
                            }

                            if (data.max < delta) {
                                data.max = delta;
                            }
                        }
                    }

                } else {

                    if (isNew) {
                        data.min = value;
                        data.max = value;
                        data.last = value;
                        data.sum = value;
                        data.count = 1;
                    } else {
                        data.count++;
                        data.sum += value;
                        data.last = value;

                        if (data.min > value) {
                            data.min = value;
                        }

                        if (data.max < value) {
                            data.max = value;
                        }
                    }
                }

                type.writeMulti(file, pos, data);

            } else {
                if (type.isCounter()) {

                    long[] lu = type.readLastUpdate(file);
                    long prevValue = lu[0];
                    long prevTime = lu[1];

                    if (prevTime == EMPTY_TIME) {
                        // do not save first data. first data cannot take diff value
                        type.setAndFlushLastUpdate(file, value, time);
                    } else {
                        if (prevTime >= time) {
                            return; // nothing to do
                        }

                        long oldValue = isNew ? 0L : type.readSingle(file, pos);
                        type.setAndFlushLastUpdate(file, value, time);
                        long diffValue = value - prevValue;

                        if (diffValue < 0) {
                            diffValue = 0;
                        }

                        long sum = diffValue;

                        if (oldValue != INVALID_VALUE) {
                            sum += oldValue;
                        }

                        type.writeSingle(file, pos, sum);
                    }
                } else {
                    type.writeSingle(file, pos, value);
                }
            }
        }
    }

    /**
     * dump skip slots information for debug
     *
     * @param out print stream
     */
    public void dumpSkipSlots(PrintStream out) {

        for (int i = 0; i < skipSlots.length; i++) {
            out.println(
                    String.format(
                            "%b: %d - %d",
                            skipSlots[i],
                            i * SKIP_SLOT_UNIT,
                            i * SKIP_SLOT_UNIT + SKIP_SLOT_UNIT - 1));
        }

        out.println();
    }

    /**
     * prepare row space.
     *
     * @param time time
     * @return true: new space, false: already used.
     * @throws IOException exception for IO operation
     */
    private boolean prepareRowSpace(long time) throws IOException {

        if (getBeginTime() == NULL_TIME) {
            originTimeSecond = (int) (time / 1000L);
            originTimeSecond = originTimeSecond / interval * interval;
            rowOffset = 0;
            rowCount = 1;
            writebackVariableHeaders();
            file.setLength(offsetData);
            file.seek(offsetData);
            fillZero(file, rowSize);
            return true;
        }

        final long diff = time - getBeginTime();

        if (diff < 0) {
            throw new IllegalArgumentException();
        }

        final int no = (int) (diff / 1000L) / interval;

        if (no - (rowOffset + rowCount) >= rowCapacity) {
            originTimeSecond = (int) (time / 1000L);
            rowOffset = 0;
            rowCount = 1;
            writebackVariableHeaders();
            file.setLength(offsetData);
            file.seek(offsetData);
            fillZero(file, rowSize);
            return true;
        }

        int skipCount = no - rowCount;

        if (skipCount < -1) {
            return false;
        } else if (skipCount == -1) {
            // update last row
            return false;
        } else if (skipCount == 0) {
            // prepare next row
            if (rowCount == rowCapacity) {
                increaseRowOffset(1);
                writebackVariableHeaders();
                return true;

            } else if (rowCount < rowCapacity) {
                file.seek(offsetData + rowCount * rowSize);
                fillZero(file, rowSize);
                rowCount++;
                return true;
            } else {
                // should not rowCount > rowCapacity
                throw new IllegalStateException();
            }
        }

        // now skip count is > 0

        int left = rowCapacity - rowCount;

        if (left > 0) {

            // left > 0, that means rowOffset == 0
            // fill growable region
            int min = left < skipCount ? left : skipCount;

            fillRows(rowCount, min);
            skipCount -= min;

            // left -= min;
            rowCount += min;

            if (rowCount == rowCapacity) {
                if (skipCount > 0) {
                    fillRows(0, skipCount);
                }

                increaseRowOffset(skipCount + 1);
                writebackVariableHeaders();
            } else {
                file.seek(offsetData + rowCount * rowSize);
                fillZero(file, rowSize);
                rowCount++;
            }

        } else if (left == 0) {
            fillRows(rowCount, skipCount);
            increaseRowOffset(skipCount + 1);
        } else {
            throw new IllegalStateException();
        }

        return true;
    }

    /**
     * increase row offset
     *
     * @param delta delta
     */
    private void increaseRowOffset(int delta) {
        rowOffset += delta;

        if (rowOffset > rowCapacity) {
            rowOffset -= rowCapacity;
            originTimeSecond += interval * rowCapacity;
        }
    }

    /**
     * write back variable headers to file
     *
     * @throws IOException exception for IO operation
     */
    private void writebackVariableHeaders() throws IOException {
        // write headers
        long pos = file.getFilePointer();
        file.seek(OFFSET_ORIGIN_TIME_SECOND);
        file.writeInt(originTimeSecond);
        file.seek(OFFSET_ROW_OFFSET);
        file.writeInt(rowOffset);
        file.seek(pos);
    }

    /**
     * is skipped
     *
     * @param time time
     * @return true: skipped, false: not skipped
     */
    private boolean isSkipped(long time) {
        return skipSlots[getRowNoFromOrigin(time) / SKIP_SLOT_UNIT];
    }

    /**
     * flush skip slots to file
     *
     * @throws IOException exception for IO operation
     */
    private void flushSkipSlots() throws IOException {
        byte[] slotData = new byte[skipSlots.length];

        for (int i = 0; i < skipSlots.length; i++) {
            slotData[i] = skipSlots[i] ? (byte) 1 : (byte) 0;
        }

        file.seek(offsetSkipSlots);
        file.write(slotData);
    }

    /**
     * fill rows
     *
     * @param begin begin slot number
     * @param count total fill count
     * @throws IOException exception for IO operation
     */
    private void fillRows(int begin, int count) throws IOException {

        if (begin + count > rowCapacity) {
            int left = rowCapacity - begin;
            fillRows0(begin, left);
            fillRows0(0, count - left);
        } else {
            fillRows0(begin, count);
        }
    }

    /**
     * fill rows - low level function
     *
     * @param begin begin slot number
     * @param count total fill count
     * @throws IOException exception for IO operation
     */
    @SuppressWarnings("PMD")
    private void fillRows0(int begin, int count) throws IOException {

        int left = SKIP_SLOT_UNIT - (begin % SKIP_SLOT_UNIT);

        if (left < SKIP_SLOT_UNIT) {
            file.seek((long) offsetData + (long) begin * (long) rowSize);

            if (count < left) {
                fillZero(file, count * rowSize);
                return;
            } else {
                fillZero(file, left * rowSize);
                count -= left;
                begin += left;
            }
        }

        while (count >= SKIP_SLOT_UNIT) {
            skipSlots[begin / SKIP_SLOT_UNIT] = true;
            begin += SKIP_SLOT_UNIT;
            count -= SKIP_SLOT_UNIT;
        }

        if (count > 0) {
            skipSlots[begin / SKIP_SLOT_UNIT] = false;
            file.seek(offsetData + begin * rowSize);
            fillZero(file, count * rowSize);
        }

        flushSkipSlots();
    }

    /**
     * get row number from origin time point
     *
     * @param time time
     * @return row number
     */
    private int getRowNoFromOrigin(long time) {
        if (time < getBeginTime()) {
            throw new IllegalArgumentException();
        }

        int timeSecond = (int) (time / 1000L);
        return (timeSecond - originTimeSecond) / interval % rowCapacity;
    }

    /**
     * get row offset by time
     *
     * @param time time
     * @return position in file
     */
    private long getRowOffset(long time) {
        return offsetData + getRowNoFromOrigin(time) * rowSize;
    }

    /**
     * implementation of Result interface
     *
     * @author lcl
     */
    static class ResultImpl extends Result {
        private final Object[] cols;
        private final boolean[] flags;
        private final Map<String, Integer> indexes;

        ResultImpl(CounterTypeInternal[] args) {
            cols = new Object[args.length];
            flags = new boolean[args.length];
            indexes = new HashMap<String, Integer>();

            for (CounterTypeInternal type : args) {
                indexes.put(type.getName(), indexes.size());
            }
        }

        ResultImpl(Map<String, Integer> indexes) {
            cols = new Object[indexes.size()];
            flags = new boolean[indexes.size()];
            this.indexes = indexes;
        }

        /**
         * set multi data to this object
         *
         * @param col col
         * @param data data
         * @param flag is counter or not
         */
        void setMulti(int col, ColumnData data, boolean flag) {
            cols[col] = data;
            flags[col] = flag;
        }

        /**
         * set single data to this object
         *
         * @param col col
         * @param val val
         * @param flag is counter or not
         */
        void setSingle(int col, long val, boolean flag) {
            cols[col] = Long.valueOf(val);
            flags[col] = flag;
        }

        /**
         * get count
         *
         * @param col col
         * @return count
         */
        public long getCount(int col) {
            Object obj = cols[col];

            if (obj instanceof ColumnData) {
                return ((ColumnData) obj).count;
            } else {
                return 1;
            }
        }

        /**
         * get minimal - low level function
         *
         * @param col col
         * @return the value
         */
        protected long getMinRaw(int col) {
            Object obj = cols[col];

            if (obj instanceof ColumnData) {
                return flags[col] ? ((ColumnData) obj).min : ((ColumnData) obj).min * 100L;
            } else if (obj instanceof Long) {
                return (Long) obj;
            } else {
                throw new IllegalStateException();
            }
        }

        /**
         * get maximal - low level function
         *
         * @param col col
         * @return the value
         */
        protected long getMaxRaw(int col) {
            Object obj = cols[col];

            if (obj instanceof ColumnData) {
                return flags[col] ? ((ColumnData) obj).max : ((ColumnData) obj).max * 100L;
            } else if (obj instanceof Long) {
                return (Long) obj;
            } else {
                throw new IllegalStateException();
            }
        }

        /**
         * get sum
         *
         * @param col col
         * @return the value
         */
        public long getSum(int col) {
            Object obj = cols[col];

            if (obj instanceof ColumnData) {
                return ((ColumnData) obj).isInvalid() ? INVALID_VALUE : ((ColumnData) obj).sum;
            } else if (obj instanceof Long) {
                return (Long) obj;
            } else {
                throw new IllegalStateException();
            }
        }

        /**
         * get last - low level
         *
         * @param col col
         * @return the value
         */
        protected long getLastRaw(int col) {
            Object obj = cols[col];

            if (obj instanceof ColumnData) {
                return flags[col] ? ((ColumnData) obj).last : ((ColumnData) obj).last * 100L;
            } else if (obj instanceof Long) {
                return (Long) obj;
            } else {
                throw new IllegalStateException();
            }
        }

        /**
         * get index by name
         *
         * @param name name
         * @return index value in Integer object. or null if not found.
         */
        @Override
        protected Integer nameToIndex(String name) {
            return indexes.get(name);
        }
    }

    /**
     * implementation of CounterType interface
     *
     * @author lcl
     */
    static class CounterTypeInternal extends CounterType {

        private final long offsetInFile;
        private final int offsetInRow;
        private long lastValue;
        private long lastUpdate = EMPTY_TIME; // EMPTY_TIME means naver update.

        CounterTypeInternal(String name, int mode, long offsetInFile, int offsetInRow) {
            super(name, mode);
            this.offsetInFile = offsetInFile;
            this.offsetInRow = offsetInRow;
        }

        /**
         * read type from file
         *
         * @param file file
         * @param offsetInRow offset in row
         * @return object for read counter type
         * @throws IOException exception for IO operation
         */
        private static CounterTypeInternal readFrom(RandomAccessFile file, int offsetInRow)
                throws IOException {

            long offset = file.getFilePointer();
            long lastValue = file.readLong();
            long lastUpdate = file.readLong();
            int mode = file.readInt();
            ByteArrayOutputStream baos = new ByteArrayOutputStream(64);

            while (true) {
                byte b1 = file.readByte();

                if (b1 == 0) {
                    break;
                }

                baos.write(b1);
            }

            String name = new String(baos.toByteArray(), "UTF-8");
            CounterTypeInternal type = new CounterTypeInternal(name, mode, offset, offsetInRow);
            type.lastValue = lastValue;
            type.lastUpdate = lastUpdate;

            return type;
        }

        /**
         * write this type to file
         *
         * @param file file
         * @throws IOException exception for IO operation
         */
        private void writeTo(RandomAccessFile file) throws IOException {
            file.seek(offsetInFile);
            file.writeLong(lastValue);
            file.writeLong(lastUpdate);
            file.writeInt(mode);
            file.write(name.getBytes("UTF-8"));
            file.writeByte(0);
        }

        /**
         * read last update information
         *
         * @param file file
         * @return first element is last value, second element is last update time
         * @throws IOException exception for IO operation
         */
        private long[] readLastUpdate(RandomAccessFile file) throws IOException {
            file.seek(offsetInFile);
            long[] res = new long[2];
            res[0] = file.readLong();
            res[1] = file.readLong();
            return res;
        }

        /**
         * set last update information and write back to file
         *
         * @param file file
         * @param lastValue last value
         * @param lastUpdate last update time since 1970-01-01 GMT+0
         * @throws IOException exception for IO operation
         */
        private void setAndFlushLastUpdate(RandomAccessFile file, long lastValue, long lastUpdate)
                throws IOException {
            this.lastUpdate = lastUpdate;
            file.seek(offsetInFile);
            file.writeLong(lastValue);
            file.writeLong(lastUpdate);
        }

        /**
         * get length of row
         *
         * @return length
         */
        private int getLength() {
            if (isMulti()) {
                switch (getRangeType()) {
                    case BYTE:
                        return 9;
                    case SHORT:
                        return 14;
                    case INT:
                        return 24;
                    case LONG:
                        return 36;
                    default:
                        throw new IllegalStateException();
                }
            } else {
                return getRangeType().getByteCount();
            }
        }

        /**
         * read single counter data value from file
         *
         * @param file file
         * @param pos offset in file
         * @return counter data value
         * @throws IOException exception for IO operation
         */
        private long readSingle(RandomAccessFile file, long pos) throws IOException {

            file.seek(pos + offsetInRow);

            //
            // if the data is zero, that means the slot is never set value.
            // in this case, must return INVALID_VALUE.
            switch (getRangeType()) {
                case BYTE:
                    byte b1 = file.readByte();
                    return b1 == 0 ? INVALID_VALUE : b1 & 0x7f;
                case SHORT:
                    short s1 = file.readShort();
                    return s1 == 0 ? INVALID_VALUE : s1 & 0x7fff;
                case INT:
                    int i1 = file.readInt();
                    return i1 == 0 ? INVALID_VALUE : i1 & 0x7fffffff;
                case LONG:
                    long l1 = file.readLong();
                    return l1 == 0 ? INVALID_VALUE : l1 & 0x7fffffffffffffffL;
                default:
                    throw new IllegalStateException();
            }
        }

        /**
         * read multi counter data (min, max, last, avg, sum) value from file
         *
         * @param file file
         * @param pos offset in file
         * @return counter data value in ColumnData
         * @throws IOException exception for IO operation
         */
        private ColumnData readMulti(RandomAccessFile file, long pos) throws IOException {

            file.seek(pos + offsetInRow);

            ColumnData data = new ColumnData();
            data.count = file.readInt();

            switch (getRangeType()) {
                case BYTE:
                    data.min = file.readByte();
                    data.max = file.readByte();
                    data.sum = file.readShort();
                    data.last = file.readByte();
                    break;

                case SHORT:
                    data.min = file.readShort();
                    data.max = file.readShort();
                    data.sum = file.readInt();
                    data.last = file.readShort();
                    break;

                case INT:
                    data.min = file.readInt();
                    data.max = file.readInt();
                    data.sum = file.readLong();
                    data.last = file.readInt();
                    break;

                case LONG:
                    data.min = file.readLong();
                    data.max = file.readLong();
                    data.sum = file.readLong();
                    data.last = file.readLong();
                    break;

                default:
                    throw new IllegalStateException();
            }

            return data;
        }

        /**
         * write single value
         *
         * @param file file
         * @param pos pos
         * @param data data
         * @throws IOException exception for IO operation
         */
        private void writeSingle(RandomAccessFile file, long pos, long data) throws IOException {
            file.seek(pos + offsetInRow);

            //
            // In single value mode, high bit is 0 that means it is empty value.
            // fillZero() method init the data to all zero bytes.
            // that cause the high bit also set to 0,
            // so that became to empty value.
            // to set a real value to slot, do not forget set the high bit to 1.
            switch (getRangeType()) {
                case BYTE:
                    file.writeByte((byte) (data | 0x80)); // set high bit to 1
                    break;
                case SHORT:
                    file.writeShort((short) (data | 0x8000)); // set high bit to 1
                    break;
                case INT:
                    file.writeInt((int) (data | 0x80000000)); // set high bit to 1
                    break;
                case LONG:
                    file.writeLong(data | 0x8000000000000000L); // set high bit to 1
                    break;
                default:
                    throw new IllegalStateException();
            }
        }

        /**
         * write multi counter data
         *
         * @param file file
         * @param pos pos
         * @param data data
         * @throws IOException exception for IO exception
         */
        private void writeMulti(RandomAccessFile file, long pos, ColumnData data)
                throws IOException {

            file.seek(pos + offsetInRow);
            file.writeInt(data.count);

            switch (getRangeType()) {
                case BYTE:
                    file.writeByte((byte) data.min);
                    file.writeByte((byte) data.max);
                    file.writeShort((short) data.sum);
                    file.writeByte((byte) data.last);
                    break;

                case SHORT:
                    file.writeShort((short) data.min);
                    file.writeShort((short) data.max);
                    file.writeInt((int) data.sum);
                    file.writeShort((short) data.last);
                    break;

                case INT:
                    file.writeInt((int) data.min);
                    file.writeInt((int) data.max);
                    file.writeLong((long) data.sum);
                    file.writeInt((int) data.last);
                    break;

                case LONG:
                    file.writeLong(data.min);
                    file.writeLong(data.max);
                    file.writeLong(data.sum);
                    file.writeLong(data.last);
                    break;

                default:
                    throw new IllegalStateException();
            }
        }
    }

    public CounterType[] getTypes() {
        return types.values().toArray(new CounterType[types.size()]);
    }

    /**
     * get index of specified type
     *
     * @param type type
     * @return index
     */
    public int indexOfType(String type) {
        Integer res = typeIndexes.get(type);
        return res == null ? -1 : res;
    }

    /** make type indexes */
    private void makeTypeIndexes() {

        int index = 0;

        for (String name : types.keySet()) {
            typeIndexes.put(name, index++);
        }
    }
}
