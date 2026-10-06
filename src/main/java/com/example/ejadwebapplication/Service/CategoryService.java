package com.example.ejadwebapplication.Service;

import com.example.ejadwebapplication.Api.ApiException;
import com.example.ejadwebapplication.DTO.CategoryDTO;
import com.example.ejadwebapplication.Model.Category;
import com.example.ejadwebapplication.Model.Report;
import com.example.ejadwebapplication.Repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories(){
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Integer id){
        return categoryRepository.findById(id).orElseThrow(() -> new ApiException("Category not found with provided id"));
    }

    public Category addCategory(CategoryDTO categoryDTO){

        Category category = new Category();

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        return categoryRepository.save(category);
    }

    public Category updateCategory(Integer id, CategoryDTO categoryDTO){

        Category category = categoryRepository.findById(id).orElseThrow(()-> new ApiException("Category not found to be updated "));

        category.setName(categoryDTO.getName());
        category.setDescription(categoryDTO.getDescription());
        return categoryRepository.save(category);
    }

    public void deleteCategory(Integer id){
        Category category = categoryRepository.findById(id).orElseThrow(() -> new ApiException("Category not found to be deleted"));
        categoryRepository.delete(category);
    }

    public List<Report> getReportsByCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ApiException("Category not found with provided id"));
        return new ArrayList<>(category.getReports());
    }

    public Integer getReportCountByCategory(Integer categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ApiException("Category not found with provided id"));
        return category.getReports().size();
    }


}
