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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.item.effect.mapping;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.game.item.effect.CharacteristicEffect;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Map item effect to characteristic
 */
final public class EffectToCharacteristicMapping implements EffectMapper<CharacteristicEffect> {
    static private class MappedCharacteristic {
        final private int multiplier;
        final private Characteristic characteristic;

        public MappedCharacteristic(int multiplier, Characteristic characteristic) {
            this.multiplier = multiplier;
            this.characteristic = characteristic;
        }
    }

    final private Map<Effect, MappedCharacteristic> map = new EnumMap<>(Effect.class);

    final private RandomUtil random = new RandomUtil();

    public EffectToCharacteristicMapping() {
        set(Effect.ADD_COUNTER_DAMAGE,   +1, Characteristic.COUNTER_DAMAGE);
        set(Effect.ADD_ACTION_POINTS,    +1, Characteristic.ACTION_POINT);
        set(Effect.ADD_DAMAGE,           +1, Characteristic.FIXED_DAMAGE);
        set(Effect.ADD_DAMAGE2,          +1, Characteristic.FIXED_DAMAGE);
        set(Effect.ADD_CRITICAL_HIT,     +1, Characteristic.CRITICAL_BONUS);
        set(Effect.SUB_RANGE_POINTS,     -1, Characteristic.SIGHT_BOOST);
        set(Effect.ADD_RANGE_POINTS,     +1, Characteristic.SIGHT_BOOST);
        set(Effect.ADD_STRENGTH,         +1, Characteristic.STRENGTH);
        set(Effect.ADD_AGILITY,          +1, Characteristic.AGILITY);
        set(Effect.ADD_CRITICAL_FAILURE, +1, Characteristic.FAIL_MALUS);
        set(Effect.ADD_CHANCE,           +1, Characteristic.LUCK);
        set(Effect.ADD_WISDOM,           +1, Characteristic.WISDOM);
        set(Effect.ADD_VITALITY,         +1, Characteristic.VITALITY);
        set(Effect.ADD_INTELLIGENCE,     +1, Characteristic.INTELLIGENCE);
        set(Effect.ADD_MOVEMENT_POINTS,  +1, Characteristic.MOVEMENT_POINT);
        set(Effect.ADD_DAMAGE_PERCENT,   +1, Characteristic.PERCENT_DAMAGE);
        set(Effect.SUB_STRENGTH,         -1, Characteristic.STRENGTH);
        set(Effect.SUB_AGILITY,          -1, Characteristic.AGILITY);
        set(Effect.SUB_CHANCE,           -1, Characteristic.LUCK);
        set(Effect.SUB_WISDOM,           -1, Characteristic.WISDOM);
        set(Effect.SUB_VITALITY,         -1, Characteristic.VITALITY);
        set(Effect.SUB_INTELLIGENCE,     -1, Characteristic.INTELLIGENCE);
        set(Effect.ADD_DODGE_AP,         +1, Characteristic.RESISTANCE_ACTION_POINT);
        set(Effect.ADD_DODGE_MP,         +1, Characteristic.RESISTANCE_MOVEMENT_POINT);
        set(Effect.SUB_DODGE_AP,         -1, Characteristic.RESISTANCE_ACTION_POINT);
        set(Effect.SUB_DODGE_MP,         -1, Characteristic.RESISTANCE_MOVEMENT_POINT);
        set(Effect.SUB_DAMAGE,           -1, Characteristic.FIXED_DAMAGE);
        set(Effect.SUB_ACTION_POINTS,    -1, Characteristic.ACTION_POINT);
        set(Effect.SUB_MOVEMENT_POINTS,  -1, Characteristic.MOVEMENT_POINT);
        set(Effect.SUB_CRITICAL_HIT,     -1, Characteristic.CRITICAL_BONUS);
        set(Effect.ADD_HEAL_POINTS,      +1, Characteristic.HEALTH_BOOST);
        set(Effect.SUB_HEAL_POINTS,      -1, Characteristic.HEALTH_BOOST);
        set(Effect.ADD_SUMMONS,          +1, Characteristic.MAX_SUMMONED_CREATURES);

        set(Effect.ADD_REDUCE_DAMAGE_PERCENT_EARTH,   +1, Characteristic.RESISTANCE_PERCENT_EARTH);
        set(Effect.ADD_REDUCE_DAMAGE_PERCENT_WATER,   +1, Characteristic.RESISTANCE_PERCENT_WATER);
        set(Effect.ADD_REDUCE_DAMAGE_PERCENT_WIND,    +1, Characteristic.RESISTANCE_PERCENT_AIR);
        set(Effect.ADD_REDUCE_DAMAGE_PERCENT_FIRE,    +1, Characteristic.RESISTANCE_PERCENT_FIRE);
        set(Effect.ADD_REDUCE_DAMAGE_PERCENT_NEUTRAL, +1, Characteristic.RESISTANCE_PERCENT_NEUTRAL);
        set(Effect.SUB_REDUCE_DAMAGE_PERCENT_EARTH,   -1, Characteristic.RESISTANCE_PERCENT_EARTH);
        set(Effect.SUB_REDUCE_DAMAGE_PERCENT_WATER,   -1, Characteristic.RESISTANCE_PERCENT_WATER);
        set(Effect.SUB_REDUCE_DAMAGE_PERCENT_WIND,    -1, Characteristic.RESISTANCE_PERCENT_AIR);
        set(Effect.SUB_REDUCE_DAMAGE_PERCENT_FIRE,    -1, Characteristic.RESISTANCE_PERCENT_FIRE);
        set(Effect.SUB_REDUCE_DAMAGE_PERCENT_NEUTRAL, -1, Characteristic.RESISTANCE_PERCENT_NEUTRAL);

        set(Effect.ADD_REDUCE_DAMAGE_EARTH,   +1, Characteristic.RESISTANCE_EARTH);
        set(Effect.ADD_REDUCE_DAMAGE_WATER,   +1, Characteristic.RESISTANCE_WATER);
        set(Effect.ADD_REDUCE_DAMAGE_WIND,    +1, Characteristic.RESISTANCE_AIR);
        set(Effect.ADD_REDUCE_DAMAGE_FIRE,    +1, Characteristic.RESISTANCE_FIRE);
        set(Effect.ADD_REDUCE_DAMAGE_NEUTRAL, +1, Characteristic.RESISTANCE_NEUTRAL);
        set(Effect.SUB_REDUCE_DAMAGE_WATER,   -1, Characteristic.RESISTANCE_WATER);
        set(Effect.SUB_REDUCE_DAMAGE_EARTH,   -1, Characteristic.RESISTANCE_EARTH);
        set(Effect.SUB_REDUCE_DAMAGE_WIND,    -1, Characteristic.RESISTANCE_AIR);
        set(Effect.SUB_REDUCE_DAMAGE_FIRE,    -1, Characteristic.RESISTANCE_FIRE);
        set(Effect.SUB_REDUCE_DAMAGE_NEUTRAL, -1, Characteristic.RESISTANCE_NEUTRAL);

        set(Effect.ADD_REDUCE_DAMAGE_PERCENT_PVP_EARTH,   +1, Characteristic.RESISTANCE_PERCENT_PVP_EARTH);
        set(Effect.ADD_REDUCE_DAMAGE_PERCENT_PVP_WATER,   +1, Characteristic.RESISTANCE_PERCENT_PVP_WATER);
        set(Effect.ADD_REDUCE_DAMAGE_PERCENT_PVP_WIND,    +1, Characteristic.RESISTANCE_PERCENT_PVP_AIR);
        set(Effect.ADD_REDUCE_DAMAGE_PERCENT_PVP_FIRE,    +1, Characteristic.RESISTANCE_PERCENT_PVP_FIRE);
        set(Effect.ADD_REDUCE_DAMAGE_PERCENT_PVP_NEUTRAL, +1, Characteristic.RESISTANCE_PERCENT_PVP_NEUTRAL);
        set(Effect.SUB_REDUCE_DAMAGE_PERCENT_PVP_WATER,   -1, Characteristic.RESISTANCE_PERCENT_PVP_WATER);
        set(Effect.SUB_REDUCE_DAMAGE_PERCENT_PVP_EARTH,   -1, Characteristic.RESISTANCE_PERCENT_PVP_EARTH);
        set(Effect.SUB_REDUCE_DAMAGE_PERCENT_PVP_WIND,    -1, Characteristic.RESISTANCE_PERCENT_PVP_AIR);
        set(Effect.SUB_REDUCE_DAMAGE_PERCENT_PVP_FIRE,    -1, Characteristic.RESISTANCE_PERCENT_PVP_FIRE);
        set(Effect.SUB_REDUCE_DAMAGE_PERCENT_PVP_NEUTRAL, -1, Characteristic.RESISTANCE_PERCENT_PVP_NEUTRAL);

        set(Effect.ADD_REDUCE_DAMAGE_PVP_EARTH,   +1, Characteristic.RESISTANCE_PVP_EARTH);
        set(Effect.ADD_REDUCE_DAMAGE_PVP_WATER,   +1, Characteristic.RESISTANCE_PVP_WATER);
        set(Effect.ADD_REDUCE_DAMAGE_PVP_WIND,    +1, Characteristic.RESISTANCE_PVP_AIR);
        set(Effect.ADD_REDUCE_DAMAGE_PVP_FIRE,    +1, Characteristic.RESISTANCE_PVP_FIRE);
        set(Effect.ADD_REDUCE_DAMAGE_PVP_NEUTRAL, +1, Characteristic.RESISTANCE_PVP_NEUTRAL);

        set(Effect.ADD_TRAP_DAMAGE,         +1, Characteristic.TRAP_BOOST);
        set(Effect.ADD_TRAP_PERCENT_DAMAGE, +1, Characteristic.PERCENT_TRAP_BOOST);
    }

    @Override
    public CharacteristicEffect create(ItemTemplateEffectEntry effect, boolean maximize) {
        return maximize ? createMaximize(effect) : createRandom(effect);
    }

    @Override
    public List<CharacteristicEffect> create(List<ItemTemplateEffectEntry> effects, boolean maximize) {
        return effects
            .stream()
            .filter(effect -> effect.effect().type() == Effect.Type.CHARACTERISTIC)
            .map(effect -> create(effect, maximize))
            .collect(Collectors.toList())
        ;
    }

    @Override
    public Class<CharacteristicEffect> type() {
        return CharacteristicEffect.class;
    }

    /**
     * Check if the effect is a negative effect
     *
     * @throws NoSuchElementException When effect is not registered
     */
    public boolean isNegative(Effect effect) {
        return get(effect).multiplier < 0;
    }

    /**
     * Create a new characteristic effect
     *
     * @param effect The effect
     * @param value The effect value
     *
     * @throws NoSuchElementException When effect is not registered
     */
    public CharacteristicEffect create(Effect effect, int value) {
        MappedCharacteristic mapped = get(effect);

        return new CharacteristicEffect(effect, value, mapped.multiplier, mapped.characteristic);
    }

    /**
     * Create new characteristic effect with random value
     */
    public CharacteristicEffect createRandom(ItemTemplateEffectEntry entry) {
        int value = entry.min();

        if (entry.max() > value) {
            value = random.rand(value, entry.max());
        }

        return create(entry.effect(), value);
    }

    /**
     * Create new characteristic effect with maximal value
     */
    public CharacteristicEffect createMaximize(ItemTemplateEffectEntry entry) {
        int value;

        if (isNegative(entry.effect())) {
            value = entry.min();
        } else {
            value = Math.max(entry.min(), entry.max());
        }

        return create(entry.effect(), value);
    }

    private void set(Effect effect, int multiplier, Characteristic characteristic) {
        map.put(effect, new MappedCharacteristic(multiplier, characteristic));
    }

    private MappedCharacteristic get(Effect effect) {
        if (!map.containsKey(effect)) {
            throw new NoSuchElementException("Characteristic effect " + effect + " is not found");
        }

        return map.get(effect);
    }
}
