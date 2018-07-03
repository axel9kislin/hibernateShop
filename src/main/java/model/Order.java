package model;

import model.Enum.OrderStatus;
import model.Enum.PaymentMethod;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "orders")
public class Order {

	private Long id;

	@Column(nullable = false)
	private List<ProductQuanity> quanity;

	@Column
	@Enumerated
	private OrderStatus status;

	@Column
	@Enumerated
	private PaymentMethod paymentMethod;

	@Column(nullable = false)
	private String address;

	@Temporal(TemporalType.TIMESTAMP)
	private Date createTime;

	@Column
	private Double summaryPrice;

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@OneToMany
	public List<ProductQuanity> getQuanity() {
		return quanity;
	}

	public void setQuanity(List<ProductQuanity> quanity) {
		this.quanity = quanity;
	}

	public OrderStatus getStatus() {
		return status;
	}

	public void setStatus(OrderStatus status) {
		this.status = status;
	}

	public PaymentMethod getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(PaymentMethod paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Double getSummaryPrice() {
		return summaryPrice;
	}

	public void setSummaryPrice(Double summaryPrice) {
		this.summaryPrice = summaryPrice;
	}
}
