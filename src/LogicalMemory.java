import java.util.Random;

public class LogicalMemory {
    private final byte[] logicalMemory;
    private final int pid;
    private final int pageSize;
    private final int numberOfPages;

    public LogicalMemory(int pid, int processLength, int pageSize) {
        this.pid = pid;
        this.logicalMemory = new byte[processLength];
        this.pageSize = pageSize;

        // Fills bytes array randomly, avoiding 0
        Random random = new Random();
        for (int i = 0; i < processLength; i++) {
            this.logicalMemory[i] = (byte) (random.nextInt(100) + 1);
        }

        this.numberOfPages = this.calculateNumberOfPages();
    }


    public int getPageByAddress(int logicalAddress) {
        if (logicalAddress < 0 || logicalAddress >= this.logicalMemory.length) {
            throw new IllegalArgumentException("Logical address out of bounds: " + logicalAddress);
        }

        return logicalAddress / this.pageSize;
    }

    public int getOffsetByAddress(int logicalAddress) {
        if (logicalAddress < 0 || logicalAddress >= this.logicalMemory.length) {
            throw new IllegalArgumentException("Logical address out of bounds: " + logicalAddress);
        }

        return logicalAddress % this.pageSize;
    }

    public int getAddressByPage(int page) {
        if (page < 0 || page >= this.getNumberOfPages()) {
            throw new IllegalArgumentException("Page number out of bounds: " + page);
        }

        return page * this.pageSize;
    }

    public byte readByte(int logicalAddress) {
        if (logicalAddress < 0 || logicalAddress >= this.logicalMemory.length) {
            throw new IllegalArgumentException("Logical address out of bounds: " + logicalAddress);
        }

        return this.logicalMemory[logicalAddress];
    }

    private int calculateNumberOfPages() {
        return (this.logicalMemory.length + pageSize - 1) / this.pageSize;
    }
    public int getNumberOfPages() {
        return numberOfPages;
    }
}
