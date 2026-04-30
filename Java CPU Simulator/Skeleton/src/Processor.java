import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;


public class Processor {

    private int currentClockCycle = 0;

    private Memory mem;
    public List<String> output = new LinkedList<>();

    private Word16 topHalf = new Word16();
    private Word16 bottomHalf = new Word16();

    private Word32 result = new Word32();
    private Word32 op1 = new Word32();//like r1 and r2
    private Word32 op2 = new Word32();

    //checks if started
    private boolean isStart = true;
    //checks if bottomHalf is fetched and ready to fetch next cycle
    private boolean fetchedSecond = false;
    private boolean isTopHalf = false;
    private Word16 currentOPcode = new Word16();

    //all registers are 32 bits wide
    private Word32[] registers = new Word32[32];

    //to increment pc
    private Word32 one = new Word32();
    private Word32 two = new Word32();

    //branch logic
    private boolean isHalted = false;
    private boolean isEqual = false;
    private boolean isLess = false;
    private boolean isGreater = false;
    private boolean branchTaken = false;

    private Word32 pc = new Word32();
    private Word32 currentInstruction = new Word32();

    private Stack<Word32> stack = new Stack<>();
    private ALU al = new ALU();
    private CacheL2 cachel2;
    private InstructionCache instructionCache;

    public Processor(Memory m) {
        mem = m;
        //init
        for(int i=0;i< registers.length;i++){
            registers[i] = new Word32();
        }
        two.k[30].assign(true);
        one.k[31].assign(true);

        //cache uses ram
        cachel2 = new CacheL2(mem);
        //IC uses l2
        instructionCache = new InstructionCache(cachel2);
    }
    //get values to int
    public static int getRegister(Word16 value, int start, int finish){
        int val = 1, retVal=0;
        Bit cur = new Bit(true);

        for (int i = finish; i >= start; i--) {
            value.getBitN(i,cur);
            if (cur.getValue())
                retVal+=val;
            val *=2;
        }
        return retVal;
    }

    public static Word32 signExtend(Word16 instruction, Word32 value){
        //make a copy of the bit before doing this
        //get the 5 bits first 6-10
        //check leftmost bit, if one, fill all left ones vice versa (until 32)
        //fill left 27
        if(instruction.k[6].getValue()){


            for(int i=0;i<27;i++ ){
                value.k[i].assign(true);
            }
            //assign 5 bits back to 32 bit
            for(int i=27;i<32;i++ ){
                value.k[i].assign(instruction.k[i-16-5].getValue());
            }
        }
        else if (!instruction.k[6].getValue()) {


            for(int i=0;i<27;i++ ){
                value.k[i].assign(false);
            }
            //assign whatever 5 bits back to 32 bit
            for(int i=27;i<32;i++ ){
                value.k[i].assign(instruction.k[i-16-5].getValue());
            }
        }
        return value;
    }

    public static Word32 signExtend11(Word16 instruction, Word32 value) {
        //make a copy of the bit before doing this
        //get the 5 bits first 6-10


        //check leftmost bit, if one, fill all left ones vice versa (until 32)
        //fill left 21
        if(instruction.k[5].getValue()){

            for(int i=0;i<21;i++ ){
                value.k[i].assign(true);
            }
            //asssign 11 bits back to 32 bit
            for(int i=21;i<32;i++ ){
                value.k[i].assign(instruction.k[i-16].getValue());
            }
        }
        else if (!instruction.k[5].getValue()) {


            for(int i=0;i<21;i++ ){
                value.k[i].assign(false);
            }
            //assign whatever 11 bits back to 32 bit
            for(int i=21;i<32;i++ ){
                value.k[i].assign(instruction.k[i-16].getValue());
            }
        }
        return value;
    }
    public void run() {
        while(isHalted==false){
            fetch();
            decode();
            execute();
            store();
        }
        System.out.println("Clock Cycle: " + currentClockCycle);
    }
    public void extractCode(Word16 extractedOPCode, Word16 instruction){
        for(int i=0;i<5;i++) {
            extractedOPCode.k[i].assign(instruction.k[i].getValue());
        }
    }

    private void fetch() {
        // We process one instruction at a time
        // We keep the second instruction for later use
        // After 2 cycles, we are done and ready to fetch other two instructions
        // Fetch reads an instruction from memory. Ours is a little tricky, because a single read() gives us 2 instructions.

        //isStart is just used for the topHalf fetch in start
        if(fetchedSecond||isStart){
            //non-cache implementation
            //pc.copy(mem.address);
            //mem.read();
            //mem.value.copy(currentInstruction);
            //currentClockCycle += 300;

            //structure: l2->l1
            //l2 & l1 implementation
            pc.copy(instructionCache.address);
            int cycles = instructionCache.read();
            instructionCache.value.copy(currentInstruction);
            currentClockCycle += cycles;

            currentInstruction.getTopHalf(topHalf);
            //topHalf is left side
            topHalf.copy(currentOPcode);


            isTopHalf = true;
            isStart=false;


        } else if(fetchedSecond==false) {
            currentInstruction.getBottomHalf(bottomHalf);
            bottomHalf.copy(currentOPcode);
            isTopHalf = false;
            isStart=false;
        }
    }

    private void decode() {
        int opcode = getRegister(currentOPcode, 0, 4);


        if (isTopHalf) {
            fetchedSecond = false;
        } else {
            fetchedSecond = true;
        }

        if ((opcode >= 8 && opcode <= 10) || (opcode >= 12 && opcode <= 17)) {
            signExtend11(currentOPcode, op1);
        }
        else if (!currentOPcode.k[5].getValue()) {
            // 2R Format (6th bit is 0)
            // op1 = Source Register, op2 = Destination Register
            registers[getRegister(currentOPcode, 6, 10)].copy(op1);
            registers[getRegister(currentOPcode, 11, 15)].copy(op2);
        }
        else {
            // Immediate Format (6th bit is 1)
            // op1 = Sign-extended Immediate, op2 = Destination Register
            Word16 temp16 = new Word16();
            Word32 temp32 = new Word32();

            currentOPcode.copy(temp16);
            signExtend(temp16, temp32).copy(op1);
            registers[getRegister(currentOPcode, 11, 15)].copy(op2);
        }
    }

    private void execute() {

        int operation = getRegister(currentOPcode, 0, 4);
        Word16 tempInstruction = new Word16();
        extractCode(tempInstruction, currentOPcode);

        switch (operation){
            case 0:
                isHalted = true;
                break;
            case 1:
                tempInstruction.copy(al.instruction);
                op1.copy(al.op1);
                op2.copy(al.op2);
                al.doInstruction();
                al.result.copy(result);
                //Adder.add(op1, op2, result);

                currentClockCycle += 2;
                break;
            case 2:
                tempInstruction.copy(al.instruction);
                op1.copy(al.op1);
                op2.copy(al.op2);
                al.doInstruction();
                al.result.copy(result);
                //Word32.and(op1,op2, result);
                currentClockCycle += 2;
                break;
            case 3:
                tempInstruction.copy(al.instruction);
                op1.copy(al.op1);
                op2.copy(al.op2);
                al.doInstruction();
                al.result.copy(result);
                //Multiplier.multiply(op1, op2, result);
                currentClockCycle += 10;
                break;
            case 4:
                tempInstruction.copy(al.instruction);
                op2.copy(al.op1);
                op1.copy(al.op2);
                al.doInstruction();
                al.result.copy(result);
                //Shifter.LeftShift(op2, TestConverter.toInt(op1), result);
                currentClockCycle += 2;
                break;
            case 5:
                tempInstruction.copy(al.instruction);
                op2.copy(al.op1);
                op1.copy(al.op2);
                al.doInstruction();
                al.result.copy(result);
                //Adder.subtract(op2, op1, result);
                currentClockCycle += 2;
                break;
            case 6:
                tempInstruction.copy(al.instruction);
                op1.copy(al.op1);
                op2.copy(al.op2);
                al.doInstruction();
                al.result.copy(result);
                //Word32.or(op1,op2,result);
                currentClockCycle += 2;
                break;
            case 7:
                tempInstruction.copy(al.instruction);
                op2.copy(al.op1);
                op1.copy(al.op2);
                al.doInstruction();
                al.result.copy(result);
                //Shifter.RightShift(op2,TestConverter.toInt(op1), result);
                currentClockCycle += 2;
                break;
            case 8://syscall- Switches to kernel mode and calls kernel function (unsigned) immediate
                //Format: Call/Return, example: = syscall 0
                int immediate = getRegister(currentOPcode, 5, 15);
                if (immediate == 0){ printReg();}
                else if (immediate == 1){ printMem();}
                break;
            case 9://call
                Word32 returnAddress = new Word32();
                Word32 tempPC = new Word32();
                pc.copy(tempPC);
                one.k[31].assign(true);
                Adder.add(tempPC, one, returnAddress);
                stack.push(returnAddress);
                Adder.add(pc, op1, pc);
                branchTaken = true;
                isStart = true;

                currentClockCycle += 2 + 2;
                break;
            case 10://return
                //Pops from the stack and sets the PC to the popped value.
                //Format: Call/Return (Immediate value is unused)
                stack.pop().copy(pc);
                branchTaken = true;
                fetchedSecond = true;
                break;
            case 11://compare
                //Compares Source to destination, setting the status register flags
                tempInstruction.copy(al.instruction);
                op1.copy(al.op1);
                op2.copy(al.op2);
                al.doInstruction();
                al.result.copy(result);

                isEqual = al.equal.getValue();
                isLess= al.less.getValue();
                isGreater = !al.less.getValue() && !al.equal.getValue();
                currentClockCycle += 2;
                break;
            case 12: // BLE (Branch if Less OR Equal), PC = PC + Immediate
                if (isLess || isEqual) {
                    Adder.add(pc, op1, pc);
                    branchTaken = true;
                    fetchedSecond = true;
                    currentClockCycle += 2;
                }
                break;
            case 13: // BLT (Branch if Less), PC = PC + Immediate
                if (isLess) {
                    Adder.add(pc, op1, pc);
                    branchTaken = true;
                    fetchedSecond = true;
                    currentClockCycle += 2;
                }
                break;
            case 14: // BGE (Branch if Greater OR Equal), PC = PC + Immediate
                if (isGreater || isEqual) {
                    Adder.add(pc, op1, pc);
                    branchTaken = true;
                    fetchedSecond = true;
                    currentClockCycle += 2;
                }
                break;
            case 15: // BGT (Branch if Greater), PC = PC + Immediate
                if (isGreater) {
                    Adder.add(pc, op1, pc);
                    branchTaken = true;
                    fetchedSecond = true;
                    currentClockCycle += 2;
                }
                break;
            case 16: // BEQ (Branch if Equal), PC = PC + Immediate
                if (isEqual) {
                    Adder.add(pc, op1, pc);
                    branchTaken = true;
                    fetchedSecond = true;
                    currentClockCycle += 2;
                }
                break;
            case 17: // BNE (Branch if NOT Equal), PC = PC + Immediate
                if (!isEqual) {
                    Adder.add(pc, op1, pc);
                    branchTaken = true;
                    // Force to next cycle
                    fetchedSecond = true;
                    currentClockCycle += 2;
                }
                break;
            case 18:// Load (2 variations)
                if (currentOPcode.k[5].getValue()) {
                    //Loads from memory address (immediate + destination) into destination register
                    //(immediate)
                    Adder.add(op1, op2, cachel2.address);
                    //Fetch memory
                    //mem.read();
                    int cacheCycles = cachel2.loadData();
                    //Copy the fetched data into the into destination register.
                    cachel2.value.copy(result);
                    currentClockCycle += cacheCycles + 2;
                } else {
                    //Loads from memory address source into destination (2R)
                    op1.copy(cachel2.address);
                    //mem.read();
                    int cacheCycles = cachel2.loadData();
                    cachel2.value.copy(result);

                    currentClockCycle += cacheCycles;
                }
                break;
            case 19://Store
                // Stores into memory address destination from source
                // Examples: store r3 r9 ,store 0 r9
                //l2 implememntatiomn
                op2.copy(cachel2.address);
                op1.copy(cachel2.value);

                //skip mem.write because we call it form cachel2
                //mem.write();

                int cacheCycle = cachel2.write();
                currentClockCycle += cacheCycle;

                break;
            case 20://Copy
                //Copies source value into destination
                op1.copy(result);
                break;
            default:
                break;
        }
    }

    private void printReg() {
        for (int i = 0; i < 32; i++) {
            var line = "r"+ i + ":" + registers[i];
            output.add(line);
            System.out.println(line);
        }
    }

    private void printMem() {
        for (int i = 0; i < 1000; i++) {
            Word32 addr = new Word32();
            Word32 value = new Word32();
            // Convert i to Word32 here...
            TestConverter.fromInt(i, addr);
            addr.copy(mem.address);
            mem.read();
            mem.value.copy(value);
            var line = i + ":" + value + "(" + TestConverter.toInt(value) + ")";
            output.add(line);
            System.out.println(line);
        }
    }

    private void store() {

        int operation = getRegister(currentOPcode, 0, 4);
        // Skip write back for 0,8-17(branches), 19
        if (operation != 0  && operation != 19 && !(operation >= 8 && operation <= 17)) {
            int dest = getRegister(currentOPcode, 11, 15);
            result.copy(registers[dest]);
        }
        if(fetchedSecond){
            if (!branchTaken) {
                one.k[31].assign(true);
                Adder.add(pc, one, pc);
            }
            branchTaken = false;
        }
    }
}