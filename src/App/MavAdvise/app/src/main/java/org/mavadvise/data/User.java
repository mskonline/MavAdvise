package org.mavadvise.data;

/**
 * Created by Remesh on 3/31/2017.
 */

public class User {
    private User() {
    }

    private static User instance;

    private String firstName;
    private String lastName;
    private String roleType;
    private String department;
    private String netID;
    private String utaID;
    private String email;

    public static User getUserInstance() {
        if (instance == null)
            instance = new User();

        return instance;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getRoleType() {
        return roleType;
    }

    public void setRoleType(String roleType) {
        this.roleType = roleType;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getNetID() {
        return netID;
    }

    public void setNetID(String netID) {
        this.netID = netID;
    }

    public String getUtaID() {
        return utaID;
    }

    public void setUtaID(String utaID) {
        this.utaID = utaID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
