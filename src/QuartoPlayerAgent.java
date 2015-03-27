import java.lang.Override;
import java.util.Random;

public class QuartoPlayerAgent extends QuartoAgent {
    private int maxDepth = -1;
    public final int NODES_PER_SECOND = 1500; //TO-DO Benchmark NODES_PER_SECOND
    public int currentDepth = 1;
    public QuartoPiece pieceToGiveMini = null;

    private static Random rand = new Random();

    //Example AI
    public QuartoPlayerAgent(GameClient gameClient, String stateFileName) {
        // because super calls one of the super class constructors(you can overload constructors),
        // you need to pass the parameters required.
        super(gameClient, stateFileName);
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

    private int calcSearchableDepth(QuartoGameState state) {
        int depthLimit = 0;
        int totalNodes = state.calcNodesInGeneration(depthLimit);
        int maxSearchableNodes = NODES_PER_SECOND * (this.timeLimitForResponse / 1000);
        while(totalNodes <= maxSearchableNodes) {
            depthLimit += 1;
            totalNodes += state.calcNodesInGeneration(depthLimit);
        }

        if(depthLimit > this.maxDepth) {
            this.maxDepth = depthLimit - 1;
        }

        return depthLimit - 1 > 0 ? depthLimit - 1 : 0;
    }

    /**
     * @param curState State from which we are starting the search
     * @param levelsLeft Levels left to search before bottoming out
     * @param limboPiece Piece to be placed
     * @param rootDepth The current root nodes depth in relation to the original tree. Used to ensure unique evaluations.
     */
    private void searchGameTree(QuartoGameState curState, QuartoPiece limboPiece, int levelsLeft, int rootDepth) {
        if(levelsLeft == 0 || curState.hasQuarto() || curState.board.checkIfBoardIsFull()) {
            curState.evaluate(limboPiece);
        }
        else {
            QuartoGameTransitionGenerator gen = new QuartoGameTransitionGenerator(curState, limboPiece);
            for(QuartoGameTransition transition : gen) {
                QuartoGameState state = transition.toState;

                // TODO: don't explore this state if it's already been explored from the current root
                state.resetMinimax();
                searchGameTree(state, transition.nextPiece, levelsLeft - 1, rootDepth);

                if(curState.isMaxState) {
                    if(state.value > curState.value) {
                        curState.bestTransition = transition;

                        if(state.value > 0) {
                            curState.value = state.value - 1;
                        }
                        else if(state.value < 0) {
                            curState.value = state.value + 1;
                        }
                        else {
                            curState.value = 0;
                        }

                        if(state.value > state.alpha) {
                            state.alpha = state.value;
                        }
                    }
                    else if(state.value == curState.value) {
                        // Take equivalent nodes with a 50/50 probability to
                        // randomize the selections
                        if(rand.nextInt(2) == 0) {
                            curState.bestTransition = transition;
                        }
                    }
                }
                else {
                    if(state.value < curState.value) {
                        curState.bestTransition = transition;

                        if(state.value > 0) {
                            curState.value = state.value - 1;
                        }
                        else if(state.value < 0) {
                            curState.value = state.value + 1;
                        }
                        else {
                            curState.value = 0;
                        }

                        if(state.value < state.beta) {
                            state.beta = state.value;
                        }
                    }
                    else if(state.value == curState.value) {
                        // Take equivalent nodes with a 50/50 probability to
                        // randomize the selections
                        if(rand.nextInt(2) == 0) {
                            curState.bestTransition = transition;
                        }
                    }
                }
            }
        }
    }

    /**
     * @return       String
     * @param        pieceID Piece given by Mini
     */
    @Override
    protected String moveSelectionAlgorithm(int pieceID) {
        /*
         * NOTE: The new game state is defined as a mini state because states
         *       are defined as being the result of a player's decisions
         *       i.e. it's a max node if max made the decisions that resulted
         *       in this new state.
         *
         *       Since mini just chose a square and piece the current state
         *       belongs to her.
         */
        QuartoGameState tmpState = new QuartoGameState(this.quartoBoard, Integer.MAX_VALUE, Integer.MIN_VALUE, false);
        QuartoPiece limboPiece = tmpState.board.getPiece(pieceID);

        this.searchGameTree(tmpState, limboPiece, this.calcSearchableDepth(tmpState), this.currentDepth);
        this.pieceToGiveMini = tmpState.bestTransition.nextPiece;

        return tmpState.bestTransition.placedPieceLocation[0] +
                "," +
                tmpState.bestTransition.placedPieceLocation[1];
    }

    /**
     * @return       String
     */
    @Override
    protected String pieceSelectionAlgorithm() {
        if(this.pieceToGiveMini != null){
            return String.format("%5s", this.pieceToGiveMini.binaryStringRepresentation());
        }
        else {
            return String.format("%5s", Integer.toBinaryString(
                            this.quartoBoard.chooseNextPieceNotPlayed())).replace(' ', '0');
        }
    }
}
