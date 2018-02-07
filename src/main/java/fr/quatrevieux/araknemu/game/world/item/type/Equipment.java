package fr.quatrevieux.araknemu.game.world.item.type;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.world.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.world.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.world.item.effect.SpecialEffect;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for equipment items
 */
abstract public class Equipment extends BaseItem {
    final private List<CharacteristicEffect> characteristics;

    public Equipment(ItemTemplate template, List<CharacteristicEffect> characteristics, List<SpecialEffect> specials) {
        super(template, specials);
        this.characteristics = characteristics;
    }

    @Override
    public List<? extends ItemEffect> effects() {
        List<ItemEffect> effects = new ArrayList<>(characteristics);

        effects.addAll(super.effects());

        return effects;
    }

    /**
     * Get item characteristics
     */
    public List<CharacteristicEffect> characteristics() {
        return characteristics;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        Equipment equipment = (Equipment) obj;

        return characteristics.equals(equipment.characteristics);
    }
}
