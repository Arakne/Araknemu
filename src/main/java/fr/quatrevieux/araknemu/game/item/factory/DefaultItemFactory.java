package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;

import java.util.EnumMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Default item factory implementation
 */
final public class DefaultItemFactory implements ItemFactory {
    final private Map<Type, ItemFactory> factories = new EnumMap<>(Type.class);

    public DefaultItemFactory(ItemFactory... factories) {
        for (ItemFactory factory : factories) {
            for (Type type : factory.types()) {
                this.factories.put(type, factory);
            }
        }
    }

    @Override
    public Item create(ItemTemplate template, boolean maximize) {
        if (!factories.containsKey(template.type())) {
            throw new NoSuchElementException("Invalid type " + template.type());
        }

        return factories.get(template.type()).create(template, maximize);
    }

    @Override
    public Type[] types() {
        return Type.values();
    }
}
