package fr.quatrevieux.araknemu.data.living.repository.account;

import fr.quatrevieux.araknemu.core.dbal.repository.MutableRepository;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;

/**
 * Repository for accounts
 */
public interface AccountRepository extends MutableRepository<Account> {
    /**
     * Find account by its username (for authenticate)
     */
    Account findByUsername(String username) throws RepositoryException;
}
