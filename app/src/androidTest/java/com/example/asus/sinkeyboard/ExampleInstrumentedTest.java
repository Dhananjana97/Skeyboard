package com.example.asus.sinkeyboard;

import android.content.Context;
import android.inputmethodservice.KeyboardView;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    Context appContext;
    PredictionProcessor predictionProcessor;
    FileInputStream fileInputStream;
    StringBuilder mComposing;



    @Before
    public void init(){
        appContext = InstrumentationRegistry.getTargetContext();
        mComposing =new StringBuilder();

        predictionProcessor= new PredictionProcessor();
        String inputword="ඩ";


        try {
            fileInputStream = appContext.openFileInput("word.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            predictionProcessor.buildDictionary(reader,inputword);




        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }




    }


    @Test
    public void useAppContext() {
        // Context of the app under test.

        assertEquals("com.example.asus.sinkeyboard", appContext.getPackageName());



    }

    @Test
    public void testGetWordList(){
        ArrayList<String> l=predictionProcessor.getWordList("ඩි");
        String[] expected={"ඩිමෝට්", "ඩිප්ලෝමාවත්"};

        Log.d("test", "testGetWordList: "+l);

       //  assertEquals(6,l.size());
        assertArrayEquals(expected,l.toArray());





    }

    @Test
    public  void testupdateCandidates(){
        Skeyboard sk=new Skeyboard();
        sk.updateCandidates();
    }

    @Test
    public  void testHandleBackspace(){
        Skeyboard sk=new Skeyboard();
        sk.handleBackspace();
    }
//---------------------------------------------------------------------------








}
