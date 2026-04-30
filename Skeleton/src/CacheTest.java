import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CacheTest {

    @Test
    public void testL1InstructionHitAndMiss() {//proves if l1 & l2 work together and checks hit/miss
        Memory mem = new Memory();
        CacheL2 l2 = new CacheL2(mem);
        InstructionCache l1 = new InstructionCache(l2);

        TestConverter.fromInt(0, l1.address);
        int missCycles = l1.read();
        assertEquals(370, missCycles, "First read should miss L1 and L2");

        TestConverter.fromInt(7, l1.address);
        int hitCycles = l1.read();
        assertEquals(10, hitCycles, "Second read should hit.");
    }

    @Test
    public void testL2DataLoadHitAndMiss() {//proves if data load works and checks hit/miss
        Memory mem = new Memory();
        CacheL2 l2 = new CacheL2(mem);

        //tested with extreme values like 200+7
        TestConverter.fromInt(200, l2.address);
        int missCycles = l2.loadData();
        assertEquals(360, missCycles, "First load should miss L2, 360 cycles.");

        TestConverter.fromInt(207, l2.address);
        int hitCycles = l2.loadData();
        assertEquals(20, hitCycles, "Second load should hit.");
    }

    @Test
    public void testL2Write() {
        //proves if write and write-through works, also checks Load()
        //and checks hit/miss
        Memory mem = new Memory();
        CacheL2 l2 = new CacheL2(mem);

        //null cache
        TestConverter.fromInt(750, l2.address);
        TestConverter.fromInt(999, l2.value);
        int checkMiss = l2.write();

        assertEquals(360, checkMiss, "Cache miss should take 360 cycles.");

        //filled cache
        //read from address 100 to pull the line into L2
        TestConverter.fromInt(100, l2.address);
        l2.loadData();

        //write to cache line
        TestConverter.fromInt(100, l2.address);
        TestConverter.fromInt(999, l2.value);
        int writeCycles = l2.write();

        assertEquals(20, writeCycles, "Cache hit should take 20 cycles.");

        //Load same address to verify the if the write was successful
        TestConverter.fromInt(100, l2.address);
        int loadCycles = l2.loadData();

        assertEquals(20, loadCycles, "Loading the data should hit the L2 Cache.");
        assertEquals(999, Memory.toInt(l2.value), "The L2 Cache should return 999.");
    }
}