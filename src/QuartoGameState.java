import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;


public class QuartoGameState implements Iterable<QuartoGameTransition> {

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

    //
    // Methods
    //

    /**
     *
     * @param board
     * @param freeSquares
     * @param freePieces
     * @param alpha
     * @param beta
     * @param isMaxState
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

    /**
     *
     * @param nextSquare
     * @param nextPiece
     * @return
     */
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
            if (square[0] != nextSquare[0] || square[1] != nextSquare[1]) {
                freeSquares.add(square.clone());
            }
        }

        QuartoBoard newBoard = new QuartoBoard(this.board);
        newBoard.insertPieceOnBoard(nextSquare[0], nextSquare[1], nextPiece.getPieceID());

        return new QuartoGameState(newBoard, freeSquares, freePieces,
                                   this.alpha, this.beta, !this.isMaxState);
    }

    /**
     *
     */
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
    public Iterator<QuartoGameTransition> iterator()
    {
        return new Iterator<QuartoGameTransition>() {
            private QuartoGameState curState = QuartoGameState.this;
            private Iterator<QuartoGameTransition> transitions = curState.transitions.values().iterator();
            private Iterator<QuartoPiece> pieces = curState.freePieces.iterator();
            private Iterator<int[]> squares = curState.freeSquares.iterator();
            private QuartoPiece nextPiece = null;
            private int[] nextSquare = null;

            @Override
            public QuartoGameTransition next() {
                if(transitions.hasNext()) {
                    return transitions.next();
                }

                if(nextSquare == null || !squares.hasNext()) {
                    squares = curState.freeSquares.iterator();
                    nextPiece = pieces.next();
                }

                nextSquare = squares.next();
                QuartoGameState newState = curState.nextState(nextSquare, nextPiece);

                String newStateHash = newState.getHash();
                if(registeredStates.get(newStateHash) == null) {
                    registeredStates.put(newStateHash, newState);
                }

                QuartoGameTransition newTransition = new QuartoGameTransition(newState, nextPiece, nextSquare);

                curState.transitions.put(newTransition.getHashCode(), newTransition);
                return newTransition;
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
