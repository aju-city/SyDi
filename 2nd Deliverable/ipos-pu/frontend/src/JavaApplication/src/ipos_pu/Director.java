package ipos_pu;

public class Director {
    private final String firstName;
    private final String lastName;
    private final String phone;

    public Director(String firstName, String lastName, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    public String getPhone() {
        return phone;
    }
}