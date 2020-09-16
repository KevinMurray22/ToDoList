package com.example.servingwebcontent;

import java.util.Arrays;
import java.util.Calendar;

import com.example.servingwebcontent.entities.Item;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {

        SpringApplication.run(Application.class, args);
    }
/*
    @Bean
    public CommandLineRunner demo(ItemRepository repository) {
        return (args) -> {
        Item item = new Item();
        item.setName("Dad's real real birthday");
        item.setCategory("Birthdays");
        Calendar calendar = Calendar.getInstance();
        calendar.set(2020,8,6);
       // calendar.add(Calendar.DATE, 6);
        item.setExpiration(calendar);
        item.setDescription("It's dad's real real birthday.");
        item.setExpirationState("Valid");
        repository.save(item);

        // fetch all customers
        log.info("Customers found with findAll():");
        log.info("-------------------------------");
        for (Item k : repository.findAll()) {
            log.info(k.toString());
        }
        log.info("");

        };
    }

*/

}