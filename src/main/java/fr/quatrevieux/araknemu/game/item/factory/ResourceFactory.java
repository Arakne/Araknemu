package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.game.world.item.Item;
import fr.quatrevieux.araknemu.game.world.item.Type;
import fr.quatrevieux.araknemu.game.world.item.effect.mapping.EffectToSpecialMapping;
import fr.quatrevieux.araknemu.game.world.item.type.Resource;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Factory for resources.
 * Will be used as default factory
 */
final public class ResourceFactory implements ItemFactory {
    final private EffectToSpecialMapping mapping;

    public ResourceFactory(EffectToSpecialMapping mapping) {
        this.mapping = mapping;
    }

    @Override
    public Item create(ItemTemplate template, boolean maximize) {
        return retrieve(template, template.effects());
    }

    @Override
    public Item retrieve(ItemTemplate template, List<ItemTemplateEffectEntry> effects) {
        return new Resource(
            template,
            effects
                .stream()
                .filter(entry -> entry.effect().type() == Effect.Type.SPECIAL)
                .map(mapping::create)
                .collect(Collectors.toList())
        );
    }

    @Override
    public Type[] types() {
        return new Type[] {
            Type.NONE,
            Type.RESSOURCE,
            Type.QUETES,
            Type.DOCUMENT,
            Type.CEREALE,
            Type.FLEUR,
            Type.PLANTE,
            Type.BOIS,
            Type.MINERAIS,
            Type.ALLIAGE,
            Type.POISSON,
            Type.FRUIT,
            Type.OS,
            Type.POUDRE,
            Type.COMESTI_POISSON,
            Type.PIERRE_PRECIEUSE,
            Type.PIERRE_BRUTE,
            Type.FARINE,
            Type.PLUME,
            Type.POIL,
            Type.ETOFFE,
            Type.CUIR,
            Type.LAINE,
            Type.GRAINE,
            Type.PEAU,
            Type.HUILE,
            Type.PELUCHE,
            Type.POISSON_VIDE,
            Type.VIANDE,
            Type.VIANDE_CONSERVEE,
            Type.QUEUE,
            Type.METARIA,
            Type.LEGUME,
            Type.TEINTURE,
            Type.EQUIP_ALCHIMIE,
            Type.OEUF_FAMILIER,
            Type.OBJET_MISSION,
            Type.CLEFS,
            Type.BOUFTOU,
            Type.PLANCHE,
            Type.ECORCE,
            Type.RACINE,
            Type.SAC_RESSOURCE,
            Type.PATTE,
            Type.AILE,
            Type.OEUF,
            Type.OREILLE,
            Type.CARAPACE,
            Type.BOURGEON,
            Type.OEIL,
            Type.GELEE,
            Type.COQUILLE,
        };
    }
}
