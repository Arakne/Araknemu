package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.SuperType;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Default item factory implementation
 */
final public class DefaultItemFactory implements ItemFactory {
    final private Map<SuperType, ItemFactory> factories = new EnumMap<>(SuperType.class);

    public DefaultItemFactory(ItemFactory... factories) {
        for (ItemFactory factory : factories) {
            this.factories.put(factory.type(), factory);
        }
    }

    @Override
    public Item create(ItemTemplate template, ItemType type, GameItemSet set, boolean maximize) {
        if (!factories.containsKey(type.superType())) {
            throw new NoSuchElementException("Invalid type " + type);
        }

        return factories.get(type.superType()).create(template, type, set, maximize);
    }

    @Override
    public Item retrieve(ItemTemplate template, ItemType type, GameItemSet set, List<ItemTemplateEffectEntry> effects) {
        if (!factories.containsKey(type.superType())) {
            throw new NoSuchElementException("Invalid type " + type);
        }

        return factories.get(type.superType()).retrieve(template, type, set, effects);
    }

    @Override
    public SuperType type() {
        return null;
    }
}
