package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToCharacteristicMapping;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.item.type.Wearable;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for wearable
 */
final public class WearableFactory implements ItemFactory {
    final private EffectToCharacteristicMapping characteristicMapping;
    final private EffectToSpecialMapping specialMapping;

    public WearableFactory(EffectToCharacteristicMapping characteristicMapping, EffectToSpecialMapping specialMapping) {
        this.characteristicMapping = characteristicMapping;
        this.specialMapping = specialMapping;
    }

    @Override
    public Item create(ItemTemplate template, boolean maximize) {
        return create(template, template.effects(), maximize);
    }

    @Override
    public Item retrieve(ItemTemplate template, List<ItemTemplateEffectEntry> effects) {
        return create(template, effects, false);
    }

    @Override
    public Type[] types() {
        return new Type[] {
            Type.AMULETTE, Type.ANNEAU, Type.CEINTURE, Type.COIFFE,
            Type.CAPE, Type.DOFUS, Type.SAC_DOS, Type.BOTTES
        };
    }

    private Item create(ItemTemplate template, List<ItemTemplateEffectEntry> effects, boolean maximize) {
        return new Wearable(
            template,
            effects
                .stream()
                .filter(entry -> entry.effect().type() == Effect.Type.CHARACTERISTIC)
                .map(
                    maximize
                        ? characteristicMapping::createMaximize
                        : characteristicMapping::createRandom
                )
                .collect(Collectors.toList()),
            effects
                .stream()
                .filter(entry -> entry.effect().type() == Effect.Type.SPECIAL)
                .map(entry -> specialMapping.create(entry, maximize))
                .collect(Collectors.toList())
        );
    }
}
