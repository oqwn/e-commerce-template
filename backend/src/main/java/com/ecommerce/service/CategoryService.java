package com.ecommerce.service;

import com.ecommerce.dto.CategoryDto;
import com.ecommerce.dto.CreateCategoryRequest;
import com.ecommerce.dto.UpdateCategoryRequest;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.mapper.CategoryMapper;
import com.ecommerce.mapper.ProductMapper;
import com.ecommerce.model.Category;
import com.ecommerce.util.SlugUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryMapper categoryMapper;
    private final ProductMapper productMapper;
    
    @Transactional
    public CategoryDto createCategory(CreateCategoryRequest request) {
        // Validate parent category if specified
        if (request.getParentId() != null) {
            Category parent = categoryMapper.findById(request.getParentId());
            if (parent == null) {
                throw new ResourceNotFoundException("Parent category not found");
            }
        }
        
        Category category = Category.builder()
                .name(request.getName())
                .slug(generateUniqueSlug(request.getName()))
                .description(request.getDescription())
                .parentId(request.getParentId())
                .imageUrl(request.getImageUrl())
                .isActive(true)
                .displayOrder(request.getDisplayOrder() != null ? request.getDisplayOrder() : 0)
                .build();
        
        categoryMapper.insert(category);
        log.info("Category created successfully: {}", category.getId());
        
        return getCategoryById(category.getId());
    }
    
    @Transactional
    public CategoryDto updateCategory(Long categoryId, UpdateCategoryRequest request) {
        Category category = categoryMapper.findById(categoryId);
        if (category == null) {
            throw new ResourceNotFoundException("Category not found");
        }
        
        // Update fields if provided
        if (request.getName() != null) {
            category.setName(request.getName());
            if (!category.getSlug().equals(SlugUtils.generateSlug(request.getName()))) {
                category.setSlug(generateUniqueSlug(request.getName()));
            }
        }
        if (request.getDescription() != null) {
            category.setDescription(request.getDescription());
        }
        if (request.getParentId() != null) {
            // Prevent circular reference
            if (request.getParentId().equals(categoryId)) {
                throw new IllegalArgumentException("Category cannot be its own parent");
            }
            
            // Check if parent exists
            if (request.getParentId() > 0) {
                Category parent = categoryMapper.findById(request.getParentId());
                if (parent == null) {
                    throw new ResourceNotFoundException("Parent category not found");
                }
                
                // Prevent nested circular reference
                if (isDescendantOf(request.getParentId(), categoryId)) {
                    throw new IllegalArgumentException("Cannot set descendant as parent");
                }
            }
            
            category.setParentId(request.getParentId() > 0 ? request.getParentId() : null);
        }
        if (request.getImageUrl() != null) {
            category.setImageUrl(request.getImageUrl());
        }
        if (request.getIsActive() != null) {
            category.setIsActive(request.getIsActive());
        }
        if (request.getDisplayOrder() != null) {
            category.setDisplayOrder(request.getDisplayOrder());
        }
        
        categoryMapper.update(category);
        log.info("Category updated successfully: {}", categoryId);
        
        return getCategoryById(categoryId);
    }
    
    public CategoryDto getCategoryById(Long categoryId) {
        Category category = categoryMapper.findByIdWithProductCount(categoryId);
        if (category == null) {
            throw new ResourceNotFoundException("Category not found");
        }
        
        return CategoryDto.fromCategory(category);
    }
    
    public CategoryDto getCategoryBySlug(String slug) {
        Category category = categoryMapper.findBySlug(slug);
        if (category == null) {
            throw new ResourceNotFoundException("Category not found");
        }
        
        category.setProductCount(productMapper.countByCategoryId(category.getId()));
        return CategoryDto.fromCategory(category);
    }
    
    public List<CategoryDto> getAllCategories(boolean activeOnly) {
        List<Category> categories = activeOnly ? 
                categoryMapper.findAllActive() : 
                categoryMapper.findAll();
                
        return categories.stream()
                .map(CategoryDto::fromCategory)
                .collect(Collectors.toList());
    }
    
    public List<CategoryDto> getRootCategories() {
        List<Category> categories = categoryMapper.findRootCategories();
        return categories.stream()
                .map(CategoryDto::fromCategory)
                .collect(Collectors.toList());
    }
    
    public List<CategoryDto> getChildCategories(Long parentId) {
        List<Category> categories = categoryMapper.findByParentId(parentId);
        return categories.stream()
                .map(CategoryDto::fromCategory)
                .collect(Collectors.toList());
    }
    
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = categoryMapper.findById(categoryId);
        if (category == null) {
            throw new ResourceNotFoundException("Category not found");
        }
        
        // Check if category has products
        if (categoryMapper.hasProducts(categoryId)) {
            throw new IllegalStateException("Cannot delete category with products");
        }
        
        // Check if category has children
        if (categoryMapper.hasChildren(categoryId)) {
            throw new IllegalStateException("Cannot delete category with subcategories");
        }
        
        categoryMapper.delete(categoryId);
        log.info("Category deleted successfully: {}", categoryId);
    }
    
    private String generateUniqueSlug(String name) {
        String baseSlug = SlugUtils.generateSlug(name);
        String slug = baseSlug;
        int counter = 1;
        
        while (categoryMapper.findBySlug(slug) != null) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }
    
    private boolean isDescendantOf(Long descendantId, Long ancestorId) {
        Category current = categoryMapper.findById(descendantId);
        while (current != null && current.getParentId() != null) {
            if (current.getParentId().equals(ancestorId)) {
                return true;
            }
            current = categoryMapper.findById(current.getParentId());
        }
        return false;
    }
}