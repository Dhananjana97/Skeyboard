package prediction;

import android.content.Context;
import android.support.test.InstrumentationRegistry;

import com.example.asus.sinkeyboard.PredictionProcessor;

import org.junit.Before;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;

import static org.junit.Assert.*;

public class WordTreeTest {
    Context appContext;
    PredictionProcessor predictionProcessor;
    FileInputStream fileInputStream;
    StringBuilder mComposing;
    WordTree wt;



    @Before
    public void init(){
        appContext = InstrumentationRegistry.getTargetContext();
        mComposing =new StringBuilder();
        wt = new WordTree();

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
    public void insert() {
        wt.insert("කාබනික");
    }
}