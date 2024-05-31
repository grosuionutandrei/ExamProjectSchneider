package easv.be;

public enum Currency {
    EUR("€"),
    USD("$");
    private final String stringValue;

    Currency(String stringValue) {
        this.stringValue = stringValue;
    }

    public String toString() {
        return stringValue;
    }
}
