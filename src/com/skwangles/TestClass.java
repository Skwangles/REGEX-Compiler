package com.skwangles;

import org.junit.jupiter.api.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestClass {

//    ByteArrayOutputStream outContent;
//    private static final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    private static final PrintStream originalOut = System.out;
    private static final PrintStream originalErr = System.err;

    @BeforeEach
    void beforeEach(){

    }

    @AfterAll
    static void restoreStreams() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    public void TestBrackets() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        REcompile rc = new REcompile();
        rc.parse("(((b|a)|b+a)b|a)b");
        assertEquals("0 10 10\s\r\n1 9 9 b\r\n2 1 3\s\r\n3 9 9 a\r\n4 2 5\s\r\n5 7 7 b\r\n6 7 7 b\r\n7 8 6\s\r\n8 9 9 a\r\n9 12 12 b\r\n10 4 11\s\r\n11 12 12 a\r\n12 13 13 b\r\n13 0 0\s\r\n", outContent.toString());
    }

    @Test
    public void TestSlash() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        REcompile rc = new REcompile();
        rc.parse("a(\\*b|cd+)*a?");
        assertEquals(
                "0 1 1\s\r\n1 9 9 a\r\n2 3 3 *\r\n3 9 9 b\r\n4 2 5\s\r\n5 6 6 c\r\n6 8 8 d\r\n7 8 8 d\r\n8 9 7\s\r\n9 11 4\s\r\n10 12 12 a\r\n11 12 10\s\r\n12 0 0\s\r\n", outContent.toString());
    }
}

