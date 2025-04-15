package org.example.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.app.dto.card.CardCreateDTO;
import org.example.app.dto.card.CardUpdateDTO;
import org.example.app.mapper.CardMapper;
import org.example.app.model.Card;
import org.example.app.model.StatusCard;
import org.example.app.repository.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
public class CardControllerIT {
    private final String BASE_URL = "/api/cards";
    private final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON;
    private CardCreateDTO createDTO;
    private CardUpdateDTO updateDTO;

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13-alpine");

    static {
        // Отключаем автоматическое удаление таблиц
        System.setProperty("spring.jpa.hibernate.ddl-auto", "none");
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CardMapper cardMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private WebApplicationContext wac;

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        token = jwt().jwt(builder -> builder.subject("admin@admin.com"));

        createDTO = new CardCreateDTO("1234567890112233", "Bank", "04/26", StatusCard.ACTIVE, BigDecimal.valueOf(20000.00));
        updateDTO = new CardUpdateDTO(StatusCard.BLOCKED);

        cardRepository.deleteAll();
    }

    @Test
    @DisplayName("Should create card and returns masked card number and status 201 CREATED")
    void shouldCreateCardAndReturnMaskedNumber() throws Exception {

        mockMvc.perform(post(BASE_URL).with(token)
                        .param("userId", "1")
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.cardNumber").value("**** **** **** 2233"));
    }

    @Test
    @DisplayName("Should return status 400 Bad Request for invalid create data")
    void shouldReturnBadRequestForInvalidCard() throws Exception {
        createDTO.setBalance(null);
        createDTO.setCardNumber("");

        mockMvc.perform(post(BASE_URL).with(token)
                        .param("userId", "1")
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should update card and return status 200 OK")
    void shouldUpdateCardAndReturnStatus200OK() throws Exception {
        CardCreateDTO createCard = new CardCreateDTO();
        createCard.setExpiryDate("04/26");
        createCard.setCardNumber("1234123412341234");
        createCard.setCardHolderName("John Doe");
        createCard.setStatus(StatusCard.ACTIVE);
        createCard.setBalance(BigDecimal.valueOf(20000.00));

        Card card = cardMapper.map(createCard);
        Long id = cardRepository.save(card).getId();

        mockMvc.perform(post(BASE_URL + "/{id}", id).with(token)
                        .param("userId", "1")
                        .contentType(JSON_CONTENT_TYPE)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("BLOCKED"));
    }

    @Test
    @DisplayName("Should card and return status 200 OK")
    void shouldCardAndReturnStatus200OK() throws Exception {
        CardCreateDTO createCard = new CardCreateDTO();
        createCard.setExpiryDate("04/26");
        createCard.setCardNumber("1234123412342211");
        createCard.setCardHolderName("John Doe");
        createCard.setStatus(StatusCard.ACTIVE);
        createCard.setBalance(BigDecimal.valueOf(20000.00));

        Card card = cardMapper.map(createCard);
        Long id = cardRepository.save(card).getId();

        mockMvc.perform(get(BASE_URL + "/{id}", id).with(token)
                        .param("userId", "1")
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.cardHolderName").value("John Doe"));
    }

    @Test
    @DisplayName("Delete card and return status 204 NO CONTENT")
    void shouldDeleteCardAndReturnStatus204NoContent() throws Exception {
        Card card = cardMapper.map(createDTO);
        Long id = cardRepository.save(card).getId();

        mockMvc.perform(delete(BASE_URL + "/{id}", id).with(token)
                        .param("userId", "1")
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Should not found card and return status 404 NOT FOUND")
    void shouldNotFoundCardAndReturnStatus404NotFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", 9999L).with(token)
                        .param("userId", "1")
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Delete not found card and return status 404 NOT FOUND")
    void deletedNotFoundCardAndReturnStatus404Found() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", 9999L).with(token)
                        .param("userId", "1")
                        .contentType(JSON_CONTENT_TYPE))
                .andExpect(status().isNotFound());
    }
}
