package service;

import model.*;
import model.Enum.OrderStatus;
import model.Enum.PaymentMethod;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static utils.HibernateUtils.getSession;
import static utils.HibernateUtils.saveObject;

public class ShoppingServiceImpl implements ShopppingService {
	static final Session session = getSession();


	public void createInitialPositions() {
		Category electronics = new Category();
		electronics.setName("Электроника");
		saveObject(electronics);
		Category smartPhones = new Category();
		smartPhones.setName("Смартфоны");
		smartPhones.setParentCategoryId(String.valueOf(electronics.getId()));
		saveObject(smartPhones);
		Category kitchenTechs = new Category();
		kitchenTechs.setName("Кухонные приборы");
		kitchenTechs.setParentCategoryId(String.valueOf(electronics.getId()));
		saveObject(kitchenTechs);
		Category clothes = new Category();
		clothes.setName("Одежда");
		saveObject(clothes);
		Category sportWear = new Category();
		sportWear.setName("Спортивная одежда");
		sportWear.setParentCategoryId(String.valueOf(clothes.getId()));
		saveObject(sportWear);

		Product nokia = new Product();
		nokia.setArtCode("001");
		nokia.setName("Nokia");
		nokia.setDescription("Smth new phone");
		nokia.setPrice(99.9);
		nokia.setCategories(getCategoriesBySelected(smartPhones, new ArrayList<Category>()));
		saveObject(nokia);
		List<PhotoStore> initialPhotostoreForNokia = createInitialPhotostoreForProduct(nokia);
		nokia.setPhotos(initialPhotostoreForNokia);
		createStockBalanceForProduct(nokia);
		saveObject(nokia);

		Product samsung = new Product();
		samsung.setArtCode("002");
		samsung.setName("Samsung");
		samsung.setDescription("again smth new phone");
		samsung.setPrice(195.0);
		samsung.setCategories(getCategoriesBySelected(smartPhones, new ArrayList<Category>()));
		saveObject(samsung);
		List<PhotoStore> initialPhotostoreForSamsung = createInitialPhotostoreForProduct(samsung);
		samsung.setPhotos(initialPhotostoreForSamsung);
		createStockBalanceForProduct(samsung);

		Product multiCook = new Product();
		multiCook.setArtCode("003");
		multiCook.setName("Bosh");
		multiCook.setDescription("multi cook for lazy wife");
		multiCook.setPrice(60.0);
		multiCook.setCategories(getCategoriesBySelected(kitchenTechs, new ArrayList<Category>()));
		saveObject(multiCook);
		List<PhotoStore> initialPhotostoreForMultiCook = createInitialPhotostoreForProduct(multiCook);
		multiCook.setPhotos(initialPhotostoreForMultiCook);
		createStockBalanceForProduct(multiCook);

		Product shoes = new Product();
		shoes.setArtCode("004");
		shoes.setName("Adidas shoes");
		shoes.setDescription("cheap shoes for you");
		shoes.setPrice(12.0);
		shoes.setCategories(getCategoriesBySelected(sportWear, new ArrayList<Category>()));
		saveObject(shoes);
		List<PhotoStore> initialPhotostoreForShoes = createInitialPhotostoreForProduct(shoes);
		shoes.setPhotos(initialPhotostoreForShoes);
		createStockBalanceForProduct(shoes);

		Product tShirt = new Product();
		tShirt.setArtCode("005");
		tShirt.setName("amazing basic t-shirt");
		tShirt.setDescription("alliexpress description");
		tShirt.setPrice(1.99);
		tShirt.setCategories(getCategoriesBySelected(sportWear, new ArrayList<Category>()));
		saveObject(tShirt);
		List<PhotoStore> initialPhotostoreForTShirt = createInitialPhotostoreForProduct(tShirt);
		tShirt.setPhotos(initialPhotostoreForTShirt);
		createStockBalanceForProduct(tShirt);
		saveObject(tShirt);
		System.out.println("test");
	}

	private List<PhotoStore> createInitialPhotostoreForProduct(Product product) {
		List<PhotoStore> photoStore = new ArrayList<PhotoStore>();
		for (int i = 0; i < 2; i++) {
			PhotoStore photo = new PhotoStore();
			photo.setProduct(product);
			photo.setLink("http://bulkLink.com/" + product.getName() + "_" + i);
			saveObject(photo);
			photoStore.add(photo);
		}
		return photoStore;
	}

	private void createStockBalanceForProduct(Product product) {
		StockBalance stockBalance = new StockBalance();
		stockBalance.setProduct(product);
		stockBalance.setAvailableCount(Math.round(Math.random() * 100));
		saveObject(stockBalance);
	}

	public Order createOrder(List<ProductQuanity> products, PaymentMethod method, Date createDate, String address) {
		double allPrice = 0;
		for (ProductQuanity pq : products) {
			int availableCountForProduct = getAvailableCountForProduct(pq.getProduct());
			if (availableCountForProduct < pq.getQuanity()) {
				System.out.println("Невозможно заказать " + pq.getQuanity()
						+ " единиц товара " + pq.getProduct().getName()
						+ ", на складе доступно только " + availableCountForProduct
						+ " единиц товара!");
				return null;
			} else {
				allPrice += pq.getProduct().getPrice() * pq.getQuanity();
			}
		}
		Order order = new Order();
		order.setQuanity(products);
		order.setPaymentMethod(method);
		order.setStatus(OrderStatus.CREATED);
		order.setAddress(address);
		order.setCreateTime(createDate);
		order.setSummaryPrice(allPrice);
		session.save(order);
		updateQuanityAfterOrder(products);
		return order;
	}

	public Product getProductByArtCode(String artCode) {
		if (artCode != null) {
			List<Product> list = session.createQuery("FROM Product p WHERE p.artCode=" + artCode).list();
			if (list != null && !list.isEmpty()) {
				return list.get(0);
			}
		}
		return null;
	}

	public List<StockBalance> getAllAvailableProducts() {
		session.clear();
		List<StockBalance> allProducts = session.createQuery("FROM StockBalance").list();
		List<StockBalance> result = new ArrayList<StockBalance>();
		for (StockBalance pr : allProducts) {
			if (pr.getAvailableCount() > 0) {
				result.add(pr);
			}
		}
		return result;
	}

	public int getAvailableCountForProduct(Product product) {
		Query query = session.createQuery("SELECT sb.availableCount FROM StockBalance sb WHERE sb.product=:product");
		query.setParameter("product", product);
		List<Long> result = query.list();
		if (result != null && result.size() > 0) {
			return result.get(0).intValue();
		} else return 0;
	}

	public List<Category> getCategoriesBySelected(Category category, List<Category> container) {
		//не самый лучший алгоритм, но для данной задачи подойдёт.
		Query query = session.createQuery("FROM Category c WHERE c.id=:parentId");
		String parentID = category.getParentCategoryId();
		if (parentID != null && !parentID.isEmpty()) {
			query.setParameter("parentId", Long.valueOf(parentID));
			List<Category> parents = query.list();
			if (parents != null && !parents.isEmpty()) {
				for (Category cat : parents) {
					container.add(category);
					List<Category> grandParents = getCategoriesBySelected(cat, container);
					container.addAll(grandParents);
				}
				return container;
			} else {
				List<Category> result = new ArrayList<Category>();
				result.add(category);
				return result;
			}
		} else {
			List<Category> result = new ArrayList<Category>();
			result.add(category);
			return result;
		}
	}

	private void updateQuanityAfterOrder(List<ProductQuanity> products) {
		for (ProductQuanity pq : products) {
			int availableOnStock = getAvailableCountForProduct(pq.getProduct());
			Transaction tx = session.beginTransaction();
			Query query = session.createQuery("UPDATE StockBalance set availableCount = :newCount WHERE product= :prod");
			query.setParameter("newCount", Long.valueOf(availableOnStock - pq.getQuanity()));
			query.setParameter("prod", pq.getProduct());
			int result = query.executeUpdate();
			tx.commit();
			System.out.println("Обновлено " + result + " позиций на складе");
		}
	}

	public List<Order> getAllOrders() {
		return session.createQuery("FROM Order").list();
	}
}
