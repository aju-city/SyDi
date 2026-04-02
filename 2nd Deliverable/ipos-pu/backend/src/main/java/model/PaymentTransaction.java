package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 * Model class representing a payment transaction for an order.
 */
public class PaymentTransaction {

    private int paymentId;
    private int orderId;
    private Timestamp paymentDatetime;
    private BigDecimal amount;
    private String paymentMethod;
    private String maskedCardNumber;
    private String paymentStatus;
    private String processorReference;
    private String failureReason;

    public PaymentTransaction() {
    }

    public PaymentTransaction(int paymentId, int orderId, Timestamp paymentDatetime,
                              BigDecimal amount, String paymentMethod, String maskedCardNumber,
                              String paymentStatus, String processorReference, String failureReason) {
        this.paymentId = paymentId;
        this.orderId = orderId;
        this.paymentDatetime = paymentDatetime;
        this.amount = amount;
        this.paymentMethod = paymentMethod;
        this.maskedCardNumber = maskedCardNumber;
        this.paymentStatus = paymentStatus;
        this.processorReference = processorReference;
        this.failureReason = failureReason;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public Timestamp getPaymentDatetime() {
        return paymentDatetime;
    }

    public void setPaymentDatetime(Timestamp paymentDatetime) {
        this.paymentDatetime = paymentDatetime;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getMaskedCardNumber() {
        return maskedCardNumber;
    }

    public void setMaskedCardNumber(String maskedCardNumber) {
        this.maskedCardNumber = maskedCardNumber;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getProcessorReference() {
        return processorReference;
    }

    public void setProcessorReference(String processorReference) {
        this.processorReference = processorReference;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }
}