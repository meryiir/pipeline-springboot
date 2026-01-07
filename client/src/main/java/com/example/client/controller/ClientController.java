package com.example.client.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/clients")
public class ClientController {

	@GetMapping
	public List<String> getClients() {
		return Arrays.asList("John Doe", "Jane Smith", "Bob Johnson", "Alice Brown");
	}

	@GetMapping("/health")
	public String health() {
		return "Client Service is UP";
	}
}