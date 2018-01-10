package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.realm.out.ServerList;

/**
 * Account for game
 */
final public class GameAccount {
    final private Account account;
    final private AccountService service;
    final private int serverId;

    private GameSession session;

    public GameAccount(Account account, AccountService service, int serverId) {
        this.account  = account;
        this.service  = service;
        this.serverId = serverId;
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
        return session != null && session.isAlive();
    }

    /**
     * Get the account race
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

    /**
     * Get the remaining premium time
     *
     * @todo save to account entity
     */
    public long remainingTime() {
        return ServerList.ONE_YEAR;
    }

    /**
     * Get the current game server race
     */
    public int serverId() {
        return serverId;
    }
}
