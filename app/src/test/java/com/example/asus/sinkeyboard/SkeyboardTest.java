package com.example.asus.sinkeyboard;

import org.junit.Test;

import static org.junit.Assert.*;

public class SkeyboardTest {

    @Test
    public void isWordSeparator() {
        boolean result;
        int input;
        boolean expect;

        input=35;
        expect=true;


        Skeyboard sk=new Skeyboard();
        result =sk.isWordSeparator(input);
        assertEquals(expect, result);

    }



    @Test
    public void takeSuggestionAt() {
        boolean result;
        int input;
        boolean expect;

        input=38;
        expect=true;


        Skeyboard sk=new Skeyboard();
        result =sk.isWordSeparator(input);
        assertEquals(expect, result);

    }

    @Test
    public void getWordList() {
        boolean result;
        int input;
        boolean expect;

        input=39;
        expect=true;


        Skeyboard sk=new Skeyboard();
        result =sk.isWordSeparator(input);
        assertEquals(expect, result);

    }




}