package com.example.servingwebcontent;

import com.example.servingwebcontent.entities.Category;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Long> {

    Category findByCategory(String category);
    List<Category> findByParentCategory(String parentCategory);
    List<Category> findByHasChild(boolean hasChild);

    Category findByCategoryAndUserName(String category, String userName);
    List<Category> findByParentCategoryAndUserName(String parentCategory, String userName);
    List<Category> findByHasChildAndUserName(boolean hasChild, String userName);
    List<Category> findByUserName(String userName);
    boolean existsByUserName(String userName);

}
