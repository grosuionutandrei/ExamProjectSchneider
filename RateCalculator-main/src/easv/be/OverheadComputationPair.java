package easv.be;



/**this class is used to hole the calculated value for the regions or countries overhead*/
public class OverheadComputationPair <K, V>{
private K key;

private V value;


    public OverheadComputationPair(K key, V value) {
        this.key = key;
        this.value = value;
    }



    @Override
    public String toString() {
        return "OverheadComputationPair{" +
                "key=" + key +
                ", value=" + value +
                '}';
    }

    public OverheadComputationPair() {
    }

    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }

    public void setValue(V value) {
        this.value = value;
    }
}
