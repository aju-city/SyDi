package ipos_pu;

import java.util.List;

/**
 Stores the data submitted for a commercial membership application.
 */
public class CommercialApplicationRequest {
    public String companyName;
    public String regNumber;
    public String businessType;
    public String address;
    public String email;
    public String phone;
    public List<Director> directors;
}