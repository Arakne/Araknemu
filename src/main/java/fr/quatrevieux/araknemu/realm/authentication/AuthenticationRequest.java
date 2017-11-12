package fr.quatrevieux.araknemu.realm.authentication;

/**
 * Request authentication
 */
public interface AuthenticationRequest {
    /**
     * The username
     */
    public String username();

    /**
     * The password
     */
    public String password();

    /**
     * Called on authenticate success
     */
    public void success(AuthenticationAccount account);

    /**
     * Called on authenticate error
     */
    public void invalidCredentials();

    /**
     * Called when account is already connected
     */
    public void alreadyConnected();
}
