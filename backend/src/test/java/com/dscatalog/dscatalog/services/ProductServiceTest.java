package com.dscatalog.dscatalog.services;

import com.dscatalog.dscatalog.dto.ProductDTO;
import com.dscatalog.dscatalog.entities.Product;
import com.dscatalog.dscatalog.repositories.CategoryRepository;
import com.dscatalog.dscatalog.repositories.ProductRepository;
import com.dscatalog.dscatalog.services.exceptions.DataBaseException;
import com.dscatalog.dscatalog.services.exceptions.ResourceNotFoundException;
import com.dscatalog.dscatalog.tests.Factory;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
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
class ProductServiceTest {

    @InjectMocks
    private ProductService service;
    @Mock
    private  ProductRepository repository;
    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private ProductDTO dto;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        dependentId = 4L;
        product = Factory.createProduct();
        page = new PageImpl<>(List.of(product));
        dto = Factory.createProductDTO();

        when(repository.findAll((Pageable) any())).thenReturn(page);
        when(repository.save(any())).thenReturn(product);
        when(repository.findById(existingId)).thenReturn(Optional.of(product));
        when(repository.findById(nonExistingId)).thenReturn(Optional.empty());
        when(repository.getOne(existingId)).thenReturn(product);

        doNothing().when(repository).deleteById(existingId);
        doThrow(EmptyResultDataAccessException.class).when(repository).deleteById(nonExistingId);
        doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
        doThrow(ResourceNotFoundException.class).when(repository).getOne(nonExistingId);

    }

    @Test
    void insertShouldReturnProduct() {
        ProductDTO result = service.insert(dto);

        assertNotNull(result);

    }

    @Test
    void updateShouldReturnResourceNotFoundExceptionWhenNonExistingId() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.update(nonExistingId, dto);
        });
    }

    @Test
    void updateShouldReturnProductWhenExistingId() {
        ProductDTO result = service.update(existingId, dto);

        assertNotNull(result);
        verify(repository).save(product);

    }

    @Test
    void findByIdShouldReturnResourceNotFoundExceptionWhenNonExistingId() {
        assertThrows(ResourceNotFoundException.class, () -> {
            service.findById(nonExistingId);
        });
        verify(repository).findById(nonExistingId);

    }

    @Test
    void findByIdShouldReturnProductWhenExistingId() {
        ProductDTO result = service.findById(existingId);

        assertNotNull(result);
        verify(repository).findById(existingId);

    }

    @Test
    void findAllPagedShouldReturnPage() {
        Pageable pageable = PageRequest.of(0,10);
        Page<ProductDTO> result = service.findAllPaged(pageable);

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