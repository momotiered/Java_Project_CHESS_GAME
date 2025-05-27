public enum Piece {
    BLACK("●"),
    WHITE("○"),
    EMPTY("·"),
    BARRIER("#"),
    CRATER("@");

    private final String symbol;

    Piece(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }
} 