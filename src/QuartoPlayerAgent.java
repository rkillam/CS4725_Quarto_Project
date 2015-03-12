import java.lang.Override;
import java.util.ArrayList;

public class QuartoPlayerAgent extends QuartoAgent {
    private static QuartoGameState curState;
    private static final int MAX_DEPTH = 1;
    public final static int NUM_PIECES = 32;
    public final static int ROW_LENGTH = 5;
    public final static int COL_LENGTH = 5;

    public int[] minisChosenSquare = {-1,-1};

    private static ArrayList<Thread> runningThreads = new ArrayList<Thread>();

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

        // if we are player number 1 we want to move to a max node, so our first node is min node
        boolean isMax = (playerNumber != 1);

        this.curState = new QuartoGameState(new QuartoBoard(this.quartoBoard),
                                            freeSquares, freePieces, Integer.MIN_VALUE,
                                            Integer.MAX_VALUE, isMax);
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
    private static void searchGameTree(QuartoGameState curState, int levelsLeft)
    {
        if(levelsLeft == 0 || curState.hasQuarto()) {
            //Reached max depth or a terminal winning node
            curState.evaluate();
        }
        else {
            for(QuartoGameTransition transition : curState) {
                QuartoGameState state = transition.toState;

                state.resetMinimax();
                searchGameTree(state, levelsLeft - 1);

                if(curState.isMaxState) {
                    if(state.value > curState.value) {
                        if(state.value == 0) {
                            curState.value = curState.alpha = 0;
                        }
                        else if(state.value < 0) {
                            curState.value = curState.alpha = state.value + 1;
                        }
                        else {
                            curState.value = curState.alpha = state.value - 1;
                        }

                        curState.bestTransition = transition;
                    }
                }
                else {
                    if(state.value < curState.value) {
                        if(state.value == 0) {
                            curState.value = curState.alpha = 0;
                        }
                        else if(state.value < 0) {
                            curState.value = curState.alpha = state.value + 1;
                        }
                        else {
                            curState.value = curState.alpha = state.value - 1;
                        }

                        curState.bestTransition = transition;
                    }
                }
//                System.out.println(curState.value);
            }
        }
    }

    /**
     * @return       String
     */
    @Override
    protected String pieceSelectionAlgorithm()
    {
        if(this.curState.bestTransition != null) {

            QuartoPiece transitionPiece = this.curState.bestTransition.transitionPiece;
            this.curState = this.curState.bestTransition.toState;

            return String.format("%5s", Integer.toBinaryString(transitionPiece.getPieceID())).replace(' ', '0');
        } else {
            return String.format("%5s", Integer.toBinaryString(this.curState.board.chooseNextPieceNotPlayed()));
        }
    }

    /**
     * @return       String
     * @param        pieceID
     */
    @Override
    protected String moveSelectionAlgorithm(int pieceID)
    {
        System.out.println("Entered move selection");

        QuartoPiece givenPiece = this.curState.board.getPiece(pieceID);
        QuartoGameState prevState = this.curState;

        // Set this.curState
        QuartoGameTransition quartoGameTransition = this.curState.transitions.get(givenPiece.binaryStringRepresentation() + ":" +
                                                    minisChosenSquare[0] + "," + minisChosenSquare[1]);

        if(quartoGameTransition == null) {
            ArrayList<int[]> tempSquares = new ArrayList<int[]>();
            for (int[] square: prevState.freeSquares) {
                if (square[0] != minisChosenSquare[0] || square[1] != minisChosenSquare[1]) {
                    tempSquares.add(square.clone());
                }
            }
            prevState.freeSquares = tempSquares;
            prevState.freePieces.remove(givenPiece);
            this.curState = new QuartoGameState(prevState.board, prevState.freeSquares, prevState.freePieces,
                                                prevState.alpha, prevState.beta, !prevState.isMaxState);
        } else {
            this.curState = quartoGameTransition.toState;
        }

        searchGameTree(curState, MAX_DEPTH);

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

    @Override
    protected void choosePieceTurn() {

        String MessageFromServer;
        //get message
        MessageFromServer = this.gameClient.readFromServer(1000000);
        String[] splittedMessage = MessageFromServer.split("\\s+");

        //close program is message is not the expected message
        isExpectedMessage(splittedMessage, SELECT_PIECE_HEADER, true);

        //determine piece
        String pieceMessage = pieceSelectionAlgorithm();

        this.gameClient.writeToServer(pieceMessage);

        MessageFromServer = this.gameClient.readFromServer(1000000);
        String[] splittedResponse = MessageFromServer.split("\\s+");
        if (!isExpectedMessage(splittedResponse, ACKNOWLEDGMENT_PIECE_HEADER) && !isExpectedMessage(splittedResponse, ERROR_PIECE_HEADER)) {
            turnError(MessageFromServer);
        }

        int pieceID = Integer.parseInt(splittedResponse[1], 2);

        MessageFromServer = this.gameClient.readFromServer(1000000);
        String[] splittedMoveResponse = MessageFromServer.split("\\s+");

        isExpectedMessage(splittedMoveResponse, MOVE_MESSAGE_HEADER, true);

        String[] moveString = splittedMoveResponse[1].split(",");
        minisChosenSquare = new int[2];
        minisChosenSquare[0] = Integer.parseInt(moveString[0]);
        minisChosenSquare[1] = Integer.parseInt(moveString[1]);

        this.quartoBoard.insertPieceOnBoard(minisChosenSquare[0], minisChosenSquare[1], pieceID);
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
