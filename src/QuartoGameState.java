import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class QuartoGameState implements Iterable<QuartoGameState> {

    /*
     * TODO: We need to come up with a way to prune unreachable
     *       game states from registeredStates, otherwise we
     *       will likely run into a big memory usage issue.
     */
    private static HashMap<String, QuartoGameState> registeredStates = new HashMap<String, QuartoGameState>();

    public QuartoBoard board;
    public int[][] takenSquares;
    public ArrayList<int[]> freeSquares;
    public QuartoPiece[] takenPieces;
    public ArrayList<QuartoPiece> freePieces;
    public HashMap<String, QuartoGameTransition> transitions;
    public boolean estimated;
    public final static int NUM_PIECES = 32;
    public final static int ROW_TIMES_COL = 25;
    /**
     * Consider putting max and mini vals into some kind of struct
     */
    public int maxVal = Integer.MAX_VALUE;
    /**
     * Consider putting max and mini vals into some kind of struct
     */
    public int maxAlpha;
    /**
     * Consider putting max and mini vals into some kind of struct
     */
    public int maxBeta;
    /**
     * Consider putting max and mini vals into some kind of struct
     */
    public QuartoGameTransition maxTransition;
    /**
     * Consider putting max and mini vals into some kind of struct
     */
    public int miniVal = Integer.MIN_VALUE;
    /**
     * Consider putting max and mini vals into some kind of struct
     */
    public int miniAlpha;
    /**
     * Consider putting max and mini vals into some kind of struct
     */
    public int miniBeta;
    /**
     * Consider putting max and mini vals into some kind of struct
     */
    public QuartoGameTransition miniTransition;

    //
    // Methods
    //

    /**
     * @param        board
     * @param        takenSquares
     * @param        freeSquares
     * @param        takenPieces
     * @param        freePieces
     * @param        maxVal
     * @param        maxAlpha
     * @param        maxBeta
     * @param        miniVal
     * @param        miniAlpha
     * @param        miniBeta
     */
    public QuartoGameState(QuartoBoard board, int[][] takenSquares,
                                ArrayList<int[]> freeSquares, QuartoPiece[] takenPieces,
                                ArrayList<QuartoPiece> freePieces, int maxVal, int maxAlpha,
                                int maxBeta, int miniVal, int miniAlpha, int miniBeta) {

        this.board = board;

        this.takenSquares = takenSquares;
        this.freeSquares = freeSquares;

        this.takenPieces = takenPieces;
        this.freePieces = freePieces;

        this.maxVal = maxVal;
        this.maxAlpha = maxAlpha;
        this.maxBeta = maxBeta;

        this.miniVal = miniVal;
        this.miniAlpha = miniAlpha;
        this.miniBeta = miniBeta;

        this.transitions = new HashMap<String, QuartoGameTransition>();
        this.estimated = true;
    }

    public QuartoGameState(QuartoBoard board) {
        this.board = board;

        this.freeSquares = new ArrayList<int[]>();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int[] temp = {i,j};
                freeSquares.add(temp);
            }
        }

        this.freePieces = new ArrayList<QuartoPiece>(NUM_PIECES);
        for (int i = 0; i<NUM_PIECES; i++) {
            freePieces.add(new QuartoPiece(i));
        }

        this.transitions = new HashMap<String, QuartoGameTransition>();
        this.estimated = true;
    }

    public QuartoGameState(QuartoBoard board, ArrayList<int[]> freeSquares, ArrayList<QuartoPiece> freePieces) {

        this.board = board;

        this.freeSquares = freeSquares;

        this.freePieces = freePieces;

        this.transitions = new HashMap<String, QuartoGameTransition>();
        this.estimated = true;
    }

    public QuartoGameState nextState(int[] nextSquare, QuartoPiece nextPiece) {
        ArrayList<QuartoPiece> freePieces = new ArrayList<QuartoPiece>(32);

        //deep copy of all freePieces
        for (int i = 0; i < NUM_PIECES; i++) {
            QuartoPiece tempPiece = this.freePieces.get(i);
            if (tempPiece != null) {
                freePieces.add(new QuartoPiece(tempPiece));
            }
        }
        freePieces.remove(nextPiece.getPieceID());


        //deep copy of all squares minus nextSquare

        ArrayList<int[]> freeSquares = new ArrayList<int[]>(25);
        for (int i = 0; i < ROW_TIMES_COL; i++) {
            int[] temp = this.freeSquares.get(i);
            if (temp != null) {
                if (temp[0] != nextSquare[0] && temp[1] != nextSquare[1]) {
                    freeSquares.add(i, temp.clone());
                }
            }
        }

        QuartoGameState newState = new QuartoGameState(new QuartoBoard(this.board), freeSquares, freePieces);
        return newState;
    }


    /**
     * Evaluates the current game state, evaluation is broken into 3 cases:
     *     1) This is a win state set:
     *         maxVal = inf and miniVal = -inf
     *     2) This state has no children which means we're at the bottom of the search
     * tree:
     *         Use heuristic to evaluate the state
     *         Set this.estimated = true to indicate that we haven't reached a win
     * state
     *     3) This state has children:
     *         Set minimax values according to children
     *         Set maxTransition and miniTransition
     */
    public void evaluate()
    {
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
     * @return       Iterator<QuartoGameState>
     */
    public Iterator<QuartoGameState> iterator()
    {
        return new Iterator<QuartoGameState>() {
            private QuartoGameState curState = QuartoGameState.this;
            private Iterator<QuartoGameTransition> transitions = curState.transitions.values().iterator();
            private Iterator<QuartoPiece> pieces = curState.freePieces.iterator();
            private Iterator<int[]> squares = curState.freeSquares.iterator();
            private QuartoPiece nextPiece = null;
            private int[] nextSquare = null;

            @Override
            public QuartoGameState next() {
                if(transitions.hasNext()) {
                    return transitions.next().toState;
                }

                if(nextSquare == null || !squares.hasNext()) {
                    squares = curState.freeSquares.iterator();
                    nextPiece = pieces.next();
                }

                nextSquare = squares.next();
                QuartoGameState newState = curState.nextState(nextSquare, nextPiece);
                QuartoGameState registeredState = registeredStates.get(newState.getHash());
                if(registeredState == null) {
                        registeredStates.put(newState.getHash(), newState);
                }
                registeredStates.put(newState.getHash(), newState);
                QuartoGameTransition newTransition = new QuartoGameTransition();
                curState.transitions.put(newTransition.getHashCode(), newTransition);
                return registeredStates.get(newState.getHash());
            }

            @Override
            public boolean hasNext() {
                return this.transitions.hasNext() ||
                       this.pieces.hasNext()      ||
                       this.squares.hasNext();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public void resetMinimax() {

    }

    public String getHash() {
        return "";
    }
}
