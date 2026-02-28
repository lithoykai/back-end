package com.joaopenascimento.backend.config;

import com.joaopenascimento.backend.model.Property;
import com.joaopenascimento.backend.model.User;
import com.joaopenascimento.backend.model.enums.PropertyType;
import com.joaopenascimento.backend.model.enums.UserRole;
import com.joaopenascimento.backend.repositories.PropertyRepository;
import com.joaopenascimento.backend.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@Configuration
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, 
                      PropertyRepository propertyRepository, 
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.propertyRepository = propertyRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        seedUsersAndProperties();
    }

    private void seedUsersAndProperties() {
        if (userRepository.count() > 0) {
            return;
        }

        // --- USUÁRIOS ---
        User admin = new User();
        admin.setName("Administrador do Sistema");
        admin.setEmail("admin@imobiliaria.com");
        admin.setPassword(passwordEncoder.encode("123456"));
        admin.setRole(UserRole.ADMIN);

        User corretor = new User();
        corretor.setName("João Corretor");
        corretor.setEmail("corretor@imobiliaria.com");
        corretor.setPassword(passwordEncoder.encode("123456"));
        corretor.setRole(UserRole.CORRETOR);

        User cliente = new User();
        cliente.setName("Maria Cliente");
        cliente.setEmail("cliente@gmail.com");
        cliente.setPassword(passwordEncoder.encode("123456"));
        cliente.setRole(UserRole.CLIENTE);

        userRepository.saveAll(Arrays.asList(admin, corretor, cliente));

        // --- IMÓVEIS ---
        
        // Imóvel 1: Apartamento
        Property prop1 = new Property();
        prop1.setName("Apartamento Luxo Boa Viagem");
        prop1.setDescription("Apartamento com vista para o mar, 3 quartos, 1 suíte, varanda gourmet. Prédio com piscina e academia.");
        prop1.setType(PropertyType.APARTAMENTO);
        prop1.setValue(850000.0);
        prop1.setArea(120);
        prop1.setBedrooms(3);
        prop1.setAddress("Av. Boa Viagem, 1000");
        prop1.setCity("Recife");
        prop1.setState("PE");
        prop1.setActive(true);
        prop1.setBroker(corretor);
        prop1.setImageUrls(
            "https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?auto=format&fit=crop&w=800&q=80"
        );

        // Imóvel 2: Casa
        Property prop2 = new Property();
        prop2.setName("Casa Espaçosa em Casa Forte");
        prop2.setDescription("Casa colonial reformada, amplo jardim e piscina. Localização privilegiada próxima à praça.");
        prop2.setType(PropertyType.CASA);
        prop2.setValue(1200000.0);
        prop2.setArea(350);
        prop2.setBedrooms(5);
        prop2.setAddress("Praça de Casa Forte");
        prop2.setCity("Recife");
        prop2.setState("PE");
        prop2.setActive(true);
        prop2.setBroker(corretor);
        prop2.setImageUrls(
            "https://images.unsplash.com/photo-1568605114967-8130f3a36994?auto=format&fit=crop&w=800&q=80"
        );

        // Imóvel 3: Terreno
        Property prop3 = new Property();
        prop3.setName("Terreno em Condomínio Fechado");
        prop3.setDescription("Lote plano, pronto para construir, área de lazer completa no condomínio com haras e lago.");
        prop3.setType(PropertyType.TERRENO);
        prop3.setValue(450000.0);
        prop3.setArea(500);
        prop3.setBedrooms(0); 
        prop3.setAddress("BR 232, Km 10");
        prop3.setCity("Gravatá");
        prop3.setState("PE");
        prop3.setActive(true);
        prop3.setBroker(corretor);
        prop3.setImageUrls(
            "https://images.unsplash.com/photo-1500382017468-9049fed747ef?auto=format&fit=crop&w=800&q=80"
        );

        propertyRepository.saveAll(Arrays.asList(prop1, prop2, prop3));

        System.out.println("Banco de dados populado com usuários e imóveis (com imagens)!");
    }
}