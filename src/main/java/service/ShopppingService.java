package service;

import model.*;
import model.Enum.PaymentMethod;

import java.util.Date;
import java.util.List;

public interface ShopppingService {

	void createInitialPositions();

	Order createOrder(List<ProductQuanity> products, PaymentMethod method, Date createDate, String address);

	List<Category> getCategoriesBySelected(Category category, List<Category> container);

	Product getProductByArtCode(String artCode);

	List<StockBalance> getAllAvailableProducts();

	int getAvailableCountForProduct(Product product);

	List<Order> getAllOrders();
}
