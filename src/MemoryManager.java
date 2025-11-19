import java.util.*;

public class MemoryManager {
    private final PhysicalMemory physicalMemory;
    private final int physicalMemorySize;
    private final int pageSize;
    private final int maxProcessSize;
    private final Queue<Integer> freeFrames = new LinkedList<>();
    private final Map<Integer, LogicalMemory> logicalMemoriesMap = new HashMap<>();
    private final Map<Integer, PagesTable> pagesTableMap = new HashMap<>();

    public MemoryManager(int physicalMemorySize, int pageSize, int maxProcessSize) {
        this.physicalMemorySize = physicalMemorySize;
        this.physicalMemory = new PhysicalMemory(physicalMemorySize, pageSize);

        this.pageSize = pageSize;
        this.maxProcessSize = maxProcessSize;

        int numberOfFrames = physicalMemory.getNumberOfFrames();
        for (int i = 0; i < numberOfFrames; i++) {
            this.freeFrames.add(i);
        }
    }

    public void createProcess(int pid, int processLength) {
        if (processLength > this.maxProcessSize) {
            throw new IllegalArgumentException(
                    "Process size exceeds maximum allowed size (" + this.maxProcessSize + ")."
            );
        }

        if (this.logicalMemoriesMap.containsKey(pid)) {
            throw new IllegalArgumentException("Process ID " + pid + " already exists.");
        }

        LogicalMemory logicalMemory = new LogicalMemory(pid, processLength, this.pageSize);
        this.logicalMemoriesMap.put(pid, logicalMemory);

        int numberOfPages = logicalMemory.getNumberOfPages();
        if (this.freeFrames.size() < numberOfPages) {
            throw new IllegalStateException("Cannot create process: Not enough free frames available.");
        }

        int[] freeFramesToBeAlocated = new int[numberOfPages];
        for (int page = 0; page < numberOfPages; page++) {
            freeFramesToBeAlocated[page] = this.freeFrames.poll();
        }

        PagesTable pagesTable = new PagesTable(numberOfPages, freeFramesToBeAlocated);
        this.pagesTableMap.put(pid, pagesTable);

        for (int page = 0; page < numberOfPages; page++) {
            int logicalAddressStart = logicalMemory.getAddressByPage(page);
            int frame = pagesTable.getFrameByPage(page);
            int frameAddressStart = physicalMemory.getAddressByFrame(frame);

            // Calculates bytes to copy: either a full page or the remainder of the last page
            int bytesToCopy = Math.min(pageSize, processLength - logicalAddressStart);
            for (int i = 0; i < bytesToCopy; i++) {
                byte data = logicalMemory.readByte(logicalAddressStart + i);
                physicalMemory.writeByte(frameAddressStart + i, data);
            }
        }
    }

    public byte readByLogicalAddress(int pid, int logicalAddress) {
        LogicalMemory logicalMemory = this.logicalMemoriesMap.get(pid);
        PagesTable pagesTable = this.pagesTableMap.get(pid);
        if (logicalMemory == null || pagesTable == null) {
            throw new IllegalArgumentException("There is no process with PID " + pid);
        }

        int logicalAddressPage = logicalMemory.getPageByAddress(logicalAddress);
        int logicalAddressOffset = logicalMemory.getOffsetByAddress(logicalAddress);

        int physicalMemoryFrame = pagesTable.getFrameByPage(logicalAddressPage);
        int physicalAddressBase = physicalMemory.getAddressByFrame(physicalMemoryFrame);
        int finalPhysicalAddress = physicalAddressBase + logicalAddressOffset;

        byte data = physicalMemory.readByte(finalPhysicalAddress);
        return data;
    }
}