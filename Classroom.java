package smart;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Classroom implements Serializable {
	private static final long serialVersionUID = 1L;
    private String roomNumber;
    private String type;
    private int capacity;
    private List<Booking> bookings;

    public Classroom(String roomNumber, String type, int capacity) {
        this.roomNumber = roomNumber;
        this.type = type;
        this.capacity = capacity;
        this.bookings = new ArrayList<>();
    }

    
    public void addBooking(Booking booking) {
        bookings.add(booking);
    }

    //overloaded method for convenience
    public void addBooking(String course, LocalDateTime startTime, LocalDateTime endTime) {
        bookings.add(new Booking(this, course, startTime, endTime));
    }

    public void removeBooking(Booking booking) {
        bookings.remove(booking);
    }

    //getters
    public String getRoomNumber() { return roomNumber; }
    public String getType() { return type; }
    public int getCapacity() { return capacity; }
    public List<Booking> getBookings() { return bookings; }
}
