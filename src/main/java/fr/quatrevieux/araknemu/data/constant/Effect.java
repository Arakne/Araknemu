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

package fr.quatrevieux.araknemu.data.constant;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Enum of all items effects
 *
 * @todo Should be refactored
 */
public enum Effect {
    NONE(0, Type.SPECIAL),

    DIVORCE(7,      Type.USE),
    LEARN_EMOTE(10, Type.USE),

    HEAL2(81,                    Type.WEAPON),
    STOLEN_AP(84,                Type.WEAPON),
    STOLEN_WATER(91,             Type.WEAPON),
    STOLEN_EARTH(92,             Type.WEAPON),
    STOLEN_WIND(93,              Type.WEAPON),
    STOLEN_FIRE(94,              Type.WEAPON),
    STOLEN_NEUTRAL(95,           Type.WEAPON),
    INFLICT_DAMAGE_WATER(96,     Type.WEAPON),
    INFLICT_DAMAGE_EARTH(97,     Type.WEAPON),
    INFLICT_DAMAGE_WIND(98,      Type.WEAPON),
    INFLICT_DAMAGE_FIRE(99,      Type.WEAPON),
    INFLICT_DAMAGE_NEUTRAL(100,  Type.WEAPON),
    SUB_ACTION_POINTS_DODGE(101, Type.WEAPON),


    HEAL(108,     Type.WEAPON),
    ADD_LIFE(110, Type.USE),

    ADD_ACTION_POINTS(111,    Type.CHARACTERISTIC),
    ADD_DAMAGE(112,           Type.CHARACTERISTIC),
    MULTIPLY_DAMAGE(114,      Type.SPECIAL), // @fixme used ?
    ADD_CRITICAL_HIT(115,     Type.CHARACTERISTIC),
    SUB_RANGE_POINTS(116,     Type.CHARACTERISTIC),
    ADD_RANGE_POINTS(117,     Type.CHARACTERISTIC),
    ADD_STRENGTH(118,         Type.CHARACTERISTIC),
    ADD_AGILITY(119,          Type.CHARACTERISTIC),
    ADD_DAMAGE2(121,          Type.CHARACTERISTIC),
    ADD_CRITICAL_FAILURE(122, Type.CHARACTERISTIC),
    ADD_CHANCE(123,           Type.CHARACTERISTIC),
    ADD_WISDOM(124,           Type.CHARACTERISTIC),
    ADD_VITALITY(125,         Type.CHARACTERISTIC),
    ADD_INTELLIGENCE(126,     Type.CHARACTERISTIC),

    SUB_MOVEMENT_POINTS_DODGE(127, Type.WEAPON),

    ADD_MOVEMENT_POINTS(128,      Type.CHARACTERISTIC),
    STOLEN_KAMAS(130,             Type.WEAPON),
    ADD_DAMAGE_PERCENT(138,       Type.CHARACTERISTIC),

    ADD_ENERGY_POINTS(139, Type.USE),
    CHANGE_SPEAK(146,      Type.SPECIAL),
    CHANGE_APPEARANCE(149, Type.USE),

    SUB_CHANCE(152,               Type.CHARACTERISTIC),
    SUB_VITALITY(153,             Type.CHARACTERISTIC),
    SUB_AGILITY(154,              Type.CHARACTERISTIC),
    SUB_INTELLIGENCE(155,         Type.CHARACTERISTIC),
    SUB_WISDOM(156,               Type.CHARACTERISTIC),
    SUB_STRENGTH(157,             Type.CHARACTERISTIC),
    ADD_PODS(158,                 Type.SPECIAL),
    SUB_PODS(159,                 Type.SPECIAL),
    ADD_DODGE_AP(160,             Type.CHARACTERISTIC),
    ADD_DODGE_MP(161,             Type.CHARACTERISTIC),
    SUB_DODGE_AP(162,             Type.CHARACTERISTIC),
    SUB_DODGE_MP(163,             Type.CHARACTERISTIC),
    SUB_DAMAGE(164,               Type.CHARACTERISTIC),
    SUB_ACTION_POINTS(168,        Type.CHARACTERISTIC),
    SUB_MOVEMENT_POINTS(169,      Type.CHARACTERISTIC),
    SUB_CRITICAL_HIT(171,         Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_MAGIC(172,  Type.SPECIAL),
    SUB_REDUCE_DAMAGE_PHYSIC(173, Type.SPECIAL),
    ADD_INITIATIVE(174,           Type.SPECIAL),
    SUB_INITIATIVE(175,           Type.SPECIAL),
    ADD_DISCERNMENT(176,          Type.SPECIAL),
    SUB_DISCERNMENT(177,          Type.SPECIAL),
    ADD_HEAL_POINTS(178,          Type.CHARACTERISTIC),
    SUB_HEAL_POINTS(179,          Type.CHARACTERISTIC),
    ADD_SUMMONS(182,              Type.CHARACTERISTIC),

    ADD_REDUCE_DAMAGE_PHYSIC(183, Type.SPECIAL), // @fixme Handle characteristic group
    ADD_REDUCE_DAMAGE_MAGIC(184,  Type.SPECIAL),

    UNKNOWN_193(193,  Type.USE),
    EARN_KAMAS(194,   Type.USE),
    TRANSFORM_TO(197, Type.USE),
    RESURECT(206,     Type.USE),

    ADD_REDUCE_DAMAGE_PERCENT_EARTH(210,   Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_PERCENT_WATER(211,   Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_PERCENT_WIND(212,    Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_PERCENT_FIRE(213,    Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_PERCENT_NEUTRAL(214, Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_PERCENT_EARTH(215,   Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_PERCENT_WATER(216,   Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_PERCENT_WIND(217,    Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_PERCENT_FIRE(218,    Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_PERCENT_NEUTRAL(219, Type.CHARACTERISTIC),

    ADD_COUNTER_DAMAGE(220,                Type.CHARACTERISTIC),
    ADD_TRAP_DAMAGE(225,                   Type.CHARACTERISTIC),
    ADD_TRAP_PERCENT_DAMAGE(226,           Type.CHARACTERISTIC),

    ADD_REDUCE_DAMAGE_EARTH(240,   Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_WATER(241,   Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_WIND(242,    Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_FIRE(243,    Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_NEUTRAL(244, Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_WATER(246,   Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_EARTH(245,   Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_WIND(247,    Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_FIRE(248,    Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_NEUTRAL(249, Type.CHARACTERISTIC),

    ADD_REDUCE_DAMAGE_PERCENT_PVP_EARTH(250,   Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_PERCENT_PVP_WATER(251,   Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_PERCENT_PVP_WIND(252,    Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_PERCENT_PVP_FIRE(253,    Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_PERCENT_PVP_NEUTRAL(254, Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_PERCENT_PVP_WATER(255,   Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_PERCENT_PVP_EARTH(256,   Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_PERCENT_PVP_WIND(257,    Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_PERCENT_PVP_FIRE(258,    Type.CHARACTERISTIC),
    SUB_REDUCE_DAMAGE_PERCENT_PVP_NEUTRAL(259, Type.CHARACTERISTIC),

    ADD_REDUCE_DAMAGE_PVP_EARTH(260,   Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_PVP_WATER(261,   Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_PVP_WIND(262,    Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_PVP_FIRE(263,    Type.CHARACTERISTIC),
    ADD_REDUCE_DAMAGE_PVP_NEUTRAL(264, Type.CHARACTERISTIC),

    SPELL_ADD_RANGE(281,            Type.SPECIAL),
    SPELL_SET_MODIFIABLE_RANGE(282, Type.SPECIAL),
    SPELL_ADD_DAMAGE(283,           Type.SPECIAL),
    SPELL_ADD_HEAL(284,             Type.SPECIAL),
    SPELL_SUB_AP(285,               Type.SPECIAL),
    SPELL_SUB_DELAY(286,            Type.SPECIAL),
    SPELL_ADD_CRITICAL_HIT(287,     Type.SPECIAL),
    SPELL_REMOVE_LINE_CAST(288,     Type.SPECIAL),
    SPELL_REMOVE_LINE_OF_SIGHT(289, Type.SPECIAL),
    SPELL_ADD_PER_TURN_CAST(290,    Type.SPECIAL),
    SPELL_ADD_PER_TARGET_CAST(291,  Type.SPECIAL),
    SPELL_SET_DELAY(292,            Type.SPECIAL),

    SPELL_ADD_BASE_DAMAGE(293,      Type.SPECIAL),
    SPELL_SUB_RANGE(294,            Type.SPECIAL),

    RETURN_TO_SAVE_POINT(600,     Type.USE),
    LEARN_JOB(603,                Type.USE),
    LEARN_SPELL(604,              Type.USE),
    ADD_XP(605,                   Type.USE),
    ADD_CHARACT_WISDOM(606,       Type.USE),
    ADD_CHARACT_STRENGTH(607,     Type.USE),
    ADD_CHARACT_LUCK(608,         Type.USE),
    ADD_CHARACT_AGILITY(609,      Type.USE),
    ADD_CHARACT_VITALITY(610,     Type.USE),
    ADD_CHARACT_INTELLIGENCE(611, Type.USE),
    ADD_CHARACT_POINT(612,        Type.USE),
    ADD_SPELL_POINT(613,          Type.USE),

    ADD_JOB_XP(614,          Type.USE),
    FORGOT_JOB(615,          Type.USE),
    FORGOT_SPELL_LEVEL(616,  Type.USE),
    CONSULT(620,             Type.USE),
    INVOKE_CAPTURE(621,      Type.USE),
    FIREWORK(654,            Type.USE),

    CHANGE_ATTACK_ELEMENT(700, Type.SPECIAL),
    REPAIR_BREAKABLE_ITEM(702, Type.SPECIAL),

    MONSTER_CAPTURE(705,       Type.SPECIAL),
    MOUNT_CAPTURE(706,         Type.SPECIAL),

    ADDITIONAL_COST(710,       Type.SPECIAL),
    HUNTER_WEAPON(795,         Type.SPECIAL),
    PET_LIFE(800,              Type.SPECIAL),
    SIZE(810,                  Type.SPECIAL),
    REMAINING_TURNS(811,       Type.SPECIAL),
    BREAKABLE_ITEM_POINTS(812, Type.SPECIAL),

    ADD_SERENITY(930,       Type.SPECIAL),
    ADD_AGGRESSIVENESS(931, Type.SPECIAL),
    ADD_ENDURANCE(932,      Type.SPECIAL),
    SUB_ENDURANCE(933,      Type.SPECIAL),
    ADD_LOVE(934,           Type.SPECIAL),
    SUB_LOVE(935,           Type.SPECIAL),
    ADD_MATURITY(936,       Type.SPECIAL),
    SUB_MATURITY(937,       Type.SPECIAL),
    INCREASE_CAPACITY(939,  Type.SPECIAL),
    INCREASED_CAPACITY(940, Type.SPECIAL),
    MOUNT_PARK_OBJECT(948,  Type.SPECIAL),

    NULL1(970, Type.SPECIAL),
    NULL2(971, Type.SPECIAL),
    NULL3(972, Type.SPECIAL),
    NULL4(973, Type.SPECIAL),
    NULL5(974, Type.SPECIAL),
    ;

    public enum Type {
        CHARACTERISTIC,
        WEAPON,
        USE,
        SPECIAL
    }

    private static final Map<Integer, Effect> effects = new HashMap<>();

    private final int id;
    private final Type type;

    static {
        for (Effect effect : values()) {
            effects.put(effect.id, effect);
        }
    }

    private Effect(int id, Type type) {
        this.id = id;
        this.type = type;
    }

    /**
     * Get the effect id
     */
    public int id() {
        return id;
    }

    /**
     * Get the effect type
     */
    public Type type() {
        return type;
    }

    /**
     * Get effect by its id
     *
     * @see Effect#id()
     */
    public static Effect byId(int id) {
        final Effect effect = effects.get(id);

        if (effect == null) {
            throw new NoSuchElementException("Effect " + id + " is not supported");
        }

        return effect;
    }
}
