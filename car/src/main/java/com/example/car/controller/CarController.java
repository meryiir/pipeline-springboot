package com.example.car.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/cars")
public class CarController {

	@GetMapping
	public List<String> getCars() {
		return Arrays.asList("Toyota", "Honda", "BMW", "Mercedes");
	}

	@GetMapping("/health")
	public String health() {
		return "Car Service is UP";
	}
}