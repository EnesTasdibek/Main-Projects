public class Adder {

    //A few small notes on multiplication
    //-	When you multiply 2 32-bit numbers, the complete answer is 64-bits. We will be ignoring the 32 high bits.
    //-	Negative numbers are not being tested for multiplication.
    //For shifting – only consider the lowest 5 bits for the amount to shift.

    //Review the slides on addition and multiplication. One thing to consider – subtraction can be implemented using addition:
    //a-b is the same as a + (-b). When you consider two’s complement, this becomes: a + (not b) + 1.
    public static void subtract(Word32 a, Word32 b, Word32 result) {

        //use twos complement, negate and add1
        //a + (not b) + 1.
        //(not b)
        b.not(b);
        //1
        Word32 one = new Word32();
        one.k[31].assign(true);
        //add
        add(a, b,result);
        //+1
        add(result, one, result);

    }

    public static void add(Word32 a, Word32 b, Word32 result) {

        //Let:
        //a = running addition result
        //b = carries
        Word32 carry = new Word32();

        //1)Find carries
        //2)Do the addition
        //3)b holds left-shifted carry

        boolean hasfalse = false;
        boolean returned = false;
        for(int i=0;i<32;i++) {
        //while(!returned){

            //And is carry, store in carry
            //a and left shifted carry into the carry
            a.and(b, carry);

            //XOR is the addition and store in a
            a.xor(b, a);

            Shifter.LeftShift(carry, 1, carry);
            carry.copy(b);
            //b.copy(carry);

            for(int j =0;j<32;j++){
                if(carry.k[j].getValue() == false){
                    hasfalse = true;
                }
                //update a and b after xor
                //this is for the use of next round
            }
            if(hasfalse == false){
                a.copy(result);
                return;
            }
        }

        for(int j =0;j<32;j++){
            if(carry.k[j].getValue() == false){
                hasfalse = true;
            }
            //update a and b after xor
            //this is for the use of next round
        }
        if(hasfalse == false){

            a.copy(result);
            return;
        }

        a.copy(result);
        return;

    }
}
