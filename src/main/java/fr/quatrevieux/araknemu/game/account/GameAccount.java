package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.common.account.AbstractLivingAccount;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.common.account.Permission;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.realm.out.ServerList;

/**
 * Account for game
 */
final public class GameAccount extends AbstractLivingAccount<GameSession> {
    final private AccountService service;
    final private int serverId;

    public GameAccount(Account account, AccountService service, int serverId) {
        super(account);

        this.service  = service;
        this.serverId = serverId;
    }

    /**
     * Attach a session to the account
     *
     * @param session Session to attach
     */
    @Override
    public void attach(GameSession session) {
        super.attach(session);

        service.login(this);
        session.attach(this);
    }

    /**
     * Detach the account from the session
     */
    @Override
    public void detach() {
        session.detach();
        service.logout(this);

        super.detach();
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
     * Get the current game server id
     */
    public int serverId() {
        return serverId;
    }

    /**
     * Check if the account has the asked permission
     */
    public boolean isGranted(Permission permission) {
        return account.permissions().contains(permission);
    }

    /**
     * Check the secret answer
     */
    public boolean checkAnswer(String input) {
        return account.answer().equalsIgnoreCase(input);
    }
}
