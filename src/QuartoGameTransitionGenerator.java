import java.util.Iterator;
import java.util.ArrayList;

public class QuartoGameTransitionGenerator implements Iterable<QuartoGameTransition>{

    public QuartoGameState fromState;
    public QuartoPiece limboPiece;
    public Iterator<QuartoPiece> pieces;

    QuartoGameTransitionGenerator(QuartoGameState fromState, QuartoPiece limboPiece) {
        this.fromState = fromState;
        this.limboPiece = limboPiece;
        ArrayList<QuartoPiece> tempPieces = (ArrayList<QuartoPiece>)fromState.freePieces.clone();
        tempPieces.remove(limboPiece);
        pieces = tempPieces.iterator();
    }

    /**
     * @return       Iterator<QuartoGameState>
     */
    @Override
    public Iterator<QuartoGameTransition> iterator()
    {
        return new Iterator<QuartoGameTransition>() {
            private QuartoGameState curState = fromState;
            private Iterator<int[]> squares = curState.freeSquares.iterator();
            private QuartoPiece nextPiece = null;
            private int[] nextSquare = null;

            @Override
            public QuartoGameTransition next() {
                //reset squares if finished exploring, or beginning exploring
                if(nextSquare == null || !squares.hasNext()) {
                    squares = curState.freeSquares.iterator();
                    nextPiece = pieces.next();
                }

                nextSquare = squares.next();

                QuartoBoard newBoard = new QuartoBoard(curState.board);

                newBoard.insertPieceOnBoard(nextSquare[0], nextSquare[1], limboPiece.getPieceID());

                QuartoGameState newState = new QuartoGameState(newBoard, curState.alpha, curState.beta, !curState.isMaxState);

                return new QuartoGameTransition(newState, limboPiece, nextSquare, nextPiece);
            }

            @Override
            public boolean hasNext() {
                return pieces.hasNext()      ||
                        this.squares.hasNext();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }
}
