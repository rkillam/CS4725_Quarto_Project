public class QuartoPlayerAgent extends QuartoAgent {
    private QuartoGameState curState;
    private int maxDepth;

    //Example AI
    public QuartoPlayerAgent(GameClient gameClient, String stateFileName) {
        // because super calls one of the super class constructors(you can overload constructors), you need to pass the parameters required.
        super(gameClient, stateFileName);

        this.curState = new QuartoGameState(this.quartoBoard);
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
            curState.evaluate();
        }
        else {
            for(QuartoGameState state : curState) {
                if (state.estimated) {
                    state.resetMinimax();
                    this.searchGameTree(state, levelsLeft - 1);
                    // adjust curState minimax values
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
                        this.curState.maxTransition.transitionPiece.getPieceID())).replace(' ', '0');
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
        return this.curState.maxTransition.transitionMove[0] + "," + this.curState.maxTransition.transitionMove[1];
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
