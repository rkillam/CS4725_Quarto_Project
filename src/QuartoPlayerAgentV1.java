import java.lang.Override;
import java.util.Random;

public class QuartoPlayerAgentV1 extends QuartoAgent {
    private QuartoGameState curState;
    private int maxDepth = -1;
    private int[] minisChosenSquare = {-1,-1};
    private int minisPieceID = -1;
    public final int NODES_PER_SECOND = 1000; //TO-DO Benchmark NODES_PER_SECOND
    public int currentDepth = 1;
    public QuartoPiece pieceToGiveMini = null;

    private static Random rand = new Random();

    //Example AI
    public QuartoPlayerAgentV1(GameClient gameClient, String stateFileName) {
        // because super calls one of the super class constructors(you can overload constructors), you need to pass the parameters required.
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
            this.maxDepth = depthLimit;
        }

        return depthLimit - 1 > 0 ? depthLimit - 1 : 0;
    }

    /**
     * @param curState
     * @param levelsLeft
     * @param limboPiece
     * @param rootDepth The current root nodes depth in relation to the original tree. Used to ensure unique evaluations.
     */
    private void searchGameTree(QuartoGameState curState, QuartoPiece limboPiece, int levelsLeft, int rootDepth) {
        if(levelsLeft == 0 || curState.hasQuarto() || curState.board.getNumberOfPieces() == 25) {
            //Reached max depth or a terminal winning node
            curState.evaluate(limboPiece);
        }
        else {
            QuartoGameTransitionGenerator gen = new QuartoGameTransitionGenerator(curState, limboPiece);
            for(QuartoGameTransition transition : gen) {
                QuartoGameState state = transition.toState;

                if (state.lastLevelExaminedFrom != rootDepth) {
                    state.lastLevelExaminedFrom = rootDepth;

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
                        }
                        else if(state.value == curState.value) {
                            if(rand.nextInt(2) == 0) {
                                curState.bestTransition = transition;
                            }
                        }
                    }
                    else {
                        if (state.value < curState.value) {
                            curState.bestTransition = transition;

                            if (state.value > 0) {
                                curState.value = state.value - 1;
                            } else if (state.value < 0) {
                                curState.value = state.value + 1;
                            } else {
                                curState.value = 0;
                            }
                        } else if (state.value == curState.value) {
                            if (rand.nextInt(2) == 0) {
                                curState.bestTransition = transition;
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * @return       String
     * @param        pieceID
     */
    @Override
    protected String moveSelectionAlgorithm(int pieceID) {
        QuartoGameState tmpState = new QuartoGameState(this.quartoBoard, Integer.MAX_VALUE, Integer.MIN_VALUE, true);
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
            return String.format("%5s", Integer.toBinaryString(this.pieceToGiveMini.getPieceID())).replace(' ', '0');
        }
        else {
            return String.format("%5s", Integer.toBinaryString(
                            this.quartoBoard.chooseNextPieceNotPlayed())).replace(' ', '0');
        }
    }
}
