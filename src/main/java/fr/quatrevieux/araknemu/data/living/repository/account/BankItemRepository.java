package fr.quatrevieux.araknemu.data.living.repository.account;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.repository.MutableRepository;
import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;

import java.util.Collection;

/**
 * Repository for {@link BankItem}
 */
public interface BankItemRepository extends MutableRepository<BankItem> {
    /**
     * Get items of a bank account
     *
     * @param bank The bank account to load
     */
    public Collection<BankItem> byBank(AccountBank bank);

    /**
     * Update the item
     * Save quantity and position
     *
     * @param item Item to save
     *
     * @throws EntityNotFoundException When no items is updated
     */
    public void update(BankItem item);

    /**
     * Delete the item from database
     *
     * @param item Item to delete
     *
     * @throws EntityNotFoundException When cannot found entity to delete
     */
    public void delete(BankItem item);
}
