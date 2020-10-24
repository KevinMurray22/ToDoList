package com.example.servingwebcontent;

import com.example.servingwebcontent.entities.Category;
import com.example.servingwebcontent.entities.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ItemRepository extends CrudRepository<Item, Long> {

    List<Item> findByCategory(String category);
    boolean existsByCategory(String category);
    List<Item> findByExpirationStateNot(String expirationState);

    List<Item> findByCategoryAndUserName(String category, String userName);
    boolean existsByCategoryAndUserName(String category, String userName);
    List<Item> findByExpirationStateNotAndUserName(String expirationState, String userName);
    List<Item> findByUserName(String userName);
    boolean existsByCategoryAndUserNameAndExpirationState(String category, String userName, String expirationState);
}
