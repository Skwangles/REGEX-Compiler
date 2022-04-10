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
        assertEquals("0 10 10\s\r\n" +
                "1 9 9 b\r\n" +
                "2 1 3 \r\n" +
                "3 9 9 a\r\n" +
                "4 2 5 \r\n" +
                "5 7 7 b\r\n" +
                "6 7 7 b\r\n" +
                "7 6 8 \r\n" +
                "8 9 9 a\r\n" +
                "9 12 12 b\r\n" +
                "10 4 11 \r\n" +
                "11 12 12 a\r\n" +
                "12 13 13 b\r\n" +
                "13 0 0 \r\n", outContent.toString());
    }

    @Test
    public void TestSlash() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        REcompile rc = new REcompile();
        rc.parse("a(\\*b|cd+)*a?");
        assertEquals(
                "0 1 1 \r\n" +
                        "1 9 9 a\r\n" +
                        "2 3 3 *\r\n" +
                        "3 9 9 b\r\n" +
                        "4 2 5 \r\n" +
                        "5 6 6 c\r\n" +
                        "6 8 8 d\r\n" +
                        "7 8 8 d\r\n" +
                        "8 7 9 \r\n" +
                        "9 4 11 \r\n" +
                        "10 12 12 a\r\n" +
                        "11 10 12 \r\n" +
                        "12 0 0 \r\n", outContent.toString());
    }

    @Test
    public void TestWildCardAndClosure(){
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        REcompile rc = new REcompile();
        rc.parse("((.*)|ba)");
        assertEquals(
                "0 3 3 \r\n" +
                        "1 2 2 __\r\n" +
                        "2 1 6 \r\n" +
                        "3 2 4 \r\n" +
                        "4 5 5 b\r\n" +
                        "5 6 6 a\r\n" +
                        "6 0 0 \r\n", outContent.toString());


    }

    @Test
    public void TestEverything(){
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        REcompile rc = new REcompile();
        rc.parse("a(c[acd]*c+)?");
        assertEquals(
                "0 1 1 \r\n" +
                        "1 12 12 a\r\n" +
                        "2 8 8 c\r\n" +
                        "3 8 8 a\r\n" +
                        "4 3 6 \r\n" +
                        "5 8 8 c\r\n" +
                        "6 5 7 \r\n" +
                        "7 8 8 d\r\n" +
                        "8 4 9 \r\n" +
                        "9 11 11 c\r\n" +
                        "10 11 11 c\r\n" +
                        "11 10 13 \r\n" +
                        "12 2 13 \r\n" +
                        "13 0 0 \r\n", outContent.toString());

    }


}

/*
a(c[acd]*c+)?
0,1,1,
1,12,12,+a
2,8,8,+c
3,8,8,+a
4,3,6,
5,8,8,+c
6,5,7,
7,8,8,+d
8,4,9,
9,11,11,+c
10,11,11,+c
11,10,13,
12,2,13,

 */
