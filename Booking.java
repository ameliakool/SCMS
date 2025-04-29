package smart;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Booking implements Serializable {
	private static final long serialVersionUID = 1L;
    private Classroom classroom;
    private String course;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Booking(Classroom classroom, String course, LocalDateTime startTime, LocalDateTime endTime) {
        this.classroom = classroom;
        this.course = course;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    //getters and Setters
    public Classroom getClassroom() { return classroom; }
    public String getCourse() { return course; }
    public void setCourse(String course) { this.course = course; }
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}
