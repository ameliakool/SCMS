package smart;

import java.io.Serializable;

public class Resource implements Serializable {
	private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String type;
    private String status;
    private String checkedOutBy;
    

    public Resource(String id, String name, String type, String status) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.status = status;
        this.checkedOutBy = null;
    }
    
    public void checkOut(String studentId) {
        this.checkedOutBy = studentId;
        this.status = "Checked Out to " + studentId;
    }

    //getters and Setters
    public String getId() { 
    	return id; }
    public String getName() { 
    	return name; }
    public void setName(String name) { 
    	this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { 
    	this.type = type; }
    public String getStatus() { 
    	return status; }
    public void setStatus(String status) { 
    	this.status = status; }
	public String getCheckedOutBy() {
		return checkedOutBy; }
	public void setCheckedOutBy(String checkedOutBy) {
		this.checkedOutBy = checkedOutBy; }
	
}
