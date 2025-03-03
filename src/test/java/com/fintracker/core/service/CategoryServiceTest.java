package com.fintracker.core.service;

import com.fintracker.core.domain.Category;
import com.fintracker.core.exception.ResourceNotFoundException;
import com.fintracker.core.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;
    private Category parentCategory;

    @BeforeEach
    void setUp() {
        parentCategory = Category.builder()
                .id(1L)
                .name("Parent Category")
                .type("EXPENSE")
                .description("Parent category description")
                .build();

        category = Category.builder()
                .id(2L)
                .name("Test Category")
                .type("EXPENSE")
                .description("Test category description")
                .parent(parentCategory)
                .build();
    }

    @Test
    void getAllCategories_ShouldReturnAllCategories() {
        // Arrange
        List<Category> categories = Arrays.asList(parentCategory, category);
        when(categoryRepository.findAll()).thenReturn(categories);

        // Act
        List<Category> result = categoryService.getAllCategories();

        // Assert
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    void getCategoryById_WithValidId_ShouldReturnCategory() {
        // Arrange
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.getCategoryById(2L);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getId());
        assertEquals("Test Category", result.getName());
        verify(categoryRepository, times(1)).findById(2L);
    }

    @Test
    void getCategoryById_WithInvalidId_ShouldThrowException() {
        // Arrange
        when(categoryRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> categoryService.getCategoryById(999L));
        verify(categoryRepository, times(1)).findById(999L);
    }

    @Test
    void getCategoriesByType_ShouldReturnCategoriesOfSpecifiedType() {
        // Arrange
        List<Category> categories = Arrays.asList(parentCategory, category);
        when(categoryRepository.findByType("EXPENSE")).thenReturn(categories);

        // Act
        List<Category> result = categoryService.getCategoriesByType("EXPENSE");

        // Assert
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findByType("EXPENSE");
    }

    @Test
    void getRootCategories_ShouldReturnCategoriesWithoutParent() {
        // Arrange
        List<Category> rootCategories = Arrays.asList(parentCategory);
        when(categoryRepository.findByParentIsNull()).thenReturn(rootCategories);

        // Act
        List<Category> result = categoryService.getRootCategories();

        // Assert
        assertEquals(1, result.size());
        assertEquals("Parent Category", result.get(0).getName());
        verify(categoryRepository, times(1)).findByParentIsNull();
    }

    @Test
    void getSubcategories_ShouldReturnChildCategories() {
        // Arrange
        List<Category> subcategories = Arrays.asList(category);
        when(categoryRepository.findByParentId(1L)).thenReturn(subcategories);

        // Act
        List<Category> result = categoryService.getSubcategories(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Test Category", result.get(0).getName());
        verify(categoryRepository, times(1)).findByParentId(1L);
    }

    @Test
    void createCategory_WithValidCategory_ShouldReturnSavedCategory() {
        // Arrange
        Category newCategory = Category.builder()
                .name("New Category")
                .type("INCOME")
                .description("New category description")
                .build();

        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        // Act
        Category result = categoryService.createCategory(newCategory);

        // Assert
        assertNotNull(result);
        assertEquals("New Category", result.getName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void createCategory_WithParent_ShouldSetParentAndReturnSavedCategory() {
        // Arrange
        Category newCategory = Category.builder()
                .name("New Category")
                .type("INCOME")
                .description("New category description")
                .build();

        Category parent = new Category();
        parent.setId(1L);
        newCategory.setParent(parent);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        // Act
        Category result = categoryService.createCategory(newCategory);

        // Assert
        assertNotNull(result);
        assertEquals("New Category", result.getName());
        verify(categoryRepository, times(1)).findById(1L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void updateCategory_WithValidCategory_ShouldReturnUpdatedCategory() {
        // Arrange
        Category updatedCategory = Category.builder()
                .id(2L)
                .name("Updated Category")
                .type("EXPENSE")
                .description("Updated description")
                .build();

        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        when(categoryRepository.save(any(Category.class))).thenReturn(updatedCategory);

        // Act
        Category result = categoryService.updateCategory(2L, updatedCategory);

        // Assert
        assertNotNull(result);
        assertEquals("Updated Category", result.getName());
        assertEquals("Updated description", result.getDescription());
        verify(categoryRepository, times(1)).findById(2L);
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    void deleteCategory_WithValidId_ShouldDeleteCategory() {
        // Arrange
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(category));
        doNothing().when(categoryRepository).delete(category);

        // Act
        categoryService.deleteCategory(2L);

        // Assert
        verify(categoryRepository, times(1)).findById(2L);
        verify(categoryRepository, times(1)).delete(category);
    }

    @Test
    void findOrCreateCategory_WithExistingCategory_ShouldReturnExistingCategory() {
        // Arrange
        when(categoryRepository.findByNameAndType("Test Category", "EXPENSE"))
                .thenReturn(Optional.of(category));

        // Act
        Category result = categoryService.findOrCreateCategory("Test Category", "EXPENSE");

        // Assert
        assertNotNull(result);
        assertEquals("Test Category", result.getName());
        verify(categoryRepository, times(1)).findByNameAndType("Test Category", "EXPENSE");
        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    void findOrCreateCategory_WithNewCategory_ShouldCreateAndReturnNewCategory() {
        // Arrange
        when(categoryRepository.findByNameAndType("New Category", "INCOME"))
                .thenReturn(Optional.empty());
        
        Category newCategory = Category.builder()
                .name("New Category")
                .type("INCOME")
                .build();
        
        when(categoryRepository.save(any(Category.class))).thenReturn(newCategory);

        // Act
        Category result = categoryService.findOrCreateCategory("New Category", "INCOME");

        // Assert
        assertNotNull(result);
        assertEquals("New Category", result.getName());
        assertEquals("INCOME", result.getType());
        verify(categoryRepository, times(1)).findByNameAndType("New Category", "INCOME");
        verify(categoryRepository, times(1)).save(any(Category.class));
    }
}