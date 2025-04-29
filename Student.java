package smart;

import java.io.Serializable;

class Student implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String degree;
    private String email;
    
    public Student(String id, String name, String degree, String email) {
        if (!email.matches(".*@.*\\.edu$")) {
            throw new IllegalArgumentException("Invalid .edu email");
        }
        if (id == null || id.isEmpty()) throw new IllegalArgumentException("ID cannot be empty");
        if (email == null || !email.matches(".*@.*\\.edu$")) {
            throw new IllegalArgumentException("Invalid .edu email");
        }
        this.id = id;
        this.name = name;
        this.degree = degree;
        this.email = email;
    }
    
    //getters and setters
    public String getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDegree() { return degree; }
    public void setDegree(String degree) { this.degree = degree; }
    public String getEmail() { return email; }
    public void setEmail(String email) { 
        if (!email.matches(".*@.*\\.edu$")) {
            throw new IllegalArgumentException("Invalid .edu email");
        }
        this.email = email; 
    }
}

