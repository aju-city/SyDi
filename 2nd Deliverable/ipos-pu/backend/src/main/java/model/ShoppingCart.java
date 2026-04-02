package model;

import java.sql.Timestamp;

/**
 * Model class representing a shopping cart.
 * A cart can belong to a member or a guest.
 */
public class ShoppingCart {

    private int cartId;
    private String customerType;
    private String memberEmail;
    private String guestToken;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public ShoppingCart() {
    }

    public ShoppingCart(int cartId, String customerType, String memberEmail,
                        String guestToken, Timestamp createdAt, Timestamp updatedAt) {
        this.cartId = cartId;
        this.customerType = customerType;
        this.memberEmail = memberEmail;
        this.guestToken = guestToken;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public String getCustomerType() {
        return customerType;
    }

    public void setCustomerType(String customerType) {
        this.customerType = customerType;
    }

    public String getMemberEmail() {
        return memberEmail;
    }

    public void setMemberEmail(String memberEmail) {
        this.memberEmail = memberEmail;
    }

    public String getGuestToken() {
        return guestToken;
    }

    public void setGuestToken(String guestToken) {
        this.guestToken = guestToken;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}