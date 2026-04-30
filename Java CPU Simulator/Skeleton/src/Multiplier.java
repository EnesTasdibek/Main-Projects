public class Multiplier {

    public static void multiply(Word32 a, Word32 b, Word32 result) {

        //if bk[31] is already 1, then the first one to add is already
        //all the bits of a, fill right with one 0 bit (left shift by 1)
        //then if(bk[30] == false)
        //second one to add is all zero bits
        //third one to add is if(bk[29] == true)
        //third one to add is all the same bits
        //total times to add is the size of a

        Word32 temprow1 = new Word32();
        Word32 temprow2 = new Word32();
        Word32 zeroRow = new Word32();
        //In this code, I followed the conventional multiplying pattern,
        //where you add each multiplied and get the total result


        for(int k =31;k>0;k--) {
            //other lines
            if(k<31){

                if (b.k[k].getValue() == true) {
                    a.copy(temprow2);
                    //shift zero if we are at first line
                    Shifter.LeftShift(temprow2, 31-k , temprow2);

                } else if (b.k[k].getValue() == false) {
                    zeroRow.copy(temprow2);

                }
                Adder.add(temprow1, temprow2, temprow1);
                temprow1.copy(result);

                //The reason I did this is because
                //this is the first line,
                //and I wanted my other lines below to this
            } else if (k==31) {
                if (b.k[k].getValue() == true) {
                    a.copy(temprow1);
                    //shift zero if we are at first line
                    Shifter.LeftShift(temprow1, 31-k , temprow1);
                } else if (b.k[k].getValue() == false) {
                    zeroRow.copy(temprow1);
                }
            }
        }
        temprow1.copy(result);

    }
}
