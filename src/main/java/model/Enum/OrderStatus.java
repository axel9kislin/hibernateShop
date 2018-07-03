package model.Enum;

public enum OrderStatus {

	CREATED("Создан"), IN_DELIVERY("Отправлен на доставку"), COMPLETED("Завершён");

	private String translate;

	OrderStatus(String translate) {
		this.translate = translate;
	}
}
