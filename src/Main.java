import java.util.LinkedList;
import java.util.Queue;

public class Main {
    public static void main(String[] args) {
        // Inicialização
        int pageSize = 4;
        int physicalMemorySize = 64;
        PhysicalMemory physicalMemory = new PhysicalMemory(physicalMemorySize, pageSize);
        int numberOfFrames = physicalMemory.getNumberOfFrames();
        Queue<Integer> freeFrames = new LinkedList<>();
        for (int i = 0; i < numberOfFrames; i++) {
            freeFrames.add(i);
        }


        // Process creation
        int pid = 1;
        int processLength = 16;
        LogicalMemory logicalMemory = new LogicalMemory(pid, processLength, pageSize);

        int numberOfPages = logicalMemory.getNumberOfPages();
        if (freeFrames.size() < numberOfPages) {
            // throw new IllegalStateException("Cannot create process: Not enough free frames available.");
            System.out.println("Cannot create process: Not enough free frames available.");
            return;
        }

        int[] freeFramesToBeAlocated = new int[numberOfPages];
        for (int page = 0; page < numberOfPages; page++) {
            freeFramesToBeAlocated[page] = freeFrames.poll();
        }

        PagesTable pagesTable = new PagesTable(numberOfPages, freeFramesToBeAlocated);

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


        // Entry point - logical address
        int logicalAddress = 11;
        int logicalAddressPage = logicalMemory.getPageByAddress(logicalAddress);
        int logicalAddressOffset = logicalMemory.getOffsetByAddress(logicalAddress);


        int physicalMemoryFrame = pagesTable.getFrameByPage(logicalAddressPage);
        int physicalAddressBase = physicalMemory.getAddressByFrame(physicalMemoryFrame);
        int finalPhysicalAddress = physicalAddressBase + logicalAddressOffset;

        int data = physicalMemory.readByte(finalPhysicalAddress);
    }
}