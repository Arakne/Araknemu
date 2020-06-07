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
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Transform {@link fr.quatrevieux.araknemu.data.world.entity.SpellTemplate.Level}
 */
public class SpellTemplateLevelTransformer implements Transformer<SpellTemplate.Level> {
    final static public int NORMAL_EFFECTS = 0;
    final static public int CRITICAL_EFFECTS = 1;
    final static public int AP_COST = 2;
    final static public int RANGE_MIN = 3;
    final static public int RANGE_MAX = 4;
    final static public int CRITICAL_RATE = 5;
    final static public int FAILURE_RATE = 6;
    final static public int LINE_ONLY = 7;
    final static public int LINE_OF_SIGHT = 8;
    final static public int FREE_CELL = 9;
    final static public int BOOST_RANGE = 10;
    final static public int CLASS_ID = 11;
    final static public int LAUNCH_BY_TURN = 12;
    final static public int LAUNCH_BY_PLAYER = 13;
    final static public int LAUNCH_DELAY = 14;
    final static public int EFFECT_AREAS = 15;
    final static public int REQUIRED_STATES = 16;
    final static public int FORBIDDEN_STATES = 17;
    final static public int MIN_PLAYE_LEVEL = 18;
    final static public int ENDS_TURN_ON_FAILURE = 19;

    final private Transformer<EffectArea> areaTransformer;

    public SpellTemplateLevelTransformer(Transformer<EffectArea> areaTransformer) {
        this.areaTransformer = areaTransformer;
    }

    @Override
    public String serialize(SpellTemplate.Level value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public SpellTemplate.Level unserialize(String serialize) {
        if (serialize.isEmpty() || "-1".equals(serialize)) {
            return null;
        }

        String[] parts = StringUtils.splitByWholeSeparatorPreserveAllTokens(serialize, "|", 20);

        try {
            return check(
                new SpellTemplate.Level(
                    effects(parts[NORMAL_EFFECTS]),
                    effects(parts[CRITICAL_EFFECTS]),
                    integer(parts[AP_COST]),
                    interval(parts[RANGE_MIN], parts[RANGE_MAX]),
                    integer(parts[CRITICAL_RATE]),
                    integer(parts[FAILURE_RATE]),
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
                    integer(parts[MIN_PLAYE_LEVEL]),
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

    private boolean bool(String value) {
        return "true".equals(value);
    }

    private Interval interval(String min, String max) {
        return new Interval(
            integer(min),
            integer(max)
        );
    }

    private List<SpellTemplateEffect> effects(String sEffects) {
        return Arrays.stream(StringUtils.split(sEffects, ";"))
            .map(this::effect)
            .collect(Collectors.toList())
        ;
    }

    private SpellTemplateEffect effect(String sEffect) {
        String[] params = StringUtils.splitByWholeSeparatorPreserveAllTokens(sEffect, ",", 7);

        return new SpellTemplateEffect(
            integer(params[0]),
            integer(params[1]),
            integer(params[2]),
            integer(params[3]),
            integer(params[4]),
            integer(params[5]),
            params.length == 7 ? params[6] : null
        );
    }

    private List<EffectArea> areas(String sAreas) {
        List<EffectArea> areas = new ArrayList<>(sAreas.length() / 2);

        for (int i = 0; i < sAreas.length(); i += 2) {
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

        if (level.range().max() < level.range().min()) {
            throw new IllegalArgumentException("Bad range");
        }

        return level;
    }
}
