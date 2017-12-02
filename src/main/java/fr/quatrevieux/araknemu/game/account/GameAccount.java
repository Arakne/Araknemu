package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Account for game
 */
final public class GameAccount {
    final private Account account;
    final private AccountService service;

    private GameSession session;

    public GameAccount(Account account, AccountService service) {
        this.account = account;
        this.service = service;
    }

    /**
     * Attach a session to the account
     *
     * @param session Session to attach
     */
    public void attach(GameSession session) {
        this.session = session;
        service.login(this);
        session.attach(this);
    }

    /**
     * Detach the account from the session
     */
    public void detach() {
        session.detach();
        session = null;
        service.logout(this);
    }

    /**
     * Check if the account is logged
     */
    public boolean isLogged() {
        return session != null;
    }

    /**
     * Get the account id
     */
    public int id() {
        return account.id();
    }

    /**
     * Get the community ID
     *
     * @todo constant, enum ?
     */
    public int community() {
        return 0;
    }
}
