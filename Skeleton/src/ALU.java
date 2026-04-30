import java.util.concurrent.TimeoutException;

public class ALU {
    public Word16 instruction = new Word16();
    public Word32 op1 = new Word32();
    public Word32 op2 = new Word32();
    public Word32 result = new Word32();
    public Bit less = new Bit(false);
    public Bit equal = new Bit(false);

    //The ALU is similar to memory in design.
    // The user will set the current instruction and the two operands (op1, op2),
    // then call doInstruction(). This will update “result”, “less” and “equal” based on
    // the information in the current instruction. Your ALU must use the adder, shifter and
    // multiplier components that we built previously. You may not convert op1 and op2 to integer.
    // You will need to do some bit integer conversions for smaller ranges in the ALU.
    //Note that some of the instructions in the SIA Spring2026 document require addition
    // (the branches, for example, to calculate the branch destination).
    // Your ALU should accept those opcodes for addition as well as “add”.
    // Making sure that the op1 and op2 are set correctly will be the processor’s job.
    // Note that compare is more work than add/subtract/multiply/shift.
    public void doInstruction() {

        //instructions are 5 bits
        Word16 OP_One_add = new Word16();
        OP_One_add.k[4].assign(true);
        //00001

        Word16 OP_Two_And = new Word16();
        OP_Two_And.k[3].assign(true);
        //00010

        Word16 OP_Three_Multiply = new Word16();
        OP_Three_Multiply.k[3].assign(true);
        OP_Three_Multiply.k[4].assign(true);
        //00011

        Word16 OP_Four_LS= new Word16();
        OP_Four_LS.k[2].assign(true);
        //00100
        Word16 OP_Five_LS= new Word16();
        OP_Five_LS.k[2].assign(true);
        OP_Five_LS.k[4].assign(true);
        //00101

        Word16 OP_Six_LS= new Word16();
        OP_Six_LS.k[2].assign(true);
        OP_Six_LS.k[3].assign(true);
        //00110

        Word16 OP_Seven_LS= new Word16();
        OP_Seven_LS.k[2].assign(true);
        OP_Seven_LS.k[3].assign(true);
        OP_Seven_LS.k[4].assign(true);

        Word16 OP_Eleven_LS= new Word16();
        OP_Eleven_LS.k[1].assign(true);
        OP_Eleven_LS.k[3].assign(true);
        OP_Eleven_LS.k[4].assign(true);
        //01011

        if (instruction.equals(OP_One_add)) {
            Adder.add(op1, op2, result);
        }

        else if (instruction.equals(OP_Two_And)) {
            op1.and(op2, result);
        } else if (instruction.equals(OP_Three_Multiply)) {
            Multiplier.multiply(op1,op2,result);
        }//I turned into integer with my implementation from memory class, for smaller ranges
        else if (instruction.equals(OP_Four_LS)) {
            Shifter.LeftShift(op1, Memory.toInt(op2), result);
        }
        else if(instruction.equals(OP_Five_LS)){

            Adder.subtract(op1, op2, result);
        }
        else if(instruction.equals(OP_Six_LS)){

            op1.or(op2,result);
        }
        else if(instruction.equals(OP_Seven_LS)){

            Shifter.RightShift(op1,Memory.toInt(op2), result);
        }
        //compare
        else if(instruction.equals(OP_Eleven_LS)){

             if(op1.equals(op2)){
                equal.assign(true);
                less.assign(false);

            }
            else{
                //compare the leftmost bits
                for(int i=0;i<32;i++){


                   if(op1.k[i].getValue() == true && op2.k[i].getValue() == false){
                       equal.assign(false);
                       less.assign(false);
                       return;
                   } else if (op1.k[i].getValue() == false && op2.k[i].getValue() == true) {
                       equal.assign(false);
                       less.assign(true);
                       return;
                   }


                }

             }
        }
    }
}
