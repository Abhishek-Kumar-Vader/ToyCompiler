package com.compiler.utils;

import org.junit.jupiter.api.Test;
import java.io.IOException;
import static org.junit.jupiter.api.Assertions.*;

public class SourceReaderTest {

    @Test
    public void testReadSingleCharacter() throws IOException {
        SourceReader reader = SourceReader.fromString("a");

        assertEquals('a', reader.current());
        assertFalse(reader.isEof());

        reader.advance();
        assertTrue(reader.isEof());
        assertEquals('\0', reader.current());

        reader.close();
    }

    @Test
    public void testReadMultipleCharacters() throws IOException {
        SourceReader reader = SourceReader.fromString("abc");

        assertEquals('a', reader.current());
        reader.advance();
        assertEquals('b', reader.current());
        reader.advance();
        assertEquals('c', reader.current());
        reader.advance();
        assertTrue(reader.isEof());

        reader.close();
    }

    @Test
    public void testLineAndColumnTracking() throws IOException {
        SourceReader reader = SourceReader.fromString("ab\ncd");

        assertEquals(1, reader.getLine());
        assertEquals(1, reader.getColumn());
        assertEquals('a', reader.current());

        reader.advance();
        assertEquals(1, reader.getLine());
        assertEquals(2, reader.getColumn());
        assertEquals('b', reader.current());

        reader.advance();
        assertEquals(2, reader.getLine());
        assertEquals(0, reader.getColumn());
        assertEquals('\n', reader.current());

        reader.advance();
        assertEquals(2, reader.getLine());
        assertEquals(1, reader.getColumn());
        assertEquals('c', reader.current());

        reader.advance();
        assertEquals(2, reader.getLine());
        assertEquals(2, reader.getColumn());
        assertEquals('d', reader.current());

        reader.close();
    }

    @Test
    public void testPeekDoesNotAdvance() throws IOException {
        SourceReader reader = SourceReader.fromString("abc");

        assertEquals('a', reader.current());
        assertEquals('b', reader.peek());
        assertEquals('a', reader.current()); // Still 'a'

        reader.advance();
        assertEquals('b', reader.current());

        reader.close();
    }

    @Test
    public void testEmptyString() throws IOException {
        SourceReader reader = SourceReader.fromString("");

        assertTrue(reader.isEof());
        assertEquals('\0', reader.current());

        reader.close();
    }

    @Test
    public void testWhitespaceHandling() throws IOException {
        SourceReader reader = SourceReader.fromString("  \t\n  ");

        assertEquals(' ', reader.current());
        reader.advance();
        assertEquals(' ', reader.current());
        reader.advance();
        assertEquals('\t', reader.current());
        reader.advance();
        assertEquals('\n', reader.current());
        reader.advance();
        assertEquals(' ', reader.current());

        reader.close();
    }
}
