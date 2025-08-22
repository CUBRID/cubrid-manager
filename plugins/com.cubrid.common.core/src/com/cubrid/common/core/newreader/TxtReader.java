package com.cubrid.common.core.newreader;

import com.cubrid.common.core.util.LogUtil;
import com.cubrid.common.core.util.StringUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

public class TxtReader {
    private static final Logger LOGGER = LogUtil.getLogger(TxtReader.class);

    private static final String LF = StringUtil.LF;
    private static final String CRLF = StringUtil.CRLF;
    private static final String CR = StringUtil.CR;

    private BufferedReader br;

    private boolean hasNext = true;

    private String separator;

    private String rowSeparator;

    private char quotechar;

    private int skipLines;

    private boolean linesSkiped;

    private String rowPreString = "";

    public static final String DEFAULT_SEPARATOR = ",";

    public static final char DEFAULT_QUOTE_CHARACTER = '"';

    public static final int DEFAULT_SKIP_LINES = 0;

    private boolean isNotFirstReadforCR = false;

    public TxtReader(Reader reader, String separator) {
        this(reader, separator, '"');
    }

    public TxtReader(Reader reader, String columnSeparator, String rowSeparator) {
        this(reader, columnSeparator, rowSeparator, '"');
    }

    public TxtReader(Reader reader, String separator, char quotechar) {
        this(reader, separator, quotechar, 0);
    }

    public TxtReader(Reader reader, String columnSeparator, String rowSeparator, char quotechar) {
        this(reader, columnSeparator, rowSeparator, quotechar, 0);
    }

    public TxtReader(
            Reader reader, String columnSeparator, String rowSeparator, char quotechar, int line) {
        this.br = new BufferedReader(reader);
        this.separator = columnSeparator;
        this.rowSeparator = rowSeparator;
        this.quotechar = quotechar;
        this.skipLines = line;
        if (this.separator == null || this.separator.length() == 0) this.separator = ",";
    }

    public TxtReader(Reader reader, String separator, char quotechar, int line) {
        this.br = new BufferedReader(reader);
        this.separator = separator;
        this.quotechar = quotechar;
        this.skipLines = line;
        if (this.separator == null || this.separator.length() == 0) this.separator = ",";
    }

    public List<String[]> readAll() throws IOException {
        List<String[]> allElements = (List) new ArrayList<String>();
        while (this.hasNext) {
            String[] nextLineAsTokens = readNext();
            if (nextLineAsTokens != null) allElements.add(nextLineAsTokens);
        }
        return allElements;
    }

    public String[] readNextRow() throws IOException {
        String nextRow = getNextRow();
        return parseLine(nextRow);
    }

    private String getNextRow() throws IOException {
        if (this.rowSeparator == null
                || this.rowSeparator.length() == 0
                || this.rowSeparator.equals(CRLF)) {
            return readCRLFLine(br);
        }

        if (this.rowSeparator.equals(LF)) {
            return readLFLine(br);
        }

        if (this.rowSeparator.equals(CR)) {
            return readCRLine(br);
        }

        if (!this.linesSkiped) {
            for (int i = 0; i < this.skipLines; i++) this.br.readLine();
            this.linesSkiped = true;
        }
        int rowSeparatorIndex = this.rowPreString.indexOf(this.rowSeparator);
        if (!this.rowPreString.equals("") && rowSeparatorIndex > -1) {
            String str = this.rowPreString.substring(0, rowSeparatorIndex);
            this.rowPreString =
                    this.rowPreString.substring(rowSeparatorIndex + this.rowSeparator.length());
            return str;
        }
        String nextRow = this.br.readLine();
        if (nextRow == null) {
            this.hasNext = false;
            nextRow = "";
            if (this.rowPreString.equals("")) return null;
        }
        String currentLine =
                this.rowPreString.equals("")
                        ? nextRow
                        : (String.valueOf(this.rowPreString) + nextRow);
        if (currentLine.equals("")) {
            if (!this.hasNext) return null;
            return getNextRow();
        }
        int currentLineSeparatorIndex = currentLine.indexOf(this.rowSeparator);
        if (!currentLine.equals("") && currentLineSeparatorIndex > -1) {
            nextRow = currentLine.substring(0, currentLineSeparatorIndex);
            this.rowPreString =
                    currentLine.substring(currentLineSeparatorIndex + this.rowSeparator.length());
            return nextRow;
        }
        if (!this.hasNext) {
            this.rowPreString = "";
            return currentLine;
        }
        this.rowPreString = currentLine;
        return getNextRow();
    }

    public String[] readNext() throws IOException {
        String nextLine = getNextLine();
        return this.hasNext ? parseLine(nextLine) : null;
    }

    private String getNextLine() throws IOException {
        if (!this.linesSkiped) {
            for (int i = 0; i < this.skipLines; i++) this.br.readLine();
            this.linesSkiped = true;
        }
        String nextLine = this.br.readLine();
        if (nextLine == null) this.hasNext = false;
        return this.hasNext ? nextLine : null;
    }

    private String[] parseLine(String nextLine) throws IOException {
        if (nextLine == null) return null;
        String line = nextLine;
        List<String> tokensOnThisLine = new ArrayList<String>();
        StringBuffer sb = new StringBuffer();
        int index = 0;
        while (index < line.length()) {
            char c = line.charAt(index);
            if (c == this.quotechar) {
                if (index + 1 < line.length() && line.charAt(index + 1) == this.quotechar) {
                    sb.append(c);
                    index += 2;
                    continue;
                }
                sb.append(c);
                index++;
                continue;
            }
            if (index + this.separator.length() < line.length()
                    && line.substring(index, index + this.separator.length())
                            .equals(this.separator)) {
                tokensOnThisLine.add(sb.toString());
                sb = new StringBuffer();
                index += this.separator.length();
                continue;
            }
            sb.append(c);
            index++;
        }
        tokensOnThisLine.add(sb.toString());
        return tokensOnThisLine.<String>toArray(new String[tokensOnThisLine.size()]);
    }

    public void close() throws IOException {
        this.br.close();
        this.br = null;
    }

    private String readCRLFLine(BufferedReader br) {
        StringBuilder result = new StringBuilder();
        char charPrvious = '0';
        char charCurrunt;
        int checkCurrunt = 0;
        boolean isEOF = false;

        try {
            while (((checkCurrunt = br.read()) != '\n') || (charPrvious != '\r')) {
                charCurrunt = (char) checkCurrunt;
                if (checkCurrunt == -1) {
                    isEOF = true;
                    break;
                }
                charPrvious = charCurrunt;
                if (charCurrunt != '\r') {
                    result.append(charCurrunt);
                }
            }
            if (result.length() < 1) {
                if (isEOF) {
                    return null;
                }
                return readCRLFLine(br);
            }
        } catch (IOException e) {
            LOGGER.error("Error readCRLFLine", e);
        }
        return result.toString();
    }

    private String readCRLine(BufferedReader br) {
        StringBuilder result = new StringBuilder();
        char charCurrunt;
        int checkCurrunt = 0;
        boolean isEOF = false;

        try {
            while (((checkCurrunt = br.read()) != '\r')) {
                charCurrunt = (char) checkCurrunt;
                if (checkCurrunt == -1) {
                    isEOF = true;
                    break;
                }
                result.append(charCurrunt);
            }

            if (result.length() < 1) {
                if (isEOF) {
                    return null;
                }
                return readCRLine(br);
            } else if (result.length() == 1 && result.charAt(0) == '\n') {
                if (isEOF) {
                    return null;
                }
                return readCRLine(br);
            } else if (isNotFirstReadforCR && result.charAt(0) == '\n') {
                result.deleteCharAt(0);
            }
        } catch (IOException e) {
            LOGGER.error("Error readCRLine", e);
        }
        isNotFirstReadforCR = true;
        return result.toString();
    }

    private String readLFLine(BufferedReader br) {
        StringBuilder result = new StringBuilder();
        char charCurrunt;
        int checkCurrunt = 0;
        boolean isEOF = false;

        try {
            while (((checkCurrunt = br.read()) != '\n')) {
                charCurrunt = (char) checkCurrunt;
                if (checkCurrunt == -1) {
                    isEOF = true;
                    break;
                }
                if (charCurrunt != '\r') {
                    result.append(charCurrunt);
                }
            }

            if (result.length() < 1) {
                if (isEOF) {
                    return null;
                }
                return readLFLine(br);
            }
        } catch (IOException e) {
            LOGGER.error("Error readLFLine", e);
        }
        return result.toString();
    }
}
