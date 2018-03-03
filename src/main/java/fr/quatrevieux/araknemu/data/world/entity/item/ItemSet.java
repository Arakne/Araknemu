package fr.quatrevieux.araknemu.data.world.entity.item;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;

import java.util.List;

/**
 * Entity for item sets
 */
final public class ItemSet {
    final private int id;
    final private String name;
    final private List<List<ItemTemplateEffectEntry>> bonus;

    public ItemSet(int id, String name, List<List<ItemTemplateEffectEntry>> bonus) {
        this.id = id;
        this.name = name;
        this.bonus = bonus;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public List<List<ItemTemplateEffectEntry>> bonus() {
        return bonus;
    }
}
