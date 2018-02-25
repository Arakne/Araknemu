package fr.quatrevieux.araknemu.game.item.type;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;

import java.util.List;

/**
 * Class for simple equipments
 */
final public class Wearable extends Equipment {
    public Wearable(ItemTemplate template, List<CharacteristicEffect> characteristics, List<SpecialEffect> specials) {
        super(template, characteristics, specials);
    }
}
