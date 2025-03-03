package com.fintracker.api.v1.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fintracker.api.v1.dto.CategoryDTO;
import com.fintracker.api.v1.mapper.CategoryMapper;
import com.fintracker.core.domain.Category;
import com.fintracker.core.service.CategoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CategoryController.class)
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private CategoryMapper categoryMapper;

    private Category category;
    private Category parentCategory;
    private CategoryDTO categoryDTO;
    private CategoryDTO parentCategoryDTO;

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

        parentCategoryDTO = CategoryDTO.builder()
                .id(1L)
                .name("Parent Category")
                .type("EXPENSE")
                .description("Parent category description")
                .build();

        categoryDTO = CategoryDTO.builder()
                .id(2L)
                .name("Test Category")
                .type("EXPENSE")
                .description("Test category description")
                .parentId(1L)
                .build();
    }

    @Test
    @WithMockUser
    void getAllCategories_ShouldReturnAllCategories() throws Exception {
        // Arrange
        List<Category> categories = Arrays.asList(parentCategory, category);
        when(categoryService.getAllCategories()).thenReturn(categories);
        when(categoryMapper.toDTO(parentCategory)).thenReturn(parentCategoryDTO);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Parent Category")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Test Category")))
                .andExpect(jsonPath("$[1].parentId", is(1)));

        verify(categoryService, times(1)).getAllCategories();
        verify(categoryMapper, times(1)).toDTO(parentCategory);
        verify(categoryMapper, times(1)).toDTO(category);
    }

    @Test
    @WithMockUser
    void getCategoryById_WithValidId_ShouldReturnCategory() throws Exception {
        // Arrange
        when(categoryService.getCategoryById(2L)).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/2"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Test Category")))
                .andExpect(jsonPath("$.type", is("EXPENSE")))
                .andExpect(jsonPath("$.description", is("Test category description")))
                .andExpect(jsonPath("$.parentId", is(1)));

        verify(categoryService, times(1)).getCategoryById(2L);
        verify(categoryMapper, times(1)).toDTO(category);
    }

    @Test
    @WithMockUser
    void getCategoriesByType_WithValidType_ShouldReturnCategories() throws Exception {
        // Arrange
        List<Category> categories = Arrays.asList(parentCategory, category);
        when(categoryService.getCategoriesByType("EXPENSE")).thenReturn(categories);
        when(categoryMapper.toDTO(parentCategory)).thenReturn(parentCategoryDTO);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/type/EXPENSE"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Parent Category")))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Test Category")));

        verify(categoryService, times(1)).getCategoriesByType("EXPENSE");
        verify(categoryMapper, times(1)).toDTO(parentCategory);
        verify(categoryMapper, times(1)).toDTO(category);
    }

    @Test
    @WithMockUser
    void getRootCategories_ShouldReturnRootCategories() throws Exception {
        // Arrange
        List<Category> rootCategories = Arrays.asList(parentCategory);
        when(categoryService.getRootCategories()).thenReturn(rootCategories);
        when(categoryMapper.toDTO(parentCategory)).thenReturn(parentCategoryDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/root"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Parent Category")));

        verify(categoryService, times(1)).getRootCategories();
        verify(categoryMapper, times(1)).toDTO(parentCategory);
    }

    @Test
    @WithMockUser
    void getSubcategories_WithValidParentId_ShouldReturnSubcategories() throws Exception {
        // Arrange
        List<Category> subcategories = Arrays.asList(category);
        when(categoryService.getSubcategories(1L)).thenReturn(subcategories);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        // Act & Assert
        mockMvc.perform(get("/api/v1/categories/subcategories/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(2)))
                .andExpect(jsonPath("$[0].name", is("Test Category")))
                .andExpect(jsonPath("$[0].parentId", is(1)));

        verify(categoryService, times(1)).getSubcategories(1L);
        verify(categoryMapper, times(1)).toDTO(category);
    }

    @Test
    @WithMockUser
    void createCategory_WithValidCategory_ShouldReturnCreatedCategory() throws Exception {
        // Arrange
        when(categoryMapper.toEntity(categoryDTO)).thenReturn(category);
        when(categoryService.createCategory(category)).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        // Act & Assert
        mockMvc.perform(post("/api/v1/categories")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Test Category")))
                .andExpect(jsonPath("$.type", is("EXPENSE")))
                .andExpect(jsonPath("$.description", is("Test category description")))
                .andExpect(jsonPath("$.parentId", is(1)));

        verify(categoryMapper, times(1)).toEntity(categoryDTO);
        verify(categoryService, times(1)).createCategory(category);
        verify(categoryMapper, times(1)).toDTO(category);
    }

    @Test
    @WithMockUser
    void updateCategory_WithValidCategory_ShouldReturnUpdatedCategory() throws Exception {
        // Arrange
        when(categoryMapper.toEntity(categoryDTO)).thenReturn(category);
        when(categoryService.updateCategory(eq(2L), any(Category.class))).thenReturn(category);
        when(categoryMapper.toDTO(category)).thenReturn(categoryDTO);

        // Act & Assert
        mockMvc.perform(put("/api/v1/categories/2")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(categoryDTO)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(2)))
                .andExpect(jsonPath("$.name", is("Test Category")))
                .andExpect(jsonPath("$.type", is("EXPENSE")))
                .andExpect(jsonPath("$.description", is("Test category description")))
                .andExpect(jsonPath("$.parentId", is(1)));

        verify(categoryMapper, times(1)).toEntity(categoryDTO);
        verify(categoryService, times(1)).updateCategory(eq(2L), any(Category.class));
        verify(categoryMapper, times(1)).toDTO(category);
    }

    @Test
    @WithMockUser
    void deleteCategory_WithValidId_ShouldReturnNoContent() throws Exception {
        // Arrange
        doNothing().when(categoryService).deleteCategory(2L);

        // Act & Assert
        mockMvc.perform(delete("/api/v1/categories/2")
                .with(csrf()))
                .andExpect(status().isNoContent());

        verify(categoryService, times(1)).deleteCategory(2L);
    }
}