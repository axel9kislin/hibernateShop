package model;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.LinkedHashMap;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

	@Column
	private Long id;

	@Column(nullable = false)
	private String artCode;

	@Column(nullable = false)
	private String name;

	@Column
	private String description;

	@Column(nullable = false)
	private Double price;

	private List<PhotoStore> photos;

	private List<Category> categories;

	public Product() {
	}

	@Id
	@GeneratedValue(generator = "increment")
	@GenericGenerator(name = "increment", strategy = "increment")
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getArtCode() {
		return artCode;
	}

	public void setArtCode(String artCode) {
		this.artCode = artCode;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@OneToMany
	public List<PhotoStore> getPhotos() {
		return photos;
	}

	public void setPhotos(List<PhotoStore> photos) {
		this.photos = photos;
	}

	@ManyToMany
	public List<Category> getCategories() {
		return categories;
	}

	public void setCategories(List<Category> categories) {
		this.categories = categories;
	}
}
