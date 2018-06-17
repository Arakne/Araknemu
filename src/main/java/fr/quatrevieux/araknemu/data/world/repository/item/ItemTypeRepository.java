package fr.quatrevieux.araknemu.data.world.repository.item;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;

import java.util.Collection;

/**
 * Repository for {@link ItemType}
 */
public interface ItemTypeRepository extends Repository<ItemType> {
    /**
     * Get the item type by its id
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException
     */
    public ItemType get(int id);

    /**
     * Load all item sets
     */
    public Collection<ItemType> load();
}
