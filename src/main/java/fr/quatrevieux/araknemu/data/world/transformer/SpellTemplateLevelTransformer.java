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

package fr.quatrevieux.araknemu.data.world.transformer;

import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.data.value.SpellTemplateEffect;
import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.util.ParseUtils;
import fr.quatrevieux.araknemu.util.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.PolyNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Transform {@link fr.quatrevieux.araknemu.data.world.entity.SpellTemplate.Level}
 */
public class SpellTemplateLevelTransformer implements Transformer<SpellTemplate.Level> {
    public static final int NORMAL_EFFECTS = 0;
    public static final int CRITICAL_EFFECTS = 1;
    public static final int AP_COST = 2;
    public static final int RANGE_MIN = 3;
    public static final int RANGE_MAX = 4;
    public static final int CRITICAL_RATE = 5;
    public static final int FAILURE_RATE = 6;
    public static final int LINE_ONLY = 7;
    public static final int LINE_OF_SIGHT = 8;
    public static final int FREE_CELL = 9;
    public static final int BOOST_RANGE = 10;
    public static final int CLASS_ID = 11;
    public static final int LAUNCH_BY_TURN = 12;
    public static final int LAUNCH_BY_PLAYER = 13;
    public static final int LAUNCH_DELAY = 14;
    public static final int EFFECT_AREAS = 15;
    public static final int REQUIRED_STATES = 16;
    public static final int FORBIDDEN_STATES = 17;
    public static final int MIN_PLAYER_LEVEL = 18;
    public static final int ENDS_TURN_ON_FAILURE = 19;

    private final Transformer<EffectArea> areaTransformer;

    public SpellTemplateLevelTransformer(Transformer<EffectArea> areaTransformer) {
        this.areaTransformer = areaTransformer;
    }

    @Override
    public @PolyNull String serialize(SpellTemplate.@PolyNull Level value) {
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings("return")
    public SpellTemplate.@PolyNull Level unserialize(@PolyNull String serialize) {
        if (serialize == null || serialize.isEmpty() || "-1".equals(serialize)) {
            return null;
        }

        final String[] parts = StringUtils.splitByWholeSeparatorPreserveAllTokens(serialize, "|", 20);

        if (parts.length < 20) {
            throw new IllegalArgumentException("Cannot parse spell level '" + serialize + "' : some data are missing");
        }

        try {
            return check(
                new SpellTemplate.Level(
                    effects(parts[NORMAL_EFFECTS]),
                    effects(parts[CRITICAL_EFFECTS]),
                    nonNegative(parts[AP_COST]),
                    interval(parts[RANGE_MIN], parts[RANGE_MAX]),
                    nonNegative(parts[CRITICAL_RATE]),
                    nonNegative(parts[FAILURE_RATE]),
                    bool(parts[LINE_ONLY]),
                    bool(parts[LINE_OF_SIGHT]),
                    bool(parts[FREE_CELL]),
                    bool(parts[BOOST_RANGE]),
                    integer(parts[CLASS_ID]),
                    integer(parts[LAUNCH_BY_TURN]),
                    integer(parts[LAUNCH_BY_PLAYER]),
                    integer(parts[LAUNCH_DELAY]),
                    areas(parts[EFFECT_AREAS]),
                    states(parts[REQUIRED_STATES]),
                    states(parts[FORBIDDEN_STATES]),
                    integer(parts[MIN_PLAYER_LEVEL]),
                    bool(parts[ENDS_TURN_ON_FAILURE])
                )
            );
        } catch (RuntimeException e) {
            throw new IllegalArgumentException("Cannot parse spell level '" + serialize + "'", e);
        }
    }

    private int integer(String value) {
        return value.isEmpty() ? 0 : Integer.parseInt(value);
    }

    private @NonNegative int nonNegative(String value) {
        return value.isEmpty() ? 0 : ParseUtils.parseNonNegativeInt(value);
    }

    private boolean bool(String value) {
        return "true".equals(value);
    }

    private Interval interval(String min, String max) {
        return new Interval(
            nonNegative(min),
            nonNegative(max)
        );
    }

    private List<SpellTemplateEffect> effects(String sEffects) {
        return Arrays.stream(StringUtils.split(sEffects, ";"))
            .map(this::effect)
            .collect(Collectors.toList())
        ;
    }

    private SpellTemplateEffect effect(String sEffect) {
        final Splitter params = new Splitter(sEffect, ',');

        return new SpellTemplateEffect(
            params.nextIntOrDefault(0),
            params.nextNonNegativeIntOrDefault(0),
            params.nextNonNegativeIntOrDefault(0),
            params.nextIntOrDefault(0),
            params.nextNonNegativeOrNegativeOneIntOrDefault(0),
            params.nextNonNegativeIntOrDefault(0),
            params.nextPartOrDefault("")
        );
    }

    private List<EffectArea> areas(String sAreas) {
        final List<EffectArea> areas = new ArrayList<>(sAreas.length() / 2);

        for (int i = 0; i < sAreas.length() - 1; i += 2) {
            areas.add(areaTransformer.unserialize(sAreas.substring(i, i + 2)));
        }

        return areas;
    }

    private int[] states(String states) {
        return Arrays.stream(StringUtils.split(states, ";"))
            .mapToInt(Integer::parseInt)
            .toArray()
        ;
    }

    private SpellTemplate.Level check(SpellTemplate.Level level) {
        if (level.criticalEffects().size() + level.effects().size() != level.effectAreas().size()) {
            throw new IllegalArgumentException("Bad areas size");
        }

        return level;
    }
}
