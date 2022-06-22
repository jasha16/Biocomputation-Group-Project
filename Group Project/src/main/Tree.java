package main;

import java.util.ArrayList;

public class Tree {

    public Node root;

    Tree() { root = null; }

    public void showTree(Node node,int depth,boolean not){
        if(node == null){
            return;
        }

        //find thresholds
        int th;

        if(node.getData() == "AND"){
            th = node.getChildren().size();
        }else if(node.getData() == "OR"){
            th = 1;
        }else if(node.getData() == "NOT"){
            th = 0;
        }else{
            th = -1;
        }

        //special case for depth = 0

        if (depth == 0){
            System.out.println("|");
            System.out.println("|");
            System.out.println("*"+Integer.valueOf(th));
        }else{
            String out = "|";
            String branch = "";

            //first add spaces and pipes
            for (int i = 1; i < depth; i++) {

                for (int x = 0; x < 6; x++){
                    out = out + " ";
                }

                out += "|";
            }

            //build branch

            branch = "__";

            if (not){
                branch += "-1__";
            }else{
                branch += "1__";
            }

            if (th != -1){
                branch += "*"+Integer.valueOf(th);
            }else{
                branch += node.getData();
            }

            //print out the branch
            System.out.println(out);
            System.out.println(out + branch);
        }

        if (node.getData() == "NOT"){
            not = true;
        }else{
            not = false;
        }

        //recurse on all child nodes
        for(Node n:node.getChildren()){
            showTree(n,depth+1,not);
        }
    }

    //optimize tree with one pass
    public void optimizeTree(Node node){


        //if we reached a leaf node then stop

        if (node.getChildren().size() == 0){
            return;
        }

        ArrayList<Node> newChildren = new ArrayList<Node>();

        for(Node child : node.getChildren()){

            //if current node and child node have the same operator then we can reassign grandchildren
            if (child.getData() == node.getData()){

                //loop through grandchildren and add them to new children array

                for(Node grandchild : child.getChildren()){
                    newChildren.add(grandchild);
                }

            }
            //if the operators aren't the same then keep the current node as a child

            else{
                newChildren.add(child);
            }
        }

        node.children = newChildren;

        for(Node child : node.getChildren()){
            optimizeTree(child);
        }
    }

    //optimize not statements
    public void optimizeNot(Node curr){
        if (curr == null){
            return;
        }

        int countNot = 0;

        ArrayList<Node> gChildrenNots = new ArrayList<Node>();
        Node newChild;
        Node duplicateChild;

        for(Node child : curr.getChildren()) {
            if (child.getData() == "NOT") {
                countNot++;

                //get all grand children of not nodes

                for (Node grandchild : child.getChildren()) {
                    gChildrenNots.add(grandchild);
                }

            }
        }

        //special case if all children are not

        if (countNot == curr.getChildren().size() && countNot > 0){

            System.out.println("Optimizing Nots");

            newChild = new Node(curr.getData());
            curr.data = "NOT";

            curr.children = new ArrayList<Node>();
            curr.children.add(newChild);

            newChild.children = gChildrenNots;
        }

        //if at least 2 are not

        else if(countNot >= 2){

            newChild = new Node("NOT");
            duplicateChild = new Node(curr.getData());

            duplicateChild.children = gChildrenNots;

            ArrayList<Node> c = new ArrayList<Node>();
            c.add(duplicateChild);

            newChild.children = c;

            //remove all not nodes from current children

            ArrayList<Node> currNewChildren = new ArrayList<Node>();
            for(Node child : curr.getChildren()){
                if (child.getData() != "NOT"){
                   currNewChildren.add(child);
                }
            }

            currNewChildren.add(newChild);

            //add new child node
            curr.children = currNewChildren;
        }


        for(Node child : curr.getChildren()){
            optimizeNot(child);
        }
    }

    //get height of boolean tree
    public int height(Node n){
        if(n.getChildren().size() == 0){
            return 1;
        }else if(n.getChildren().size() == 1){
            return 1 + height(n.getChildren().get(0));
        }else{
            Node left = n.getChildren().get(0);
            Node right = n.getChildren().get(1);

            return 1 + Math.max(height(left),height(right));
        }
    }
}
