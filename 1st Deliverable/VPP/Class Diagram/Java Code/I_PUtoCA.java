public interface I_PUtoCA {

	public List<CatalogueItem> getCatalogue();

	public boolean checkStockAvailability(String aItemId, int aQty);

	public Product getProductDetails(String aItemId);

	public String submitOnlineOrder(OnlineOrder aOrder);

	public boolean deductStockForOnlineOrder(String aOrderId, List<Items> aItems);

	public FulfilmentStatus getOnlineOrderFulfilmentStatus(String aOrderId);
}