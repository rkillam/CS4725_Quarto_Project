
public class QuartoGameTransition {

    public QuartoGameState toState;
    public QuartoPiece transitionPiece;
    public int[] transitionMove;

    //
    // Methods
    //

    /**
     * @param        toState
     * @param        transitionPiece
     * @param        transitionMove
     */
    public void QuartoGameTransition(QuartoGameState fromState, QuartoGameState toState,
                                     QuartoPiece transitionPiece, int[] transitionMove) {
        this.toState = toState;
        this.transitionPiece = transitionPiece;
        this.transitionMove = transitionMove;
    }


    /**
     * Returns a string containing the fromState.hash(), the transitionsSquare, and the
     * transitionPiece.
     *
     * Will be used to quickly transition to a new state once mini makes their move.
     * @return       String
     */
    public String getHashCode()
    {
        return  this.transitionPiece.binaryStringRepresentation() + ":" +
                this.transitionMove[0] + "," + this.transitionMove[1];
    }
}
