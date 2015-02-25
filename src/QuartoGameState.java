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
    public QuartoPiece[] takenPiece;
    public ArrayList<QuartoPiece> freePieces;
    public HashMap<String, QuartoGameTransition> transitions;
    public boolean estimated;
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
    public void QuartoGameState(QuartoBoard board, int[][] takenSquares,
                                ArrayList<int[]> freeSquares, QuartoPiece[] takenPieces,
                                ArrayList<QuartoPiece> freePieces, int maxVal, int maxAlpha,
                                int maxBeta, int miniVal, int miniAlpha, int miniBeta) {

        this.board = board;

        this.takenSquares = takenSquares;
        this.freeSquares = freeSquares;

        this.takenPiece = takenPieces;
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
        return new Iterator<QuartoGameState>(this) {
            private QuartoGameState curState;
            private Iterator<QuartoGameTransition> transitions;
            private Iterator<QuartoPiece> pieces;
            private QuartoPiece nextPiece;
            private Iterator<int[]> squares;
            private int[] nextSquare;

            public Iterator(QuartoGameState curState) {
                this.curState = curState;
                this.transitions = this.curState.transitions.values().iterator();
                this.pieces = this.curState.freePieces.iterator();
                this.squares = this.curState.freeSquares.iterator();
                this.nextSquare = null;
                this.nextPiece = null;
            }

            @Override
            public QuartoGameState next() {
                if(this.transitions.hasNext()) {
                    return this.transitions.next().toState;
                }

                if(this.nextSquare == null || !this.squares.hasNext()) {
                    this.squares = this.curState.freeSquares.iterator();
                    this.nextPiece = this.pieces.next();
                }

                this.nextSquare = this.squares.next();
                QuartoGameState newState = new QuartoGameState();
                QuartoGameState registeredState = registeredStates.get(newState.getHash());
                if(registeredState == null) {
                        registeredStates.put(newState.getHash(), newState);
                }
                registeredStates.put(newState.getHash(), newState);
                QuartoGameTransition newTransition = new QuartoGameTransition();
                this.curState.transitions.put(newTransition.getHashCode(), newTransition);
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
