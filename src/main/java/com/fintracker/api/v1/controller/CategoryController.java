package com.fintracker.api.v1.controller;

import com.fintracker.api.v1.dto.CategoryDTO;
import com.fintracker.api.v1.mapper.CategoryMapper;
import com.fintracker.core.domain.Category;
import com.fintracker.core.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Categories", description = "Category management API")
public class CategoryController {

    private final CategoryService categoryService;
    private final CategoryMapper categoryMapper;

    @GetMapping
    @Operation(summary = "Get all categories", description = "Get a list of all categories")
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Get category details by ID")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(categoryMapper.toDTO(category));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get categories by type", description = "Get a list of categories by type")
    public ResponseEntity<List<CategoryDTO>> getCategoriesByType(@PathVariable String type) {
        List<Category> categories = categoryService.getCategoriesByType(type);
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }

    @GetMapping("/root")
    @Operation(summary = "Get root categories", description = "Get a list of root categories (without parent)")
    public ResponseEntity<List<CategoryDTO>> getRootCategories() {
        List<Category> categories = categoryService.getRootCategories();
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }

    @GetMapping("/subcategories/{parentId}")
    @Operation(summary = "Get subcategories", description = "Get a list of subcategories for a parent category")
    public ResponseEntity<List<CategoryDTO>> getSubcategories(@PathVariable Long parentId) {
        List<Category> categories = categoryService.getSubcategories(parentId);
        List<CategoryDTO> categoryDTOs = categories.stream()
                .map(categoryMapper::toDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(categoryDTOs);
    }

    @PostMapping
    @Operation(summary = "Create category", description = "Create a new category")
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        Category savedCategory = categoryService.createCategory(category);
        return ResponseEntity.ok(categoryMapper.toDTO(savedCategory));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Update an existing category")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDTO) {
        Category category = categoryMapper.toEntity(categoryDTO);
        Category updatedCategory = categoryService.updateCategory(id, category);
        return ResponseEntity.ok(categoryMapper.toDTO(updatedCategory));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Delete a category by ID")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}