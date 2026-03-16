package model;

import java.sql.Date;

public class CommercialApplicant {
    private int applicationID;
    private String companyName;
    private String companyRegistrationNumber;
    private String businessType;
    private String email;
    private Date applicationDate;
    private String applicationStatus;
    private String notificationPreference;
    private String companyAddress;

    public CommercialApplicant() {
    }

    public CommercialApplicant(String companyName, String companyRegistrationNumber, String businessType,
                               String email, Date applicationDate, String applicationStatus,
                               String notificationPreference, String companyAddress) {
        this.companyName = companyName;
        this.companyRegistrationNumber = companyRegistrationNumber;
        this.businessType = businessType;
        this.email = email;
        this.applicationDate = applicationDate;
        this.applicationStatus = applicationStatus;
        this.notificationPreference = notificationPreference;
        this.companyAddress = companyAddress;
    }

    public int getApplicationID() {
        return applicationID;
    }

    public void setApplicationID(int applicationID) {
        this.applicationID = applicationID;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCompanyRegistrationNumber() {
        return companyRegistrationNumber;
    }

    public void setCompanyRegistrationNumber(String companyRegistrationNumber) {
        this.companyRegistrationNumber = companyRegistrationNumber;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getApplicationDate() {
        return applicationDate;
    }

    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }

    public String getApplicationStatus() {
        return applicationStatus;
    }

    public void setApplicationStatus(String applicationStatus) {
        this.applicationStatus = applicationStatus;
    }

    public String getNotificationPreference() {
        return notificationPreference;
    }

    public void setNotificationPreference(String notificationPreference) {
        this.notificationPreference = notificationPreference;
    }

    public String getCompanyAddress() {
        return companyAddress;
    }

    public void setCompanyAddress(String companyAddress) {
        this.companyAddress = companyAddress;
    }
}