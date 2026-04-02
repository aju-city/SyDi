package model;

import java.sql.Timestamp;

/**
 * Model class representing a non-commercial member.
 */
public class NonCommercialMember {

    private int memberID;
    private String email;
    private String password;
    private boolean mustChangePassword;
    private int totalOrders;
    private Timestamp createdAt;
    private String memberAccountNo;

    public NonCommercialMember() {
    }

    public NonCommercialMember(int memberID, String email, String password,
                               boolean mustChangePassword, int totalOrders, Timestamp createdAt) {
        this.memberID = memberID;
        this.email = email;
        this.password = password;
        this.mustChangePassword = mustChangePassword;
        this.totalOrders = totalOrders;
        this.createdAt = createdAt;
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

    public String getMemberAccountNo() {
        return memberAccountNo;
    }

    public void setMemberAccountNo(String memberAccountNo) {
        this.memberAccountNo = memberAccountNo;
    }


}