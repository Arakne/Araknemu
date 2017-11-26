package fr.quatrevieux.araknemu.game.connector;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.game.account.TokenService;

/**
 * Service for handle realm connector requests
 */
final public class ConnectorService {
    final private TokenService tokens;

    public ConnectorService(TokenService tokens) {
        this.tokens = tokens;
    }

    /**
     * Check if the account is logged into the game server
     */
    public boolean isLogged(int accountId) {
        return false;
    }

    /**
     * Generate account token
     *
     * @param accountId Account id
     */
    public String token(int accountId) {
        return tokens.generate(new Account(accountId));
    }
}
