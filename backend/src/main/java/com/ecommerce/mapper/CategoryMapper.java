package com.ecommerce.mapper;

import com.ecommerce.model.Category;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface CategoryMapper {
    
    @Select("SELECT * FROM categories WHERE id = #{id}")
    @Results(id = "categoryResultMap", value = {
        @Result(property = "parentId", column = "parent_id"),
        @Result(property = "imageUrl", column = "image_url"),
        @Result(property = "isActive", column = "is_active"),
        @Result(property = "displayOrder", column = "display_order"),
        @Result(property = "createdAt", column = "created_at"),
        @Result(property = "updatedAt", column = "updated_at")
    })
    Category findById(Long id);
    
    @Select("SELECT * FROM categories WHERE slug = #{slug}")
    @ResultMap("categoryResultMap")
    Category findBySlug(String slug);
    
    @Select("SELECT * FROM categories WHERE parent_id IS NULL AND is_active = 1 ORDER BY display_order")
    @ResultMap("categoryResultMap")
    List<Category> findRootCategories();
    
    @Select("SELECT * FROM categories WHERE parent_id = #{parentId} AND is_active = 1 ORDER BY display_order")
    @ResultMap("categoryResultMap")
    List<Category> findByParentId(Long parentId);
    
    @Select("SELECT * FROM categories WHERE is_active = 1 ORDER BY display_order")
    @ResultMap("categoryResultMap")
    List<Category> findAllActive();
    
    @Select("SELECT * FROM categories ORDER BY display_order")
    @ResultMap("categoryResultMap")
    List<Category> findAll();
    
    @Insert("INSERT INTO categories (name, slug, description, parent_id, image_url, is_active, display_order) " +
            "VALUES (#{name}, #{slug}, #{description}, #{parentId}, #{imageUrl}, #{isActive}, #{displayOrder})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(Category category);
    
    @Update("UPDATE categories SET name = #{name}, slug = #{slug}, description = #{description}, " +
            "parent_id = #{parentId}, image_url = #{imageUrl}, is_active = #{isActive}, " +
            "display_order = #{displayOrder} WHERE id = #{id}")
    void update(Category category);
    
    @Delete("DELETE FROM categories WHERE id = #{id}")
    void delete(Long id);
    
    @Select("SELECT c.*, COUNT(p.id) as product_count FROM categories c " +
            "LEFT JOIN products p ON c.id = p.category_id AND p.status = 'ACTIVE' " +
            "WHERE c.id = #{id} GROUP BY c.id")
    @ResultMap("categoryResultMap")
    Category findByIdWithProductCount(Long id);
    
    @Select("SELECT EXISTS(SELECT 1 FROM products WHERE category_id = #{categoryId})")
    boolean hasProducts(Long categoryId);
    
    @Select("SELECT EXISTS(SELECT 1 FROM categories WHERE parent_id = #{categoryId})")
    boolean hasChildren(Long categoryId);
}