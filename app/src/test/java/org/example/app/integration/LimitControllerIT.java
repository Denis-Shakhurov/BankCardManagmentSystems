package org.example.app.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.app.dto.limit.LimitCreateDTO;
import org.example.app.dto.limit.LimitDTO;
import org.example.app.dto.limit.LimitUpdateDTO;
import org.example.app.mapper.LimitMapper;
import org.example.app.model.Card;
import org.example.app.model.Limit;
import org.example.app.model.PeriodType;
import org.example.app.model.User;
import org.example.app.repository.CardRepository;
import org.example.app.repository.LimitRepository;
import org.example.app.repository.UserRepository;
import org.example.app.util.EntityGenerator;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class LimitControllerIT {
    private final String BASE_URL = "/api/cards/{cardId}/limits";
    private final MediaType JSON_CONTENT_TYPE = MediaType.APPLICATION_JSON;
    private LimitDTO limitDTO;
    private LimitCreateDTO createDTO;
    private LimitUpdateDTO updateDTO;
    private User testUser;
    private Card testCard;

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
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LimitMapper limitMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CardRepository cardRepository;

    @Autowired
    private LimitRepository limitRepository;

    @Autowired
    private WebApplicationContext wac;

    private final EntityGenerator entityGenerator = new EntityGenerator();
    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor token;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .apply(springSecurity())
                .build();

        token = jwt().jwt(builder -> builder.subject("admin@admin.com"));

        createDTO = new LimitCreateDTO(PeriodType.DAILY, BigDecimal.valueOf(1000.00), LocalDate.now());
        updateDTO = new LimitUpdateDTO(PeriodType.MONTHLY, BigDecimal.valueOf(5000.00),LocalDate.now().plusDays(5), true);

    }
}
