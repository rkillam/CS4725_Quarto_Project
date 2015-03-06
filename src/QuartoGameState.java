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
    public ArrayList<int[]> freeSquares;
    public ArrayList<QuartoPiece> freePieces;
    public HashMap<String, QuartoGameTransition> transitions;
    /**
     * Consider putting max and mini vals into some kind of struct
     */
    public Integer value;
    public int alpha;
    public int beta;
    public boolean isMaxState;
    public QuartoGameTransition bestMove;

    //
    // Methods
    //

    /**
     * @param        board
     * @param        freeSquares
     * @param        freePieces
     * @param        alpha
     * @param        beta
     */
    public QuartoGameState(QuartoBoard board, ArrayList<int[]> freeSquares,
                                ArrayList<QuartoPiece> freePieces, int alpha, int beta,
                                boolean isMaxState) {

        this.board = board;

        this.freeSquares = freeSquares;

        this.freePieces = freePieces;

        this.alpha = alpha;
        this.beta = beta;

        this.isMaxState = isMaxState;

        this.transitions = new HashMap<String, QuartoGameTransition>();
    }

    public QuartoGameState nextState(int[] nextSquare, QuartoPiece nextPiece) {
        ArrayList<QuartoPiece> freePieces = new ArrayList<QuartoPiece>();

        //deep copy of all freePieces

        for (QuartoPiece piece: this.freePieces) {
            if (piece.getPieceID() != nextPiece.getPieceID()) {
                freePieces.add(piece);
            }
        }

        //deep copy of all squares minus nextSquare

        ArrayList<int[]> freeSquares = new ArrayList<int[]>();

        for (int[] square: this.freeSquares) {
            if (square[0] != nextSquare[0] && square[1] != nextSquare[1]) {
                freeSquares.add(square.clone());
            }
        }

        return new QuartoGameState(new QuartoBoard(this.board), freeSquares, freePieces, this.alpha,
                this.beta, !this.isMaxState);
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
