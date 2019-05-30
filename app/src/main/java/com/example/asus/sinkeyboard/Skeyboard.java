package com.example.asus.sinkeyboard;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.text.Selection;
import android.text.method.MetaKeyKeyListener;
import android.util.Log;
import android.util.TimingLogger;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Skeyboard extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private KeyboardView kv;
    private Keyboard keyboard;
    private Keyboard shifted_keyboard;
    private Keyboard number_keyboard;
    private Keyboard symbol_keyboard;
    private String  type="language";
    private boolean isCaps = false;
    private boolean isPicked=false;

    private InputMethodManager mInputMethodManager;
    private CandidateView mCandidateView;
    private CompletionInfo[] mCompletions;

    private PredictionProcessor predictionProcessor= new PredictionProcessor();
    private StringBuilder mComposing;
    private boolean mPredictionOn=true;
    private boolean mCompletionOn;
    private boolean mSound;
    private int mLastDisplayWidth;
    private boolean mCapsLock;
    private long mLastShiftTime;
    private long mMetaState;
    private String mWordSeparators;
    private boolean iscomposing;

    private ArrayList<String> suggestionList;
    private String typedWord="";
    private boolean noSugg;
    private String veryNewWord="";



    @Override
    public void onInitializeInterface(){
        keyboard=new Keyboard(this,R.xml.qwerty);
        shifted_keyboard=new Keyboard(this,R.xml.qwerty_shift);
        symbol_keyboard=new Keyboard(this,R.xml.symbols);
        number_keyboard=new Keyboard(this,R.xml.numbers);
        Log.d("inita", "onInitializeInterface: ");
        //making new keyboard1
    }


    @Override
    public View onCreateInputView() {
        onInitializeInterface();
        Log.d("fpath", "onCreateInputView: ");
        //copy word file into internal storage after only installation
        Context context =getApplicationContext();


       // String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/Skeyboard/";
        File file=new File(context.getFilesDir(),"word");
        Log.d("fpath", "pathss: "+context.getFilesDir());
        if(!file.exists()){
            Log.d("fpath", "pathss: "+context.getFilesDir());
            try{
                boolean b=file.mkdir();
                Log.d("fpath", "filemake: "+b);
                writetofile("word.txt","word file is this");
            }catch (Exception e){
                Log.d("fpath", "makedir error: "+e.getMessage());
            }



          //  copyFile(context,path);

        }else{
            Log.d("fpath", "elselsels: ");
            readfromfile();

        }

      //  AssetManager assetManager=getAssets();
  //      InputStream in =null;
  //      OutputStream out=null;

    //    try {
      //      in =assetManager.open("words.txt");
        //    File outFile=new File()
   //     } catch (IOException e) {
     //       e.printStackTrace();
       // }



       kv =(KeyboardView)getLayoutInflater().inflate(R.layout.keyboard,null);


       kv.setKeyboard(keyboard);
       kv.setOnKeyboardActionListener(this);
       return kv;



    }

    private void writetofile(String filename, String content) throws IOException {

        FileOutputStream outputStream;
        String line;

        AssetManager assetManager = getAssets();
        InputStream is = assetManager.open("words.txt");
        InputStreamReader inputStreamReader = new InputStreamReader(is);
        BufferedReader reader = new BufferedReader(inputStreamReader,50000);
        outputStream =openFileOutput(filename,Context.MODE_PRIVATE);

        while ( (line = reader.readLine() ) != null){
            outputStream.write(line.getBytes());
            outputStream.write("   \n".getBytes());
            Log.d("fpath", "writetofile====: "+line+"\n");

           // String[] split = line.split(" ");

           // for (int i = 0; i < split.length; i++) {
             //   String w="  "+split[i].trim()+"  ";
               // Log.d("fpath", "writetofile: "+w);
                //outputStream.write(line.getBytes());
            }


        //}

       // outputStream.close();
       // reader.close();

    }

    private void readfromfile(){
        try {
            FileInputStream fileInputStream=openFileInput("word.txt");
            InputStreamReader inputStreamReader =new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader,500000);
            StringBuffer stringBuffer =new StringBuffer();

            String lines;
            StringBuffer s = null;
            while((lines=bufferedReader.readLine())!=null){
                Log.d("fpath", "readfromfile:lines-----------"+lines);
                s=stringBuffer.append(lines+"\n");

            }

            fileInputStream.close();


            Log.d("fpath", "readfromfile: "+s);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void copyFile(Context context,String path){
        AssetManager assetManager = context.getAssets();
        String line;
        try {
            Log.d("fpath", "copyFile:cccccccccccccccc");
            InputStream in = assetManager.open("words.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader buffer = new BufferedReader(inputStreamReader);
            FileOutputStream out = context.openFileOutput(path,Context.MODE_PRIVATE);
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out);
            BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);


            try {

                while ( (line = buffer.readLine() ) != null) {
                   // String[] split = line.split(" ");

                    Log.d("wordlt","array"+line);

                    writeDataToFile(bufferedWriter, line);
                 //   for (int i = 0; i < split.length; i++) {

                   //     this.wt.insert(split[i]);//give parameter only word string space removed

                   // }

                }
                buffer.close();

            } catch (Exception e) {
                //System.out.println(e);
            }

            outputStreamWriter.close();
            out.close();

         //   int read = in.read(buffer);
        //    while (read != -1) {
          //      Log.d("fpath", "copyFFFFFile: ");
            //    out.write(buffer, 0, read);
              //  read = in.read(buffer);
         //   }
        } catch (Exception e) {
            e.getMessage();
            Log.d("fpath", "copyFileeee: "+e.getMessage());
        }
    }



    private void writeDataToFile(BufferedWriter bufferedWriter, String data)
    {
        try {
            bufferedWriter.write(data);

            bufferedWriter.flush();
            bufferedWriter.close();
          //  outputStreamWriter.close();
        }catch(FileNotFoundException ex)
        {
            Log.e("eerr", ex.getMessage(), ex);
        }catch(IOException ex)
        {
            Log.e("eerr", ex.getMessage(), ex);
        }
    }




    private String readFromFileInputStream(FileInputStream fileInputStream)
    {
        StringBuffer retBuf = new StringBuffer();
        Log.d("taggg", "readFrom: 232323");

        try {
            if (fileInputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                String lineData = bufferedReader.readLine();
                while (lineData != null) {
                    retBuf.append(lineData);
                    lineData = bufferedReader.readLine();
                    Log.d("taggg", "readFromFileInputStream: "+lineData);
                }
            }
        }catch(IOException ex)
        {
            Log.e("errr", ex.getMessage(), ex);
        }finally
        {
            return retBuf.toString();
        }
    }


    public View onCreateCandidatesView() {
        mCandidateView = new CandidateView(this);
        mCandidateView.setService(this);

        return mCandidateView;
    }

  /**  private  void writeWordToFile(String word){
        AssetManager asstManager = getAssets();
        try {
            File folder =Environment.getDataDirectory();
            OutputStream inputStream=asstManager.open("words.txt");
            OutputStreamWriter outputStreamWriter=new OutputStreamWriter(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }**/

    private void loadDictionary(String typedWord){

        try {
//            ArrayList<String> file= new ArrayList<String>();
//            file.add("article1rewrite.txt");
//            file.add("article2rewrite.txt");

            // for (int i = 0 ; i< file.size(); i++) {
        //    AssetManager assetManager = getAssets();
          // InputStream is = assetManager.open("words.txt");
            FileInputStream fileInputStream=openFileInput("word.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader reader = new BufferedReader(inputStreamReader);
            predictionProcessor.buildDictionary(reader,typedWord);

            fileInputStream.close();
            inputStreamReader.close();
            reader.close();
              Log.d("fpath", "status :" + "Initializing is ok");
            //  }
        } catch (IOException e) {
            System.out.println(e);
        }



    }

    @Override
    public void onPress(int primaryCode) {
        kv.setPreviewEnabled(true);

        // Disable preview key on Shift, Delete, Space, Language, Symbol and Emoticon.
        if (primaryCode == -1 || primaryCode == -5 || primaryCode == -2 || primaryCode == -10000
                || primaryCode == -101 || primaryCode == 32) {
            kv.setPreviewEnabled(false);
        }
    }

    /**
     * Helper to send a key down / key up pair to the current editor.
     */
    private void keyDownUp(int keyEventCode) {
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_DOWN, keyEventCode));
        getCurrentInputConnection().sendKeyEvent(
                new KeyEvent(KeyEvent.ACTION_UP, keyEventCode));
    }

    /**
     * Helper to send a character to the editor as raw key events.
     */
    private void sendKey(int keyCode) {
        switch (keyCode) {
            case '\n':
                keyDownUp(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                if (keyCode >= '0' && keyCode <= '9') {
                    keyDownUp(keyCode - '0' + KeyEvent.KEYCODE_0);
                } else {
                    getCurrentInputConnection().commitText(String.valueOf((char) keyCode), 1);
                }
                break;
        }
    }


    /**
     * Helper function to commit any text being composed in to the editor.
     */



    private String getWordSeparators() {
        mWordSeparators=" @#%&*-=!\"':;/?«~±×÷•°`´_+¡¢|\\¿»";
        return mWordSeparators;
    }

    public boolean isWordSeparator(int code) {
        String separators = getWordSeparators();
        return separators.contains(String.valueOf((char)code));
    }


    //    private void handleCharacter(int primaryCode, int[] keyCodes) {
//        if (isInputViewShown()) {
//            if (mInputView.isShifted()) {
//                primaryCode = Character.toUpperCase(primaryCode);
//            }
//        }
//        if (mPredictionOn) {
//            mComposing.append((char) primaryCode);
//            getCurrentInputConnection().setComposingText(mComposing, 1);
//            updateShiftKeyState(getCurrentInputEditorInfo());
//            updateCCandidates();
//        } else {
//            getCurrentInputConnection().commitText(
//                    String.valueOf((char) primaryCode), 1);
//        }
//    }

    private void handleCharacter(int primaryCode, int[] keyCodes) {

        Log.d("extract", "handleCharacter.................");
        StringBuilder wd=null;
        if(mComposing==null){
            mComposing =new StringBuilder();
        }
        Log.d("extract", "handleCharacter:ispicked:"+isPicked);
        Log.d("extract", "handleCharacter:newwordadded"+mCandidateView.getNewWordAdded());

        if(isPicked){
            ExtractedText t=getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(),0);

            Log.d("extract", "handleCharacter: "+t.text.toString());


            int cursorPosition = t.selectionStart;

            Log.d("extract", "handleCharacter cursorpoint: "+cursorPosition);

            CharSequence enteredText = t.text.toString();
            //CharSequence cursorToEnd = enteredText.subSequence(cursorPosition, enteredText.length());

            String[] arr=((String) enteredText).split(" ");
            ArrayList<String> typed_arr = new ArrayList<String>(Arrays.asList(arr));
            Log.d("extract", "handleCharacter: "+typed_arr);

            int before_length=-1;
            for (int i = 0; i <typed_arr.size(); i++) {
                String w= typed_arr.get(i);
                before_length+=w.length()+1;
                if (before_length<cursorPosition)
                {

                    Log.d("extract", "handleCharacter"+w.length());

                }else{
                    wd=new StringBuilder(w);

                    Log.d("extract", "handleCharacter eelse");
                    break;
                }

                Log.d("extract", "handleCharacter typed: "+wd+"-----"+i);



            }
            Log.d("extract", " handleCharacter typed: "+wd+"-----");
            isPicked=false;

        }else{
            wd=new StringBuilder(mComposing);
            Log.d("extract", "handleCharacter:nopicked:"+wd);
        }

        mComposing.append((char) primaryCode);
        this.iscomposing=true;
        typedWord=mComposing.toString();

        if (isWordSeparator(primaryCode)) {
            getCurrentInputConnection().commitText(mComposing,1);


          /*  if(predictionProcessor.getWordList(wd.toString()).isEmpty()){
                if(mCandidateView.getNewWordAdded()){
                    getCurrentInputConnection().setComposingText((Character.toString((char)primaryCode)), 1);
                    mCandidateView.setNewWordAdded(false);
                }else{
                    getCurrentInputConnection().setComposingText(wd+"*"+(char)primaryCode, 1);
                }

                Log.d("qqq", "handleCharacter:wordsep ");
            }else{
                if(predictionProcessor.getWordList(wd.toString()).contains(wd)){
                    getCurrentInputConnection().setComposingText(mComposing,1);
                }else{
                    getCurrentInputConnection().setComposingText(wd+"*"+(char)primaryCode, 1);
                }
                getCurrentInputConnection().setComposingText(mComposing,1);
            }*/




            typedWord="";
            getCurrentInputConnection().finishComposingText();
            mComposing.setLength(0);
            setCandidatesViewShown(false);

            //  updateShiftKeyState(getCurrentInputEditorInfo());
        } else {

            getCurrentInputConnection().setComposingText(mComposing, 1);
            Log.d("qqq", "handleCharacter:normal ");



        }


        updateCandidates();
    }

    private void addToNewWordsArray() {
    }


    @Override
    public void onRelease(int primaryCode) {

    }



    @Override
    public void onUpdateSelection(int oldSelStart, int oldSelEnd,
                                  int newSelStart, int newSelEnd,
                                  int candidatesStart, int candidatesEnd) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd,
                candidatesStart, candidatesEnd);

        // If the current selection in the text view changes, we should
        // clear whatever candidate text we have.
        if (mComposing.length() > 0 && (newSelStart != candidatesEnd
                || newSelEnd != candidatesEnd)) {
            mComposing.setLength(0);
            updateCandidates();
            InputConnection ic = getCurrentInputConnection();
            if (ic != null) {
                ic.finishComposingText();
            }
        }
    }


    /**
     * This tells us about completions that the editor has determined based
     * on the current text in it.  We want to use this in fullscreen mode
     * to show the completions ourselves, since the editor can not be seen
     * in that situation.
     */
    @Override
    public void onDisplayCompletions(CompletionInfo[] completions) {
        if (mCompletionOn) {
            mCompletions = completions;
            if (completions == null) {
                setSuggestions(null, false, false);
                return;
            }

            List<String> stringList = new ArrayList<>();
            for (CompletionInfo ci : completions) {
                if (ci != null) stringList.add(ci.getText().toString());
            }
            setSuggestions(stringList, true, true);
        }
    }


    /**
     * This translates incoming hard key events in to edit operations on an
     * InputConnection.  It is only needed when using the
     * PROCESS_HARD_KEYS option.
     */
    private boolean translateKeyDown(int keyCode, KeyEvent event) {
        mMetaState = MetaKeyKeyListener.handleKeyDown(mMetaState,
                keyCode, event);
        int c = event.getUnicodeChar(MetaKeyKeyListener.getMetaState(mMetaState));
        mMetaState = MetaKeyKeyListener.adjustMetaAfterKeypress(mMetaState);
        InputConnection ic = getCurrentInputConnection();
        if (c == 0 || ic == null) {
            return false;
        }

        if ((c & KeyCharacterMap.COMBINING_ACCENT) != 0) {
            c = c & KeyCharacterMap.COMBINING_ACCENT_MASK;
        }

        if (mComposing.length() > 0) {
            char accent = mComposing.charAt(mComposing.length() - 1);
            int composed = KeyEvent.getDeadChar(accent, c);
            if (composed != 0) {
                c = composed;
                mComposing.setLength(mComposing.length() - 1);
            }
        }

        onKey(c, null);

        return true;
    }







    /**
     * Update the list of available candidates from the current composing
     * text.  This will need to be filled in by however you are determining
     * candidates.
     */
    public void updateCandidates() {
//        if (!mCompletionOn) {
//            if (mComposing.length() > 0) {
//                ArrayList<String> list = new ArrayList<String>();
//                list.add(mComposing.toString());
//                Log.d("SoftKeyboard", "REQUESTING: " + mComposing.toString());
//                //mScs.getSentenceSuggestions(new TextInfo[] {new TextInfo(mComposing.toString())}, 5);
//                setSuggestions(list, true, true);
//            } else {
//                setSuggestions(null, false, false);
//            }
//        }



        if (!mCompletionOn) {
            try{

                ExtractedText t=getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(),0);
                String[] lines=t.text.toString().split("\n");
                Log.d("extract", "updateCandidates: "+t.text.toString()+"------"+lines);


                int cursorPosition = t.selectionStart;

                Log.d("extract", "cursorpoint: "+cursorPosition);

               CharSequence enteredText = t.text.toString();
                //CharSequence cursorToEnd = enteredText.subSequence(cursorPosition, enteredText.length());

                String[] arr=((String) enteredText).split(" ");
                ArrayList<String> typed_arr = new ArrayList<String>(Arrays.asList(arr));
                Log.d("extract", "updateCandidates: "+typed_arr);

                int before_length=-1;
                for (int i = 0; i <typed_arr.size(); i++) {
                   String w= typed_arr.get(i);
                    before_length+=w.length()+1;
                   if (before_length<cursorPosition)
                   {

                       Log.d("extract", "upda"+w.length());

                   }else{
                     //  Log.d("extract", "updateCandidates: "+w.charAt(w.length()-1));
                       if(w.length()>0){
                           if(w.charAt(w.length()-1)==(("*").charAt(0))){
                               typedWord=w.substring(0,w.length()-1);
                           }else{
                               typedWord=w;
                           }
                       }


                       Log.d("extract", "updateelse");
                       break;
                   }

                    Log.d("extract", "typed: "+typedWord+"-----"+i);



                }
                Log.d("extract", "typed: "+typedWord+"-----");



                if(typedWord.length()==1) {
                    Log.d("extract", "REQUESTING: " + "9999999999");
                    loadDictionary(typedWord);
                }

                if (typedWord.length() > 0) {
                    Log.d("extract", "REQUESTING: " + "oooooooooooooooooooooooooooo");
                    suggestionList = new ArrayList<String>();
                    ArrayList<String> suggestedList;

                    suggestedList=predictionProcessor.getWordList(typedWord);
                    Log.d("extract", "REQUESTING: " + suggestedList);
                    //Log.d("keyboard","candidate"+ typedWord);
                    //mScs.getSentenceSuggestions(new TextInfo[] {new TextInfo(mComposing.toString())}, 5);
                    if(!suggestedList.isEmpty()){
                        if(suggestedList.contains(typedWord)){
                            suggestionList.add(typedWord);
                            suggestionList.addAll(suggestedList);
                        }else{
                            suggestionList.add(typedWord+"*");
                            suggestionList.addAll(suggestedList);
                        }

                        noSugg=false;

                        Log.d("candidate", "updateCandidates: "+suggestionList);

                    }else{
                        noSugg=true;
                        suggestionList.add(typedWord+"*");
                        Log.d("candidate", "typed: "+typedWord);
                    }

                    Log.d("extract", "updateCandidates:"+suggestedList);

                    setSuggestions(suggestionList, true, true);
                } else {
                    setSuggestions(null, false, false);
                }

            }
            catch(NullPointerException e){

            }


        }
    }

    public void onDestroy(){
        Log.d("fpath", "onDestroy:www ");
    }


    public void handleBackspace() {
/*        final int length = mComposing.length();
        if (length > 1) {
            mComposing.delete(length - 1, length);
            getCurrentInputConnection().setComposingText(mComposing, 1);
            updateCandidates();
        } else if (length > 0) {
            mComposing.setLength(0);
            getCurrentInputConnection().commitText("", 0);
            updateCandidates();
        }
        else{

        }*/
        Log.d("extract", "handleBackspace: "+mComposing);


        if(mComposing!=null){
            if(mComposing.length()>0) {
                mComposing.deleteCharAt(mComposing.length()-1);
                typedWord = mComposing.toString();
                getCurrentInputConnection().setComposingText(mComposing, 1);

            }else{
                getCurrentInputConnection().deleteSurroundingText(1,0);// delete surrounding text
            }
        }


        updateCandidates();
    }

    /**
     * This is called when the user is done editing a field.  We can use
     * this to reset our state.
     */
    @Override public void onFinishInput() {
        super.onFinishInput();

        // Clear current composing text and candidates.
        if(mComposing!=null){
            mComposing.setLength(0);
            updateCandidates();
        }



        // We only hide the candidates window when finishing input on
        // a particular editor, to avoid popping the underlying application
        // up and down if the user is entering text into the bottom of
        // its window.
        setCandidatesViewShown(false);


    }

    public void setSuggestions(List<String> suggestionsList, boolean completions,
                               boolean typedWordValid) {
        if (suggestionsList != null && suggestionsList.size() > 0) {
            setCandidatesViewShown(true);
        } else if (isExtractViewShown()) {
            setCandidatesViewShown(true);
        }
        if (mCandidateView != null) {

            mCandidateView.setSuggestions(suggestionsList, completions, typedWordValid);
        }
    }




    @Override
    public void onKey(int i, int[] ints) {

        InputConnection ic=getCurrentInputConnection();
        playClick(i);
        Log.d("awawa", "onKey: ");
       /* Context ctxt =getApplicationContext();
        try {
            Log.d("onkkk", "onKey:222222222222 ");
            FileInputStream fileInputStream= ctxt.openFileInput("word.txt");
            String fileData =readFromFileInputStream(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("onk", "onKey:44444 ");
        }
        //getdoc();*/
        switch (i)
        {
            case Keyboard.KEYCODE_DELETE:
                handleBackspace();

                break;
            case Keyboard.KEYCODE_SHIFT:

                isCaps=!isCaps;
                handleShift();
                kv.invalidateAllKeys(); // redraw whole keyboard
                break;
            case Keyboard.KEYCODE_DONE:
                ic.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_ENTER));
                //Log.d("awawa", "onKey: ");
                finish();
                handleCharacter(32, ints);

                break;
            case Keyboard.KEYCODE_ALT:
                handleAlt();
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                handleModeChange();
                break;
            default:

                handleCharacter(i, ints);

               // char code = (char)i;
                //if (Character.isLetter(code) && isCaps)
                  //  code=Character.toUpperCase(code);
                //Log.d("qq",String.valueOf(code));
                //ic.commitText(String.valueOf(code),1);


        }
    }

    private void finish() {
        Log.d("fpath", "finish: ");
       // typedWord="";
       // getCurrentInputConnection().finishComposingText();
       // mComposing.setLength(0);
       // setCandidatesViewShown(false);
       // updateCandidates();

    }



    public void getdoc(){
        Context ctx=getApplicationContext();
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = ctx.openFileInput("word.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);

        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        try {
            String lineData = bufferedReader.readLine();
            Log.d("12121", "getdoc: "+lineData);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public void pickSuggestionManually(int index) {
//        if (mCompletionOn  && index >= 0 && index < mCompletions.length) {
//            CompletionInfo ci = mCompletions[index];
//            getCurrentInputConnection().commitCompletion(ci);
//            if (mCandidateView != null) {
//                mCandidateView.clear();
//            }
//        }
        if(mComposing.length()>0){
            removeComposing();
        }
        getSelectedWordandRemove();

        if (typedWord.length() > 0) {
            // If we were generating candidate suggestions for the current
            // text, we would commit one of them here. But for this sample,
            // we will just commit the current text.
           // mComposing.setLength(index);
           String selectedWord = suggestionList.get(index);
           if(selectedWord.charAt(selectedWord.length()-1)==("*").charAt(0)){
               typedWord=selectedWord.substring(0,selectedWord.length()-1);
           }else{
               typedWord=selectedWord;
           }


           // typedWord=mComposing.toString();// set the suggetion word list according to picked word
           // System.out.println("This is the composing text : "+mComposing);
            Log.d("extract", "pickSuggestionManually: "+typedWord);

            boolean a=getCurrentInputConnection().commitText(typedWord , 1);
            Log.d("extract", "pickSuggestionManually:"+a);
            isPicked=true;

        }
        updateCandidates();
    }

    private void getSelectedWordandRemove() {
        int editWordStart=0;
        int editWordEnd=0;
        int cursorPosition;
        ExtractedText t=getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(),0);

       // Log.d("extract", "select sugg : "+t.text.toString());

        if(t==null){
            cursorPosition=0;
        }else{
            cursorPosition = t.selectionStart;
        }


        Log.d("extract", "select sugg cursorpoint: "+cursorPosition);
        CharSequence enteredText = t.text.toString();
        //CharSequence cursorToEnd = enteredText.subSequence(cursorPosition, enteredText.length());

        String[] arr=((String) enteredText).split(" ");
        ArrayList<String> typed_arr = new ArrayList<String>(Arrays.asList(arr));
        Log.d("extract", "select sugg : "+typed_arr);

        int before_length=-1;
        for (int i = 0; i <typed_arr.size(); i++) {
            String w= typed_arr.get(i);
            before_length+=w.length()+1;

            Log.d("extract", "select sugg w.length"+w.length()+"before_length"+before_length);

            if (before_length<cursorPosition)
            {
                if(cursorPosition==before_length+1){
                    break;
                }

                Log.d("extract", "select sugg w.length************************");

            }else{



                editWordStart=before_length-w.length();
                editWordEnd=before_length;
                int beforecu=cursorPosition-editWordStart;
                int aftercu=editWordEnd-cursorPosition;
                Log.d("extract", "typed: "+typedWord+"-----"+beforecu+"   "+aftercu);

                getCurrentInputConnection().deleteSurroundingText(beforecu,aftercu);


               // Log.d("extract", "getSelectedWordandRemove: "+editWordStart+"  "+editWordEnd);

                if(editWordStart== editWordEnd){

                }else{
                    Log.d("extract", "select sugg updateelse   "+editWordStart+"-------"+editWordEnd);
                   // break;
                }

                break;

            }

            Log.d("extract", "select sugg typed: "+typedWord+"-----"+i);



        }

    }

    private void removeComposing() {
        mComposing.delete(0,mComposing.length());

        getCurrentInputConnection().setComposingText(mComposing, 1);
        ExtractedText t=getCurrentInputConnection().getExtractedText(new ExtractedTextRequest(),0);
        int cursorPosition = t.selectionStart;
        Log.d("extract", "remove composin : "+t.text.toString()+"  curpos:"+cursorPosition);




        
    }

    private void handleModeChange() {
        Keyboard currentKeyboard= kv.getKeyboard();
        if (currentKeyboard == keyboard || currentKeyboard == shifted_keyboard || currentKeyboard == symbol_keyboard){
            kv.setKeyboard(number_keyboard);
        }else{
            kv.setKeyboard(keyboard);
        }
    }

    private void handleAlt() {
        Keyboard currentKeyboard= kv.getKeyboard();
        if (currentKeyboard == symbol_keyboard){
            kv.setKeyboard(keyboard);
        }else if(currentKeyboard == number_keyboard){
            kv.setKeyboard(symbol_keyboard);
        }

    }

    public void handleShift(){
        if(isCaps){
            kv.setKeyboard(shifted_keyboard);

        }else{
            kv.setKeyboard(keyboard);


        }
    }

    private void playClick(int i) {

        AudioManager am= (AudioManager) getSystemService(AUDIO_SERVICE);
        switch (i)
        {
            case 32:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                break;
            case Keyboard.KEYCODE_DONE:
            case 10:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                break;
            case Keyboard.KEYCODE_DELETE:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                break;
            default:
                am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);

        }

    }

    @Override
    public void onText(CharSequence text) {

    }

    @Override
    public void swipeLeft() {

    }

    @Override
    public void swipeRight() {

    }

    @Override
    public void swipeDown() {

    }

    @Override
    public void swipeUp() {

    }
}
