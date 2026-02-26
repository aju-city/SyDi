/**
 * Interface for System Administrator to Processing Unit communication
 * This interface defines the contract for communication from the System Administrator
 * to the Processing Unit component of the IPOS system.
 */
public interface I_SAtoPU {

	/**
	 * Sends an email message from the System Administrator to the Processing Unit
	 * @param aMsg The email message to be sent
	 * @return boolean indicating success or failure of the email operation
	 */
	public boolean sendEmail(EmailMessage aMsg);
}