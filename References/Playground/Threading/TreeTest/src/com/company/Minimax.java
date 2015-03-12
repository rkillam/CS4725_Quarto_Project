package com.company;

import java.math.BigInteger;
import java.util.*;


class ThreadTest implements Runnable {
    private static BigInteger index = new BigInteger("0");

    private String threadName;
    private boolean cont = true;

    private Node node;

    public ThreadTest(Node node) {
        this.node = node;
        this.threadName = "Thread #" + index;
        index = index.add(new BigInteger("1"));

        System.out.println("Creating " + this.threadName);

        Thread t = new Thread(this, this.threadName);
        t.start();
    }

    public void run() {
        System.out.println("running: " + this.threadName);
    }

    public void stop() {
        this.cont = false;
    }
}

/*
 * The Minimax class contains 3 static functions:
 - The main() method
 - The expectiminimax function
 - A function to build all the nodes and link them together properly.
 **/

public class Minimax {

    /*
     * The main method()
     */
    static boolean displayFlag = true;

    public static void main(String[] args) {
        //root is the root node of the minimax game tree
        Node root = buildTree();

        if(args.length != 0 && args[0].equals("nodisplay")) {
            displayFlag = false;
        }

        //the value returned by the expectiminimax function is the expected value of the root node

        new ThreadTest(root);
        System.out.println(root.bestMove);
    }

    /*
     * The Expectiminimax algorithm used for computing the expected value
     * @param node a node from the minimax game tree
     * @return the expected value for this game.
     */
    public static double expectiminimax(Node node) {

        double val = 0;
        List<Node> children = node.getChildren();

        //the current node is a TerminatingNode
        if (node instanceof TerminatingNode) {
            //return the heuristic value assigned to the Terminating node
            return ((TerminatingNode)node).getValue();
        }

        //the current node is a MinNode
        else if(node instanceof MinNode) {

            val = Double.POSITIVE_INFINITY;

            //for each Child
            for(Iterator<Node> i = children.iterator(); i.hasNext(); ) {
                Node child = i.next();

                // Write code here to update the value of val appropriately and
                // to set the bestMove of node to be the appropriate child node.
                double tmpVal = expectiminimax(child);
                if(tmpVal < val) {
                    val = tmpVal;
                    node.setBestMove(child);
                }
            }
        }

        //the current node is a MaxNode
        else if(node instanceof MaxNode) {

            val = Double.NEGATIVE_INFINITY;

            //for each Child
            for(Iterator<Node> i = children.iterator(); i.hasNext(); ) {
                Node child = i.next();

                // Write code here to update the value of val appropriately and
                // to set the bestMove of node to be the appropriate child node.
                double tmpVal = expectiminimax(child);
                if(tmpVal > val) {
                    val = tmpVal;
                    node.setBestMove(child);
                }
            }
        }

        //the current node is a ChanceNode
        else if(node instanceof ChanceNode) {

            val = 0;

            //for each Child
            for(Iterator<Node> i = children.iterator(); i.hasNext(); ) {
                Node child = i.next();

                // Write code here to update the value of val appropriately.
                val += expectiminimax(child) * child.getProbablity();
            }
        }

        else {
            System.out.println("\nInput is corrupt");
            System.exit(-1);
        }

        //display the best move
        if((node instanceof MaxNode || node instanceof MinNode) && displayFlag == true) {
            System.out.println(node.getBestMoveString());
        }

        if (displayFlag == true) {
            System.out.print("Value of node " + node.name + " is ");
            System.out.printf("%.2f\n", val);
        }

        return val;
    }

    /*
     * This function is used for building the game tree
     * @return root node
     */
    public static Node buildTree() {

        //start with root node
        MaxNode A = new MaxNode("A", "Start");

        //creating the other nodes of the tree:
        //ChanceNodes
        ChanceNode B = new ChanceNode("B", "a1");
        ChanceNode C = new ChanceNode("C", "a2");

        //MinNodes
        MinNode D = new MinNode("D", "b1");
        MinNode E = new MinNode("E", "b2");
        MinNode F = new MinNode("F", "c1");
        MinNode G = new MinNode("G", "c2");

        //TerminatingNodes
        TerminatingNode D1 = new TerminatingNode("D1", "d1", 2);
        TerminatingNode D2 = new TerminatingNode("D2", "d2", 1);

        TerminatingNode E1 = new TerminatingNode("E1", "e1", 3);
        TerminatingNode E2 = new TerminatingNode("E2", "e2", -2);

        TerminatingNode F1 = new TerminatingNode("F1", "f1", 10);
        TerminatingNode F2 = new TerminatingNode("F2", "f2", 12);

        TerminatingNode G1 = new TerminatingNode("G1", "g1", -1);
        TerminatingNode G2 = new TerminatingNode("G2", "g2", 0);

        //linking the nodes
        A.addChild(B);
        A.addChild(C);

        B.addChild(D, 0.6);
        B.addChild(E, 0.4);

        C.addChild(F, 0.1);
        C.addChild(G, 0.9);

        D.addChild(D1);
        D.addChild(D2);

        E.addChild(E1);
        E.addChild(E2);

        F.addChild(F1);
        F.addChild(F2);

        G.addChild(G1);
        G.addChild(G2);

        return A;
    }
}
