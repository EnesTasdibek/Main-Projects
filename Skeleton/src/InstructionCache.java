public class InstructionCache {
    // A cache is a small amount of memory (SRAM) that is
    // close to the CPU and much faster than main memory.
    // Our caches are made up of lines – an address and 8 data items.
    // If the address is X, the data items are *X, *(X+1), *(X+2) … *(X+7).

    private Word32[] line = new Word32[9];
    //track if the cache is free
    private boolean isStart = true;
    //pc
    public Word32 address = new Word32();
    //currentInstruction
    public Word32 value = new Word32();
    private Word32 one = new Word32();
    private CacheL2 cacheL2;

public InstructionCache(CacheL2 l2){
    this.cacheL2 = l2;
    one.k[31].assign(true);
    for(int i=0;i< 9;i++){
        line[i] = new Word32();
    }
}
    //base address->data,data,data,data,data
    //value needs to be updated
    public int read() {
        int addrInt = Memory.toInt(this.address);
        int baseInt = Memory.toInt(line[0]);

        //if the address is in range(0,9)
        if (!isStart && addrInt >= baseInt && addrInt < baseInt + 8) {

                //instruction
                int offset = addrInt - baseInt +1;
                line[offset].copy(this.value);

                return 10;
            }else {

            //extract the word and return what the L2 calculated
            int l2cycle = cacheL2.read(this.address, this.line);
            isStart = false;
            int base = Memory.toInt(line[0]);
            int requested = Memory.toInt(this.address);
            int offset = requested - base + 1;
            line[offset].copy(this.value);

            return l2cycle;

            }
    }
}
