package IPOS_PU;

/**
 * SAtoPU - System Administrator to Processing Unit Interface
 * This class implements the communication interface between the System Administrator
 * and the Processing Unit component of the system.
 */
public class SAtoPU implements I_SAtoPU {

	/**
	 * Sends an email message from the System Administrator to the Processing Unit
	 * @param aMsg The email message to be sent
	 * @return boolean indicating success or failure of the email operation
	 * @throws UnsupportedOperationException - method not yet implemented
	 */
	public boolean sendEmail(EmailMessage aMsg) {
		throw new UnsupportedOperationException();
	}

	/**
	 * Constructor for SAtoPU class
	 * Initializes the System Administrator to Processing Unit communication handler
	 * @throws UnsupportedOperationException - constructor not yet implemented
	 */
	public SAtoPU() {
		throw new UnsupportedOperationException();
	}
}