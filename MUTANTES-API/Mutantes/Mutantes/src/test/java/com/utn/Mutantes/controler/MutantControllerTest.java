package com.utn.Mutantes.controler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.utn.Mutantes.controller.MutantController;
import com.utn.Mutantes.dto.DnaRequest;
import com.utn.Mutantes.service.MutantService;
import com.utn.Mutantes.service.StatsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MutantController.class)
class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private MutantService mutantService;


    @MockitoBean
    private StatsService statsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testIsMutant() throws Exception {
        String[] dna = {"ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG"};
        DnaRequest request = new DnaRequest();
        request.setDna(dna);

        when(mutantService.analyze(dna)).thenReturn(true);

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void testIsHuman() throws Exception {
        String[] dna = {"ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG"};
        DnaRequest request = new DnaRequest();
        request.setDna(dna);

        when(mutantService.analyze(dna)).thenReturn(false);

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testInvalidDna_Returns400_InvalidCharacters() throws Exception {
        String[] dna = {"1234", "CAGT", "TTAT", "AGAC"};
        DnaRequest request = new DnaRequest();
        request.setDna(dna);


        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidDna_Returns400_NullDna() throws Exception {
        DnaRequest request = new DnaRequest();
        request.setDna(null);

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testInvalidDna_Returns400_NonSquare() throws Exception {
        String[] dna = {"ATG", "CAG", "TTA", "AGA"};
        DnaRequest request = new DnaRequest();
        request.setDna(dna);

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testEmptyBody_Returns400() throws Exception {
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("")) // Body vacío
                .andExpect(status().isBadRequest());
    }

    @Test
    void testWrongMethod_Returns405() throws Exception {
        mockMvc.perform(get("/mutant"))
                .andExpect(status().isMethodNotAllowed());
    }
}