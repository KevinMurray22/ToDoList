package com.example.servingwebcontent;

import com.example.servingwebcontent.entities.Category;
import com.example.servingwebcontent.entities.Item;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, String> {

    Category findByCategory(String category);
    List<Category> findByParentCategory(String parentCategory);
    List<Category> findByHasChild(boolean hasChild);

}
