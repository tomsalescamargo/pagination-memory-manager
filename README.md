# 🧠 Memory Management Simulator (Pagination)

A robust, CLI-based simulator implemented in **Java** that demonstrates how Operating Systems manage memory using **Pagination**. 

This project simulates low-level memory operations including logical-to-physical address translation (MMU), handling page tables, managing free frames, and visualizing internal fragmentation.

---

## 🚀 Key Features

* **Configurable System:** Custom setup for Physical Memory size, Page/Frame size, and Process limits (validating Power of 2).
* **Non-Contiguous Allocation:** Simulates how OS allocates pages to scattered physical frames.
* **MMU Simulation:** Translates Logical Addresses to Physical Addresses in real-time.
* **Visual Reports:** * Physical Memory Map (showing specific byte content and ownership).
    * Page Tables per process.
    * Free memory percentage calculation.
* **Error Handling:** Robust validation for "Out of Memory", "Invalid PID", and "Segmentation Faults" (out of bounds access).

---

## 🛠 Tech Stack & Tools

* **Language:** Java 17
* **Build Tool:** Maven (Standard directory structure & dependency management)
* **Testing:** JUnit 5 (Jupiter)
* **Architecture:** Object-Oriented Design (Separation of concerns between Hardware, Kernel, and User Space).

---

## 🏗 Architecture Overview

The project is structured to mimic real OS components:

* **`MemoryManager` (The Kernel):** The core class. It manages the `Frame Table`, tracks free frames using a `Queue` (FIFO), and handles the allocation logic. It enforces boundaries and permissions.
* **`PhysicalMemory` (The Hardware):** Represents the RAM as a raw byte array. It has no logic, strictly performing read/write operations when instructed.
* **`LogicalMemory` (The Process):** Represents a program. It generates random data (avoiding zero) to simulate process content.
* **`PagesTable` (The Map):** Stores the mapping between Logical Pages and Physical Frames.
* **`FrameState`:** An internal class (similar to a Core Map in Linux) to track the status (Free/Occupied) and ownership of every physical frame.

---

## 📸 Usage CLI

### 1. Physical Memory Visualization
The system provides a byte-level view of the RAM. You can see:
* **Allocated Frames:** Showing the Process ID and the actual data bytes.
* **Internal Fragmentation:** Note the trailing `0`s in the last frame of a process if it doesn't fill the entire page.
* **Free Frames:** Marked as `[FREE]`.

<img width="889" height="538" alt="view-physical-memory" src="https://github.com/user-attachments/assets/fe1cbc62-7c24-4ac1-9f5b-f47819a1307c" />


### 2. Process Creation
When a process is created, the system calculates the required pages and finds available frames.
*(Example: Creating a process with PID 3 and size 40 bytes)*

<img width="513" height="193" alt="create-process" src="https://github.com/user-attachments/assets/66f387ef-d866-474e-acc2-f6d44847df0d" />


### 3. Page Tables
Users can inspect the specific Page Table for any active process to see the mapping logic.

<img width="336" height="297" alt="page-table-p3" src="https://github.com/user-attachments/assets/4a38454e-c16c-4aab-af8c-5e8749765705" />


### 4. MMU Simulation (Address Translation)
This feature simulates the hardware MMU. The user provides a **Logical Address** (e.g., index 25 of the process), and the system:
1.  Calculates the Page Number.
2.  Looks up the Frame Number in the Page Table.
3.  Calculates the Offset.
4.  Retrieves the actual data from Physical Memory.

<img width="997" height="406" alt="read-logical" src="https://github.com/user-attachments/assets/2bc24164-a663-44f6-8bcb-660e80429af3" />


---

## 🧪 Testing & Reliability

Reliability was a priority. The project uses **JUnit 5** to ensure the logic holds up under stress. The test suite covers:

* **Happy Path:** Standard allocation and reading.
* **Boundary Testing:** Verifying allocation when process size is exactly the page size vs. page size + 1 byte.
* **Out of Memory:** Ensuring the system gracefully rejects processes when RAM is full.
* **Security:** Preventing access to invalid logical addresses (simulating Segmentation Faults).

Run the tests using Maven:
```bash
mvn test
```

---

## 🧠 Learning Outcomes

Developing this simulator provided deep insights into:

1.  **OS Fundamentals:** Understanding why Pagination solves external fragmentation but introduces internal fragmentation.
2.  **Bitwise Logic:** Validating inputs using bitwise operations to ensure Power of 2 compliance.
3.  **Project Structure:** Moving from simple file management to a standardized **Maven** structure.
4.  **Unit Testing:** Writing assertive tests to validate mathematical logic in memory allocation.

---

## 🏃‍♂️ How to Run

1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/tomsalescamargo/pagination-memory-manager.git](https://github.com/tomsalescamargo/pagination-memory-manager.git)
    ```

2.  **Build with Maven:**
    ```bash
    mvn clean install
    ```

3.  **Run the Main application:**
    You can run the `Main` class directly from your IDE (IntelliJ/Eclipse) or via command line:
    ```bash
    java -cp target/classes Main
    ```
