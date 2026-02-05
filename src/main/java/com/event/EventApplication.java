package com.event;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Glavna klasa Spring Boot aplikacije
 * 
 * @SpringBootApplication je kombinacija tri anotacije:
 * 1. @Configuration - označava klasu kao izvor bean definicija
 * 2. @EnableAutoConfiguration - omogućava Spring Boot auto-konfiguraciju
 * 3. @ComponentScan - skenira pakete za komponente (@Service, @Repository, @Controller, itd.)
 * 
 * Kada pokrenemo main() metodu, Spring Boot:
 * - Pokreće embedded Tomcat server (default port 8080)
 * - Učitava sve komponente iz paketa com.event i podpaketa
 * - Konfiguriše sve što je potrebno na osnovu dependency-ja u pom.xml
 */
@SpringBootApplication
public class EventApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventApplication.class, args);
    }
}
