import java.util.HashMap;
import java.util.Map;

/**
 * ==========================================================
 * CLASS - Room
 * ==========================================================
 *
 * Use Case 4: Room Search & Availability Check
 *
 * Description:
 * This class represents a room entity.
 * It stores room details like type, beds,
 * size and price.
 *
 * @version 4.0
 */

class Room {

    private String type;
    private int beds;
    private int size;
    private double price;

    public Room(String type, int beds, int size, double price) {
        this.type = type;
        this.beds = beds;
        this.size = size;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public int getBeds() {
        return beds;
    }

    public int getSize() {
        return size;
    }

    public double getPrice() {
        return price;
    }
}


/**
 * ==========================================================
 * CLASS - RoomInventory
 * ==========================================================
 *
 * Description:
 * Centralized storage of room availability.
 * Acts as read-only source during search.
 *
 * @version 4.0
 */

class RoomInventory {

    private Map<String, Integer> roomAvailability;

    public RoomInventory() {
        roomAvailability = new HashMap<>();
        initializeInventory();
    }

    private void initializeInventory() {
        roomAvailability.put("Single", 5);
        roomAvailability.put("Double", 3);
        roomAvailability.put("Suite", 2);
    }

    public Map<String, Integer> getRoomAvailability() {
        return roomAvailability;
    }
}


/**
 * ==========================================================
 * CLASS - RoomSearchService
 * ==========================================================
 *
 * Use Case 4: Room Search & Availability Check
 *
 * Description:
 * Provides read-only search functionality.
 * Displays only available rooms.
 *
 * No modification to inventory is done here.
 *
 * @version 4.0
 */

class RoomSearchService {

    public void searchAvailableRooms(
            RoomInventory inventory,
            Room singleRoom,
            Room doubleRoom,
            Room suiteRoom) {

        Map<String, Integer> availability = inventory.getRoomAvailability();

        System.out.println("Room Search\n");

        // Single Room
        if (availability.get("Single") > 0) {
            displayRoom(singleRoom, availability.get("Single"));
        }

        // Double Room
        if (availability.get("Double") > 0) {
            displayRoom(doubleRoom, availability.get("Double"));
        }

        // Suite Room
        if (availability.get("Suite") > 0) {
            displayRoom(suiteRoom, availability.get("Suite"));
        }
    }

    private void displayRoom(Room room, int available) {
        System.out.println(room.getType() + " Room:");
        System.out.println("Beds: " + room.getBeds());
        System.out.println("Size: " + room.getSize() + " sqft");
        System.out.println("Price per night: " + room.getPrice());
        System.out.println("Available: " + available);
        System.out.println();
    }
}


/**
 * ==========================================================
 * MAIN CLASS - UseCase4RoomSearch
 * ==========================================================
 *
 * Use Case 4: Room Search & Availability Check
 *
 * Description:
 * Demonstrates how users can view available rooms
 * without modifying inventory data.
 *
 * @version 4.0
 */

public class BookMyStayApp {

    public static void main(String[] args) {

        // Create Room Objects
        Room singleRoom = new Room("Single", 1, 250, 1500.0);
        Room doubleRoom = new Room("Double", 2, 400, 2500.0);
        Room suiteRoom = new Room("Suite", 3, 750, 5000.0);

        // Initialize Inventory
        RoomInventory inventory = new RoomInventory();

        // Perform Search
        RoomSearchService service = new RoomSearchService();
        service.searchAvailableRooms(inventory, singleRoom, doubleRoom, suiteRoom);
    }
}