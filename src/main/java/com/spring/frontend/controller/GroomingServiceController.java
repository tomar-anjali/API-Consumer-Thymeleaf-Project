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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.frontend.dto.GroomingServiceDto;

@Controller
@RequestMapping("/services")
public class GroomingServiceController {

    @Autowired
    private RestTemplate restTemplate;

    private final String BASE_URL = "http://localhost:8091";

    @GetMapping
    public String getAllServices(@RequestParam(value = "search", required = false) String search, Model model) {
        try {
            String url = BASE_URL + "/services";
            if (search != null && !search.isEmpty()) {
                url += "?search=" + search; // Modify the URL to include search query
            }

            String response = restTemplate.getForObject(url, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode servicesNode = rootNode.get("data");
            List<Map<String, Object>> services = objectMapper.convertValue(servicesNode, List.class);
            model.addAttribute("services", services);
            model.addAttribute("search", search); // Add search term to model for persistence in the form
            return "services"; // Return the correct view name
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to fetch services: " + e.getMessage());
            return "error";
        }
    }

    @GetMapping("/add")
    public String addService(Model model) {
        model.addAttribute("service", new GroomingServiceDto());
        return "addService";
    }

    @PostMapping("/save")
    public String saveGroomingService(@ModelAttribute GroomingServiceDto groomingServiceDto) {
        try {
            restTemplate.postForObject(BASE_URL + "/services/add", groomingServiceDto, GroomingServiceDto.class);
            return "redirect:/services";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    @GetMapping("/edit/{id}")
    public String editService(@PathVariable("id") Long id, Model model) {
        try {
            String response = restTemplate.getForObject(BASE_URL + "/services/" + id, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode serviceNode = rootNode.get("data");
            GroomingServiceDto groomingServiceDto = objectMapper.treeToValue(serviceNode, GroomingServiceDto.class);
            model.addAttribute("service", groomingServiceDto);
            return "editService";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Failed to fetch service: " + e.getMessage());
            return "error";
        }
    }

    @PostMapping("/update/{id}")
    public String updateGroomingService(@PathVariable("id") Long id, @ModelAttribute GroomingServiceDto groomingServiceDto) {
        try {
            restTemplate.put(BASE_URL + "/services/update/" + id, groomingServiceDto);
            return "redirect:/services";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }

    // Implementing Delete functionality (Optional)
    @GetMapping("/delete/{id}")
    public String deleteService(@PathVariable("id") Long id) {
        try {
            restTemplate.delete(BASE_URL + "/services/" + id);
            return "redirect:/services";
        } catch (Exception e) {
            e.printStackTrace();
            return "error";
        }
    }
}
