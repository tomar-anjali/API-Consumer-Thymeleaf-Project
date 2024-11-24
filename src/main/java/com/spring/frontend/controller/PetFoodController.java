package com.spring.frontend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.frontend.dto.PetFoodDto;

@Controller
public class PetFoodController {

    private final String BASE_URL = "http://localhost:8091/pet_foods"; // Base URL of the backend API

    @Autowired
    private RestTemplate restTemplate;

    // Fetch all pet categories
    @GetMapping("/getAllFood")
    public String getAllPetFood(Model model) {
        try {
            String url = BASE_URL;
            String response = restTemplate.getForObject(url, String.class);
            System.out.println(response);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode foodNode = rootNode.get("data");

            // Convert JSON response into List<Map<String, Object>>
            List<Map<String, Object>> petFoods = objectMapper.convertValue(foodNode, List.class);
            model.addAttribute("petFoods",petFoods ); // Pass categories to Thymeleaf
            return "petfoods";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to fetch categories.");
            return "error";
        }
    }
    @GetMapping("/petfoods/search")
    public String searchPetFoods(@RequestParam("name") String name, Model model) throws Exception {
        // Build the URL for searching pet food by name
        String url = BASE_URL + "/name/" + name.trim(); // Use the correct URL endpoint for pet food search
        String response = restTemplate.getForObject(url, String.class); // Call the backend API
        System.out.println(response);
        // Parse the JSON response
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(response);
        JsonNode petFoodsNode = rootNode.get("data"); // Assuming the response contains a "data" field

        // Convert the response into a list of pet foods
  
        	List<Map<String, Object>> petFoods = objectMapper.convertValue(petFoodsNode, List.class);
        	
        
         // Add to the model to display in Thymeleaf
        	model.addAttribute("petFoods", petFoods);
        // Return the view that will display the search results
        return "search-petfood"; // Thymeleaf view for displaying pet food search results
    }

    // Open the form to add a new category
    @GetMapping("/petFood/add")
    public String addCategoryForm(Model model) {
        model.addAttribute("category", new PetFoodDto());
        return "add-petfood";
    }

    // Save a new category
    @PostMapping("/petFood/save")
    public String saveCategory(@ModelAttribute PetFoodDto categoryDto) {
        try {
            String url = BASE_URL;
            restTemplate.postForObject(url, categoryDto, String.class);
            return "redirect:/getAllFood";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    // Edit category
    @GetMapping("/petFood/edit/{foodId}")
    public String editCategory(@PathVariable int foodId, Model model) {
        try {
            String url = BASE_URL + "/" + foodId;
            String response = restTemplate.getForObject(url, String.class);

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode categoryNode = rootNode.get("data");

            Map<String, Object> petFood = objectMapper.convertValue(categoryNode, Map.class);
            model.addAttribute("petFood", petFood);
            return "edit-petfood";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    // Update an existing category
    @GetMapping("/petFood/update/{foodId}")
    public String updateCategory(@PathVariable int foodId, @ModelAttribute PetFoodDto petFoodDto) {
        try {
            String url = BASE_URL + "/" + foodId;
            restTemplate.put(url, petFoodDto);
            return "redirect:/getAllFood";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
