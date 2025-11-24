import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== MEMORY MANAGEMENT SIMULATOR (PAGING) ===");
        System.out.println("System Initial Configuration:");

        System.out.print("Enter Physical Memory size (e.g., 64, 128): ");
        int physicalSize = scanner.nextInt();
        if (!isPowerOfTwo(physicalSize)) {
            System.out.println("Error: Memory size must be a power of 2 (e.g., 64, 128, 256)!");
            return;
        }

        System.out.print("Enter Page/Frame size (e.g., 4, 8): ");
        int pageSize = scanner.nextInt();
        if (!isPowerOfTwo(pageSize)) {
            System.out.println("Error: Page size must be a power of 2!");
            return;
        }

        System.out.print("Enter MAXIMUM process size (e.g., 16, 32): ");
        int maxProcessSize = scanner.nextInt();
        if (!isPowerOfTwo(maxProcessSize)) {
            System.out.println("Error: Maximum process size must be a power of 2!");
            return;
        }

        MemoryManager memoryManager = new MemoryManager(physicalSize, pageSize, maxProcessSize);

        int option = 0;
        do {
            System.out.println("\n------------------------------------------------");
            System.out.println("MAIN MENU");
            System.out.println("1. Create Process");
            System.out.println("2. View Physical Memory (Frame Map)");
            System.out.println("3. View Process Page Table");
            System.out.println("4. Read Logical Address (Simulate MMU)");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");

            try {
                option = scanner.nextInt();
            } catch (Exception e) {
                scanner.nextLine(); // Clear buffer
                option = -1;
            }

            switch (option) {
                case 1:
                    createProcessUI(scanner, memoryManager);
                    break;
                case 2:
                    visualizeMemoryUI(memoryManager);
                    break;
                case 3:
                    visualizePagesTableUI(scanner, memoryManager);
                    break;
                case 4:
                    readLogicalAddressUI(scanner, memoryManager);
                    break;
                case 0:
                    System.out.println("Exiting simulator...");
                    break;
                default:
                    System.out.println("Invalid option! Please try again.");
            }

        } while (option != 0);

        scanner.close();
    }

    private static void createProcessUI(Scanner scanner, MemoryManager manager) {
        System.out.println("\n[CREATE NEW PROCESS]");
        System.out.print("Enter PID (Process ID): ");
        int pid = scanner.nextInt();
        System.out.print("Enter process size (bytes): ");
        int size = scanner.nextInt();

        try {
            manager.createProcess(pid, size);
            System.out.println("Success! Process " + pid + " created and allocated in memory.");
        } catch (Exception e) {
            System.out.println("ERROR creating process: " + e.getMessage());
        }
    }

    private static void visualizeMemoryUI(MemoryManager manager) {
        System.out.println("\n[PHYSICAL MEMORY VISUALIZATION]");
        float freePercent = manager.getFreeMemoryPercentage();
        System.out.printf("Free Memory: %.2f%%\n", freePercent);
        System.out.println("Frame Map:");

        List<String> report = manager.getMemoryReport();
        for (String line : report) {
            System.out.println(line);
        }
    }

    private static void visualizePagesTableUI(Scanner scanner, MemoryManager manager) {
        System.out.println("\n[VIEW PAGE TABLE]");
        System.out.print("Enter Process PID: ");
        int pid = scanner.nextInt();

        try {
            String tableInfo = manager.getProcessPageTableInfo(pid);
            System.out.println(tableInfo);
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void readLogicalAddressUI(Scanner scanner, MemoryManager manager) {
        System.out.println("\n[READ LOGICAL ADDRESS]");
        System.out.print("Enter Process PID: ");
        int pid = scanner.nextInt();

        Integer processSize = manager.getProcessSize(pid);
        if (processSize == null) {
            System.out.println("Error: Process not found!");
            return;
        }

        System.out.print("Enter logical address (0 to " + (processSize - 1) + "): ");
        int logicalAddress = scanner.nextInt();

        try {
            byte data = manager.readByLogicalAddress(pid, logicalAddress);
            System.out.println("------------------------------------------------");
            System.out.println("Data successfully read: " + data);
            System.out.println("(The Manager translated logical address " + logicalAddress +
                    " to the corresponding physical address and fetched the value)");
        } catch (Exception e) {
            System.out.println("Read error: " + e.getMessage());
        }
    }

    private static boolean isPowerOfTwo(int n) {
        return n > 0 && Integer.bitCount(n) == 1;
    }
}