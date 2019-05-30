package com.example.asus.sinkeyboard;

import android.util.Log;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Comparator;

import prediction.Dictionary;

public class PredictionProcessor {
    private Dictionary dictionary=new Dictionary();


    public void buildDictionary(BufferedReader reader,String typedWord){

        dictionary.buildDictionary(reader,typedWord);
        //Log.d("list22", "buildDictionary: ");;

        Log.d("message","status :"+"build is ok");
    }

    public ArrayList<String> getWordList(String word){
        // Log.d("message","status :"+"getWordListOk");
        //this.dictionary.setFile();
        //this.sinhalaDictionary= dictionary.getDictionary();

        ArrayList<String> wordList=this.dictionary.getSinhalaWordList(word);
        //ArrayList<String> copywordList=new ArrayList<String>();
        //for (int i = 0; i < wordList.size(); i++) {
        //    wordArrayList.add(wordList.get(i));
        //}
        if (wordList!=null){
            wordList.sort(Comparator.comparing(String::length));
        }


        // this part is ok!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
        Log.d("extract", "dic word "+ word+"    "+wordList);
        ArrayList<String> wordArrayList=new ArrayList<String>();
        if(wordList!=null) {
            for (int i = 0; i < Math.min(wordList.size(),6); i++) {
                wordArrayList.add(wordList.get(i));
            }
            return wordArrayList;
        }else{
            ///  System.out.println("this line is ok");
            return wordArrayList;
        }
    }
}
