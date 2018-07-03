package model.Enum;

public enum PaymentMethod {
	CASH("Рассчёт наличными"), CARD("Рассчёт по карте");

	private String translate;

	PaymentMethod(String translate) {
		this.translate = translate;
	}
}
