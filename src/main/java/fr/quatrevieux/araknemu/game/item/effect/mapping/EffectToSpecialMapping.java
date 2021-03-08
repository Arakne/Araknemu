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

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.SpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.special.AddSpecialEffect;
import fr.quatrevieux.araknemu.game.item.effect.special.BoostSpellEffect;
import fr.quatrevieux.araknemu.game.item.effect.special.NullEffectHandler;
import fr.quatrevieux.araknemu.game.item.effect.special.SetSpellModifierEffect;
import fr.quatrevieux.araknemu.game.item.effect.special.SpecialEffectHandler;
import fr.quatrevieux.araknemu.game.item.effect.special.SubSpecialEffect;
import fr.quatrevieux.araknemu.game.player.characteristic.SpecialEffects;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Map item effect to special effect
 */
public final class EffectToSpecialMapping implements EffectMapper<SpecialEffect> {
    private final Map<Effect, SpecialEffectHandler> handlers = new EnumMap<>(Effect.class);

    public EffectToSpecialMapping() {
        handlers.put(Effect.ADD_DISCERNMENT, new AddSpecialEffect(SpecialEffects.Type.DISCERNMENT));
        handlers.put(Effect.ADD_PODS,        new AddSpecialEffect(SpecialEffects.Type.PODS));
        handlers.put(Effect.ADD_INITIATIVE,  new AddSpecialEffect(SpecialEffects.Type.INITIATIVE));

        handlers.put(Effect.SUB_DISCERNMENT, new SubSpecialEffect(SpecialEffects.Type.DISCERNMENT));
        handlers.put(Effect.SUB_PODS,        new SubSpecialEffect(SpecialEffects.Type.PODS));
        handlers.put(Effect.SUB_INITIATIVE,  new SubSpecialEffect(SpecialEffects.Type.INITIATIVE));

        handlers.put(Effect.SPELL_ADD_RANGE,            new BoostSpellEffect(SpellsBoosts.Modifier.RANGE));
        handlers.put(Effect.SPELL_SET_MODIFIABLE_RANGE, new BoostSpellEffect(SpellsBoosts.Modifier.MODIFIABLE_RANGE));
        handlers.put(Effect.SPELL_ADD_DAMAGE,           new BoostSpellEffect(SpellsBoosts.Modifier.DAMAGE));
        handlers.put(Effect.SPELL_ADD_HEAL,             new BoostSpellEffect(SpellsBoosts.Modifier.HEAL));
        handlers.put(Effect.SPELL_SUB_AP,               new BoostSpellEffect(SpellsBoosts.Modifier.AP_COST));
        handlers.put(Effect.SPELL_SUB_DELAY,            new BoostSpellEffect(SpellsBoosts.Modifier.REDUCE_DELAY));
        handlers.put(Effect.SPELL_ADD_CRITICAL_HIT,     new BoostSpellEffect(SpellsBoosts.Modifier.CRITICAL));
        handlers.put(Effect.SPELL_REMOVE_LINE_CAST,     new BoostSpellEffect(SpellsBoosts.Modifier.LAUNCH_LINE));
        handlers.put(Effect.SPELL_REMOVE_LINE_OF_SIGHT, new BoostSpellEffect(SpellsBoosts.Modifier.LINE_OF_SIGHT));
        handlers.put(Effect.SPELL_ADD_PER_TURN_CAST,    new BoostSpellEffect(SpellsBoosts.Modifier.LAUNCH_PER_TURN));
        handlers.put(Effect.SPELL_ADD_PER_TARGET_CAST,  new BoostSpellEffect(SpellsBoosts.Modifier.LAUNCH_PER_TARGET));
        handlers.put(Effect.SPELL_SET_DELAY,            new SetSpellModifierEffect(SpellsBoosts.Modifier.SET_DELAY));
    }

    @Override
    public SpecialEffect create(ItemTemplateEffectEntry entry, boolean maximize) {
        return handlers.getOrDefault(entry.effect(), NullEffectHandler.INSTANCE).create(entry, maximize);
    }

    @Override
    public List<SpecialEffect> create(List<ItemTemplateEffectEntry> effects, boolean maximize) {
        return effects
            .stream()
            .filter(effect -> effect.effect().type() == Effect.Type.SPECIAL)
            .map(effect -> create(effect, maximize))
            .collect(Collectors.toList())
        ;
    }

    @Override
    public Class<SpecialEffect> type() {
        return SpecialEffect.class;
    }
}
