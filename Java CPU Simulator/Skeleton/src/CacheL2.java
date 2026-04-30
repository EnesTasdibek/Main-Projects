public class CacheL2 {

    //4 lines, l2->l1
    private Word32[][] l2Line = new Word32[4][9];
    private Word32 one = new Word32();
    private Memory mem;
    //tracks which cache line to overwrite next
    private int evictIndex = 0;

    Word32 address = new Word32();
    Word32 value = new Word32();

    //tracks if it is in initial state (is cache free)
    private boolean[] isStart = new boolean[4];

    public CacheL2(Memory m) {
        this.mem = m;
        one.k[31].assign(true);
        //init
        for (int i = 0; i < 4; i++) {
            isStart[i] = true;
            for (int j = 0; j < 9; j++) {
                l2Line[i][j] = new Word32();
            }
        }
    }
    //my target is to do this 4 times in a loop with simple eviction
    public int read(Word32 address, Word32[] line) {

        int addrInt = Memory.toInt(address);

        for(int i=0;i<4;i++){
            if(!isStart[i]) {
                int baseInt = Memory.toInt(l2Line[i][0]);
                //if the address is in range(0,9)
                if (addrInt >= baseInt && addrInt < baseInt + 8) {
                    for(int j=0; j<9;j++){
                        l2Line[i][j].copy(line[j]);
                    }
                    return 30;
                }
            }
        }

        //MISS, fetch *(X) up until *(X)+32
        //sequential eviction
        int i = evictIndex;
        evictIndex = evictIndex + 1;
        // If the pointer goes out of bounds, reset it to 0
        if (evictIndex >= 4) {
            evictIndex = 0;
        }
            //update base address line[0], from pc
            address.copy(l2Line[i][0]);
            //copy to mem.adress to mem.read
            address.copy(mem.address);

            for (int k = 1; k < 9; k++) {
                mem.read();
                //mem.value.copy(line[k]);
                mem.value.copy(l2Line[i][k]);
                //data items are *(X), *(X+1)....*(X+7)
                one.k[31].assign(true);
                Adder.add(mem.address, one, mem.address);
            }
            isStart[i] = false;

            //return first data point as instruction *(X)
            for(int j = 0; j < 9; j++) {
                l2Line[i][j].copy(line[j]);
            }
            //revert mem.address after iterating
            address.copy(mem.address);

        //10-cycle L1 check + 20-cycle L2 check + 340-cycle Main Memory fill
        return 370;//10+360
    }

    public int write() {
        int addrInt = Memory.toInt(this.address);
        int cycles = 360;

        //check for l2 hit
        for (int i = 0; i < 4; i++) {
            if (!isStart[i]) {
                int baseInt = Memory.toInt(l2Line[i][0]);

                if (addrInt >= baseInt && addrInt < baseInt + 8) {
                    int offset = addrInt - baseInt + 1;
                    this.value.copy(l2Line[i][offset]);

                    cycles = 20;//hit
                    break;
                }
            }
        }
        //write-through implementation
        this.address.copy(mem.address);
        this.value.copy(mem.value);
        mem.write();

        return cycles;//360 or 20
    }

    //used by the processor for load instructions
    //used same hit/miss costs with write()
    public int loadData() {
        int addrInt = Memory.toInt(this.address);

        //check for l2 data hit
        for (int i = 0; i < 4; i++) {
            if (!isStart[i]) {
                int baseInt = Memory.toInt(l2Line[i][0]);

                if (addrInt >= baseInt && addrInt < baseInt + 8) {

                    int offset = addrInt - baseInt + 1;
                    l2Line[i][offset].copy(this.value);
                    return 20;//hit
                }
            }
        }

        //if we missed, we must fetch the 8-word line from memory.
        //we create a temporary array to catch the data from the read() method.
        Word32[] tempLine = new Word32[9];
        for (int i = 0; i < 9; i++) {
            tempLine[i] = new Word32();
        }
        //reading from memory, and returning block to temporary line
        this.read(this.address, tempLine);
        //return *(X) as updated value of this class
        tempLine[1].copy(this.value);
        return 360; //miss penalty is 360
    }
}

