import java.util.Arrays;
import java.util.Objects;

public class Memory {
    public Word32 address = new Word32();
    public Word32 value = new Word32();

//Complete the Memory class. Initialize the memory.
// When read() is called, “address” is read, and “value” is filled in with the value from memory.
// Similarly, when write() is called, the data in “value” is written to the memory pointed to by “address”.
// Make sure you use assign(), not Java’s assignment statement. addressAsInt() is similar to the implementation of
// TestConverter with a few exceptions:
//-	Addresses are always unsigned.
//-	The range is 0-999, not +/- 2 billion.
//Load takes an array of strings in the format:
//01010101010101010101010101010101 (32 characters, either 0 or 1)
//Any other length should throw an exception.
//The first string is written to address 0, then next to address 1, etc. This is different from C’s model – in C,
// the addresses would be 0,4,8, etc.

/*
public static void toBinary(int value, Word32 result) {
    Bit t= new Bit(true);
    Bit f= new Bit(false);
    if (value >=0 ) {
        for (int i = 31; i > 0; i--) {
            result.setBitN(i,value % 2 == 0 ? f : t );
            value /=2;
        }
        result.setBitN(0, f);
    }
}
 */
    public static int toInt(Word32 value) {

        int val = 1, retVal=0;
        Bit cur = new Bit(true);
        //10 bits for range 0-999 => 1111100111
        for (int i = 31; i > 21; i--) {
            value.getBitN(i,cur);
            if (cur.getValue())
                retVal+=val;
            val *=2;
        }
        return retVal;
    }

    private final Word32[] dram= new Word32[1000];

    public int addressAsInt()  {

            return toInt(address);
    }

    public Memory() {
        //Dram has address
        //every address has word32
        //initialize dram
        for(int j=0;j<1000;j++) {

            dram[j] = new Word32();
        }

    }

    public void read() {
        //when this is called, address is read
        //and “value” is filled in with the value from memory.
        //Make sure you use assign(),

        Word32 cell = dram[addressAsInt()];
        for(int i=0;i<32;i++) {
            value.k[i].assign(cell.k[i].getValue());
        }
    }

    public void write() {
        //The first string is written to address 0, then next to address 1, etc. This is different from C’s model – in C, the addresses would be 0,4,8, etc.
        //when write() is called, the data in “value” is written to the memory pointed to by “address
        //Make sure you use assign()
        Word32 cell = dram[addressAsInt()];
        for(int i=0;i<32;i++) {
            cell.k[i].assign(value.k[i].getValue());
        }

    }

    public void load(String[] data) throws StringIndexOutOfBoundsException {

        for(int i=0;i<data.length;i++){

            Word32 cell = dram[i];

            for (int j = 0; j < 32; j++) {
                if (data[addressAsInt()].length() > 32) {
                    throw new StringIndexOutOfBoundsException(Arrays.toString(data));
                }
                //assigns true if the bit equals one
                cell.k[j].assign(data[i].charAt(j) == '1');
            }
        }
    }
}
