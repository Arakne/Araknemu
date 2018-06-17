package fr.quatrevieux.araknemu.game.item.type;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;

import java.util.List;

/**
 * Class for simple equipments
 */
final public class Wearable extends Equipment {
    public Wearable(ItemTemplate template, ItemType type, GameItemSet set, List<CharacteristicEffect> characteristics, List<SpecialEffect> specials) {
        super(template, type, set, characteristics, specials);
    }
}
