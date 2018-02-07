package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.effect.mapping.EffectToCharacteristicMapping;
import fr.quatrevieux.araknemu.game.world.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.world.item.type.Wearable;

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
        return new Wearable(
            template,
            template
                .effects()
                .stream()
                .filter(entry -> entry.effect().type() == Effect.Type.CHARACTERISTIC)
                .map(
                    maximize
                        ? characteristicMapping::createMaximize
                        : characteristicMapping::createRandom
                )
                .collect(Collectors.toList()),
            template
                .effects()
                .stream()
                .filter(entry -> entry.effect().type() == Effect.Type.SPECIAL)
                .map(specialMapping::create)
                .collect(Collectors.toList())
        );
    }

    @Override
    public Type[] types() {
        return new Type[] {
            Type.AMULETTE, Type.ANNEAU, Type.CEINTURE, Type.BOIS, Type.COIFFE,
            Type.CAPE, Type.DOFUS, Type.SAC_DOS,
        };
    }
}
