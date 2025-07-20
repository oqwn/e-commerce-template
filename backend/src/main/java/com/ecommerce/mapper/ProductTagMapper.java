package com.ecommerce.mapper;

import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface ProductTagMapper {
    
    @Insert("INSERT INTO product_tags (product_id, tag) VALUES (#{productId}, #{tag})")
    void insert(@Param("productId") Long productId, @Param("tag") String tag);
    
    @Select("SELECT tag FROM product_tags WHERE product_id = #{productId}")
    List<String> findTagsByProductId(Long productId);
    
    @Select("SELECT DISTINCT product_id FROM product_tags WHERE tag = #{tag}")
    List<Long> findProductIdsByTag(String tag);
    
    @Delete("DELETE FROM product_tags WHERE product_id = #{productId}")
    void deleteByProductId(Long productId);
    
    @Delete("DELETE FROM product_tags WHERE product_id = #{productId} AND tag = #{tag}")
    void deleteTag(@Param("productId") Long productId, @Param("tag") String tag);
    
    @Select("SELECT tag, COUNT(*) as count FROM product_tags GROUP BY tag ORDER BY count DESC LIMIT #{limit}")
    List<TagCount> findPopularTags(Integer limit);
    
    class TagCount {
        private String tag;
        private Integer count;
        
        // Getters and setters
        public String getTag() { return tag; }
        public void setTag(String tag) { this.tag = tag; }
        public Integer getCount() { return count; }
        public void setCount(Integer count) { this.count = count; }
    }
}