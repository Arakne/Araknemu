package fr.quatrevieux.araknemu.realm.host;

import fr.quatrevieux.araknemu.realm.authentication.AuthenticationAccount;

/**
 * GameConnector for communicate between game and realm server
 */
public interface GameConnector {
    public interface HostResponse<T> {
        /**
         * Response of the game host
         */
        public void response(T response);
    }

    /**
     * Check login state of the given account
     *
     * @param account Account to check
     * @param response The response listener
     */
    public void checkLogin(AuthenticationAccount account, HostResponse<Boolean> response);

    /**
     * Register account to game server and get the login token
     *
     * @param account Account to register
     * @param response The response listener, will received the token
     */
    public void token(AuthenticationAccount account, HostResponse<String> response);
}
