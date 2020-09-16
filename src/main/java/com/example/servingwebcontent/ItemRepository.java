package com.example.servingwebcontent;

import com.example.servingwebcontent.entities.Category;
import com.example.servingwebcontent.entities.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Long> {

    List<Item> findByCategory(String category);
    boolean existsByCategory(String category);
    List<Item> findByExpirationStateNot(String expirationState);
}
