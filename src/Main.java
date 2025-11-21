import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("=== SIMULADOR DE GERENCIAMENTO DE MEMÓRIA (PAGINAÇÃO) ===");
        System.out.println("Configuração Inicial do Sistema:");

        System.out.print("Digite o tamanho da Memória Física (ex: 64, 128): ");
        int physicalSize = scanner.nextInt();
        if (!isPowerOfTwo(physicalSize)) {
            System.out.println("Erro: O tamanho da memória deve ser potência de 2 (ex: 64, 128, 256)!");
            return;
        }

        System.out.print("Digite o tamanho da Página/Quadro (ex: 4, 8): ");
        int pageSize = scanner.nextInt();
        if (!isPowerOfTwo(pageSize)) {
            System.out.println("Erro: O tamanho da página deve ser potência de 2!");
            return;
        }

        System.out.print("Digite o tamanho MÁXIMO por processo (ex: 16, 32): ");
        int maxProcessSize = scanner.nextInt();
        if (!isPowerOfTwo(maxProcessSize)) {
            System.out.println("Erro: O tamanho máximo do processo deve ser potência de 2!");
            return;
        }

        MemoryManager memoryManager = new MemoryManager(physicalSize, pageSize, maxProcessSize);

        int option = 0;
        do {
            System.out.println("\n------------------------------------------------");
            System.out.println("MENU PRINCIPAL");
            System.out.println("1. Criar Processo");
            System.out.println("2. Visualizar Memória Física (Mapa de Quadros)");
            System.out.println("3. Visualizar Tabela de Páginas de um Processo");
            System.out.println("4. Ler Endereço Lógico (Simular MMU)");
            System.out.println("0. Sair");
            System.out.print("Escolha uma opção: ");

            try {
                option = scanner.nextInt();
            } catch (Exception e) {
                scanner.nextLine();
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
                    System.out.println("Encerrando simulador...");
                    break;
                default:
                    System.out.println("Opção inválida! Tente novamente.");
            }

        } while (option != 0);

        scanner.close();
    }

    private static void createProcessUI(Scanner scanner, MemoryManager manager) {
        System.out.println("\n[CRIAR NOVO PROCESSO]");
        System.out.print("Informe o PID (ID do processo): ");
        int pid = scanner.nextInt();
        System.out.print("Informe o tamanho do processo (bytes): ");
        int size = scanner.nextInt();

        try {
            manager.createProcess(pid, size);
            System.out.println("Sucesso! Processo " + pid + " criado e alocado na memória.");
        } catch (Exception e) {
            System.out.println("ERRO ao criar processo: " + e.getMessage());
        }
    }

    private static void visualizeMemoryUI(MemoryManager manager) {
        System.out.println("\n[VISUALIZAÇÃO DA MEMÓRIA FÍSICA]");
        float freePercent = manager.getFreeMemoryPercentage();
        System.out.printf("Memória Livre: %.2f%%\n", freePercent);
        System.out.println("Mapa de Quadros:");

        List<String> report = manager.getMemoryReport();
        for (String line : report) {
            System.out.println(line);
        }
    }

    private static void visualizePagesTableUI(Scanner scanner, MemoryManager manager) {
        System.out.println("\n[VISUALIZAR TABELA DE PÁGINAS]");
        System.out.print("Informe o PID do processo: ");
        int pid = scanner.nextInt();

        try {
            String tableInfo = manager.getProcessPageTableInfo(pid);
            System.out.println(tableInfo);
        } catch (Exception e) {
            System.out.println("Erro: " + e.getMessage());
        }
    }

    private static void readLogicalAddressUI(Scanner scanner, MemoryManager manager) {
        System.out.println("\n[LER ENDEREÇO LÓGICO]");
        System.out.print("Informe o PID do processo: ");
        int pid = scanner.nextInt();

        Integer processSize = manager.getProcessSize(pid);
        if (processSize == null) {
            System.out.println("Erro: Processo não encontrado!");
            return;
        }

        System.out.print("Informe o endereço lógico (0 a " + (processSize - 1) + "): ");
        int logicalAddress = scanner.nextInt();

        try {
            byte data = manager.readByLogicalAddress(pid, logicalAddress);
            System.out.println("------------------------------------------------");
            System.out.println("Dado lido com sucesso: " + data);
            System.out.println("(O Gerenciador traduziu o endereço lógico " + logicalAddress +
                    " para o endereço físico correspondente e buscou o valor)");
        } catch (Exception e) {
            System.out.println("Erro na leitura: " + e.getMessage());
        }
    }

    private static boolean isPowerOfTwo(int n) {
        return n > 0 && Integer.bitCount(n) == 1;
    }
}