import java.util.Arrays;
import java.util.Objects;

public class Word16 {
     Bit[] k;

    public Word16()
    {
        this.k= new Bit[16];
        //I have to initialize the values and the false is default value
        //I then later change this with copy(); returning to original values after initialization
        for(int i=0; i< k.length; i++){
            k[i] = new Bit(false);
        }
    }

    public Word16(Bit[] in) {
        this();
        for(int i=0; i< k.length; i++){
            k[i].assign(in[i].getValue());
        }
    }

    public void copy(Word16 result) {// sets the values in "result" to be the same as the values in this instance; use "bit.assign"

        for (int i =0;i< result.k.length;i++){
                result.k[i].assign(k[i].getValue());
        }
    }

    public void setBitN(int n, Bit source) {
        // sets the nth bit of this word to "source"
        k[n].assign(source.getValue());
    }

    public void getBitN(int n, Bit result) {
        // sets result to be the same value as the nth bit of this word
        result.assign(k[n].getValue());
    }

    public boolean equals(Word16 other) { // is other equal to this
        return equals(this, other);
    }

    public static boolean equals(Word16 a, Word16 b) {

        for(int i=0;i<16;i++){

            if(a.k[i].getValue() != b.k[i].getValue()){
                return false;
            }
        }
        //return true if not returned any false
       return true;
    }

    public void and(Word16 other, Word16 result) {
        //this copies the values back after initializing
        //I need to add this class to result array
        copy(this);
        and( other, result, this);
    }

    public static void and(Word16 a, Word16 b, Word16 result) {
        //if 1 && 1 = 1
        //if 0 && 0 = 0

        for(int i =0; i< result.k.length;i++){
            //I am changing the original k, but have to change result
                result.k[i].and(a.k[i],b.k[i]);
        }
    }

    public void or(Word16 other, Word16 result) {
        copy(this);
        or( other, result, this);
    }

    public static void or(Word16 a, Word16 b, Word16 result) {
        for(int i =0; i< result.k.length;i++){
                result.k[i].or(a.k[i],b.k[i]);
        }
    }

    public void xor(Word16 other, Word16 result) {
        //Word16 result = new Word16();
        //usage: fw.xor(sw,result);
        //sw = other, sw2 = result, result = this

        // sets the values in "result" to be the same as the values in this instance; use "bit.assign"
        //doing this in order to revert back
        copy(this);
        xor(other, result, this);
    }

    public static void xor(Word16 a, Word16 b, Word16 result) {
        for(int i =0; i< result.k.length;i++){
                result.k[i].xor(a.k[i],b.k[i]);
        }
    }

    public void not( Word16 result) {
        copy(this);
        not(result, this);
    }

    public static void not(Word16 a, Word16 result) {
            for(int i =0; i< a.k.length;i++){
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