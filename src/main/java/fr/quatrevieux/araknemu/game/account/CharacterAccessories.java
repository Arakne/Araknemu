package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AbstractAccessories;
import fr.quatrevieux.araknemu.game.world.creature.accessory.Accessory;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import fr.quatrevieux.araknemu.game.world.creature.accessory.NullAccessory;

import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

/**
 * List of character accessories
 */
final public class CharacterAccessories extends AbstractAccessories {
    final private Map<AccessoryType, Accessory> accessories = new EnumMap<>(AccessoryType.class);

    public CharacterAccessories(Collection<PlayerItem> items) {
        items
            .stream()
            .map(CharacterAccessory::new)
            .forEach(this::set)
        ;
    }

    @Override
    public Accessory get(AccessoryType type) {
        if (!accessories.containsKey(type)) {
            return new NullAccessory(type);
        }

        return accessories.get(type);
    }

    private void set(Accessory accessory) {
        accessories.put(accessory.type(), accessory);
    }
}
