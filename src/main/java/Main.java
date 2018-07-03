import model.*;
import model.Enum.PaymentMethod;
import service.ShoppingServiceImpl;
import service.ShopppingService;

import java.util.*;

import static utils.HibernateUtils.saveObject;

public class Main {

	public static void main(final String[] args) throws Exception {
		ShopppingService shopppingService = new ShoppingServiceImpl(); //по-здоровому, это инжектится, но разворачивать DI ради одного сервиса очень неохото
		shopppingService.createInitialPositions();
		while (1 == 1) {
			System.out.println("Welcome to internet shop");
			List<StockBalance> allProducts = shopppingService.getAllAvailableProducts();
			printStocks(allProducts);
			System.out.println("****************");
			Map<String, Integer> userChoose = new HashMap();
			Scanner sc = new Scanner(System.in);
			int userQuanityInInt = 0;
			Order order = createOrder(shopppingService, userChoose, sc, userQuanityInInt);
			if (order != null) {
				System.out.println("Сумма вашего заказа: " + order.getSummaryPrice());
				System.out.println("Выбраны следующие позиции: ");
				for (ProductQuanity pq : order.getQuanity()) {
					System.out.println("------------------");
					System.out.println("Наименование: " + pq.getProduct().getName());
					System.out.println("Цена: " + pq.getProduct().getPrice());
					System.out.println("Колличество: " + pq.getQuanity());
				}
				System.out.println("Заказ оформлен, позиции зарезервированы.");
				System.out.println("Обновлённая информация о остатках:");
			}
		}
	}

	private static Order createOrder(ShopppingService shopppingService, Map<String, Integer> userChoose, Scanner sc, int userQuanityInInt) {
		String userInput;
		String userQuanity;
		while (1 == 1) {
			System.out.println("Для того, что бы сформировать заказ введите артикулы желаемых товаров, по одному, после каждого - нажать enter");
			System.out.println("Для того, что бы посмотреть имеющиеся заказы введите 'orders'"); //я долго думал что от меня хотят, когда просят в консольном приложение дать возможность закрыть браузер и заново его открыть, вернувшись к выполнению заказа.
			//но решил, что вот так сохраняя текущие заказы я с небольшими допущениями эту возможность дам.
			System.out.println("Для завершения заказа введите 'done'.");
			userInput = sc.nextLine();
			if (userInput.equals("done")) break;
			if (userInput.equals("orders")) {
				List<Order> allOrders = shopppingService.getAllOrders();
				System.out.println("Всего заказов в системе: " + allOrders.size());
				for (Order order : allOrders) {
					System.out.println("Идентификатор: " + order.getId());
					System.out.println("Статус: " + order.getStatus());
					System.out.println("Общая сумма: " + order.getSummaryPrice());
					System.out.println("Создан: " + order.getCreateTime());
				}
				continue;
			}
			System.out.println("Введите количество: ");
			userQuanity = sc.nextLine();
			try {
				userQuanityInInt = Integer.valueOf(userQuanity);
			} catch (Exception e) {
				System.out.println("Не числовое колличество, этот товар будет проигнорирован");
			}
			if (userInput != null && !userInput.isEmpty()
					&& userQuanityInInt != 0) {
				userChoose.put(userInput, userQuanityInInt);
			}
		}
		System.out.println("Выберите метод оплаты: 1 - Наличными, 2 - Банковская карта");
		String payStr = sc.nextLine();
		PaymentMethod choosenMethod = PaymentMethod.CARD;
		if (payStr.equals("1")) {
			choosenMethod = PaymentMethod.CASH;
		}
		System.out.println("Введите адрес в удобном для вас формате");
		String address = sc.nextLine();

		System.out.println("Вы выбрали " + userChoose.size() + " наименований");
		List<ProductQuanity> productQuanities = new ArrayList<ProductQuanity>();
		for (Map.Entry<String, Integer> elment : userChoose.entrySet()) {
			Product product = shopppingService.getProductByArtCode(elment.getKey());
			if (product != null) {
				ProductQuanity quanity = new ProductQuanity();
				quanity.setProduct(product);
				quanity.setQuanity(elment.getValue());
				saveObject(quanity);
				productQuanities.add(quanity);
			}
		}
		return shopppingService.createOrder(productQuanities, choosenMethod, new Date(), address);
	}

	private static void printStocks(List<StockBalance> allProducts) {
		System.out.println("Доступно товаров: " + allProducts.size());
		for (StockBalance sb : allProducts) {
			System.out.println("---------------------------------------");
			Product product = sb.getProduct();
			System.out.println("Артикул товара: " + product.getArtCode());
			System.out.println("Имя товара: " + product.getName());
			System.out.println("Доступно на складе: " + sb.getAvailableCount());
			System.out.println("Описание товара: " + product.getDescription());
			StringBuilder builderForCategories = new StringBuilder();
			for (Category category : product.getCategories()) {
				builderForCategories.append(category.getName()).append(", ");
			}
			System.out.println("Категории товара: " + builderForCategories.toString());
			System.out.println("Описание товара: " + product.getDescription());
			System.out.println("Стоимость товара: " + product.getPrice());
			StringBuilder builderForLinks = new StringBuilder();
			for (PhotoStore photoStore : product.getPhotos()) {
				builderForLinks.append(photoStore.getLink()).append(", ");
			}
			System.out.println("Фотографии по ссылкам: " + builderForLinks.toString());
		}
	}
}