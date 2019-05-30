package prediction;

import android.util.Log;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class WordTree {
    Node curNode;
    Node root;
    HashMap<String, Node> nodeList;

    Node currentNode;
    HashMap<String, Node> readNodeList;
    ArrayList<String> suggWordList;






    public WordTree(){
        this.root = new Node();



    }

    public void insert(String word){
      //  Log.d("in_word", "insert: "+word);
       Log.d("inserttt", "insert:"+word);
        this.curNode= this.root;

        for (int i = 0; i <word.length() ; i++) {
            //  System.out.println(this.curNode.getChildren()+"kkk");
            this.nodeList=this.curNode.getChildren();
            String l=String.valueOf((word.charAt(i)));
            if(this.nodeList.containsKey(l)){
                // System.out.println("1111111111        "+l);
                this.curNode=this.nodeList.get(l);
                if(i==word.length()-1){
                    this.curNode.setisFinishedPoint(true);
                    Log.d("inserttt", "insert: "+l);
                }

              // Log.d("inserttt", "insert:=======================");
            }else{
                Node n =new Node();
                // System.out.println(String.valueOf((word.charAt(i)))+"  -  "+this.nodeList.size());
                this.nodeList.put(l,n);
                n.setValue(l);
                Log.d("insert", "insert:"+l);
                n.setParent(this.curNode);
               // Log.d("inserttt", "insert: "+(word.length()-1)+"--------"+i);
                if(i==word.length()-1){
                    n.setisFinishedPoint(true);
                  //  Log.d("inserttt", "insert: "+l);
                }
                // System.out.println(String.valueOf((word.charAt(i)))+"    "+this.nodeList.size());
                this.curNode = n;

            }

        }

       // Log.d("insert", "insert:************************************************************* ");

        //System.out.println("*************************************************************");
    }

    public Node traversThrougGivenWord(String subStr){
        this.currentNode=this.root;
        for (int i = 0; i < subStr.length(); i++) {
            this.readNodeList=this.currentNode.getChildren();
            String l=String.valueOf((subStr.charAt(i)));
            if(this.readNodeList.containsKey(l)){
                Log.d("chek", "traversThrougGivenWord: "+l);
                //System.out.println(l+"   ---    "+currentNode.getValue());
                this.currentNode=this.readNodeList.get(l);

            }else{
                Node n=new Node();
                n.setValue("wrong");
                this.currentNode=n;
                break;
            }


        }
        System.out.println(currentNode.getValue());
        return currentNode;
    }

    public ArrayList<String> getSuggestWords(Node cNode,String subStr,ArrayList<String> arr){
        this.suggWordList=arr;
        Log.d("suggwo", "getSuggestWords: "+suggWordList);
        if(cNode.getValue().equals("wrong")){
            Log.d("suggwo", "getSuggestWords:99999");
            this.suggWordList=null;
        }else{
            this.currentNode=cNode;
            if(this.currentNode.getChildren().size()>0){
                if(this.currentNode.getisFinishedPoint() && !suggWordList.contains(subStr) ){

                    suggWordList.add(subStr);
                }
                this.readNodeList=this.currentNode.getChildren();
                for (Map.Entry<String, Node> n : readNodeList.entrySet()) {
                    String key = n.getKey();
                    Node value = n.getValue();
                    Node nextNode=value;

                    String w=subStr+nextNode.getValue();
                    Log.d("suggwo", "getSuggestWords:11111"+w);
                    if(nextNode.getisFinishedPoint()){
                        if(!suggWordList.contains(w)){
                            Log.d("suggwo", "getSuggestWords:----------- "+suggWordList);
                            suggWordList.add(w);
                        }
                    }
                    getSuggestWords(nextNode,w,this.suggWordList);
                }
            }else{
                if(!suggWordList.contains(subStr)){
                    suggWordList.add(subStr);
                }
            }
            }
        return suggWordList;
    }


    public ArrayList<String> getPredictions(String subStr){
        ArrayList<String> arr =new ArrayList<String>();
        Log.d("predstr", "getPredictions: "+subStr);
        Node n =traversThrougGivenWord(subStr);
        ArrayList<String> predictedWords=getSuggestWords(n,subStr,arr);

        Log.d("predstr_words", "getPredictions: "+subStr+"   "+predictedWords);
        return predictedWords;

    }



}

