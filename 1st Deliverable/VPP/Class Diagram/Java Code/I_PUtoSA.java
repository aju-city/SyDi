/**
 * Interface for Processing Unit to System Administrator communication
 * This interface defines the contract for communication from the Processing Unit
 * to the System Administrator component of the IPOS system.
 */
public interface I_PUtoSA {

	/**
	 * Submits a commercial application for processing
	 * @param aApp The CommercialApplication object containing application details
	 * @return String representing the application ID or confirmation
	 */
	public String submitCommercialApplication(CommercialApplication aApp);

	/**
	 * Receives and processes discount updates from the system
	 * @param aUpdate The DiscountUpdate object containing discount information
	 * @return boolean indicating success or failure of the update processing
	 */
	public boolean receiveDiscountUpdates(DiscountUpdate aUpdate);
}