package fr.quatrevieux.araknemu.realm.authentication;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for handle authentication
 */
final public class AuthenticationService {
    final private AccountRepository repository;

    /**
     * Set of accounts which wait for authentication process
     * There are loaded, but not yet authenticated
     */
    final private Set<AuthenticationAccount> pending = Collections.synchronizedSet(new HashSet<>());

    /**
     * Map of authenticated accounts
     * There are linked to a session
     */
    final private ConcurrentMap<Integer, AuthenticationAccount> authenticated = new ConcurrentHashMap<>();

    public AuthenticationService(AccountRepository repository) {
        this.repository = repository;
    }

    /**
     * Perform authenticate request
     */
    synchronized public void authenticate(AuthenticationRequest request) {
        AuthenticationAccount account;

        try {
            account = new AuthenticationAccount(
                repository.findByUsername(request.username()),
                this
            );
        } catch (EntityNotFoundException e) {
            request.invalidCredentials();
            return;
        }

        if (!account.checkPassword(request.password())) {
            request.invalidCredentials();
            return;
        }

        if (
            isAuthenticated(account)
            || pending.contains(account)
        ) {
            request.alreadyConnected();
            return;
        }

        pending.add(account);

        //@todo check authenticate game

        request.success(account);
        pending.remove(account);
    }

    /**
     * Check if the account is authenticated
     */
    public boolean isAuthenticated(AuthenticationAccount account) {
        if (!authenticated.containsKey(account.id())) {
            return false;
        }

        return authenticated.get(account.id()).isAlive();
    }

    /**
     * Remove account from authenticated accounts
     */
    void logout(AuthenticationAccount account) {
        authenticated.remove(account.id());
    }

    /**
     * Add account to authenticated account
     */
    void login(AuthenticationAccount account) {
        authenticated.put(account.id(), account);
    }
}
