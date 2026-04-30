import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class assemblyTests {

    //1)	Sum the numbers from 1-100 in a loop (no using Gauss’ (n(n+1))/2 formula)
    //2)	Create an array in memory of 100 integers. Then loop over then and sum them.
    //3)	Create a linked list of 100 integers. Loop over them and sum them.
    // They must all equal to 5050
    @Test
    public void testLoopSum100() {
        String[] program = {

                "copy 10 r3",//pc1      //used multiplication in order to get 100 and store in some register to compare
                "copy 10 r4",//pc1      //cannot directly do (compare 100 r1), max count is 31

                "multiply r3 r4",//pc2
                "copy 0 r0",//pc2 padding

                "copy 0 r1", //pc3      //init r1 and r2 to 0
                "copy 0 r2",//pc3       //r1 = counter, r2 = sum

                "add 1 r1 ",//pc4       //counter = counter +1;
                "add r1 r2", //pc4      //sum = counter + sum;
                "compare r4 r1",//pc5   //is counter == 100?
                "bne -1",//pc5          //jump PC -1

                "syscall 0",
                "halt"
        };
        var p = runProgram(program);
        assertEquals("r2:0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,1,1,0,1,1,1,0,1,0,", p.output.get(2));
    }

    @Test
    public void testArraySum100() {
        String[] program = {//select safe base address, fill array to memory, sum

                "copy 5 r5",//pc1
                "copy 10 r6",
                "multiply r6 r5", //pc2   //r5 = 50 (Base Address of Array)
                "copy 10 r4",
                "multiply r4 r4", //pc3   //r4 = 100 (Array Length)
                "copy 0 r2",              //offset counter = 0

                //fill array loop
                "copy r5 r8",//pc4
                "add r2 r8",             //r8 = base + offset (50+1, 50+2)..
                "copy r2 r9",//pc5
                "add 1 r9",              //r9 = value to insert (1 to 100)
                "store r9 r8",//pc6      //mem[r8] = r9
                "add 1 r2",              //offset = offset +1
                "compare r4 r2",//pc7    //is offset == 100?
                "bne -3",                //jump to PC 4

                //init variables
                "copy 0 r1", //pc8       //sum = 0
                "copy 0 r2",             //offset = 0

                //sum with loop
                "copy r5 r8", //pc9
                "add r2 r8",             //8 = Base + offset
                "load 0 r8", //pc10      //r8 = Mem[r8]
                "add r8 r1",             //sum = sum + r8
                "add 1 r2", //pc11       //offset++
                "compare r4 r2",         //is offset == 100?
                "bne -3",  //pc12        //jump to PC 9

                "syscall 0",
                "halt"
        };
        var p = runProgram(program);
        assertEquals("r1:0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,1,1,0,1,1,1,0,1,0,", p.output.get(1));
    }

    @Test
    public void testLinkedListSum100() {
        String[] program = {

                //init
                "copy 10 r5",//pc1
                "multiply r5 r5",         //r5 = 100 (head address)
                "copy r5 r8",//pc2        //current address
                "copy 10 r4",
                "multiply r4 r4",//pc3    //max 100 nodes
                "copy 0 r2",              //counter = 0

                //linked list loop
                "copy r2 r9",//pc4
                "add 1 r9",               //value = counter + 1
                "store r9 r8", //pc5      //mem[current] = Value
                "add 1 r2",               //counter = counter +1
                "compare r4 r2",//pc6     //reached 100?
                "beq 5",                  //if yes, jump to PC 11 (terminate node)
                "copy r8 r6",//pc7
                "add 2 r6",               //next address = Current + 2
                "copy r8 r7",//pc8
                "add 1 r7",               //pointer Slot = current + 1
                "store r6 r7",//pc9       //mem[current+1] = next Address
                "copy r6 r8",             //current = next
                "compare 0 r0",//pc10
                "beq -6",                 //jump back to PC 3

                //terminate last node, (null pointer)
                "copy r8 r7",//pc11
                "add 1 r7",
                "copy 0 r6", //pc12       //0 is NULL
                "store r6 r7",            //mem[last+1] = 0

                //init variables
                "copy 0 r1",  //pc13      //sum = 0
                "copy r5 r8",             //current = head

                //sum loop
                "compare 0 r8", //pc14    //is current == 0 (null)?
                "beq 6",                  //if yes, jump out to PC (14 + 6)  = 20 (End)
                "copy r8 r7",//pc15
                "load 0 r7",              //r7 = mem[current]
                "add r7 r1",//pc16        //sum = sum + Value
                "copy r8 r7",
                "add 1 r7",//pc17
                "load 0 r7",              //r7 = mem[current+1]
                "copy r7 r8",//pc18       //current = next pointer
                "compare 0 r0",
                "beq -5",//pc19           //jump back to pc 14
                "copy 0 r0",              //padding for pc

                "syscall 0",
                "halt"
        };
        var p = runProgram(program);
        assertEquals("r1:0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1,0,0,1,1,1,0,1,1,1,0,1,0,", p.output.get(1));
    }

    private static Processor runProgram(String[] program) {
        var assembled = Assembler.assemble(program);
        var merged = Assembler.finalOutput(assembled);
        var m = new Memory();
        m.load(merged);
        var p = new Processor(m);
        p.run();
        return p;
    }
}