package fr.quatrevieux.araknemu.data.living.repository.account;

import fr.quatrevieux.araknemu.core.dbal.repository.MutableRepository;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;

/**
 * Repository for account bank
 */
public interface AccountBankRepository extends MutableRepository<AccountBank> {
    /**
     * Get the account bank
     * If the entity is not found, the given entity (parameter) is returned
     * No exceptions is thrown when entity is not found
     *
     * @param entity The entity to find (used as primary key criteria)
     *
     * @return The database entity if found, or the given entity if not
     * @throws RepositoryException When a DBAL error occurs
     */
    @Override
    AccountBank get(AccountBank entity) throws RepositoryException;
}
