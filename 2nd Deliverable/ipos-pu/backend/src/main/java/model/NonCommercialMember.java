package model;

import java.sql.Timestamp;

public class NonCommercialMember {
    private int memberID;
    private String email;
    private String password;
    private boolean mustChangePassword;
    private int totalOrders;
    private Timestamp createdAt;

    public NonCommercialMember() {
    }

    public NonCommercialMember(String email, String password, boolean mustChangePassword, int totalOrders) {
        this.email = email;
        this.password = password;
        this.mustChangePassword = mustChangePassword;
        this.totalOrders = totalOrders;
    }

    public int getMemberID() {
        return memberID;
    }

    public void setMemberID(int memberID) {
        this.memberID = memberID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isMustChangePassword() {
        return mustChangePassword;
    }

    public void setMustChangePassword(boolean mustChangePassword) {
        this.mustChangePassword = mustChangePassword;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}