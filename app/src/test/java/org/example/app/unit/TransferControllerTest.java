package org.example.app.unit;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.app.component.UserUtils;
import org.example.app.controller.TransferController;
import org.example.app.dto.TransferDTO;
import org.example.app.handler.GlobalExceptionHandler;
import org.example.app.model.User;
import org.example.app.service.TransferService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
@ExtendWith(MockitoExtension.class)
public class TransferControllerTest {
    private final String BASE_URL = "/api/users/transfers/between-own-cards";
    private final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TransferService transferService;

    @MockBean
    private UserUtils userUtils;

    private TransferDTO validTransferDTO;
    private TransferDTO invalidTransferDTO;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new TransferController(transferService, userUtils))
                .setControllerAdvice(GlobalExceptionHandler.class)
                .build();

        // Валидный DTO для перевода
        validTransferDTO = new TransferDTO();
        validTransferDTO.setFromCardId(1L);
        validTransferDTO.setToCardId(2L);
        validTransferDTO.setAmount(BigDecimal.valueOf(100.50));
        validTransferDTO.setDescription("Перевод между своими картами");

        // Невалидный DTO для перевода
        invalidTransferDTO = new TransferDTO();
        invalidTransferDTO.setFromCardId(null); // Нарушение @NotNull
        invalidTransferDTO.setToCardId(null);   // Нарушение @NotNull
        invalidTransferDTO.setAmount(BigDecimal.valueOf(-10)); // Нарушение @Positive
        invalidTransferDTO.setDescription(""); // Нарушение @NotBlank
    }

    @Test
    @DisplayName("Successful transfer between your cards")
    void transferBetweenOwnCards_Success() throws Exception {
        // Мок авторизованного пользователя
        User mockUser = new User();
        mockUser.setId(1L);
        given(userUtils.getCurrentUser()).willReturn(mockUser);

        // Мок сервиса - успешное выполнение
        doNothing().when(transferService)
                .transferBetweenOwnCards(anyLong(), any(TransferDTO.class));

        mockMvc.perform(post(BASE_URL)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(validTransferDTO)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Attempted translation with invalid data")
    void transferBetweenOwnCards_InvalidData() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(invalidTransferDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempted zero sum transfer")
    void transferWithZeroAmount() throws Exception {
        TransferDTO zeroAmountTransfer = new TransferDTO();
        zeroAmountTransfer.setFromCardId(1L);
        zeroAmountTransfer.setToCardId(2L);
        zeroAmountTransfer.setAmount(BigDecimal.ZERO); // Нарушение @Positive
        zeroAmountTransfer.setDescription("Тестовый перевод");

        mockMvc.perform(post(BASE_URL)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(zeroAmountTransfer)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Attempt to translate without description")
    void transferWithoutDescription() throws Exception {
        TransferDTO noDescriptionTransfer = new TransferDTO();
        noDescriptionTransfer.setFromCardId(1L);
        noDescriptionTransfer.setToCardId(2L);
        noDescriptionTransfer.setAmount(BigDecimal.valueOf(50));
        noDescriptionTransfer.setDescription(null); // Нарушение @NotBlank

        mockMvc.perform(post(BASE_URL)
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(noDescriptionTransfer)))
                .andExpect(status().isBadRequest());
    }
}
