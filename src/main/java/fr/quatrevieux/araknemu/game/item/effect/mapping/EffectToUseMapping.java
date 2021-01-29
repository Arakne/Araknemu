/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.item.effect.mapping;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.game.item.effect.use.AddCharacteristicEffect;
import fr.quatrevieux.araknemu.game.item.effect.use.AddLifeEffect;
import fr.quatrevieux.araknemu.game.item.effect.use.FireworkEffect;
import fr.quatrevieux.araknemu.game.item.effect.use.LearnSpellEffect;
import fr.quatrevieux.araknemu.game.item.effect.use.NullEffectHandler;
import fr.quatrevieux.araknemu.game.item.effect.use.UseEffectHandler;
import fr.quatrevieux.araknemu.game.spell.SpellService;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Map template effect to use effect
 */
final public class EffectToUseMapping implements EffectMapper<UseEffect> {
    final private Map<Effect, UseEffectHandler> handlers = new EnumMap<>(Effect.class);

    public EffectToUseMapping(SpellService spellService) {
        handlers.put(Effect.ADD_CHARACT_WISDOM,       new AddCharacteristicEffect(Characteristic.WISDOM));
        handlers.put(Effect.ADD_CHARACT_STRENGTH,     new AddCharacteristicEffect(Characteristic.STRENGTH));
        handlers.put(Effect.ADD_CHARACT_LUCK,         new AddCharacteristicEffect(Characteristic.LUCK));
        handlers.put(Effect.ADD_CHARACT_AGILITY,      new AddCharacteristicEffect(Characteristic.AGILITY));
        handlers.put(Effect.ADD_CHARACT_VITALITY,     new AddCharacteristicEffect(Characteristic.VITALITY));
        handlers.put(Effect.ADD_CHARACT_INTELLIGENCE, new AddCharacteristicEffect(Characteristic.INTELLIGENCE));

        handlers.put(Effect.ADD_LIFE, new AddLifeEffect());

        handlers.put(Effect.FIREWORK,    new FireworkEffect());
        handlers.put(Effect.LEARN_SPELL, new LearnSpellEffect(spellService));
    }

    @Override
    public UseEffect create(ItemTemplateEffectEntry entry) {
        return new UseEffect(
            handlers.getOrDefault(entry.effect(), NullEffectHandler.INSTANCE),
            entry.effect(),
            new int[] {entry.min(), entry.max(), entry.special()}
        );
    }

    @Override
    public UseEffect create(ItemTemplateEffectEntry effect, boolean maximize) {
        return create(effect);
    }

    @Override
    public List<UseEffect> create(List<ItemTemplateEffectEntry> effects, boolean maximize) {
        return create(effects);
    }

    @Override
    public List<UseEffect> create(List<ItemTemplateEffectEntry> effects) {
        return effects
            .stream()
            .filter(e -> e.effect().type() == Effect.Type.USE)
            .map(this::create)
            .collect(Collectors.toList())
        ;
    }

    @Override
    public Class<UseEffect> type() {
        return UseEffect.class;
    }
}
