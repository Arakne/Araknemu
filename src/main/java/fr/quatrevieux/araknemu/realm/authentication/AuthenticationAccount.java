package fr.quatrevieux.araknemu.realm.authentication;

import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.network.realm.RealmSession;

/**
 * AuthenticationAccount entity for realm
 */
final public class AuthenticationAccount {
    final private Account account;
    final private AuthenticationService service;

    private RealmSession session;

    public AuthenticationAccount(Account account, AuthenticationService service) {
        this.account = account;
        this.service = service;
    }

    /**
     * Check if the given password is valid
     *
     * @param password Input password
     */
    public boolean checkPassword(String password) {
        return account.password().equals(password);
    }

    /**
     * Attach account to a session
     */
    public void attach(RealmSession session) {
        session.attach(this);
        service.login(this);

        this.session = session;
    }

    /**
     * Detach account from session
     */
    public void detach() {
        session.detach();
        service.logout(this);
        session = null;
    }

    /**
     * Check if the account session is active
     *
     * The session MAY not be notified for socket closed
     * This method will check if the current session is active or not
     */
    public boolean isAlive() {
        if (session == null) {
            return false;
        }

        return session.isAlive();
    }

    /**
     * Get the account id
     */
    public int id() {
        return account.id();
    }

    /**
     * Get the account pseudo
     */
    public String pseudo() {
        return account.pseudo();
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
     * Is the account is a game master
     *
     * @todo to implements
     */
    public boolean isGameMaster() {
        return false;
    }

    /**
     * Get the secret answer
     */
    public String answer() {
        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthenticationAccount that = (AuthenticationAccount) o;

        return account.id() == that.account.id();
    }

    @Override
    public int hashCode() {
        return 23 * account.id();
    }
}
