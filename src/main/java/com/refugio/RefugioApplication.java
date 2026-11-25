package com.refugio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada del artefacto refugio-api.jar de la vista de despliegue.
 *
 * El codigo se reparte en los cuatro paquetes de la vista de desarrollo:
 *   presentacion -> Presentation_Controller
 *   negocio      -> Business_Service
 *   datos        -> Data_Repository
 *   modelo       -> Domain_Model
 */
@SpringBootApplication
public class RefugioApplication {

    public static void main(String[] args) {
        SpringApplication.run(RefugioApplication.class, args);
    }
}
