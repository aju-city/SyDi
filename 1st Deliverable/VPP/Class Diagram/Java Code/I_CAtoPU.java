/**
 * Interface for Customer Application to Processing Unit communication
 * This interface defines the contract for communication between the Customer Application
 * and the Processing Unit component of the IPOS system.
 */
public interface I_CAtoPU {

	/**
	 * Updates the status of an online order in the system
	 * @param aOrderId The unique identifier of the order to be updated
	 * @param aStatus The new status to be set for the order
	 */
	public void updateOnlineOrderStatus(String aOrderId, Object aStatus);

	/**
	 * Sends an email message from the Customer Application to the Processing Unit
	 * @param aMsg The email message to be sent
	 * @return boolean indicating success or failure of the email operation
	 */
	public boolean sendEmail(EmailMessage aMsg);
}