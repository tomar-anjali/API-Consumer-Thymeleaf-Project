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
import com.spring.frontend.dto.VaccinationDto;

@Controller
public class IndexController {
	@GetMapping("/index")
	public String index(Model model) {
		return "index";
	}

	@GetMapping("/akanksha")
	public String akanksha(Model model) {
		return "akanksha";
	}

	@GetMapping("/anjali")
	public String anjali(Model model) {
		return "anjali";
	}

	@GetMapping("/impana")
	public String impana(Model model) {
		return "impana";
	}

	@GetMapping("/maheshwari")
	public String maheshwari(Model model) {
		return "maheshwari";
	}

	@GetMapping("/sunaina")
	public String sunaina(Model model) {
		return "sunaina";
	}

	private final String BASE_URL = "http://localhost:8091"; // Base URL of your previous project

	@Autowired
	private RestTemplate restTemplate;

	// Fetch all pets from the API
	@GetMapping("/pets")
	public String getAllPets(Model model) {
		try {
			// Fetch pets from the API
			String url = BASE_URL + "/pets";
			String response = restTemplate.getForObject(url, String.class);
			System.out.println(response);
			// Parse JSON response
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode petsNode = rootNode.get("data");
			// Convert JsonNode to List of Maps
			List<Map<String, Object>> pets = objectMapper.convertValue(petsNode, List.class);
			model.addAttribute("pets", pets); // Add to the model to display in Thymeleaf
			return "pets"; // Thymeleaf view
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Failed to fetch pets.");
			return "error";
		}
	}

	// Anjali
	@GetMapping("/vaccinations")
	public String displayVaccinations(Model model) {
		try {
			String apiUrl = BASE_URL + "/vaccinations";
			RestTemplate restTemplate = new RestTemplate();
			String response = restTemplate.getForObject(apiUrl, String.class);
//            System.err.println(response);
			// Parse the JSON response
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode dataNode = rootNode.get("data");

			// Handle dataNode
			if (dataNode == null || !dataNode.isArray() || dataNode.isEmpty()) {
				model.addAttribute("error", "No vaccinations found.");
				return "error";
			}
			// Convert JsonNode to List of Maps
			List<Map<String, Object>> vaccines = objectMapper.convertValue(dataNode, List.class);
			model.addAttribute("vaccinations", vaccines);

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Failed to fetch vaccinations. Please try again later.");
			return "error";
		}
		return "vaccinations";
	}

	@GetMapping("/editVaccination/{id}")
	public String editVaccination(@PathVariable("id") int id, Model model) {
		System.err.println(id);
		try {
			String apiUrl = BASE_URL + "/vaccinations" + "/" + id;
			System.err.println(apiUrl);
			RestTemplate restTemplate = new RestTemplate();
			String response = restTemplate.getForObject(apiUrl, String.class);
			System.err.println(response);
			// Parse the JSON response
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode vaccinationNode = rootNode.get("data");

			// Convert JsonNode to Map
			if (vaccinationNode == null) {
				model.addAttribute("error", "Vaccination not found.");
				return "error";
			}
			Map<String, Object> vaccination = objectMapper.convertValue(vaccinationNode, Map.class);
			model.addAttribute("vaccination", vaccination);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Error fetching vaccination details.");
			return "error";
		}
		return "editVaccination";
	}

	@PostMapping("/updateVaccination/{id}")
	public String updateVaccination(@PathVariable("id") int id, @ModelAttribute VaccinationDto vaccinationDto) {
		String url = "http://localhost:8090/vaccinations/update/" + id;
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.put(url, vaccinationDto);
		return "redirect:/vaccinations";
	}

	@GetMapping("/searchVaccination")
	public String searchVaccination(@RequestParam("id") int id, Model model) {
		try {
			String apiUrl = BASE_URL + "/vaccinations/" + id;
			RestTemplate restTemplate = new RestTemplate();
			String response = restTemplate.getForObject(apiUrl, String.class);

			// Parse JSON response
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode rootNode = objectMapper.readTree(response);
			JsonNode vaccinationNode = rootNode.get("data");

			if (vaccinationNode == null) {
				model.addAttribute("error", "No vaccination found with ID: " + id);
				return "searchResult";
			}

			Map<String, Object> vaccination = objectMapper.convertValue(vaccinationNode, Map.class);
			model.addAttribute("vaccination", vaccination);
		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("error", "Error fetching vaccination details.");
		}
		return "searchResult";
	}

	@GetMapping("/insertVaccination")
	public String insertVaccination() {
		return "insertVaccination"; // Returns the form for inserting data
	}

	@PostMapping("/saveVaccination")
	public String saveVaccination(@ModelAttribute VaccinationDto vaccinationDto) {
		try {
			String apiUrl = BASE_URL + "/vaccinations/" + "save";
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.postForObject(apiUrl, vaccinationDto, Void.class);
		} catch (Exception e) {
			e.printStackTrace();
			return "error"; // Redirect to error page if insertion fails
		}
		return "redirect:/vaccinations"; // Redirect to the vaccinations list after success
	}

}
