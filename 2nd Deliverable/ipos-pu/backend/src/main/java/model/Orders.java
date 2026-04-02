package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model class representing an order.
 * An order can be placed by a member or a guest.
 */
public class Orders {

    private int orderId;
    private String customerType;
    private String memberEmail;
    private String customerEmail;
    private Timestamp orderDate;
    private String deliveryAddress;
    private BigDecimal totalAmount;
    private String status;

    public Orders() {
    }

    public Orders(int orderId, String customerType, String memberEmail, String customerEmail,
                  Timestamp orderDate, String deliveryAddress, BigDecimal totalAmount, String status) {
        this.orderId = orderId;
        this.customerType = customerType;
        this.memberEmail = memberEmail;
        this.customerEmail = customerEmail;
        this.orderDate = orderDate;
        this.deliveryAddress = deliveryAddress;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
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

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public Timestamp getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Timestamp orderDate) {
        this.orderDate = orderDate;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}