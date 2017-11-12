package fr.quatrevieux.araknemu.data;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;

/**
 * Container for repositories
 *
 * @todo Interfaces
 */
final public class RepositoriesContainer {
    final private AccountRepository accounts;

    public RepositoriesContainer(ConnectionPool connection) {
        this.accounts = new AccountRepository(connection);
    }

    public AccountRepository accounts() {
        return accounts;
    }
}
