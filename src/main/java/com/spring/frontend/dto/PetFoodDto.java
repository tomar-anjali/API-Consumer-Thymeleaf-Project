package com.spring.frontend.dto;


import lombok.Data;

@Data
public class PetFoodDto {
    private int foodId; // Assuming you want to store the categoryId
    private String name;
    private String brand;
    private String type;
    private int quantity;
    private double price;
}
