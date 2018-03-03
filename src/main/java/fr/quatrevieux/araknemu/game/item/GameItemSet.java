package fr.quatrevieux.araknemu.game.item;

import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;

import java.util.Collections;
import java.util.List;

/**
 * ItemSet for game
 */
final public class GameItemSet {
    final static public class Bonus {
        final static private Bonus EMPTY = new Bonus(Collections.emptyList(), Collections.emptyList(), Collections.emptyList());

        final private List<ItemTemplateEffectEntry> effects;
        final private List<CharacteristicEffect> characteristics;
        final private List<SpecialEffect> specials;

        public Bonus(List<ItemTemplateEffectEntry> effects, List<CharacteristicEffect> characteristics, List<SpecialEffect> specials) {
            this.effects = effects;
            this.characteristics = characteristics;
            this.specials = specials;
        }

        public List<ItemTemplateEffectEntry> effects() {
            return effects;
        }

        public List<CharacteristicEffect> characteristics() {
            return characteristics;
        }

        public List<SpecialEffect> specials() {
            return specials;
        }
    }

    final private ItemSet entity;
    final private List<Bonus> bonus;

    public GameItemSet(ItemSet entity, List<Bonus> bonus) {
        this.entity = entity;
        this.bonus = bonus;
    }

    public int id() {
        return entity.id();
    }

    public String name() {
        return entity.name();
    }

    /**
     * Get the item set bonus for given number of items
     *
     * @param nbOfItems The number of weared items
     *                  If this number is less than 2, the bonus will always be empty
     *                  If this number is higher than maximum bonus level, the maximal bonus will be returned
     */
    public Bonus bonus(int nbOfItems) {
        if (nbOfItems <= 1) {
            return Bonus.EMPTY;
        }

        if (nbOfItems > bonus.size() + 1) {
            nbOfItems = bonus.size() + 1;
        }

        return bonus.get(nbOfItems - 2);
    }
}
