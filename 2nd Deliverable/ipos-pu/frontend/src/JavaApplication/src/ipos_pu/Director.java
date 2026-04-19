package ipos_pu;

/**
 Stores details for a company director in a commercial application.
 */
public class Director {
    private final String firstName;
    private final String lastName;
    private final String phone;

    /**
     Creates a director record.
     */
    public Director(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    /**
     Returns the director's full name.
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    /**
     Returns the director's phone number.
     */
    public String getPhone() {
        return phone;
    }
}