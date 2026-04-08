import java.io.*;
import java.util.*;

/**
 * ===================================================
 * MAIN CLASS - UseCase12DataPersistenceRecovery
 * ===================================================
 *
 * Use Case 12: Data Persistence & System Recovery
 *
 * Description:
 * Demonstrates how system state is saved
 * and restored across application restarts.
 *
 * Inventory is loaded from a file before
 * any booking operations occur.
 *
 * @version 12.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("System Recovery");

        RoomInventory inventory = new RoomInventory();
        FilePersistenceService persistence = new FilePersistenceService();

        String filePath = "inventory.txt";

        // Load previous state
        persistence.loadInventory(inventory, filePath);

        // Show current inventory
        System.out.println("\nCurrent Inventory:");
        inventory.display();

        // Save state
        persistence.saveInventory(inventory, filePath);

        System.out.println("Inventory saved successfully.");
    }
}

/**
 * ===================================================
 * CLASS - FilePersistenceService
 * ===================================================
 *
 * Use Case 12: Data Persistence & System Recovery
 *
 * Description:
 * Responsible for saving and loading
 * inventory data from a file.
 *
 * @version 12.0
 */
class FilePersistenceService {

    /**
     * Saves room inventory to file.
     *
     * Format:
     * roomType=availableCount
     */
    public void saveInventory(RoomInventory inventory, String filePath) {

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {

            for (Map.Entry<String, Integer> entry : inventory.getAll().entrySet()) {
                writer.write(entry.getKey() + "=" + entry.getValue());
                writer.newLine();
            }

        } catch (IOException e) {
            System.out.println("Error saving inventory.");
        }
    }

    /**
     * Loads room inventory from file.
     */
    public void loadInventory(RoomInventory inventory, String filePath) {

        File file = new File(filePath);

        if (!file.exists()) {
            System.out.println("No valid inventory data found. Starting fresh.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

            String line;

            while ((line = reader.readLine()) != null) {

                String[] parts = line.split("=");

                if (parts.length == 2) {
                    String type = parts[0];
                    int count = Integer.parseInt(parts[1]);

                    inventory.set(type, count);
                }
            }

        } catch (IOException | NumberFormatException e) {
            System.out.println("Error loading inventory. Starting fresh.");
        }
    }
}

/**
 * ===================================================
 * CLASS - RoomInventory
 * ===================================================
 *
 * Description:
 * Maintains room availability.
 *
 * @version 12.0
 */
class RoomInventory {

    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();

        // Default values
        inventory.put("Single", 5);
        inventory.put("Double", 3);
        inventory.put("Suite", 2);
    }

    public Map<String, Integer> getAll() {
        return inventory;
    }

    public void set(String type, int count) {
        inventory.put(type, count);
    }

    public void display() {
        for (String key : inventory.keySet()) {
            System.out.println(key + ": " + inventory.get(key));
        }
    }
}