
public class QuartoGameTransition {

    public QuartoGameState toState;
    public QuartoPiece placedPiece;
    public int[] placedPieceLocation;
    public QuartoPiece nextPiece;

    //
    // Methods
    //

    /**
     * @param        toState
     * @param        placedPiece
     * @param        placedPieceLocation
     * @param        nextPiece;
     */
    public QuartoGameTransition(QuartoGameState toState, QuartoPiece placedPiece,
                                     int[] placedPieceLocation, QuartoPiece nextPiece) {
        this.toState = toState;
        this.placedPiece = placedPiece;
        this.placedPieceLocation = placedPieceLocation;
        this.nextPiece = nextPiece;
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
        return placedPiece.binaryStringRepresentation() + ":" + placedPieceLocation[0] + "," + placedPieceLocation[1] +
                ":" + nextPiece.binaryStringRepresentation();
    }
}
