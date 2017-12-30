package fr.quatrevieux.araknemu.game.connector;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.TokenService;

/**
 * Service for handle realm connector requests
 */
final public class ConnectorService {
    final private TokenService tokens;
    final private AccountService accounts;

    public ConnectorService(TokenService tokens, AccountService accounts) {
        this.tokens   = tokens;
        this.accounts = accounts;
    }

    /**
     * Check if the account is logged into the game server
     */
    public boolean isLogged(int accountId) {
        return accounts.isLogged(accountId);
    }

    /**
     * Generate account token
     *
     * @param accountId Account race
     */
    public String token(int accountId) {
        return tokens.generate(new Account(accountId));
    }
}
