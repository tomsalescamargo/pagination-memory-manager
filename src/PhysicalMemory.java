public class PhysicalMemory {
    private final byte[] physicalMemory;
    private final int frameSize;


    private final int numberOfFrames;

    public PhysicalMemory(int size, int frameSize) {
        this.physicalMemory = new byte[size];
        this.frameSize = frameSize;
        this.numberOfFrames = this.calculateNumberOfFrames();
    }

    public int getAddressByFrame(int frame) {
        if (frame < 0 || frame >= this.getNumberOfFrames()) {
            throw new IllegalArgumentException("Frame out of bounds: " + frame);
        }

        return frame * this.frameSize;
    }

    public int getFrameByAddress(int physicalAddress) {
        if (physicalAddress < 0 || physicalAddress >= this.physicalMemory.length) {
            throw new IllegalArgumentException("Physical address out of bounds: " +  physicalAddress);
        }

        return physicalAddress / this.frameSize;
    }

    public boolean writeByte(int physicalAddress, byte data) {
        if (physicalAddress < 0 || physicalAddress >= this.physicalMemory.length) {
            throw new IllegalArgumentException("Physical address out of bounds: " + physicalAddress);
        }

        this.physicalMemory[physicalAddress] = data;
        return true;
    }

    public int readByte(int physicalAddress) {
        if (physicalAddress < 0 || physicalAddress >= this.physicalMemory.length) {
            throw new IllegalArgumentException("Physical address out of bounds: " + physicalAddress);
        }

        return this.physicalMemory[physicalAddress];
    }

    private int calculateNumberOfFrames() {
        return this.physicalMemory.length / this.frameSize;
    }

    public int getNumberOfFrames() {
        return numberOfFrames;
    }
}
