package com.fintracker.api.v1.mapper;

import com.fintracker.api.v1.dto.CategoryDTO;
import com.fintracker.core.domain.Category;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper {
    
    public CategoryDTO toDTO(Category category) {
        if (category == null) {
            return null;
        }
        
        return CategoryDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .type(category.getType())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .build();
    }
    
    public Category toEntity(CategoryDTO categoryDTO) {
        if (categoryDTO == null) {
            return null;
        }
        
        Category category = Category.builder()
                .id(categoryDTO.getId())
                .name(categoryDTO.getName())
                .description(categoryDTO.getDescription())
                .type(categoryDTO.getType())
                .build();
        
        if (categoryDTO.getParentId() != null) {
            Category parent = new Category();
            parent.setId(categoryDTO.getParentId());
            category.setParent(parent);
        }
        
        return category;
    }
}