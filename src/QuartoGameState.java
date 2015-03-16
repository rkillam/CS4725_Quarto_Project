import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


public class QuartoGameState {

    private static HashMap<String, QuartoGameState> registeredStates = new HashMap<String, QuartoGameState>();

    public QuartoBoard board;
    public ArrayList<int[]> freeSquares;
    public ArrayList<QuartoPiece> freePieces;
    public HashMap<String, QuartoGameTransition> transitions;

    public int value;
    public int alpha;
    public int beta;
    public boolean isMaxState;
    public QuartoGameTransition bestTransition;
    public int lastLevelExaminedFrom = 0;

    //
    // Methods
    //

    /**
     *
     * @param board
     * @param alpha
     * @param beta
     * @param isMaxState
     */
    public QuartoGameState(QuartoBoard board, int alpha, int beta, boolean isMaxState) {

        this.board = board;

        this.freeSquares = new ArrayList<int[]>();
        for (int i = 0; i < this.board.board.length; i++) {
            for (int j = 0; j < this.board.board[i].length; j++) {
                if (!this.board.isSpaceTaken(i, j)) {
                    int[] temp = {i, j};
                    this.freeSquares.add(temp);
                }
            }
        }

        this.freePieces = new ArrayList<QuartoPiece>();
        for (QuartoPiece piece : this.board.pieces) {
            if (!piece.isInPlay()) {
                this.freePieces.add(piece);
            }
        }


        this.alpha = alpha;
        this.beta = beta;
        this.isMaxState = isMaxState;

        this.value = this.isMaxState ? Integer.MIN_VALUE : Integer.MAX_VALUE;

        this.transitions = new HashMap<String, QuartoGameTransition>();

        registeredStates.put(this.getHash(), this);
    }
        
    /**
     * Returns the number of UNIQUE descendants from this state after the
     * given number of generations
     *
     * numOfDesc calculates the number of descendants regardless of path:
     *          (#FreePieces! * #FreeSquares) / [(#FreePieces - gens)! *(#FreeSquares - gens)!]
     *
     * permsOfMoves is what ensures that the given number of descendants is unique. After a 2
     * generations we can have 2 states that each have the same pieces in the same square, that
     * is because one state will have put piece x in its square first, while the other state will
     * have placed piece y first.
     *
     * If we extend this to n moves, there are n! orders for those pieces to have been placed in
     * those squares, so we need to divide by n!.
     *
     * @param generations number of generations to calculate for
     * @return number of UNIQUE descendants after the given number of generations
    */
    public int calcNodesInGeneration(int generations) {
        int numOfDesc = 1;
        int permsOfMoves = 1;

        int j = 0;
        for(int i = this.freePieces.size(); i > this.freePieces.size() - generations; --i) {
            numOfDesc *= i;
            permsOfMoves *= ++j;
        }

        for(int i = this.freeSquares.size(); i > this.freeSquares.size() - generations; --i) {
            numOfDesc *= i;
        }

        return numOfDesc / permsOfMoves;
    }

    /**
     * @param level The root curState the lead to this nodes evaluation
     */
    public void evaluate(int level)
    {
        if(this.hasQuarto()) {
            value = isMaxState ? 27 : -27;
        } else {
            value = 0;
        }

        this.lastLevelExaminedFrom = level;
    }

    /**
     * @return       boolean
     */
    public boolean hasQuarto()
    {
        //loop through rows
        for(int i = 0; i < this.board.getNumberOfRows(); i++) {
            if (this.board.checkRow(i)) {
                return true;
            }
        }

        //loop through columns
        for(int i = 0; i < this.board.getNumberOfColumns(); i++) {
            //gameIsWon = this.quartoBoard.checkColumn(i);
            if (this.board.checkColumn(i)) {
                return true;
            }
        }

        //check Diagonals
        if (this.board.checkDiagonals()) {
            return true;
        }

        return false;
    }

    /**
     *  Resets minimax values
     */
    public void resetMinimax() {
        alpha = Integer.MIN_VALUE;
        beta = Integer.MAX_VALUE;
    }

    /*
     *  Dump no longer referenced states by clearing
     *  currently register states for garbage collection
     */
    public void clearStates() {
        registeredStates.clear();
    }

    /**
     * Generates a hash based on current board layout.
     *
     * @return String representation of the game board.
     */
    public String getHash() {
        String hash = "";
        QuartoPiece p;
        for(int i=0; i<this.board.board.length;i++) {
            for(int j=0; j<this.board.board[0].length;j++) {
                p = this.board.getPieceOnPosition(i,j);
                hash += p == null ? "_" : p.binaryStringRepresentation();
            }
        }
        return hash;
    }
}
