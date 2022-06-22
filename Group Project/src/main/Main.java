package main;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.comments.LineComment;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.EnclosedExpr;
import com.github.javaparser.printer.DefaultPrettyPrinter;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.resolution.types.ResolvedType;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;

import java.io.FileInputStream;

public class Main {

    // Read the file with the SAME path of the main class.
    // Please put the all the test cases in the same path of the main class in your project too.

    // Test the expression of which the root of the AST is a binary expression.
    // private static final String FILE_PATH = "src/main/Example1.java";

    // Test the expression of which the root of the AST is a unary expression.
    private static final String FILE_PATH = "src/main/Example2.java";


    public static void main(String[] args) throws Exception {
        // Set up a minimal type solver that only looks at the classes used to run this sample.
        CombinedTypeSolver combinedTypeSolver = new CombinedTypeSolver();
        combinedTypeSolver.add(new ReflectionTypeSolver());

        // Configure JavaParser to use type resolution
        JavaSymbolSolver symbolSolver = new JavaSymbolSolver(combinedTypeSolver);
        StaticJavaParser.getConfiguration().setSymbolResolver(symbolSolver);
        CompilationUnit cu = StaticJavaParser.parse(new FileInputStream(FILE_PATH));

        // Find the first recognized expression, which is the boolean propositional logic formula.
        Expression root = cu.findAll(Expression.class).get(0);

        System.out.println(root.toString());
        Tree t = new Tree();
        Node r = new Node("");

        //remove any additional brackets surrounding the formula
        if (root instanceof EnclosedExpr){
            root = ((EnclosedExpr) root).getInner();
        }

        // Initialize the tree with a root node. Root is the first node and so it must be done in the main function
        // and not the buildTree() function.
        if (root instanceof BinaryExpr) {

            String op = ((BinaryExpr) root).getOperator().toString();

            r = new Node(op);
            Expression left = ((BinaryExpr) root).getLeft();
            Expression right = ((BinaryExpr) root).getRight();

            //run build tree method on both right and left

            buildTree(r,left);
            buildTree(r,right);
        }else if (root instanceof UnaryExpr) {

            String op = ((UnaryExpr) root).getOperator().toString();

            if (op == "LOGICAL_COMPLEMENT") {
                System.out.println("Op is !");
                r = new Node("NOT");

                Expression child = ((UnaryExpr) root).getExpression();

                buildTree(r, child);

            }
        }else{
            r = new Node(root.toString());
        }

        //Find height of initial binary tree

        int height = t.height(r);

        //Optimize the tree

        for (int i = 0; i < height; i++) {
            t.optimizeTree(r);
        }

        t.optimizeNot(r);

        //print the tree to the console
        t.showTree(r,0,false);
    }

    //function that recursively builds a binary tree from the expression
    public static void buildTree(Node root, Expression e){

        //handle any bracketed expression and remove brackets
        if (e instanceof EnclosedExpr){
            e = ((EnclosedExpr) e).getInner();
        }

        //initialize new node that will be added to the tree
        Node n;

        //Check if the expression is a binary expr
        if (e instanceof BinaryExpr){
            n = new Node(((BinaryExpr) e).getOperator().toString());

            Expression left = ((BinaryExpr) e).getLeft();
            Expression right = ((BinaryExpr) e).getRight();

            //add node n to the tree
            root.children.add(n);

            //recurse on left and right sub-expressions
            buildTree(n,left);
            buildTree(n,right);
        }
        //If it isnt a binary expression, check if it is a unary expression
        else if (e instanceof UnaryExpr){

            String op = ((UnaryExpr) e).getOperator().toString();

            //rename LOGICAL_COMPLEMENT to NOT for simplicity
            if (op == "LOGICAL_COMPLEMENT"){

                n = new Node("NOT");
                root.children.add(n);

                //get child expression
                Expression child = ((UnaryExpr) e).getExpression();

                buildTree(n,child);
            }

        }

        //If the expression is neither a unary or binary it must only contain the variable
        else{
            n = new Node(e.toString());
            root.children.add(n);
        }


    }
}
