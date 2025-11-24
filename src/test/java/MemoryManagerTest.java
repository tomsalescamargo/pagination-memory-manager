import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MemoryManagerTest {

    @Test
    void testInitialState() {
        MemoryManager memoryManager = new MemoryManager(64, 4, 32);

        // Verification 1: Memory should initiliaze 100% free
        assertEquals(100.0f, memoryManager.getFreeMemoryPercentage(), "Memory should initialize 100% free");

        // Verification 2: Must have 16 frames (64 / 4) on report
        assertEquals(16, memoryManager.getMemoryReport().size(), "Must have 16 frames");
    }

    @Test
    void testProcessAllocation() {
        MemoryManager memoryManager = new MemoryManager(128, 8, 32);

        memoryManager.createProcess(1, 16);

        // Verification:
        // Total frames: 16 (128 / 8). Occupied = 2. Free = 14
        // % free expected = (14/16) * 100 = 87.5%
        assertEquals(87.5f, memoryManager.getFreeMemoryPercentage(), "Free percentage should be 87.5%");
    }

    @Test
    void testAllocationTooBig() {
        MemoryManager memoryManager = new MemoryManager(64, 4, 16);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            memoryManager.createProcess(1, 20);
        });

        assertTrue(exception.getMessage().contains("exceeds maximum"));
    }

    @Test
    void testDuplicatePid() {
        MemoryManager memoryManager = new MemoryManager(64, 4, 32);

        memoryManager.createProcess(1, 20);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            memoryManager.createProcess(1, 11);
        });

        assertTrue(exception.getMessage().contains("already exists"));
    }

    @Test
    void testOutOfMemory() {
        MemoryManager memoryManager = new MemoryManager(16, 4, 16);

        memoryManager.createProcess(1, 8);
        memoryManager.createProcess(2, 8);

        // Memory must be 0% free: physicalMemorySize = 16 -> and we got 2 processes of 8
        assertEquals(0.0f, memoryManager.getFreeMemoryPercentage());

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            memoryManager.createProcess(3, 4);
        });

        assertTrue(exception.getMessage().contains("Not enough free frames"));
    }

    @Test
    void testReadInvalidLogicalAddress() {
        MemoryManager memoryManager = new MemoryManager(64, 4, 32);

        // 10 bytes process: indexes from 0 to 9
        memoryManager.createProcess(1, 10);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            memoryManager.readByLogicalAddress(1, 10);
        });

        assertTrue(exception.getMessage().contains("out of bounds"));
    }

    @Test
    void testReadValidAddress() {
        MemoryManager memoryManager = new MemoryManager(64, 4, 32);

        // 10 bytes process: indexes from 0 to 9
        memoryManager.createProcess(1, 10);

        assertDoesNotThrow(() -> {
            memoryManager.readByLogicalAddress(1, 5);
        });
    }

    @Test
    void testPageBoundaries() {
        MemoryManager memoryManager = new MemoryManager(32, 4, 32);

        // Case A: EXACT page size (4 bytes) -> Must use only 1 frame
        memoryManager.createProcess(1, 4);
        // 8 total - 1 used = 7 free. (7/8)*100 = 87.5%
        assertEquals(87.5f, memoryManager.getFreeMemoryPercentage(), "Exact frame size should occupy only 1 frame");

        // Case B: (5 bytes) -> Must use 2 frames
        // (4 bytes at first, 1 byte at second)
        memoryManager.createProcess(2, 5);

        // Proc 1 (1 frame) + Proc 2 (2 frames) = 3 frames occupied.
        // 8 total - 3 occupied = 5 free. (5/8)*100 = 62.5%
        assertEquals(62.5f, memoryManager.getFreeMemoryPercentage(), "Size page+1 must occupy 2 frames");
    }
}