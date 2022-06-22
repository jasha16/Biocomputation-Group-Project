package main;

import java.util.ArrayList;

public class Node {
    protected String data;
    protected ArrayList<Node> children;

    public Node(String data){
        this.data = data;
        children = new ArrayList<Node>();
    }

    public ArrayList<Node> getChildren() {
        return children;
    }

    public String getData(){
        return this.data;
    }
}
