import java.util.*;

/**
 * ===================================================
 * MAIN CLASS - UseCase6RoomAllocationService
 * ===================================================
 *
 * Use Case 6: Reservation Confirmation & Room Allocation
 *
 * Description:
 * This class demonstrates how booking
 * requests are confirmed and rooms
 * are allocated safely.
 *
 * It ensures:
 * - FIFO request processing
 * - Unique room assignment
 * - Immediate inventory consistency
 *
 * @version 6.0
 */
public class BookMyStayApp {

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        System.out.println("Room Allocation Processing");

        BookingRequestQueue queue = new BookingRequestQueue();

        queue.addRequest(new Reservation("Abhi", "Single"));
        queue.addRequest(new Reservation("Subha", "Single"));
        queue.addRequest(new Reservation("Vamathi", "Suite"));

        RoomAllocationService service = new RoomAllocationService();

        while (queue.hasPendingRequests()) {
            Reservation request = queue.getNextRequest();

            String roomId = service.allocateRoom(request);

            System.out.println(
                    "Booking confirmed for Guest: " +
                            request.getGuestName() +
                            ", Room ID: " + roomId
            );
        }
    }
}

/**
 * ===================================================
 * CLASS - RoomAllocationService
 * ===================================================
 *
 * Use Case 6: Reservation Confirmation & Room Allocation
 *
 * Description:
 * Responsible for confirming bookings
 * and assigning unique room IDs.
 *
 * Ensures:
 * - No duplicate room assignment
 * - Inventory consistency
 * - Safe allocation logic
 *
 * @version 6.0
 */
class RoomAllocationService {

    /** Stores all allocated room IDs */
    private Set<String> allocatedRoomIds;

    /** Maps room type to assigned room IDs */
    private Map<String, Set<String>> assignedRoomsByType;

    /** Initializes tracking structures */
    public RoomAllocationService() {
        allocatedRoomIds = new HashSet<>();
        assignedRoomsByType = new HashMap<>();
    }

    /**
     * Allocates a room for a reservation.
     *
     * @param reservation booking request
     * @return allocated room ID
     */
    public String allocateRoom(Reservation reservation) {

        String roomType = reservation.getRoomType();

        String roomId = generateRoomId(roomType);

        allocatedRoomIds.add(roomId);

        assignedRoomsByType
                .computeIfAbsent(roomType, k -> new HashSet<>())
                .add(roomId);

        return roomId;
    }

    /**
     * Generates a unique room ID.
     *
     * @param roomType type of room
     * @return unique room ID
     */
    private String generateRoomId(String roomType) {

        int count = assignedRoomsByType
                .getOrDefault(roomType, new HashSet<>())
                .size() + 1;

        String roomId = roomType + "-" + count;

        while (allocatedRoomIds.contains(roomId)) {
            count++;
            roomId = roomType + "-" + count;
        }

        return roomId;
    }
}

/**
 * ===================================================
 * CLASS - BookingRequestQueue
 * ===================================================
 *
 * Description:
 * Handles booking requests using FIFO queue.
 *
 * @version 6.0
 */
class BookingRequestQueue {

    private Queue<Reservation> requestQueue;

    public BookingRequestQueue() {
        requestQueue = new LinkedList<>();
    }

    public void addRequest(Reservation reservation) {
        requestQueue.offer(reservation);
    }

    public Reservation getNextRequest() {
        return requestQueue.poll();
    }

    public boolean hasPendingRequests() {
        return !requestQueue.isEmpty();
    }
}

/**
 * ===================================================
 * CLASS - Reservation
 * ===================================================
 *
 * Description:
 * Represents a booking request made by a guest.
 *
 * @version 6.0
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