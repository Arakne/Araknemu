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

package fr.quatrevieux.araknemu.data.world.transformer;

import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.transformer.TransformerException;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Transformer for item set bonus effects
 */
public final class ItemSetBonusTransformer implements Transformer<List<List<ItemTemplateEffectEntry>>> {
    @Override
    public String serialize(List<List<ItemTemplateEffectEntry>> value) {
        return value.stream()
            .map(
                effects -> effects
                    .stream()
                    .map(effect -> effect.effect().id() + ":" + effect.min())
                    .collect(Collectors.joining(","))
            )
            .collect(Collectors.joining(";"))
        ;
    }

    @Override
    public List<List<ItemTemplateEffectEntry>> unserialize(String serialize) {
        if (serialize == null || serialize.isEmpty()) {
            return Collections.emptyList();
        }

        final List<List<ItemTemplateEffectEntry>> bonus = new ArrayList<>();

        try {
            for (String effects : StringUtils.splitByWholeSeparatorPreserveAllTokens(serialize, ";")) {
                if (effects.isEmpty()) {
                    bonus.add(Collections.emptyList());
                    continue;
                }

                final String[] aEffects = StringUtils.split(effects, ',');

                if (aEffects.length == 1) {
                    bonus.add(Collections.singletonList(parseEffect(aEffects[0])));
                    continue;
                }

                bonus.add(
                    Arrays.stream(aEffects)
                        .map(this::parseEffect)
                        .collect(Collectors.toList())
                );
            }
        } catch (RuntimeException e) {
            throw new TransformerException("Cannot parse item set bonus '" + serialize + "'", e);
        }

        return bonus;
    }

    private ItemTemplateEffectEntry parseEffect(String effect) {
        final String[] parts = StringUtils.split(effect, ":", 2);

        return new ItemTemplateEffectEntry(
            Effect.byId(Integer.parseInt(parts[0])),
            Integer.parseInt(parts[1]),
            0, 0, ""
        );
    }
}
