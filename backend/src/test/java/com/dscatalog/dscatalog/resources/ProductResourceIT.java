package com.dscatalog.dscatalog.resources;

import com.dscatalog.dscatalog.dto.ProductDTO;
import com.dscatalog.dscatalog.tests.Factory;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private long existingId;
    private long nonExistingId;
    private long countTotalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/products?page=0&size=12&sort=name,asc")
                .accept(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isOk());
        perform.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
        perform.andExpect(jsonPath("$.content").exists());
        perform.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        perform.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
        perform.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
        
    }

    @Test
    void updateShouldReturnProductDTOWhenIdExists() throws Exception {

        ProductDTO productDTO = Factory.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        String expectedName = productDTO.getName();
        String expectedDescription = productDTO.getDescription();

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", existingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isOk());

        perform.andExpect(jsonPath("$.id").exists());
        perform.andExpect(jsonPath("$.name").value(expectedName));
        perform.andExpect(jsonPath("$.description").value(expectedDescription));

    }

    @Test
    void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {

        ProductDTO productDTO = Factory.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", nonExistingId)
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON));

        perform.andExpect(status().isNotFound());
    }

















}
