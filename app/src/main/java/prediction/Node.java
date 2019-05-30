package prediction;

import java.util.HashMap;

public class Node {

    private String value;
    private HashMap<String, Node> children;
    private Node parent;
    private boolean isFinishedPoint;


    public Node(String value){
        setValue(value);
        HashMap<String, Node> arr = new HashMap<String, Node>();
        setChildren(arr);


    }
    public Node(){
        HashMap<String, Node> arr = new HashMap<String, Node>();
        setChildren(arr);


    }

    public String getValue(){
        return this.value;
    }

    public void setValue(String value){
        this.value=value;
    }

    public HashMap<String, Node> getChildren() {
        return this.children;
    }

    public void setChildren(HashMap<String, Node> arr){
        this.children=arr;
    }

    public Node getParent() {
        return this.parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }
    public void setChild(Node child){
        this.children.put(child.getValue(), child);
        child.setParent(this);
    }

    public void setisFinishedPoint(boolean b){
        this.isFinishedPoint=b;

    }

    public boolean getisFinishedPoint(){
        return this.isFinishedPoint;
    }


}
