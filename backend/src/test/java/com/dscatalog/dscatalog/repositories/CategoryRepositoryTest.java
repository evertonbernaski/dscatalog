package com.dscatalog.dscatalog.repositories;

import com.dscatalog.dscatalog.entities.Category;
import com.dscatalog.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Optional;

@DataJpaTest
public class CategoryRepositoryTest {

    @Autowired
    private CategoryRepository repository;

    private long existingId;
    private long nonExistingId;
    private long countTotalCategories;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalCategories = 25L;
    }

    @Test
    void saveShouldPersistWithAutoIncrementWhenIdIsNull() {

        Category category = Factory.createCategory();
        category.setId(null);

        category = repository.save(category);

        Assertions.assertNotNull(category.getId());
    }

    @Test
    void deleteShouldDeleteObjectWhenIdExists() {

        repository.deleteById(existingId);

        Optional<Category> result = repository.findById(existingId);

        Assertions.assertFalse(result.isPresent());
    }

    @Test
    void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(EmptyResultDataAccessException.class, () -> {
            repository.deleteById(nonExistingId);
        });
    }

    @Test
    void findByIdShouldReturnNonEmptyOptionalWhenIdExists() {

        Optional<Category> id = repository.findById(existingId);

        Assertions.assertTrue(id.isPresent());
    }

    @Test
    void findByIdShouldReturnEmptyOptionalWhenIdDoesNotExists() {

        Optional<Category> id = repository.findById(nonExistingId);

        Assertions.assertTrue(id.isEmpty());
    }
}
