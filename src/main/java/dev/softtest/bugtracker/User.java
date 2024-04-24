package dev.softtest.bugtracker;

import org.bson.types.ObjectId;

public class User {        
    private ObjectId id;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String password;

    public User() {}

    public User(String username, String email, String firstName, String lastName, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
    public ObjectId getId() {
        return id;
    }
    public String getUsername() {
        return username;
    }
    public String getEmail() {
        return email;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getPassword() {
        return password;
    }
}
