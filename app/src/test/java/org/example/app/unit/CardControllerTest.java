package org.example.app.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.app.controller.CardController;
import org.example.app.dto.card.CardCreateDTO;
import org.example.app.dto.card.CardDTO;
import org.example.app.dto.card.CardParamDTO;
import org.example.app.dto.card.CardUpdateDTO;
import org.example.app.exception.ResourceNotFoundException;
import org.example.app.handler.GlobalExceptionHandler;
import org.example.app.model.StatusCard;
import org.example.app.service.CardService;
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
import java.time.LocalDateTime;
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

@WebMvcTest(CardController.class)
@ExtendWith(MockitoExtension.class)
public class CardControllerTest {
    private final String BASE_URL = "/api/cards";
    private final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON;
    private CardCreateDTO createDTO;
    private CardUpdateDTO updateDTO;
    private CardDTO cardDTO;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CardService cardService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CardController(cardService))
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();

        createDTO = new CardCreateDTO("1234567890112233", "Bank", "04/26", StatusCard.ACTIVE, BigDecimal.valueOf(20000.00));
        updateDTO = new CardUpdateDTO(StatusCard.BLOCKED);
        cardDTO = new CardDTO(1L,
                "**** **** **** 2233",
                "Bank",
                "05/27",
                StatusCard.ACTIVE,
                BigDecimal.valueOf(20000.00),
                LocalDateTime.now().minusDays(2),
                LocalDateTime.now(),
                List.of(1L),
                List.of(1L),1L);
    }

    @Test
    @DisplayName("GET /api/cards show all cards")
    void getAllCardsTest() throws Exception {
        List<CardDTO> cardDTOS = List.of(cardDTO);
        CardParamDTO params = new CardParamDTO();
        given(cardService.findAll(params)).willReturn(cardDTOS);

        mockMvc.perform(get(BASE_URL)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/cards/{id} show card")
    void getCardByIdTest() throws Exception {
        given(cardService.findById(anyLong())).willReturn(cardDTO);

        mockMvc.perform(get(BASE_URL + "/{id}", cardDTO.getId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cardDTO.getId()));
    }

    @Test
    @DisplayName("POST /api/cards create card")
    void createCardTest() throws Exception {
        given(cardService.create(any(CardCreateDTO.class), anyLong()))
                .willReturn(cardDTO);

        mockMvc.perform(post(BASE_URL)
                        .contentType(JSON_CONTENT_TYPE)
                        .param("userId", "1")
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cardNumber").value("**** **** **** 2233"));
    }

    @Test
    @DisplayName("POST /api/cards/{id} update card")
    void updateCardTest() throws Exception {
        given(cardService.update(any(CardUpdateDTO.class), anyLong()))
                .willReturn(cardDTO);

        mockMvc.perform(post(BASE_URL + "/{id}", cardDTO.getId())
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("DELETE /api/cards/{id} delete card")
    void deleteCardTest() throws Exception {
        doNothing().when(cardService).delete(anyLong());

        mockMvc.perform(delete(BASE_URL + "/{id}", cardDTO.getId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/cards/{id} card not found")
    void cardNotFoundTest() throws Exception {
        given(cardService.findById(anyLong()))
                .willThrow(new ResourceNotFoundException("Card not found"));

        mockMvc.perform(get(BASE_URL + "/{id}", cardDTO.getId())
                .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/cards create card with invalid data")
    void createCardWithInvalidDataTest() throws Exception {
        given(cardService.create(any(CardCreateDTO.class), anyLong()))
                .willThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/cards/{id} update card with invalid data")
    void updateCardWithInvalidDataTest() throws Exception {
        given(cardService.update(any(CardUpdateDTO.class), anyLong()))
                .willThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post(BASE_URL)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/cards/{id} delete card not found")
    void cardNotFoundDeleteTest() throws Exception {
        doThrow(new ResourceNotFoundException("Card not found"))
                .when(cardService).delete(anyLong());

        mockMvc.perform(delete(BASE_URL + "/{id}", cardDTO.getId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNotFound());
    }
}
