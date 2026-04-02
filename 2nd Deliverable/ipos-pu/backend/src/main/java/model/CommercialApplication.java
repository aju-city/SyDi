package model;

import java.sql.Timestamp;

/**
 * Model class representing a commercial application from IPOS_SA.
 */
public class CommercialApplication {

    private int applicationId;
    private String companyName;
    private String regNumber;
    private String directorDetails;
    private String businessType;
    private String address;
    private String email;
    private String phone;
    private String status;
    private Timestamp submittedAt;
    private Integer reviewedBy;
    private String notes;

    public CommercialApplication() {
    }

    public CommercialApplication(int applicationId, String companyName, String regNumber,
                                 String directorDetails, String businessType, String address,
                                 String email, String phone, String status,
                                 Timestamp submittedAt, Integer reviewedBy, String notes) {
        this.applicationId = applicationId;
        this.companyName = companyName;
        this.regNumber = regNumber;
        this.directorDetails = directorDetails;
        this.businessType = businessType;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.status = status;
        this.submittedAt = submittedAt;
        this.reviewedBy = reviewedBy;
        this.notes = notes;
    }

    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getRegNumber() {
        return regNumber;
    }

    public void setRegNumber(String regNumber) {
        this.regNumber = regNumber;
    }

    public String getDirectorDetails() {
        return directorDetails;
    }

    public void setDirectorDetails(String directorDetails) {
        this.directorDetails = directorDetails;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Timestamp getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(Timestamp submittedAt) {
        this.submittedAt = submittedAt;
    }

    public Integer getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(Integer reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}