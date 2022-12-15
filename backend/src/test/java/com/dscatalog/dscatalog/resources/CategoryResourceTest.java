package com.dscatalog.dscatalog.resources;

import com.dscatalog.dscatalog.dto.CategoryDTO;
import com.dscatalog.dscatalog.services.CategoryService;
import com.dscatalog.dscatalog.services.exceptions.DataBaseException;
import com.dscatalog.dscatalog.services.exceptions.ResourceNotFoundException;
import com.dscatalog.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryResource.class)
class CategoryResourceTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CategoryService service;
    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDTO categoryDTO;

    private PageImpl<CategoryDTO> page;
    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;

        categoryDTO = Factory.createCategoryDTO();
        page = new PageImpl<>(List.of(categoryDTO));
        when(service.findAllPaged(any())).thenReturn(page);

        when(service.insert(any())).thenReturn(categoryDTO);

        when(service.findById(existingId)).thenReturn(categoryDTO);
        when(service.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        when(service.update(eq(existingId), any())).thenReturn(categoryDTO);
        when(service.update(eq(nonExistingId), any())).thenThrow(ResourceNotFoundException.class);

        doNothing().when(service).delete(existingId);
        doThrow(ResourceNotFoundException.class).when(service).delete(nonExistingId);
        doThrow(DataBaseException.class).when(service).delete(dependentId);
    }

    @Test
    void insertShouldReturnCategory() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/categories")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isCreated());

    }
    @Test
    void deleteShouldReturnNoContentWhenIdExists() throws Exception {

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.delete("/categories/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isNoContent());

    }

    @Test
    void deleteShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.delete("/categories/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isNotFound());

    }

    @Test
    void findAllShouldReturnPage() throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/categories")
                .accept(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isOk());

    }

    @Test
    void findByIdShouldReturnCategoryWhenIdExists() throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/categories/{id}", existingId)
                .accept(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isOk());
        perform.andExpect(jsonPath("$.id").exists());
        perform.andExpect(jsonPath("$.name").exists());

    }

    @Test
    void findByIdShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/categories/{id}", nonExistingId)
                .accept(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isNotFound());

    }

    @Test
    void updateShouldReturnCategoryDTOWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/categories/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isOk());

        perform.andExpect(jsonPath("$.id").exists());
        perform.andExpect(jsonPath("$.name").exists());


    }

    @Test
    void updateShouldReturnNotFoundWhenIdDoesNotExists() throws Exception {

        String jsonBody = objectMapper.writeValueAsString(categoryDTO);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/categories/{id}", nonExistingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isNotFound());

    }

}