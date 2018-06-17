package fr.quatrevieux.araknemu.data.world.entity.item;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;

import java.util.List;

/**
 * Template for game items
 */
final public class ItemTemplate {
    final private int id;
    final private int type;
    final private String name;
    final private int level;
    final private List<ItemTemplateEffectEntry> effects;
    final private int weight;
    final private String condition;
    final private int itemSet;
    final private String weaponInfo;
    final private int price;

    public ItemTemplate(int id, int type, String name, int level, List<ItemTemplateEffectEntry> effects, int weight, String condition, int itemSet, String weaponInfo, int price) {
        this.id = id;
        this.type = type;
        this.name = name;
        this.level = level;
        this.effects = effects;
        this.weight = weight;
        this.condition = condition;
        this.itemSet = itemSet;
        this.weaponInfo = weaponInfo;
        this.price = price;
    }

    public int id() {
        return id;
    }

    public int type() {
        return type;
    }

    public String name() {
        return name;
    }

    public int level() {
        return level;
    }

    public List<ItemTemplateEffectEntry> effects() {
        return effects;
    }

    public int weight() {
        return weight;
    }

    public String condition() {
        return condition;
    }

    public int itemSet() {
        return itemSet;
    }

    public String weaponInfo() {
        return weaponInfo;
    }

    public int price() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ItemTemplate template = (ItemTemplate) o;

        return id == template.id;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
