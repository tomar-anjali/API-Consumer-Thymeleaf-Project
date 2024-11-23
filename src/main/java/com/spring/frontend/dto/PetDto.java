package com.spring.frontend.dto;

import lombok.Data;

@Data
public class PetDto {
	private int petId;
	private String name;
	private String breed;
	private int age;
	private double price;
	private String description;
	private String imageUrl;
	private int petCategoryId;

}
