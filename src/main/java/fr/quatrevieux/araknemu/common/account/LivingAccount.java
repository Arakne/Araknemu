package fr.quatrevieux.araknemu.common.account;

import fr.quatrevieux.araknemu.network.adapter.Session;

/**
 * Living account, connected to a session
 */
public interface LivingAccount<S extends Session> {
    /**
     * Check if the account is a master (admin) account
     */
    public boolean isMaster();

    /**
     * Get the account id
     */
    public int id();

    /**
     * Get the account pseudo
     */
    public String pseudo();

    /**
     * Get the community ID
     *
     * @todo constant, enum ?
     */
    public int community();

    /**
     * Attach the account to the session
     */
    public void attach(S session);

    /**
     * Detach the account from the session
     */
    public void detach();

    /**
     * Check if the account is logged
     */
    public boolean isLogged();
}
