public interface I_CAtoPU {

	public void updateOnlineOrderStatus(String aOrderId, Object aStatus);

	public boolean sendEmail(EmailMessage aMsg);
}