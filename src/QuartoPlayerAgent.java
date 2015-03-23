import java.lang.Override;
import java.util.Random;

public class QuartoPlayerAgent extends QuartoAgent {
    private static QuartoGameState curState;
    private int maxDepth = -1;
    private int[] minisChosenSquare = {-1,-1};
    private int minisPieceID = -1;
    public final int NODES_PER_SECOND = 1000; //TO-DO Benchmark NODES_PER_SECOND
    public int currentDepth = 1;

    private static Random rand = new Random();

    //Example AI
    public QuartoPlayerAgent(GameClient gameClient, String stateFileName) {
        // because super calls one of the super class constructors(you can overload constructors), you need to pass the parameters required.
        super(gameClient, stateFileName);
        setPlayerNumber();

        // if we are player number 1 we want to move to a max node, so our first node is min node
        boolean isMax = (playerNumber == 1);

        QuartoPlayerAgent.curState = new QuartoGameState(new QuartoBoard(this.quartoBoard),
                                            Integer.MIN_VALUE, Integer.MAX_VALUE, isMax);
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

    private int calcSearchableDepth() {
        int depthLimit = 0;
        int totalNodes = QuartoPlayerAgent.curState.calcNodesInGeneration(depthLimit);
        int maxSearchableNodes = NODES_PER_SECOND * (this.timeLimitForResponse / 1000);
        while(totalNodes <= maxSearchableNodes) {
            depthLimit += 1;
            totalNodes += QuartoPlayerAgent.curState.calcNodesInGeneration(depthLimit);
        }

        if(depthLimit > this.maxDepth) {
            this.maxDepth = depthLimit;
        }

        return depthLimit - 1;
    }

    /**
     * @param curState
     * @param levelsLeft
     * @param limboPiece
     * @param rootDepth The current root nodes depth in relation to the original tree. Used to ensure unique evaluations.
     */
    private static void searchGameTree(QuartoGameState curState, QuartoPiece limboPiece, int levelsLeft, int rootDepth)
    {
        if(levelsLeft == 0 || curState.hasQuarto() || curState.board.getNumberOfPieces() == 25) {
            //Reached max depth or a terminal winning node
            curState.evaluate(rootDepth);
        }
        else {
            QuartoGameTransitionGenerator gen = new QuartoGameTransitionGenerator(curState, limboPiece);
            for(QuartoGameTransition transition : gen) {
                QuartoGameState state = transition.toState;

                if (state.lastLevelExaminedFrom != rootDepth) {
                    state.lastLevelExaminedFrom = rootDepth;

                    state.resetMinimax();
                    searchGameTree(state, transition.nextPiece, levelsLeft - 1, rootDepth);

                    if (curState.isMaxState) {
                        //Choose largest value
                        if (state.value > Math.abs(curState.value)) {
                            curState.value = curState.alpha = (state.value == 0) ? 0 : (Math.abs(state.value) - 1) * (Math.abs(state.value) / state.value);
                            curState.bestTransition = transition;
                        } else if (state.value == curState.value && QuartoPlayerAgent.rand.nextInt(2)==0) {
                            //50-50 chance we'll take a new equivalent transition
                            curState.bestTransition = transition;
                        }

                        if (state.value < curState.beta) {
                            curState.beta = state.value;
                        }
                    } else {
                        /*
                        *  Choose the most negative value, or the largest value. We choose a large positive value over
                        *  0 so that when the player agent picks a best move we are at least approaching a win instead
                        *  or constantly approaching ties.
                        */
                        if (state.value < 0) {
                            if (state.value < curState.value) {
                                curState.value = curState.beta = state.value + 1;
                                curState.bestTransition = transition;
                            }
                        } else if (curState.value > 27 || state.value - 1 > curState.value) {
                            curState.value = curState.alpha = (state.value == 0) ? 0 : state.value - 1;
                            curState.bestTransition = transition;
                        } else if (state.value == Math.abs(curState.value) && QuartoPlayerAgent.rand.nextInt(2)==0) {
                            //50-50 chance we'll take a new equivalent transition
                            curState.bestTransition = transition;
                        }

                        if (state.value - 1 > curState.alpha) {
                            curState.alpha = state.value - 1;
                        }
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
        if(QuartoPlayerAgent.curState.bestTransition != null) {

            QuartoPiece transitionPiece = QuartoPlayerAgent.curState.bestTransition.nextPiece;
            QuartoPlayerAgent.curState = QuartoPlayerAgent.curState.bestTransition.toState;

            return String.format("%5s", Integer.toBinaryString(transitionPiece.getPieceID())).replace(' ', '0');
        } else {
            return String.format("%5s", Integer.toBinaryString(QuartoPlayerAgent.curState.board.chooseNextPieceNotPlayed()));
        }
    }

    /**
     * @return       String
     * @param        pieceID
     */
    @Override
    protected String moveSelectionAlgorithm(int pieceID)
    {
        QuartoGameState prevState = QuartoPlayerAgent.curState;
        QuartoPiece givenPiece = QuartoPlayerAgent.curState.board.getPiece(pieceID);

        QuartoGameTransition quartoGameTransition = null;

        if (prevState.bestTransition != null) {
            quartoGameTransition = prevState.transitions.get(prevState.bestTransition.placedPiece.binaryStringRepresentation() + ":" +
                    minisChosenSquare[0] + "," + minisChosenSquare[1] + ":" + givenPiece.binaryStringRepresentation());
        }

        if(quartoGameTransition == null) {
            QuartoPlayerAgent.curState = new QuartoGameState(this.quartoBoard, Integer.MAX_VALUE, Integer.MIN_VALUE, !QuartoPlayerAgent.curState.isMaxState);
        } else {
            QuartoPlayerAgent.curState = quartoGameTransition.toState;
        }

        QuartoPiece limboPiece = QuartoPlayerAgent.curState.board.getPiece(pieceID);
        searchGameTree(QuartoPlayerAgent.curState, limboPiece, this.calcSearchableDepth(), this.currentDepth);

        return QuartoPlayerAgent.curState.bestTransition.placedPieceLocation[0] + "," + QuartoPlayerAgent.curState.bestTransition.placedPieceLocation[1];
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

            this.currentDepth = this.currentDepth + 1;
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

        //determine piece for opponent to use
        String pieceMessage = pieceSelectionAlgorithm();

        this.gameClient.writeToServer(pieceMessage);

        MessageFromServer = this.gameClient.readFromServer(1000000);
        String[] splittedResponse = MessageFromServer.split("\\s+");
        if (!isExpectedMessage(splittedResponse, ACKNOWLEDGMENT_PIECE_HEADER) && !isExpectedMessage(splittedResponse, ERROR_PIECE_HEADER)) {
            turnError(MessageFromServer);
        }

        minisPieceID = Integer.parseInt(splittedResponse[1], 2);

        MessageFromServer = this.gameClient.readFromServer(1000000);
        String[] splittedMoveResponse = MessageFromServer.split("\\s+");

        isExpectedMessage(splittedMoveResponse, MOVE_MESSAGE_HEADER, true);

        String[] moveString = splittedMoveResponse[1].split(",");
        minisChosenSquare = new int[2];
        minisChosenSquare[0] = Integer.parseInt(moveString[0]);
        minisChosenSquare[1] = Integer.parseInt(moveString[1]);

        this.quartoBoard.insertPieceOnBoard(minisChosenSquare[0], minisChosenSquare[1], minisPieceID);
    }

    @Override
    protected void chooseMoveTurn() {
        //get message
        String MessageFromServer;
        MessageFromServer = this.gameClient.readFromServer(1000000);
        String[] splittedMessage = MessageFromServer.split("\\s+");

        //close program is message is not the expected message
        isExpectedMessage(splittedMessage, SELECT_MOVE_HEADER, true);
        int pieceID = Integer.parseInt(splittedMessage[1], 2);

        //determine move based on piece given by opponent
        String moveMessage = moveSelectionAlgorithm(pieceID);

        this.gameClient.writeToServer(moveMessage);

        MessageFromServer = this.gameClient.readFromServer(1000000);
        String[] splittedMoveResponse = MessageFromServer.split("\\s+");
        if (!isExpectedMessage(splittedMoveResponse, ACKNOWLEDGMENT_MOVE_HEADER) && !isExpectedMessage(splittedMoveResponse, ERROR_MOVE_HEADER)) {
            turnError(MessageFromServer);
        }

        //after confirming move legal, place piece on board
        String[] moveString = splittedMoveResponse[1].split(",");
        int[] move = new int[2];
        move[0] = Integer.parseInt(moveString[0]);
        move[1] = Integer.parseInt(moveString[1]);

        this.quartoBoard.insertPieceOnBoard(move[0], move[1], pieceID);

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
