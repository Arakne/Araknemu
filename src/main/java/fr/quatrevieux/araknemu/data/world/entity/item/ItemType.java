package fr.quatrevieux.araknemu.data.world.entity.item;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.item.SuperType;

/**
 * Type for items
 */
final public class ItemType {
    final private int id;
    final private String name;
    final private SuperType superType;
    final private EffectArea effectArea;

    public ItemType(int id, String name, SuperType superType, EffectArea effectArea) {
        this.id = id;
        this.name = name;
        this.superType = superType;
        this.effectArea = effectArea;
    }

    public int id() {
        return id;
    }

    public String name() {
        return name;
    }

    public SuperType superType() {
        return superType;
    }

    public EffectArea effectArea() {
        return effectArea;
    }

    @Override
    public String toString() {
        return name;
    }
}
