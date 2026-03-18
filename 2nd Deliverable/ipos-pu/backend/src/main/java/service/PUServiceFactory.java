package service;

import dao.CommercialApplicantDAO;
import dao.CompanyDirectorDAO;
import dao.NonCommercialMemberDAO;

public class PUServiceFactory {

    private static IPUService instance;

    public static IPUService getService() {
        if (instance == null) {
            instance = new PUServiceImpl(
                    new CommercialApplicantDAO(),
                    new CompanyDirectorDAO(),
                    new NonCommercialMemberDAO()
            );
        }
        return instance;
    }
}
