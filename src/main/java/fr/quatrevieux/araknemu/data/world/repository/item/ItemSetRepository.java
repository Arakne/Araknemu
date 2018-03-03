package fr.quatrevieux.araknemu.data.world.repository.item;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;

import java.util.Collection;

/**
 * Repository for {@link ItemSet}
 */
public interface ItemSetRepository extends Repository<ItemSet> {
    /**
     * Get the item set by its id
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException
     */
    public ItemSet get(int id);

    /**
     * Load all item sets
     */
    public Collection<ItemSet> load();
}
