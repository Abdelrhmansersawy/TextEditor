package text_editor;

public class Tuple<K, V, X> {
    private K first;
    private V second;
    private X third;
    public Tuple(K first, V second, X third) {
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public K getFirst() {
        return this.first;
    }

    public V getSecond() {
        return this.second;
    }
    public X getThird(){
        return this.third;
    }
    public void setFirst(K first) {
        this.first = first;
    }
    public void setSecond(V second){ this.second = second; }
    public void setThird(X third){ this.third = third; }

    @Override
    public String toString() {
        return "{" + first + ", " + second + ", " + third + "}";
    }
}
