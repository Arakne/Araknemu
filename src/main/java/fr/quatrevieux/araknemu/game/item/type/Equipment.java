package fr.quatrevieux.araknemu.game.item.type;

import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.ItemEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.MutableCharacteristics;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Base class for equipment items
 */
abstract public class Equipment implements Item {
    final private ItemTemplate template;
    final private ItemType type;
    final private GameItemSet set;
    final private List<CharacteristicEffect> characteristics;
    final private List<SpecialEffect> specials;

    public Equipment(ItemTemplate template, ItemType type, GameItemSet set, List<CharacteristicEffect> characteristics, List<SpecialEffect> specials) {
        this.template = template;
        this.type = type;
        this.set = set;
        this.characteristics = characteristics;
        this.specials = specials;
    }

    @Override
    public List<? extends ItemEffect> effects() {
        List<ItemEffect> effects = new ArrayList<>(characteristics);

        effects.addAll(specials);

        return effects;
    }

    @Override
    public ItemTemplate template() {
        return template;
    }

    @Override
    public Optional<GameItemSet> set() {
        return Optional.ofNullable(set);
    }

    @Override
    public List<SpecialEffect> specials() {
        return specials;
    }

    @Override
    public ItemType type() {
        return type;
    }
    /**
     * Get item characteristics
     */
    public List<CharacteristicEffect> characteristics() {
        return characteristics;
    }

    /**
     * Apply equipment effect to the characteristics
     */
    public void apply(MutableCharacteristics characteristics) {
        for (CharacteristicEffect effect : this.characteristics) {
            characteristics.add(effect.characteristic(), effect.boost());
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Equipment equipment = (Equipment) obj;

        return
            template.equals(equipment.template)
            && characteristics.equals(equipment.characteristics)
            && specials.equals(equipment.specials)
        ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(template, characteristics, specials);
    }
}
