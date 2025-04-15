package org.example.app.util;

import net.datafaker.Faker;
import org.example.app.model.Card;
import org.example.app.model.Limit;
import org.example.app.model.PeriodType;
import org.example.app.model.Role;
import org.example.app.model.StatusCard;
import org.example.app.model.Transaction;
import org.example.app.model.TransactionType;
import org.example.app.model.User;
import org.instancio.Instancio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.instancio.Select.field;

public class EntityGenerator {
    private final Faker faker = new Faker();

    public Card createCard() {
        return Instancio.of(Card.class)
                .ignore(field(Card::getId))
                .set(field(Card::getCardNumber), faker.text().text(16))
                .set(field(Card::getCardHolderName), faker.internet().username())
                .set(field(Card::getBalance),
                        BigDecimal.valueOf(faker.number().randomDouble(2, 50000, 1000000)))
                .set(field(Card::getStatus), faker.options().option(StatusCard.class))
                .set(field(Card::getExpiryDate), faker.text().text(5))
                .ignore(field(Card::getUser))
                .create();
    }

    public Limit createLimit() {
        return Instancio.of(Limit.class)
                .ignore(field(Limit::getId))
                .set(field(Limit::getPeriodType), faker.options().option(PeriodType.class))
                .set(field(Limit::getLimitAmount),
                        BigDecimal.valueOf(faker.number().randomDouble(2, 1000, 10000)))
                .ignore(field(Limit::getCard))
                .set(field(Limit::getPeriodStartDate), LocalDate.now())
                .create();
    }

    public User createUser() {
        return Instancio.of(User.class)
                .ignore(field(User::getId))
                .set(field(User::getFirstName), faker.internet().username())
                .set(field(User::getLastName), faker.internet().username())
                .set(field(User::getEmail), faker.internet().emailAddress())
                .set(field(User::getPassword), faker.internet().password())
                .set(field(User::getRole), faker.options().option(Role.class))
                .create();
    }

    public Transaction createTransaction(Card card) {
        return Instancio.of(Transaction.class)
                .ignore(field(Transaction::getId))
                .set(field(Transaction::getAmount),
                        BigDecimal.valueOf(faker.number().randomDouble(2, 1000, 10000)))
                .set(field(Transaction::getTransactionDate), LocalDateTime.now())
                .set(field(Transaction::getType), faker.options().option(TransactionType.class))
                .set(field(Transaction::getDescription), faker.text().text(10))
                .set(field(Transaction::getCard), card)
                .create();
    }
}
