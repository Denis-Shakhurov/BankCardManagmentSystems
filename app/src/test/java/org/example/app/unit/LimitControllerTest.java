package org.example.app.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.app.controller.LimitController;
import org.example.app.dto.limit.LimitCreateDTO;
import org.example.app.dto.limit.LimitDTO;
import org.example.app.dto.limit.LimitUpdateDTO;
import org.example.app.exception.ResourceNotFoundException;
import org.example.app.handler.GlobalExceptionHandler;
import org.example.app.model.PeriodType;
import org.example.app.service.LimitService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LimitController.class)
@ExtendWith(MockitoExtension.class)
public class LimitControllerTest {
    private final String BASE_URL = "/api/cards/{cardId}/limits";
    private final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON;
    private LimitDTO limitDTO;
    private LimitCreateDTO createDTO;
    private LimitUpdateDTO updateDTO;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LimitService limitService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new LimitController(limitService))
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();

        limitDTO = new LimitDTO(1L, PeriodType.DAILY, BigDecimal.valueOf(1000.00), LocalDate.now(), true, 1L);
        createDTO = new LimitCreateDTO(PeriodType.DAILY, BigDecimal.valueOf(1000.00), LocalDate.now());
        updateDTO = new LimitUpdateDTO(PeriodType.MONTHLY, BigDecimal.valueOf(5000.00),LocalDate.now().plusDays(5), true);
    }

    @Test
    @DisplayName("GET /api/cards/{cardId}/limits show all limit for card")
    void getAllLimitForCardTest() throws Exception {
        List<LimitDTO> limitDTOS = List.of(limitDTO);
        given(limitService.findAll(anyLong()))
                .willReturn(limitDTOS);

        mockMvc.perform(get(BASE_URL, limitDTO.getCardId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/cards/{cardId}/limits/{id} show limit")
    void getLimitByIdTest() throws Exception {
        given(limitService.findById(anyLong()))
                .willReturn(limitDTO);

        mockMvc.perform(get(BASE_URL + "/{id}", limitDTO.getCardId(), limitDTO.getId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(limitDTO.getId()));
    }

    @Test
    @DisplayName("POST /api/cards/{cardId}/limits create limit")
    void createLimitTest() throws Exception {
        given(limitService.save(any(LimitCreateDTO.class), anyLong()))
                .willReturn(limitDTO);

        mockMvc.perform(post(BASE_URL, limitDTO.getCardId())
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("POST /api/cards/{cardId}/limits/{id} update limit")
    void updateLimitTest() throws Exception {
        given(limitService.update(anyLong(), any(LimitUpdateDTO.class)))
                .willReturn(limitDTO);

        mockMvc.perform(post(BASE_URL + "/{id}", limitDTO.getCardId(), limitDTO.getId())
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /api/cards/{cardId}/limits/{id} delete limit")
    void deleteLimitTest() throws Exception {
        doNothing().when(limitService).delete(anyLong());

        mockMvc.perform(delete(BASE_URL + "/{id}", limitDTO.getCardId(), limitDTO.getId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/cards/{cardId}/limits/{id} not found limit")
    void notFoundLimitTest() throws Exception {
        doThrow(new ResourceNotFoundException("Limit not found"))
                .when(limitService).findById(anyLong());

        mockMvc.perform(get(BASE_URL + "/{id}", limitDTO.getCardId(), limitDTO.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/cards/{cardId}/limits create limit with invalid data")
    void createLimitWithInvalidDataTest() throws Exception {
        LimitCreateDTO limitCreateDTO = new LimitCreateDTO(PeriodType.DAILY, null, LocalDate.now().minusDays(5));
        given(limitService.save(any(LimitCreateDTO.class), anyLong()))
                .willThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post(BASE_URL + "/{id}", limitDTO.getCardId(), limitDTO.getId())
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(limitCreateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/cards/{cardId}/limits/{id] update limit with invalid data")
    void updateLimitWithInvalidDataTest() throws Exception {
        given(limitService.update(anyLong(), any(LimitUpdateDTO.class)))
                .willThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post(BASE_URL + "/{id}", limitDTO.getCardId(), limitDTO.getId())
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/cards/{cardId}/limits/{id} delete not found limit")
    void deleteLimitNotFoundTest() throws Exception {
        doThrow(new ResourceNotFoundException("Limit not found"))
                .when(limitService).delete(anyLong());

        mockMvc.perform(delete(BASE_URL + "/{id}", limitDTO.getCardId(), limitDTO.getId()))
                .andExpect(status().isNotFound());
    }
}
