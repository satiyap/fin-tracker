package com.fintracker.core.service;

import com.fintracker.core.domain.Category;
import com.fintracker.core.exception.ResourceNotFoundException;
import com.fintracker.core.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Category> getCategoriesByType(String type) {
        return categoryRepository.findByType(type);
    }

    @Transactional(readOnly = true)
    public List<Category> getRootCategories() {
        return categoryRepository.findByParentIsNull();
    }

    @Transactional(readOnly = true)
    public List<Category> getSubcategories(Long parentId) {
        return categoryRepository.findByParentId(parentId);
    }

    @Transactional
    public Category createCategory(Category category) {
        if (category.getParent() != null && category.getParent().getId() != null) {
            Category parent = getCategoryById(category.getParent().getId());
            category.setParent(parent);
        }
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, Category categoryDetails) {
        Category category = getCategoryById(id);
        
        category.setName(categoryDetails.getName());
        category.setDescription(categoryDetails.getDescription());
        category.setType(categoryDetails.getType());
        
        if (categoryDetails.getParent() != null && categoryDetails.getParent().getId() != null) {
            Category parent = getCategoryById(categoryDetails.getParent().getId());
            category.setParent(parent);
        } else {
            category.setParent(null);
        }
        
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        Category category = getCategoryById(id);
        categoryRepository.delete(category);
    }

    @Transactional
    public Category findOrCreateCategory(String name, String type) {
        Optional<Category> existingCategory = categoryRepository.findByNameAndType(name, type);
        if (existingCategory.isPresent()) {
            return existingCategory.get();
        } else {
            Category newCategory = new Category();
            newCategory.setName(name);
            newCategory.setType(type);
            return categoryRepository.save(newCategory);
        }
    }
}