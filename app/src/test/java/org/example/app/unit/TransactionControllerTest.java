package org.example.app.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.app.controller.TransactionController;
import org.example.app.dto.transaction.TransactionCreateDTO;
import org.example.app.dto.transaction.TransactionDTO;
import org.example.app.dto.transaction.TransactionParamDTO;
import org.example.app.dto.transaction.TransactionUpdateDTO;
import org.example.app.exception.ResourceNotFoundException;
import org.example.app.handler.GlobalExceptionHandler;
import org.example.app.model.TransactionType;
import org.example.app.service.TransactionService;
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

@WebMvcTest(TransactionController.class)
@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {
    private final String BASE_URL = "/api/cards/{cardId}/transactions";
    private final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON;
    private TransactionDTO transactionDTO;
    private TransactionCreateDTO createDTO;
    private TransactionUpdateDTO updateDTO;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TransactionController(transactionService))
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();

        transactionDTO = new TransactionDTO(1L, BigDecimal.valueOf(1000.00), "payment", LocalDateTime.now(), TransactionType.PAYMENT, 1L);
        createDTO = new TransactionCreateDTO(BigDecimal.valueOf(1000.00), "payment", TransactionType.PAYMENT);
        updateDTO = new TransactionUpdateDTO(BigDecimal.valueOf(2000.00), "transfer", LocalDateTime.now(), TransactionType.TRANSFER);
    }

    @Test
    @DisplayName("GET /api/cards/{cardId}/transactions show all transactions")
    void getAllTransactionsTest() throws Exception {
        List<TransactionDTO> transactionDTOS = List.of(transactionDTO);
        TransactionParamDTO params = new TransactionParamDTO();

        given(transactionService.findAll(params)).willReturn(transactionDTOS);

        mockMvc.perform(get(BASE_URL, transactionDTO.getCardId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/cards/{cardId}/transactions/{id} show transaction")
    void getTransactionByIdTest() throws Exception {
        given(transactionService.findById(anyLong())).willReturn(transactionDTO);

        mockMvc.perform(get(BASE_URL + "/{id}", transactionDTO.getCardId(), transactionDTO.getId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionDTO.getId()));
    }

    @Test
    @DisplayName("POST /api/cards/{cardId}/transactions create transaction")
    void createTransactionTest() throws Exception {
        given(transactionService.save(any(TransactionCreateDTO.class), anyLong()))
                .willReturn(transactionDTO);

        mockMvc.perform(post(BASE_URL, transactionDTO.getCardId())
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.description").value(createDTO.getDescription()));
    }

    @Test
    @DisplayName("POST /api/cards/{cardId}/transactions/{id} update transaction")
    void updateTransactionTest() throws Exception {
        transactionDTO.setDescription(updateDTO.getDescription());

        given(transactionService.update(anyLong(), any(TransactionUpdateDTO.class)))
                .willReturn(transactionDTO);

        mockMvc.perform(post(BASE_URL + "/{id}", transactionDTO.getCardId(), transactionDTO.getId())
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").value(updateDTO.getDescription()));
    }

    @Test
    @DisplayName("DELETE /api/cards/{cardId}/transactions/{id}")
    void deleteTransactionTest() throws Exception {
        doNothing().when(transactionService).delete(anyLong());

        mockMvc.perform(delete(BASE_URL + "/{id}", transactionDTO.getCardId(), transactionDTO.getId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GET /api/cards/{cardId}/transactions/{id} get transaction not found")
    void getTransactionNotFoundTest() throws Exception {
        given(transactionService.findById(anyLong()))
                .willThrow(new ResourceNotFoundException("Transaction not found"));

        mockMvc.perform(get(BASE_URL + "/{id}", transactionDTO.getCardId(), transactionDTO.getId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/cards/{cardId}/transactions create transaction with not check limit")
    void createTransactionWithNotCheckLimitTest() throws Exception {
        given(transactionService.save(any(TransactionCreateDTO.class), anyLong()))
                .willThrow(new RuntimeException("Exceeding the limit"));

        mockMvc.perform(post(BASE_URL + "/{id}", transactionDTO.getCardId(), transactionDTO.getId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/cards/{cardId}/transactions create transaction with invalid data")
    void createTransactionWithInvalidDataTest() throws Exception {
        TransactionCreateDTO transactionCreateDTO = new TransactionCreateDTO(
                null, "payment", TransactionType.PAYMENT);

        given(transactionService.save(any(TransactionCreateDTO.class), anyLong()))
                .willThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post(BASE_URL, transactionDTO.getCardId())
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(transactionCreateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/cards/{cardId}/transactions/{id} update transaction with invalid data")
    void updateTransactionWithInvalidDataTest() throws Exception {
        TransactionUpdateDTO transactionUpdateDTO = new TransactionUpdateDTO(
                null, null, LocalDateTime.now(), TransactionType.TRANSFER);

        given(transactionService.update(anyLong(), any(TransactionUpdateDTO.class)))
                .willThrow(new IllegalArgumentException("Invalid data"));

        mockMvc.perform(post(BASE_URL + "/{id}", transactionDTO.getCardId(), transactionDTO.getId())
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(transactionUpdateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("DELETE /api/cards/{cardId}/transactions/{id} delete not found transaction")
    void deleteTransactionNotFoundTest() throws Exception {
        doThrow(new ResourceNotFoundException("Transaction not found"))
                .when(transactionService).delete(anyLong());

        mockMvc.perform(delete(BASE_URL + "/{id}", transactionDTO.getCardId(), transactionDTO.getId())
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNotFound());
    }
}
