package entities.investment;

public enum Investment {
    STOCK(1, "Aktien"),
    ALTERNATIVE(2, "alt. Anlagen"),
    IOAN(3, "Anleihen"),
    LIQUIDITY(4, "Liquidität");

    private final int id;
    private final String name;

    Investment(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}
