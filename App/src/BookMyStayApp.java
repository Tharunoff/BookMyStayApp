import java.util.*;

/**
 * ===================================================
 * MAIN CLASS - UseCase11ConcurrentBookingSimulation
 * ===================================================
 *
 * Use Case 11: Concurrent Booking Simulation
 *
 * Description:
 * Simulates multiple users booking rooms
 * at the same time using threads.
 *
 * Demonstrates synchronization to prevent
 * inconsistent allocations.
 *
 * @version 11.0
 */
public class BookMyStayApp {

    public static void main(String[] args) {

        System.out.println("Concurrent Booking Simulation");

        // Shared resources
        BookingRequestQueue bookingQueue = new BookingRequestQueue();
        RoomInventory inventory = new RoomInventory();
        RoomAllocationService allocationService = new RoomAllocationService();

        // Add booking requests
        bookingQueue.addRequest(new Reservation("Abhi", "Single"));
        bookingQueue.addRequest(new Reservation("Subha", "Single"));
        bookingQueue.addRequest(new Reservation("Vamathi", "Double"));
        bookingQueue.addRequest(new Reservation("Kunal", "Suite"));

        // Create threads
        Thread t1 = new Thread(
                new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService)
        );

        Thread t2 = new Thread(
                new ConcurrentBookingProcessor(bookingQueue, inventory, allocationService)
        );

        // Start threads
        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            System.out.println("Thread execution interrupted.");
        }

        // Show remaining inventory
        System.out.println("\nRemaining Inventory:");
        inventory.display();
    }
}

/**
 * ===================================================
 * CLASS - ConcurrentBookingProcessor
 * ===================================================
 *
 * Description:
 * Processes booking requests using threads.
 *
 * Ensures thread-safe access to shared resources.
 *
 * @version 11.0
 */
class ConcurrentBookingProcessor implements Runnable {

    private BookingRequestQueue bookingQueue;
    private RoomInventory inventory;
    private RoomAllocationService allocationService;

    public ConcurrentBookingProcessor(
            BookingRequestQueue bookingQueue,
            RoomInventory inventory,
            RoomAllocationService allocationService
    ) {
        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
        this.allocationService = allocationService;
    }

    @Override
    public void run() {

        while (true) {

            Reservation reservation;

            // Critical section: queue access
            synchronized (bookingQueue) {
                if (!bookingQueue.hasPendingRequests()) {
                    break;
                }
                reservation = bookingQueue.getNextRequest();
            }

            // Critical section: inventory + allocation
            synchronized (inventory) {
                String roomId = allocationService.allocateRoom(reservation, inventory);

                if (roomId != null) {
                    System.out.println(
                            "Booking confirmed for Guest: " +
                                    reservation.getGuestName() +
                                    ", Room ID: " + roomId
                    );
                } else {
                    System.out.println(
                            "Booking failed for Guest: " +
                                    reservation.getGuestName()
                    );
                }
            }
        }
    }
}

/**
 * ===================================================
 * CLASS - RoomAllocationService
 * ===================================================
 */
class RoomAllocationService {

    private Map<String, Integer> counters = new HashMap<>();

    public String allocateRoom(Reservation reservation, RoomInventory inventory) {

        String type = reservation.getRoomType();

        if (!inventory.isAvailable(type)) {
            return null;
        }

        inventory.decrement(type);

        int count = counters.getOrDefault(type, 0) + 1;
        counters.put(type, count);

        return type + "-" + count;
    }
}

/**
 * ===================================================
 * CLASS - RoomInventory
 * ===================================================
 */
class RoomInventory {

    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();
        inventory.put("Single", 2);
        inventory.put("Double", 2);
        inventory.put("Suite", 1);
    }

    public boolean isAvailable(String type) {
        return inventory.getOrDefault(type, 0) > 0;
    }

    public void decrement(String type) {
        inventory.put(type, inventory.get(type) - 1);
    }

    public void display() {
        for (String key : inventory.keySet()) {
            System.out.println(key + ": " + inventory.get(key));
        }
    }
}

/**
 * ===================================================
 * CLASS - BookingRequestQueue
 * ===================================================
 */
class BookingRequestQueue {

    private Queue<Reservation> queue = new LinkedList<>();

    public void addRequest(Reservation r) {
        queue.offer(r);
    }

    public Reservation getNextRequest() {
        return queue.poll();
    }

    public boolean hasPendingRequests() {
        return !queue.isEmpty();
    }
}

/**
 * ===================================================
 * CLASS - Reservation
 * ===================================================
 */
class Reservation {

    private String guestName;
    private String roomType;

    public Reservation(String guestName, String roomType) {
        this.guestName = guestName;
        this.roomType = roomType;
    }

    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
}