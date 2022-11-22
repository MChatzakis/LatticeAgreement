package cs451.commonUtils;


public class IntPair {
    private int v1;
    private int v2;

    public IntPair(int v1, int v2){
        this.v1 = v1;
        this.v2 = v2;
    }

    public boolean equals(Object obj) {
        if (obj == null)
            return false;

        if (obj == this)
            return true;

        IntPair p = (IntPair) obj;

        boolean result = (
                p.v1 == this.v1 &&
                p.v2 == this.v2
        );

        return result;
    }

    public int hashCode(){
        return this.v1 * this.v2;
    }
}
