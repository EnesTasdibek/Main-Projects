public class Shifter {
    public static void LeftShift(Word32 source, int amount, Word32 result) {


        for(int i=0;i<32-amount;i++){


            result.k[i].assign(source.k[i+amount].getValue());
            //1001010100101 <= if shift by two to the left, left two side goes left(deleted) (two end values of array)
            //1001010100101 <= if shift by two to the right, right two side goes right(deleted) (two end values of array)

        }
        for(int i=32-amount;i<32;i++){

            //because the bits on the right (smaller than amount ) are never shifted,
            //make them false
            //for example, if you left shifted 1's, the old array position is still filled with 1's
            result.k[i].assign(false);

        }
    }

    public static void RightShift(Word32 source, int amount, Word32 result) {


        for(int i=amount;i<32;i++){

            result.k[i].assign(source.k[i-amount].getValue());
            //1001010100101 <= if shift by two to the left, left two side goes left(deleted) (two end values of array)
            //1001010100101 <= if shift by two to the right, right two side goes right(deleted) (two end values of array)
        }
        for(int i=0;i<amount;i++){

            //because the bits on the right (smaller than amount ) are shifted but never changed if they were 1.
            //make them false
            //for example, if you left shifted 1's, the old array position is still filled with 1's
            result.k[i].assign(false);

        }
    }
}
