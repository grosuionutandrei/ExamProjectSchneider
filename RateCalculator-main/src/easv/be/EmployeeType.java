package easv.be;

public enum EmployeeType {
    Overhead("Overhead"),
    Resource("Resource");

    private final String stringValue;

    EmployeeType(String stringValue) {
        this.stringValue = stringValue;
    }

    public String toString() {
        return stringValue;
    }
}
