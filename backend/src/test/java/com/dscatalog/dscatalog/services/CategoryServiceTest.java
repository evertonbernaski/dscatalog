package com.dscatalog.dscatalog.services;

import com.dscatalog.dscatalog.dto.CategoryDTO;
import com.dscatalog.dscatalog.dto.ProductDTO;
import com.dscatalog.dscatalog.entities.Category;
import com.dscatalog.dscatalog.entities.Product;
import com.dscatalog.dscatalog.repositories.CategoryRepository;
import com.dscatalog.dscatalog.services.exceptions.DataBaseException;
import com.dscatalog.dscatalog.services.exceptions.ResourceNotFoundException;
import com.dscatalog.dscatalog.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService service;

    @Mock
    private CategoryRepository repository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Category> page;
    private Category category;
    private CategoryDTO dto;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4L;
        category = Factory.createCategory();
        page = new PageImpl<>(List.of(category));
        dto = Factory.createCategoryDTO();

        when(repository.findAll((Pageable) any())).thenReturn(page);
        when(repository.save(any())).thenReturn(category);
        when(repository.findById(existingId)).thenReturn(Optional.of(category));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        when(repository.getOne(existingId)).thenReturn(category);

        doNothing().when(repository).deleteById(existingId);
        doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
        doThrow(ResourceNotFoundException.class).when(repository).getOne(nonExistingId);
    }

    @Test
    void insertShouldReturnCategory() {
        CategoryDTO result = service.insert(dto);

        assertNotNull(result);

    }

    @Test
    void updateShouldReturnResourceNotFoundExceptionWhenNonExistingId() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, dto);
        });
    }

    @Test
    void updateShouldReturnCategoryWhenExistingId() {
        CategoryDTO result = service.update(existingId, dto);

        assertNotNull(result);
        verify(repository).save(category);

    }

    @Test
    void findByIdShouldReturnResourceNotFoundExceptionWhenNonExistingId() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
        verify(repository).findById(nonExistingId);

    }

    @Test
    void findByIdShouldReturnCategoryWhenExistingId() {
        CategoryDTO result = service.findById(existingId);

        assertNotNull(result);
        verify(repository).findById(existingId);

    }

    @Test
    void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0,10);
        Page<CategoryDTO> result = service.findAllPaged(pageable);

        assertNotNull(result);
        verify(repository).findAll(pageable);

    }

    @Test
    void deleteShouldThrowDataBaseExceptionWhenDependentId() {

        assertThrows(DataBaseException.class, () -> {
            service.delete(dependentId);
        });
        verify(repository).deleteById(dependentId);

    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        assertThrows(ResourceNotFoundException.class, () -> {
            service.delete(nonExistingId);
        });
        verify(repository).deleteById(nonExistingId);

    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {

        assertDoesNotThrow(() -> {
            service.delete(existingId);
        });
        verify(repository).deleteById(existingId);

    }

}