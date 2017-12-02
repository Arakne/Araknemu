package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {
    private TokenService service;

    @BeforeEach
    void setUp() {
        service = new TokenService();
    }

    @Test
    void generateAndGet() {
        Account account = new Account(-1);

        String token = service.generate(account);
        assertSame(account, service.get(token));
    }

    @Test
    void getNotFound() {
        assertThrows(NoSuchElementException.class, () -> service.get("not_found"));
    }

    @Test
    void getTwice() {
        String token = service.generate(new Account(-1));
        service.get(token);
        assertThrows(NoSuchElementException.class, () -> service.get("not_found"));
    }

    @Test
    void generateNotSameToken() {
        assertNotEquals(service.generate(new Account(-1)), service.generate(new Account(-1)));
    }
}