package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.util.RandomStringUtil;

import java.security.SecureRandom;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handle login tokens
 */
final public class TokenService {
    static private class ExpirableAccount {
        final private Account account;
        final private long expiration;

        public ExpirableAccount(Account account, long expiration) {
            this.account = account;
            this.expiration = expiration;
        }
    }

    final private ConcurrentMap<String, ExpirableAccount> accounts = new ConcurrentHashMap<>();
    final private RandomStringUtil randomStringUtil = new RandomStringUtil(
        new SecureRandom(),
        "abcdefghijklmnopqrstuvwxyz"
    );

    /**
     * Register an account and get token
     * @param account Account to register
     */
    public String generate(Account account) {
        String token = generateToken();

        accounts.put(
            token,
            new ExpirableAccount(account, System.currentTimeMillis() + 30000)
        );

        return token;
    }

    /**
     * Get an account by its token and remove from service
     *
     * @param token The token
     *
     * @throws NoSuchElementException When the token cannot be found or is expired
     */
    public Account get(String token) {
        if (!accounts.containsKey(token)) {
            throw new NoSuchElementException();
        }

        ExpirableAccount account = accounts.remove(token);

        if (System.currentTimeMillis() > account.expiration) {
            throw new NoSuchElementException();
        }

        return account.account;
    }

    /**
     * Generate a random and unique token
     */
    private String generateToken() {
        String token;

        do {
            token = randomStringUtil.generate(32);
        } while (accounts.containsKey(token));

        return token;
    }
}
