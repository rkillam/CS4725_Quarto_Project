import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;

public class QuartoGameTransitionGenerator implements Iterable<QuartoGameTransition>{

    public QuartoGameState fromState;
    public QuartoPiece limboPiece;
    public List<QuartoPiece> piecesList;
    public int rootDepth;

    QuartoGameTransitionGenerator(QuartoGameState fromState, QuartoPiece limboPiece, int rootDepth) {
        this.fromState = fromState;
        this.limboPiece = limboPiece;
        this.piecesList = (ArrayList<QuartoPiece>)fromState.freePieces.clone();
        this.piecesList.remove(limboPiece);
        this.rootDepth = rootDepth;
    }

    /**
     * @return       Iterator<QuartoGameState>
     */
    @Override
    public Iterator<QuartoGameTransition> iterator()
    {
        return new Iterator<QuartoGameTransition>() {
            private QuartoGameState curState = fromState;
            private Iterator<QuartoPiece> pieces;
            private Iterator<int[]> squares = curState.freeSquares.iterator();
            private QuartoPiece nextPiece = null;
            private int[] nextSquare = null;

            private QuartoGameState newState = null;

            @Override
            public QuartoGameTransition next() {
                if(nextPiece == null || !pieces.hasNext()) {
                    pieces = piecesList.iterator();

                    // While we are creating states that have already been visited from this root
                    newState = null;
                    while((newState == null || newState.lastLevelExaminedFrom == rootDepth) && squares.hasNext()) {
                        nextSquare = squares.next();

                        QuartoBoard newBoard = new QuartoBoard(curState.board);
                        newBoard.insertPieceOnBoard(nextSquare[0], nextSquare[1], limboPiece.getPieceID());

                        newState = QuartoGameState.getRegisteredState(
                                newBoard,
                                curState.alpha,
                                curState.beta,
                                !curState.isMaxState
                        );
                    }

                    newState.lastLevelExaminedFrom = rootDepth;
                }

                nextPiece = pieces.next();

                return new QuartoGameTransition(newState, limboPiece, nextSquare, nextPiece);
            }

            @Override
            public boolean hasNext() {
                return curState.freeSquares.size() > 0 &&
                        (squares.hasNext() || pieces.hasNext());
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
