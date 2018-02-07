package fr.quatrevieux.araknemu.data.world.repository.item;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;

import java.util.Collection;

/**
 * Repository for {@link ItemTemplate}
 */
public interface ItemTemplateRepository extends Repository<ItemTemplate> {
    /**
     * Get an itemtemplate by its id
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException When cannot found the entity
     */
    public ItemTemplate get(int id);

    /**
     * Load all item templates
     */
    public Collection<ItemTemplate> load();
}
