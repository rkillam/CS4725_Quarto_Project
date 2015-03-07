import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


public class QuartoGameState implements Iterable<QuartoGameState> {

    private static HashMap<String, QuartoGameState> registeredStates = new HashMap<String, QuartoGameState>();

    public QuartoBoard board;
    public ArrayList<int[]> freeSquares;
    public ArrayList<QuartoPiece> freePieces;
    public HashMap<String, QuartoGameTransition> transitions;

    public int[] square;
    public QuartoPiece piece;

    public int value;
    public int alpha;
    public int beta;
    public boolean isMaxState;
    public QuartoGameTransition bestTransition;

    //
    // Methods
    //

    /**
     * @param        board
     * @param        freeSquares
     * @param        freePieces
     * @param        alpha
     * @param        beta
     * @param        isMaxState
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

        this.value = this.isMaxState ? Integer.MIN_VALUE : Integer.MAX_VALUE;

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

        QuartoBoard newBoard = new QuartoBoard(this.board);
        newBoard.insertPieceOnBoard(nextSquare[0], nextSquare[1], nextPiece.getPieceID());

        return new QuartoGameState(newBoard, freeSquares, freePieces,
                                   this.alpha, this.beta, !this.isMaxState);
    }

    public void evaluate()
    {
        if(this.hasQuarto()) {
            value = isMaxState ? 27 : -27;
        } else {
            value = 0;
        }
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

                newState.setTransitionInfo(nextSquare, nextPiece);

                QuartoGameTransition newTransition = new QuartoGameTransition(newState, nextPiece, nextSquare);
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

    /**
     *  Resets minimax values based off new values.
     */
    public void resetMinimax() {
        alpha = 0;
        beta = 0;
    }

    public void setTransitionInfo(int[] square, QuartoPiece piece) {
        this.square = square;
        this.piece = piece;
    }

    /*
     *  Dump no longer referenced states by clearing
     *  currently register states for garbage collection
     */
    public void clearStates() {
        registeredStates.clear();
    }

    public String getHash() {
        return "";
    }
}
