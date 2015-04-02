import java.lang.Override;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

class SearchSpaceThread implements Runnable {
    public QuartoPlayerAgent agent;
    public boolean rootLocked;
    public boolean done;
    public QuartoPiece limboPiece;

    public SearchSpaceThread(QuartoPlayerAgent agent) {
        this.agent = agent;
        this.rootLocked = false;
        this.done = false;
        this.limboPiece = null;
    }

    @Override
    public void run() {
        this.done = false;
        agent.searchGameTree(
                agent.rootState,
                this.limboPiece,
                agent.calcSearchableDepth(agent.rootState),
                agent.currentDepth++
        );
        this.done = true;
    }
}

class CleanupThread implements Runnable {
    private QuartoPiece placedPiece;
    private int[] placedPieceLocation;
    private int numberOfFreePieces;

    public boolean done;

    public CleanupThread(QuartoPlayerAgent agent) {
        this.placedPiece = agent.rootState.bestTransition.placedPiece;
        this.placedPieceLocation = agent.rootState.bestTransition.placedPieceLocation;
        this.numberOfFreePieces = agent.rootState.freePieces.size();

        this.done = false;
    }

    @Override
    public void run() {
        List<String> statesToPrune = new ArrayList<String>();
        for(Map.Entry<String, QuartoGameState> stateMap : QuartoGameState.getIterator()) {
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
                statesToPrune.add(key);
                System.out.println("About to prune");
                state.board.printBoardState();
            }
        }

        System.out.println("\n\n\n");

        for(String stateKey : statesToPrune) {
            QuartoGameState.removeState(stateKey);
        }

//        QuartoGameState.pruneStates(this.placedPiece, this.placedPieceLocation, this.numberOfFreePieces);
//        QuartoGameState.clearStates();
        this.done = true;
    }
}

public class QuartoPlayerAgent extends QuartoAgent {
    private int maxDepth = -1;
    public final int NODES_PER_SECOND = 1000; //TO-DO Benchmark NODES_PER_SECOND
    public int currentDepth = 1;
    public QuartoGameState rootState = null;

    private static Random rand = new Random();

    public SearchSpaceThread searchSpaceThread;

    //Example AI
    public QuartoPlayerAgent(GameClient gameClient, String stateFileName) {
        // because super calls one of the super class constructors(you can overload constructors),
        // you need to pass the parameters required.
        super(gameClient, stateFileName);

        this.searchSpaceThread = new SearchSpaceThread(this);
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

    public int calcSearchableDepth(QuartoGameState state) {
        int depthLimit = 0;
        int totalNodes = state.calcNodesInGeneration(depthLimit);
        int maxSearchableNodes = NODES_PER_SECOND * ((this.timeLimitForResponse - COMMUNICATION_DELAY)/ 1000);
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
    public void searchGameTree(QuartoGameState curState, QuartoPiece limboPiece, int levelsLeft, int rootDepth) {
        curState.resetMinimax();

        if(levelsLeft == 0 || curState.hasQuarto() || curState.board.checkIfBoardIsFull()) {
            curState.evaluate(limboPiece);
        }
        else {
            QuartoGameTransitionGenerator gen = new QuartoGameTransitionGenerator(curState, limboPiece, rootDepth);
            for(QuartoGameTransition transition : gen) {
                QuartoGameState state = transition.toState;

                // We need this check because the transition generator might return null if
                // the last state it tries to generate has already been visited
                if(state != null) {
                    searchGameTree(state, transition.nextPiece, levelsLeft - 1, rootDepth);

                    if (curState.isMaxState) {
                        if (state.value > curState.value) {
                            curState.bestTransition = transition;

                            if(curState.getHash().equals(this.rootState.getHash())) {
                                System.out.println("Setting root state's best trans as max");
                                this.rootState.bestTransition.toState.board.printBoardState();
                                System.out.println("curState's best trans board");
                                curState.bestTransition.toState.board.printBoardState();
                            }

                            if (state.value > 0) {
                                curState.value = state.value - 1;
                            } else if (state.value < 0) {
                                curState.value = state.value + 1;
                            } else {
                                curState.value = 0;
                            }

                            if (curState.value > curState.alpha) {
                                curState.alpha = curState.value;
                            }

                            /* Implements alpha beta pruning, also if this state's value is
                             * 26 then that means we can do no better, as such there is no
                             * point in exploring the rest of this node's children
                             */
                            if (curState.beta <= curState.alpha || curState.value >= 26) {
                                break;
                            }
                        } else if (state.value == curState.value) {
                            // Take equivalent nodes with a 50/50 probability to
                            // randomize the selections
                            if (rand.nextInt(2) == 0) {
                                curState.bestTransition = transition;
                            }
                        }
                    } else {
                        if (state.value < curState.value) {
                            curState.bestTransition = transition;

                            if(curState.getHash().equals(this.rootState.getHash())) {
                                System.out.println("Setting root state's best trans as mini");
                                System.out.println("Root's board state");
                                this.rootState.board.printBoardState();
                                System.out.printf("\nPutting piece: %s in %d,%d and giving piece: %s\n\n",
                                        this.rootState.bestTransition.placedPiece.binaryStringRepresentation(),
                                        this.rootState.bestTransition.placedPieceLocation[0],
                                        this.rootState.bestTransition.placedPieceLocation[1],
                                        this.rootState.bestTransition.nextPiece.binaryStringRepresentation()
                                );
                                this.rootState.bestTransition.toState.board.printBoardState();
                            }

                            if (state.value > 0) {
                                curState.value = state.value - 1;
                            } else if (state.value < 0) {
                                curState.value = state.value + 1;
                            } else {
                                curState.value = 0;
                            }

                            if (curState.value < curState.beta) {
                                curState.beta = curState.value;
                            }

                            /* Implements alpha beta pruning, also if this state's value is
                             * 26 then that means we can do no better, as such there is no
                             * point in exploring the rest of this node's children
                             */
                            if (curState.beta <= curState.alpha || curState.value <= -26) {
                                break;
                            }
                        } else if (state.value == curState.value) {
                            // Take equivalent nodes with a 50/50 probability to
                            // randomize the selections
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
     * @param        pieceID Piece given by Mini
     */
    @Override
    protected String moveSelectionAlgorithm(int pieceID) {
//        QuartoGameState.clearStates();

        /*
         * NOTE: The new game state is defined as a mini state because states
         *       are defined as being the result of a player's decisions
         *       i.e. it's a max node if max made the decisions that resulted
         *       in this new state.
         *
         *       Since mini just chose a square and piece the current state
         *       belongs to her.
         */
        this.rootState = QuartoGameState.getRegisteredState(
                this.quartoBoard,
                Integer.MIN_VALUE,
                Integer.MAX_VALUE,
                false
        );

//        this.searchGameTree(
//                this.rootState,
//                this.rootState.board.getPiece(pieceID),
//                this.calcSearchableDepth(this.rootState),
//                this.currentDepth++
//        );

        this.searchSpaceThread.limboPiece = this.rootState.board.getPiece(pieceID);
        this.searchSpaceThread.run();

        while(!this.searchSpaceThread.done){}
//        try {
//            Thread.sleep(this.timeLimitForResponse - (COMMUNICATION_DELAY * 10));
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        if(this.rootState.bestTransition == null) {
            System.out.println("best transition is null; has " + this.rootState.transitions.size() + " transitions");
            this.rootState.board.printBoardState();
        }

        System.out.println("Root's board state");
        this.rootState.board.printBoardState();
        System.out.printf("\nPutting piece: %s in %d,%d and giving piece: %s\n\n",
                this.rootState.bestTransition.placedPiece.binaryStringRepresentation(),
                this.rootState.bestTransition.placedPieceLocation[0],
                this.rootState.bestTransition.placedPieceLocation[1],
                this.rootState.bestTransition.nextPiece.binaryStringRepresentation()
        );
        this.rootState.bestTransition.toState.board.printBoardState();

        return this.rootState.bestTransition.placedPieceLocation[0] +
                "," +
                this.rootState.bestTransition.placedPieceLocation[1];
    }

    /**
     * @return       String
     */
    @Override
    protected String pieceSelectionAlgorithm() {
        QuartoPiece retPiece;
        if(this.rootState != null) {
            retPiece = this.rootState.bestTransition.nextPiece;

            QuartoGameState.clearStates();
//            CleanupThread ct = new CleanupThread(this);
//            ct.run();
//
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//
//            System.out.println("Done sleeping");
//
//            if(!ct.done) {System.out.println("Not done pruning");}
//
//            while(!ct.done){}
            System.out.println("done ct");
        }
        else {
            //Should only occur on first move of a game
            QuartoGameState tmpState = QuartoGameState.getRegisteredState(
                    this.quartoBoard,
                    Integer.MAX_VALUE,
                    Integer.MIN_VALUE,
                    true
            );

            retPiece = tmpState.getSafePiece();
        }

        return String.format("%5s", retPiece.binaryStringRepresentation());
    }
}
