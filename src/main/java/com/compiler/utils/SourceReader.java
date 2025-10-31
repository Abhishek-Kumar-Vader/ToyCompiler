package com.compiler.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;

public class SourceReader {
    private final BufferedReader reader;
    private int currentChar;
    private int line;
    private int column;
    private boolean eof;

    public SourceReader(String filename) throws IOException {
        this.reader = new BufferedReader(new FileReader(filename));
        this.line = 1;
        this.column = 0;
        this.eof = false;
        advance();
    }

    private SourceReader(BufferedReader reader) throws IOException {
        this.reader = reader;
        this.line = 1;
        this.column = 0;
        this.eof = false;
        advance();
    }

    public static SourceReader fromString(String source) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(source));
        return new SourceReader(reader);
    }

    public char current() {
        if (eof) return '\0';
        return (char) currentChar;
    }

    public void advance() throws IOException {
        currentChar = reader.read();

        if (currentChar == -1) {
            eof = true;
            currentChar = '\0';
        } else {
            column++;
            if (currentChar == '\n') {
                line++;
                column = 0;
            }
        }
    }

    public char peek() throws IOException {
        reader.mark(1);
        int next = reader.read();
        reader.reset();
        return next == -1 ? '\0' : (char) next;
    }

    public boolean isEof() {
        return eof;
    }

    public int getLine() {
        return line;
    }

    public int getColumn() {
        return column;
    }

    public void close() throws IOException {
        if (reader != null) {
            reader.close();
        }
    }
}
