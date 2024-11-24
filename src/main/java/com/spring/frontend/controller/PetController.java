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
import com.spring.frontend.dto.PetDto;

@Controller
public class PetController {

    private final String BASE_URL = "http://localhost:8091/pets"; // Base URL of your previous project

    @Autowired
    private RestTemplate restTemplate;

    // Fetch all pets from the API
    @GetMapping("/pet")
    public String getAllPets(Model model) {
        try {
            // Fetch pets from the API
            String url = BASE_URL; 
            String response = restTemplate.getForObject(url, String.class);
            System.out.println(response);
            // Parse JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode petsNode = rootNode.get("data");

            // Convert JsonNode to List of Maps
            List<Map<String, Object>> pets = objectMapper.convertValue(petsNode, List.class);
            model.addAttribute("pets", pets); // Add to the model to display in Thymeleaf
            return "pet"; // Thymeleaf view
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to fetch pets.");
            return "error";
        }
    }

    // Fetch pet by ID for editing
    @GetMapping("/pet/edit/{petId}")
    public String editPet(@PathVariable int petId, Model model) {
        try {
            // Fetch pet by ID from the API
            String url = BASE_URL + "/" + petId;
            String response = restTemplate.getForObject(url, String.class);

            // Parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode petNode = rootNode.get("data");

            // Convert JsonNode to Map
            Map<String, Object> pet = objectMapper.convertValue(petNode, Map.class);
            model.addAttribute("pet", pet); // Add pet to the model
            return "edit-pet"; // Thymeleaf edit page
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to fetch pet details.");
            return "error";
        }
    }

    // Update pet details
//    @PostMapping("/pets/update/{petId}")
//    public String updatePet(@PathVariable int petId, @RequestParam Map<String, String> params, RedirectAttributes redirectAttributes) {
//        try {
//            // Assuming you're sending a PUT request to the backend API to update the pet
//            String apiUrl = "http://localhost:8099/pets/update/" + petId;
//            
//            // Creating the updated pet data as a map or a DTO object
//            Map<String, String> updatedPet = new HashMap<>();
//            updatedPet.put("name", params.get("name"));
//            updatedPet.put("age", params.get("age"));
//            updatedPet.put("breed", params.get("breed"));
//            updatedPet.put("price", params.get("price"));
//            updatedPet.put("description", params.get("description"));
//            updatedPet.put("imageUrl", params.get("imageUrl"));
//            
//            // Make a PUT request to update the pet data
//            restTemplate.put(apiUrl, updatedPet);
//
//            // Add a success message and redirect
//            redirectAttributes.addFlashAttribute("message", "Pet updated successfully!");
//            return "redirect:/pets";  // Redirect to the pets list after the update
//        } catch (Exception e) {
//            e.printStackTrace();
//            redirectAttributes.addFlashAttribute("error", "Failed to update pet.");
//            return "redirect:/pets";
//        }
//    }
    
    @PostMapping("/pets/update/{petId}")
    public String updatePet(@PathVariable int petId, @ModelAttribute PetDto petDto) {
        try {
            // Assuming you're sending a PUT request to the backend API to update the pet
            String apiUrl = "http://localhost:8099/pets/" + petId;
            
            
            restTemplate.put(apiUrl, petDto);

           
            return "redirect:/pet";  // Redirect to the pets list after the update
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/pet";
        }
    }
    
 // Search pets by name
//    @GetMapping("/pets/search")
//    public String searchPets(@RequestParam("name") String name, Model model) {
//        try {
//            // Fetch pet by name from the API
//            String url = BASE_URL + "/pets/search?name=" + name.trim();
//            String response = restTemplate.getForObject(url, String.class);
//
//            // Parse the JSON response
//            ObjectMapper objectMapper = new ObjectMapper();
//            JsonNode rootNode = objectMapper.readTree(response);
//            JsonNode petsNode = rootNode.get("data");
//
//            // Convert JsonNode to List of Maps
//            List<Map<String, Object>> pets = objectMapper.convertValue(petsNode, List.class);
//
//            model.addAttribute("pets", pets); // Add to the model to display in Thymeleaf
//            return "pets"; // Thymeleaf view
//        } catch (Exception e) {
//            e.printStackTrace();
//            model.addAttribute("error", "Failed to fetch pets.");
//            return "error";
//        }
//    }
    
    
    @GetMapping("/pets/search")
    public String searchPets(@RequestParam("name") String name, Model model) throws Exception{
        try {
            // Fetch pet by name from the API
//            String url = BASE_URL + "/pets/search?name=" + name.trim();
            String url = BASE_URL + "/name/" + name.trim();
            String response = restTemplate.getForObject(url, String.class);

            // Parse the JSON response
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode petsNode = rootNode.get("data");

//            // Convert JsonNode to List of PetDto
//            List<PetDto> pets = objectMapper.readValue(
//                petsNode.toString(),
//                objectMapper.getTypeFactory().constructCollectionType(List.class, PetDto.class)
//            );
            List<Map<String, Object>> pets = objectMapper.convertValue(petsNode, List.class);
            model.addAttribute("pets", pets); // Add to the model to display in Thymeleaf
            return "search-pets"; // Thymeleaf view
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to fetch pets.");
            return "error";
        }
    }

    
 // Display the form to add a new pet
    @GetMapping("/pets/new")
    public String showAddPetForm(Model model) {
        model.addAttribute("pet", new PetDto()); // Create an empty PetDto for the form
        return "add-pet"; // Thymeleaf view for adding a pet
    }

    // Handle the form submission for adding a new pet
    @PostMapping("/pets/new")
    public String addNewPet(@ModelAttribute PetDto petDto) {
        try {
            String apiUrl = BASE_URL; // API endpoint for adding a new pet
            restTemplate.postForObject(apiUrl, petDto, Void.class); // Send the pet data to the API
            return "redirect:/pet"; // Redirect to the pets list
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/pet"; // Handle errors and redirect to the pets list
        }
    }


}