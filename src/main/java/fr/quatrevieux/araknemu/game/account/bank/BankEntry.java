package fr.quatrevieux.araknemu.game.account.bank;

import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.inventory.AbstractItemEntry;

/**
 * Entry for a bank item
 * Because a bank doesn't have item slots, the entries are always on default position (-1)
 */
final public class BankEntry extends AbstractItemEntry {
    final private BankItem entity;

    public BankEntry(Bank bank, BankItem entity, Item item) {
        super(bank, entity, item, bank);

        this.entity = entity;
    }

    @Override
    public int position() {
        return DEFAULT_POSITION;
    }

    /**
     * Get the database entity
     */
    public BankItem entity() {
        return entity;
    }
}
