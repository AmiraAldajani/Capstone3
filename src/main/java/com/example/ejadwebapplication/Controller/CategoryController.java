package com.example.ejadwebapplication.Controller;

import com.example.ejadwebapplication.DTO.CategoryDTO;
import com.example.ejadwebapplication.Service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/get")
    public ResponseEntity<?> getAllCategories() {
        return ResponseEntity.status(200).body(categoryService.getAllCategories());
    }

    @GetMapping("get/{id}")
    public ResponseEntity<?> getCategoryById(@PathVariable Integer id) {
        return ResponseEntity.status(200).body(categoryService.getCategoryById(id));
    }

    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.status(200).body(categoryService.addCategory(categoryDTO));
    }

    @PutMapping("update/{id}")
    public ResponseEntity<?> updateCategory(@PathVariable Integer id, @Valid @RequestBody CategoryDTO categoryDTO) {
        return ResponseEntity.status(200).body(categoryService.updateCategory(id, categoryDTO));
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.status(200).body("Category deleted successfully");
    }

    @GetMapping("/{categoryId}/reports")
    public ResponseEntity<?> getReportsByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.status(200).body(categoryService.getReportsByCategory(categoryId));
    }

    @GetMapping("/{categoryId}/report-count")
    public ResponseEntity<?> getReportCountByCategory(@PathVariable Integer categoryId) {
        return ResponseEntity.status(200).body(categoryService.getReportCountByCategory(categoryId));
    }
}
