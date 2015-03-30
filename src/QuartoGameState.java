import java.util.*;


public class QuartoGameState {

    private static HashMap<String, QuartoGameState> registeredStates = new HashMap<String, QuartoGameState>();
    private static boolean registeredStatesBusy = false;

    public QuartoBoard board;
    public ArrayList<int[]> freeSquares;
    public ArrayList<QuartoPiece> freePieces;
    public HashMap<String, QuartoGameTransition> transitions;

    public int value;
    public int alpha;
    public int beta;
    public boolean isMaxState;
    public QuartoGameTransition bestTransition;
    public int lastLevelExaminedFrom;

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
    private QuartoGameState(QuartoBoard board, int alpha, int beta, boolean isMaxState) {

        this.board = board;

        this.lastLevelExaminedFrom = 0;

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
    }

    /**
     * registeredStates thread safe accessor methods
     */

    /*
     *  Dump no longer referenced states by clearing
     *  currently register states for garbage collection
     */
    public static void clearStates() {
        while(registeredStatesBusy);
        registeredStatesBusy = true;
        registeredStates.clear();
        registeredStatesBusy = false;
    }

    public static void pruneStates(QuartoPiece placedPiece, int[] placedPieceLocation, int numberOfFreePieces) {
        while(registeredStatesBusy){}
        registeredStatesBusy = true;
        Iterator<Map.Entry<String, QuartoGameState>> stateMapIterator = registeredStates.entrySet().iterator();

        while(stateMapIterator.hasNext()) {
            Map.Entry<String, QuartoGameState> stateMap = stateMapIterator.next();
            String key = stateMap.getKey();
            QuartoGameState state = stateMap.getValue();

            // Are there enough pieces on the board for us to be able to reach this state?
            boolean enoughPiecesPlayed = state.freePieces.size() <= numberOfFreePieces;

            QuartoPiece pieceInSquare = state.board.getPieceOnPosition(
                    placedPieceLocation[0],
                    placedPieceLocation[1]
            );

            // Is the piece we placed in the right square
            boolean isPieceInSquare = pieceInSquare != null && pieceInSquare.getPieceID() == placedPiece.getPieceID();

            /**
             * A state is unreachable from our current root if:
             *      It has fewer pieces on the board == has more free pieces
             *      It does not have the placedPiece in the placedPieceLocation
             */
            if(!enoughPiecesPlayed || !isPieceInSquare) {
                stateMapIterator.remove();
            }
        }


        registeredStatesBusy = false;
    }

    public static Set<Map.Entry<String, QuartoGameState>> getIterator() {
        while(registeredStatesBusy){}
        registeredStatesBusy = true;
        Set<Map.Entry<String, QuartoGameState>> retIter = registeredStates.entrySet();
        registeredStatesBusy = false;

        return retIter;
    }

    public static void registerState(String key, QuartoGameState state) {
        while(registeredStatesBusy);
        registeredStatesBusy = true;
        registeredStates.put(key, state);
        registeredStatesBusy = false;
    }

    public static QuartoGameState getRegisteredState(String key) {
        return getRegisteredStateHelper(key);
    }

    public static QuartoGameState getRegisteredState(QuartoBoard board, int alpha, int beta, boolean isMaxState) {
        // Create a state in order to get the appropriate hash code
        QuartoGameState state = new QuartoGameState(board, alpha, beta, isMaxState);
        String key = state.getHash();

        // Attempt to get an already existing singleton of this state
        QuartoGameState tmpState = getRegisteredStateHelper(key);
        if(tmpState != null) {
            // If one does exist, use it
            state = tmpState;
        }
        else {
            // If one doesn't exist, register the one we created
            registerState(key, state);
        }

        return state;
    }

    private static QuartoGameState getRegisteredStateHelper(String key) {
        while(registeredStatesBusy);
        registeredStatesBusy = true;
        QuartoGameState state = registeredStates.get(key);
        registeredStatesBusy = false;

        return state;
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

        return (permsOfMoves == 0) ? 1 : numOfDesc / permsOfMoves;
    }

    /**
     * @param limboPiece Piece to be placed at current state
     */
    public void evaluate(QuartoPiece limboPiece) {
        if(this.hasQuarto()) {
            value = isMaxState ? -27 : 27;
        }
        else {
            value = 0;
            // FIXME/OPTIMIZE: this is just hacky and awful
            for(int[] square : this.freeSquares) {
                QuartoBoard tmpBoard = new QuartoBoard(this.board);
                tmpBoard.insertPieceOnBoard(square[0], square[1], limboPiece.getPieceID());

                if(this.hasQuarto(tmpBoard)) {
                    value = this.isMaxState ? 27 : -27;
                    break;
                }
            }
        }
    }

    /**
     * @return       boolean
     */
    // TODO: Get rid of this method
    public boolean hasQuarto(QuartoBoard tmpBoard)
    {
        //loop through rows
        for(int i = 0; i < tmpBoard.getNumberOfRows(); i++) {
            if (tmpBoard.checkRow(i)) {
                return true;
            }
        }

        //loop through columns
        for(int i = 0; i < tmpBoard.getNumberOfColumns(); i++) {
            if (tmpBoard.checkColumn(i)) {
                return true;
            }
        }

        //check Diagonals
        if (tmpBoard.checkDiagonals()) {
            return true;
        }

        return false;
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

        value = isMaxState ? alpha : beta;
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
