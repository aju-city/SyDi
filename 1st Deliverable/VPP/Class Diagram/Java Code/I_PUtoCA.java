/**
 * Interface for Processing Unit to Customer Application communication
 * This interface defines the contract for communication from the Processing Unit
 * to the Customer Application component of the IPOS system.
 */
public interface I_PUtoCA {

	/**
	 * Retrieves the current catalogue of items available in the system
	 * @return List of CatalogueItem objects representing available products
	 */
	public List<CatalogueItem> getCatalogue();

	/**
	 * Checks the availability of stock for a specific item
	 * @param aItemId The unique identifier of the item to check
	 * @param aQty The quantity of the item to check availability for
	 * @return boolean indicating whether the requested quantity is available
	 */
	public boolean checkStockAvailability(String aItemId, int aQty);

	/**
	 * Retrieves detailed information about a specific product
	 * @param aItemId The unique identifier of the product
	 * @return Product object containing detailed product information
	 */
	public Product getProductDetails(String aItemId);

	/**
	 * Submits a new online order to the system
	 * @param aOrder The OnlineOrder object containing order details
	 * @return String representing the order ID or confirmation
	 */
	public String submitOnlineOrder(OnlineOrder aOrder);

	/**
	 * Deducts stock items from inventory for a confirmed online order
	 * @param aOrderId The unique identifier of the order
	 * @param aItems List of items to be deducted from stock
	 * @return boolean indicating success or failure of stock deduction
	 */
	public boolean deductStockForOnlineOrder(String aOrderId, List<Items> aItems);

	/**
	 * Retrieves the fulfillment status of an online order
	 * @param aOrderId The unique identifier of the order
	 * @return FulfilmentStatus object containing current fulfillment information
	 */
	public FulfilmentStatus getOnlineOrderFulfilmentStatus(String aOrderId);
}