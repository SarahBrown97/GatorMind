package com.DigitalHealth.Intervention;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.DigitalHealth.Intervention.Service.UtilityService;

@SpringBootApplication
@EnableScheduling
public class App {
	public static void main(String[] args) {
		System.out.println("starting an app");
		SpringApplication.run(App.class, args);
	}
}
