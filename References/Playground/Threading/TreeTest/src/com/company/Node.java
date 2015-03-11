package com.company;

import java.util.*;

/*
 * The node class contains:
 - A Node's reference to its children (if any),
 - A string description of the last move made to get to this node
 - Probability of getting to this node from the previous node
 - A reference to the node's parent
 - A string storing a name for the node
 - A reference to the best move found so far at this node
 * For most practical uses of this class, it would be necessary to add
 more information about the _state_ represented by the node.
 */

class Node {
    //the children of the node
    protected List<Node> children;

    //the move made to get to this node
    protected String moveMade;

    //probability of getting to this node from the previous node
    protected double probability;

    //references the parent node
    protected Node parent;

    //name of the node
    protected String name;

    //The best move is a pointer to one of the child Nodes in the children list
    protected Node bestMove;

    //constructor method
    public Node(String name, String moveMade) {
        this.name = name;
        this.children = new ArrayList<Node>();
        this.moveMade = moveMade;
        this.probability = 1.0;
    }

    //add a new child to the children list.  This also sets the child node's parent node
    public void addChild(Node child) {
        children.add(child);
        child.setParentNode(this);
    }

    //returns the list of children for this node
    public List<Node> getChildren() {
        return children;
    }

    //returns the probability of getting to this node from the previous node
    public double getProbablity() {
        return probability;
    }

    //changes the node's parent node
    public void setParentNode(Node newParentNode) {
        this.parent = newParentNode;
    }

    //gets the node's parent node
    public Node getParentNode() {
        return this.parent;
    }

    //sets the node's best move, which is a pointer to one of its children
    public void setBestMove(Node bestMove) {
        this.bestMove = bestMove;
    }

    //gets a formatted string that explains what the best move is at the current node
    public String getBestMoveString() {
        if(bestMove == null) {
            return "error:  bestMove node is not defined";
        }
        return "At node '" + name + "', the best move is '" + bestMove.moveMade + "', leading to node '" + bestMove.name + "'";
    }
}

/*
 * The MinNode class is a subclass of the node class
 * that requires no modification of functionality
 */
class MinNode extends Node {
    //constructor method
    public MinNode(String name, String moveMade) {
        super(name, moveMade);
    }

    //gets a formatted string that explains what the best move is at the current node
    //overrides Node's getBestMoveString() function to give a more specific output
    public String getBestMoveString() {
        if(bestMove == null) {
            return "error:  bestMove node is not defined";
        }
        return "At node '" + name + "', Min's best move is '" + bestMove.moveMade + "', leading to node '" + bestMove.name + "'";
    }

}

/*
 * The MaxNode class is a subclass of the node class
 * that requires no modification of functionality.
 */
class MaxNode extends Node {
    //constructor method
    public MaxNode(String name, String moveMade) {
        super(name, moveMade);
    }

    //gets a formatted string that explains what the best move is at the current node
    //overrides Node's getBestMoveString() function to give a more specific output
    public String getBestMoveString() {
        if(bestMove == null) {
            return "error:  bestMove node is not defined";
        }
        return "At node '" + name + "', Max's best move is '" + bestMove.moveMade + "', leading to node '" + bestMove.name +"'";
    }
}

/*
 * The ChanceNode class is a subclass of the node class.
 * When a child is added to a ChanceNode, the child's probability is modified.
 */
class ChanceNode extends Node {
    //constructor method
    public ChanceNode(String name, String moveMade) {
        super(name, moveMade);
    }

    //when a child is added to a ChanceNode, the child's probability is modified.  This also sets the child node's parent node
    public void addChild(Node child, double probability) {
        this.children.add(child);
        child.probability = probability;
        child.setParentNode(this);
    }
}

/*
 * The TerminatingNode class is a subclass of the node class.
 * A TerminatingNode has a heuristic value assigned to it.
 */
class TerminatingNode extends Node {
    private int value;

    //constructor method
    public TerminatingNode(String name, String moveMade, int value) {
        //		super("Terminating Node with value " + value, moveMade);
        super(name, moveMade);
        this.value = value;

    }

    //terminating nodes should not have children
    public void addChild(Node child) {
        System.out.println("\nTerminating Nodes cannot have children");
        System.exit(-1);
    }

    //return the heuristic value
    public int getValue() {
        return this.value;
    }
}
