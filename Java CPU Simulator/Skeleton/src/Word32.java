public class Word32 {

    Bit[] k;
    public Word32() {
        this.k = new Bit[32];

        for(int i=0;i<32;i++){
            k[i] = new Bit(false);
        }
    }

    public Word32(Bit[] in) {
        this();
        for(int i=0; i< k.length; i++){
            k[i].assign(in[i].getValue());
        }

    }

    public void getTopHalf(Word16 result) {// sets result = bits 0-15 of this word. use bit.assign
        for(int i=0;i<16;i++){
            result.k[i].assign(k[i].getValue());
        }

    }

    public void getBottomHalf(Word16 result) {// sets result = bits 16-31 of this word. use bit.assign

            for(int i=0; i< 16;i++){
                result.k[i].assign(k[i+16].getValue());
            }

    }

    public void copy(Word32 result) { // sets result's bit to be the same as this. use bit.assign

        for(int i=0;i<k.length;i++) {
            result.k[i].assign(k[i].getValue());
        }
    }

    public boolean equals(Word32 other) {

      return equals(this, other);
    }

    public static boolean equals(Word32 a, Word32 b) {

        for(int i=0;i<32;i++){
            if(a.k[i].getValue() != b.k[i].getValue()){
                return false;
            }
        }
        return true;
    }

    public void getBitN(int n, Bit result) {// use bit.assign
        result.assign(k[n].getValue());
    }

    public void setBitN(int n, Bit source) {//  use bit.assign

        k[n].assign(source.getValue());
    }

    public void and(Word32 other, Word32 result) {
        copy(this);
        and(other, result, this);

    }

    public static void and(Word32 a, Word32 b, Word32 result) {

        for(int i = 0;i<result.k.length; i++){
            result.k[i].and(a.k[i], b.k[i]);
        }

    }

    public void or(Word32 other, Word32 result) {
        copy(this);
        or(other, result, this);
    }

    public static void or(Word32 a, Word32 b, Word32 result) {
        for(int i = 0;i<result.k.length; i++){
            result.k[i].or(a.k[i], b.k[i]);
        }
    }

    public void xor(Word32 other, Word32 result) {
        copy(this);
        xor(other, result, this);

    }

    public static void xor(Word32 a, Word32 b, Word32 result) {
        for(int i = 0;i<result.k.length; i++){
            result.k[i].xor(a.k[i], b.k[i]);
        }
    }

    public void not( Word32 result) {
        copy(this);
        not(result, this);
    }

    public static void not(Word32 a, Word32 result) {

        for(int i = 0;i<result.k.length; i++){
            result.k[i].not(a.k[i]);
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (Bit bit : k) {
            sb.append(bit.toString());
            sb.append(",");
        }
        return sb.toString();
    }
}
