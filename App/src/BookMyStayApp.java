import java.util.*;

/**
 * ===================================================
 * MAIN CLASS - UseCase10BookingCancellation
 * ===================================================
 *
 * Use Case 10: Booking Cancellation & Inventory Rollback
 *
 * Description:
 * This class demonstrates how confirmed
 * bookings can be cancelled safely.
 *
 * Inventory is restored and rollback
 * history is maintained.
 *
 * @version 10.0
 */
public class BookMyStayApp {

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        // Display application header
        System.out.println("Booking Validation");

        Scanner scanner = new Scanner(System.in);

        // Initialize required components
        RoomInventory inventory = new RoomInventory();
        ReservationValidator validator = new ReservationValidator();

        try {
            // Take user input
            System.out.print("Enter guest name: ");
            String guestName = scanner.nextLine();

            System.out.print("Enter room type (Single/Double/Suite): ");
            String roomType = scanner.nextLine();

            // Validate input
            validator.validate(guestName, roomType, inventory);

            // If valid
            System.out.println("Booking successful for " + guestName);

        } catch (InvalidBookingException e) {
            // Handle validation errors
            System.out.println("Booking failed: " + e.getMessage());
        } finally {
            scanner.close();
        }
    }
}

/**
 * ===================================================
 * CLASS - InvalidBookingException
 * ===================================================
 *
 * Use Case 9: Error Handling & Validation
 *
 * Description:
 * This custom exception represents
 * invalid booking scenarios.
 *
 * Using a domain-specific exception
 * makes error handling clearer and safer.
 *
 * @version 9.0
 */
class InvalidBookingException extends Exception {

    /**
     * Creates an exception with message.
     *
     * @param message error description
     */
    public InvalidBookingException(String message) {
        super(message);
    }
}

/**
 * ===================================================
 * CLASS - ReservationValidator
 * ===================================================
 *
 * Use Case 9: Error Handling & Validation
 *
 * Description:
 * Responsible for validating booking input
 * before processing.
 *
 * All validation rules are centralized.
 *
 * @version 9.0
 */
class ReservationValidator {

    /**
     * Validates booking input.
     *
     * @param guestName name of guest
     * @param roomType requested room type
     * @param inventory room inventory
     * @throws InvalidBookingException if validation fails
     */
    public void validate(
            String guestName,
            String roomType,
            RoomInventory inventory
    ) throws InvalidBookingException {

        // Validate guest name
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("Guest name cannot be empty.");
        }

        // Validate room type (case-sensitive as per requirement)
        if (!inventory.isValidRoomType(roomType)) {
            throw new InvalidBookingException("Invalid room type selected.");
        }

        // Validate availability
        if (!inventory.isAvailable(roomType)) {
            throw new InvalidBookingException("Selected room is not available.");
        System.out.println("Booking Cancellation");

        // Initialize components
        RoomInventory inventory = new RoomInventory();
        CancellationService cancellationService = new CancellationService();

        // Simulate confirmed booking
        String reservationId = "Single-1";
        String roomType = "Single";

        cancellationService.registerBooking(reservationId, roomType);

        // Perform cancellation
        cancellationService.cancelBooking(reservationId, inventory);

        // Show rollback history
        cancellationService.showRollbackHistory();

        // Show updated inventory
        System.out.println(
                "Updated Single Room Availability: " +
                        inventory.getAvailableRooms("Single")
        );
    }
}

/**
 * ===================================================
 * CLASS - CancellationService
 * ===================================================
 *
 * Use Case 10: Booking Cancellation & Inventory Rollback
 *
 * Description:
 * Handles booking cancellations and rollback logic.
 *
 * It ensures:
 * - Cancelled room IDs are tracked
 * - Inventory is restored correctly
 * - Invalid cancellations are prevented
 *
 * A stack is used to model rollback behavior.
 *
 * @version 10.0
 */
class CancellationService {

    /** Stack that stores recently released room IDs */
    private Stack<String> releasedRoomIds;

    /** Maps reservation ID to room type */
    private Map<String, String> reservationRoomMap;

    /** Initializes cancellation tracking structures */
    public CancellationService() {
        releasedRoomIds = new Stack<>();
        reservationRoomMap = new HashMap<>();
    }

    /**
     * Registers a confirmed booking.
     *
     * @param reservationId reservation ID
     * @param roomType allocated room type
     */
    public void registerBooking(String reservationId, String roomType) {
        reservationRoomMap.put(reservationId, roomType);
    }

    /**
     * Cancels a booking and restores inventory.
     *
     * @param reservationId reservation to cancel
     * @param inventory room inventory
     */
    public void cancelBooking(String reservationId, RoomInventory inventory) {

        // Validate reservation
        if (!reservationRoomMap.containsKey(reservationId)) {
            System.out.println("Invalid cancellation request.");
            return;
        }

        String roomType = reservationRoomMap.get(reservationId);

        // Push to rollback stack
        releasedRoomIds.push(reservationId);

        // Restore inventory
        inventory.increment(roomType);

        // Remove booking
        reservationRoomMap.remove(reservationId);

        System.out.println(
                "Booking cancelled successfully. Inventory restored for room type: "
                        + roomType
        );
    }

    /**
     * Displays rollback history.
     */
    public void showRollbackHistory() {

        System.out.println("\nRollback History (Most Recent First):");

        while (!releasedRoomIds.isEmpty()) {
            System.out.println("Released Reservation ID: " + releasedRoomIds.pop());
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
 * @version 9.0
 */
class RoomInventory {

    private Map<String, Integer> inventory;

    public RoomInventory() {
        inventory = new HashMap<>();

        // Initial room counts
        inventory.put("Single", 2);
        inventory.put("Double", 2);
        inventory.put("Suite", 1);
    }

    /** Checks if room type is valid */
    public boolean isValidRoomType(String roomType) {
        return inventory.containsKey(roomType);
    }

    /** Checks availability */
    public boolean isAvailable(String roomType) {
        return inventory.get(roomType) > 0;
    }
}