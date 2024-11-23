package com.spring.frontend.dto;


import lombok.Data;

@Data
public class GroomingServiceDto {
    private int serviceId;
    private String name;
    private String description;
    private double price;
    private boolean available;
}
