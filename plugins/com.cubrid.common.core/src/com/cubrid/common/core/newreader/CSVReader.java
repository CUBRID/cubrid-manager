package com.cubrid.common.core.newreader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CSVReader {
    private static final String NEWLINE = System.getProperty("line.separator");

    private BufferedReader br;

    private boolean hasNext = true;

    private char separator;

    private char quotechar;

    private int skipLines;

    private boolean linesSkiped;

    public static final char DEFAULT_SEPARATOR = ',';

    public static final char DEFAULT_QUOTE_CHARACTER = '"';

    public static final int DEFAULT_SKIP_LINES = 0;

    public CSVReader(Reader reader) {
        this(reader, ',');
    }

    public CSVReader(Reader reader, char separator) {
        this(reader, separator, '"');
    }

    public CSVReader(Reader reader, char separator, char quotechar) {
        this(reader, separator, quotechar, 0);
    }

    public CSVReader(Reader reader, char separator, char quotechar, int line) {
        this.br = new BufferedReader(reader);
        this.separator = separator;
        this.quotechar = quotechar;
        this.skipLines = line;
    }

    public List<String[]> readAll() throws IOException {
        List<String[]> allElements = new ArrayList<>();
        while (this.hasNext) {
            String[] nextLineAsTokens = readNext();
            if (nextLineAsTokens != null) allElements.add(nextLineAsTokens);
        }
        return allElements;
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
        boolean inQuotes = false;
        do {
            if (inQuotes) {
                sb.append(NEWLINE);
                line = getNextLine();
                if (line == null) break;
            }
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c == this.quotechar) {
                    if (inQuotes) {
                        if (sb.length() > 0 && sb.charAt(sb.length() - 1) == '\\') {
                            sb.deleteCharAt(sb.length() - 1);
                            sb.append(this.quotechar);
                        } else if (line.length() > i + 1 && line.charAt(i + 1) == this.quotechar) {
                            sb.append(this.quotechar);
                            i++;
                        } else {
                            inQuotes = false;
                        }
                    } else {
                        inQuotes = true;
                    }
                } else if (c == this.separator && !inQuotes) {
                    String str1 = sb.toString();
                    tokensOnThisLine.add(str1);
                    sb = new StringBuffer();
                } else {
                    sb.append(c);
                }
            }
        } while (inQuotes);
        String str = sb.toString();
        tokensOnThisLine.add(str);
        return tokensOnThisLine.<String>toArray(new String[tokensOnThisLine.size()]);
    }

    public void close() throws IOException {
        this.br.close();
        this.br = null;
    }
}
