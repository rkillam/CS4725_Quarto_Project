import java.util.ArrayList;

public class QuartoPlayerAgent extends QuartoAgent {
    private QuartoGameState curState;
    private int maxDepth;
    public final static int NUM_PIECES = 32;
    public final static int ROW_LENGTH = 5;
    public final static int COL_LENGTH = 5;

    //Example AI
    public QuartoPlayerAgent(GameClient gameClient, String stateFileName) {
        // because super calls one of the super class constructors(you can overload constructors), you need to pass the parameters required.
        super(gameClient, stateFileName);
        setPlayerNumber();

        ArrayList<int[]> freeSquares = new ArrayList<int[]>();
        for (int i = 0; i < ROW_LENGTH; i++) {
            for (int j = 0; j < COL_LENGTH; j++) {
                int[] temp = {i,j};
                freeSquares.add(temp);
            }
        }

        ArrayList<QuartoPiece> freePieces = new ArrayList<QuartoPiece>(NUM_PIECES);
        for (int i = 0; i<NUM_PIECES; i++) {
            freePieces.add(new QuartoPiece(i));
        }

        // if we are player number 1, our first node is max node
        boolean isMax = (playerNumber == 1);
        this.curState = new QuartoGameState(new QuartoBoard(this.quartoBoard),
                                            freeSquares, freePieces, Integer.MIN_VALUE,
                                            Integer.MAX_VALUE, isMax);
        this.maxDepth = 5;
    }

    //MAIN METHOD
    public static void main(String[] args) {
        //start the server
        GameClient gameClient = new GameClient();

        String ip = null;
        String stateFileName = null;
        //IP must be specified
        if(args.length > 0) {
            ip = args[0];
        } else {
            System.out.println("No IP Specified");
            System.exit(0);
        }
        if (args.length > 1) {
            stateFileName = args[1];
        }

        gameClient.connectToServer(ip, 4321);
        QuartoPlayerAgent quartoAgent = new QuartoPlayerAgent(gameClient, stateFileName);
        quartoAgent.play();

        gameClient.closeConnection();
    }
    /**
     * @param        curState
     * @param        levelsLeft
     */
    private void searchGameTree(QuartoGameState curState, int levelsLeft)
    {
        if(levelsLeft == 0 || curState.hasQuarto()) {
            //Reached depth or a terminal winning node
            curState.evaluate();
        }
        else {
            for(QuartoGameState state : curState) {
                System.out.print(state.value + " ");
                state.resetMinimax();
                System.out.print(state.value);
                searchGameTree(state, levelsLeft - 1);

                if(curState.isMaxState) {
                    if(state.value > curState.value) {
                        curState.value = state.value-1;
                    }
                } else {
                    if(state.value < curState.value) {
                        curState.value = state.value + 1;
                    }
                }

            }
        }
    }

    /**
     * @return       String
     */
    @Override
    protected String pieceSelectionAlgorithm()
    {
        return String.format("%5s",
                Integer.toBinaryString(
                        this.curState.bestTransition.transitionPiece.getPieceID())).replace(' ', '0');
    }

    /**
     * @return       String
     * @param        pieceID
     */
    @Override
    protected String moveSelectionAlgorithm(int pieceID)
    {
        // Set this.curState
        this.searchGameTree(this.curState, this.maxDepth);

        return this.curState.bestTransition.transitionMove[0] + "," + this.curState.bestTransition.transitionMove[1];
    }

    @Override
    protected void play() {
        boolean gameOn = true;
        setTurnTimeLimit();

        //player 2 gets first move
        if (playerNumber == 2) {
            choosePieceTurn();
        }

        while(gameOn) {
            //print board
            this.quartoBoard.printBoardState();
            //turn order swaps
            chooseMoveTurn();

            this.quartoBoard.printBoardState();

            choosePieceTurn();
        }
	}

    //loop through board and see if the game is in a won state
    private boolean checkIfGameIsWon() {

        //loop through rows
        for(int i = 0; i < NUMBER_OF_ROWS; i++) {
            //gameIsWon = this.quartoBoard.checkRow(i);
            if (this.quartoBoard.checkRow(i)) {
                System.out.println("Win via row: " + (i) + " (zero-indexed)");
                return true;
            }
        }
        //loop through columns
        for(int i = 0; i < NUMBER_OF_COLUMNS; i++) {
            //gameIsWon = this.quartoBoard.checkColumn(i);
            if (this.quartoBoard.checkColumn(i)) {
                System.out.println("Win via column: " + (i) + " (zero-indexed)");
                return true;
            }
        }

        //check Diagonals
        if (this.quartoBoard.checkDiagonals()) {
            System.out.println("Win via diagonal");
            return true;
        }

        return false;
    }
}
