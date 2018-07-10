package fr.quatrevieux.araknemu.game.item.factory;

import fr.quatrevieux.araknemu.data.value.Interval;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.game.item.GameItemSet;
import fr.quatrevieux.araknemu.game.item.Item;
import fr.quatrevieux.araknemu.game.item.SuperType;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.WeaponEffect;
import fr.quatrevieux.araknemu.game.item.effect.mapping.EffectMappers;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffectService;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Factory for weapons
 */
final public class WeaponFactory implements ItemFactory {
    final private EffectMappers mappers;
    final private SpellEffectService effectService;

    public WeaponFactory(EffectMappers mappers, SpellEffectService effectService) {
        this.mappers = mappers;
        this.effectService = effectService;
    }

    @Override
    public Item create(ItemTemplate template, ItemType type, GameItemSet set, boolean maximize) {
        return create(template, type, set, template.effects(), maximize);
    }

    @Override
    public Item retrieve(ItemTemplate template, ItemType type, GameItemSet set, List<ItemTemplateEffectEntry> effects) {
        return create(template, type, set, effects, false);
    }

    @Override
    public SuperType type() {
        return SuperType.WEAPON;
    }

    private Item create(ItemTemplate template, ItemType type, GameItemSet set, List<ItemTemplateEffectEntry> effects, boolean maximize) {
        return new Weapon(
            template,
            type,
            set,
            mappers.get(WeaponEffect.class).create(effects),
            mappers.get(CharacteristicEffect.class).create(effects, maximize),
            mappers.get(SpecialEffect.class).create(effects, maximize),
            parseInfo(template.weaponInfo()),
            effectService.area(type.effectArea())
        );
    }

    private Weapon.WeaponInfo parseInfo(String info) {
        String[] parts = StringUtils.split(info, ";");

        return new Weapon.WeaponInfo(
            Integer.parseInt(parts[0]),
            new Interval(
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2])
            ),
            Integer.parseInt(parts[3]),
            Integer.parseInt(parts[4]),
            Integer.parseInt(parts[5]),
            parts[6].equals("1")
        );
    }
}
