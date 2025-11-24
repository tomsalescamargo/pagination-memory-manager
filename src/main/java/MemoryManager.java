import java.util.*;

public class MemoryManager {
    private final PhysicalMemory physicalMemory;
    private final int physicalMemorySize;
    private final int pageSize;
    private final int maxProcessSize;
    private final Queue<Integer> freeFrames = new LinkedList<>();
    private final Map<Integer, LogicalMemory> logicalMemoriesMap = new HashMap<>();
    private final Map<Integer, PagesTable> pagesTableMap = new HashMap<>();
    private final FrameState[] framesState; // "Frame Table"

    public MemoryManager(int physicalMemorySize, int pageSize, int maxProcessSize) {
        this.physicalMemorySize = physicalMemorySize;
        this.physicalMemory = new PhysicalMemory(physicalMemorySize, pageSize);

        this.pageSize = pageSize;
        this.maxProcessSize = maxProcessSize;

        int numberOfFrames = physicalMemory.getNumberOfFrames();
        this.framesState = new FrameState[numberOfFrames];
        for (int i = 0; i < numberOfFrames; i++) {
            this.freeFrames.add(i);
            this.framesState[i] = new FrameState(i);
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
            this.logicalMemoriesMap.remove(pid);
            throw new IllegalStateException("Cannot create process: Not enough free frames available.");
        }

        int[] allocatedFrames = new int[numberOfPages];
        for (int page = 0; page < numberOfPages; page++) {
            int frameIndex = this.freeFrames.poll();
            allocatedFrames[page] = frameIndex;
            this.framesState[frameIndex].allocate(pid);
        }

        PagesTable pagesTable = new PagesTable(numberOfPages, allocatedFrames);
        this.pagesTableMap.put(pid, pagesTable);

        for (int page = 0; page < numberOfPages; page++) {
            int logicalAddressStart = logicalMemory.getAddressByPage(page);
            int frame = pagesTable.getFrameByPage(page);
            int frameAddressStart = physicalMemory.getAddressByFrame(frame);

            // Calculates how many bytes of the process should be copied into this frame:
            // either a full page or the remaining bytes of the last page.
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

    public String getProcessPageTableInfo(int pid) {
        if (!this.pagesTableMap.containsKey(pid)) {
            throw new IllegalArgumentException("Process ID " + pid + " not found.");
        }

        PagesTable pagesTable = this.pagesTableMap.get(pid);
        LogicalMemory logicalMemory = this.logicalMemoriesMap.get(pid);

        StringBuilder sb = new StringBuilder();
        sb.append("PID: ").append(pid).append("\n");
        sb.append("Total Size: ").append(this.getProcessSize(pid)).append(" bytes\n");
        sb.append("Page Table:\n");
        sb.append("Page    |   Frame\n");
        sb.append("------- | -------\n");

        int numPages = logicalMemory.getNumberOfPages();
        for (int i = 0; i < numPages; i++) {
            int frame = pagesTable.getFrameByPage(i);
            sb.append(String.format("   %d    |    %d\n", i, frame));
        }

        return sb.toString();
    }

    public List<String> getMemoryReport() {
        List<String> report = new ArrayList<>();
        int numberOfFrames = this.physicalMemory.getNumberOfFrames();

        for (int frame = 0; frame < numberOfFrames; frame++) {
            FrameState frameState = this.framesState[frame];

            String frameOwner = (frameState.isFree) ? "[FREE]" : "[PROCESS " + frameState.pidOwner + "]";
            String frameContent = this.getFrameContentString(frame);

            // Uses StringBuilder to reduce string concatenation overhead.
            StringBuilder sb = new StringBuilder();
            sb.append("Frame ")
                    .append(frameState.frameNumber)
                    .append(": ")
                    .append(frameOwner)
                    .append(" -> ")
                    .append(frameContent);

            report.add(sb.toString());
        }
        return report;
    }

    private String getFrameContentString(int frameIndex) {
        int addressStart = this.physicalMemory.getAddressByFrame(frameIndex);

        StringBuilder sb = new StringBuilder("[");

        for (int i = 0; i < this.pageSize; i++) {
            byte data = physicalMemory.readByte(addressStart + i);
            sb.append(data);

            if (i != this.pageSize - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public float getFreeMemoryPercentage() {
        return ((float) this.freeFrames.size() / this.physicalMemory.getNumberOfFrames()) * 100;
    }

    public Integer getProcessSize(int pid) {
        LogicalMemory logicalMemory = this.logicalMemoriesMap.get(pid);
        if (logicalMemory == null) return null;
        return logicalMemory.getSize();
    }

    public static class FrameState {
        int frameNumber;
        boolean isFree;
        int pidOwner; // -1 indicates that the frame is free (no owner)

        public FrameState(int frameNumber) {
            this.frameNumber = frameNumber;
            this.isFree = true;
            this.pidOwner = -1;
        }

        public void allocate(int pid) {
            this.isFree = false;
            this.pidOwner = pid;
        }

        public void release() {
            this.isFree = true;
            this.pidOwner = -1;
        }
    }
}
