package com.fintracker.core.repository;

import com.fintracker.core.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByType(String type);
    List<Category> findByParentId(Long parentId);
    List<Category> findByParentIsNull();
    Optional<Category> findByNameAndType(String name, String type);
}