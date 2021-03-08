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

package fr.quatrevieux.araknemu.game.spell;

import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.data.world.repository.SpellTemplateRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.spell.adapter.SpellLevelAdapter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffectService;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for handle spells
 */
public final class SpellService implements PreloadableService {
    private final SpellTemplateRepository repository;
    private final SpellEffectService effectService;

    private final ConcurrentMap<Integer, SpellLevels> spells = new ConcurrentHashMap<>();

    public SpellService(SpellTemplateRepository repository, SpellEffectService effectService) {
        this.repository = repository;
        this.effectService = effectService;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading spells...");

        for (SpellTemplate template : repository.load()) {
            spells.put(template.id(), load(template));
        }

        logger.info("{} spells loaded", spells.size());
    }

    /**
     * Get a spell
     *
     * @param spellId The spell id
     */
    public SpellLevels get(int spellId) {
        if (!spells.containsKey(spellId)) {
            spells.put(spellId, load(repository.get(spellId)));
        }

        return spells.get(spellId);
    }

    /**
     * Load the spell levels
     *
     * @param template The spell template
     */
    private SpellLevels load(SpellTemplate template) {
        final List<Spell> levels = new ArrayList<>(template.levels().length);

        for (int i = 0; i < template.levels().length; ++i) {
            final SpellTemplate.Level level = template.levels()[i];

            if (level == null) {
                break;
            }

            levels.add(makeLevel(i + 1, template, level));
        }

        return new SpellLevels(template, levels.toArray(new Spell[levels.size()]));
    }

    /**
     * Make one spell level
     */
    private Spell makeLevel(int level, SpellTemplate template, SpellTemplate.Level data) {
        return new SpellLevelAdapter(
            level,
            template,
            data,
            effectService.makeAll(data.effects(), data.effectAreas(), template.targets()),
            effectService.makeAll(
                data.criticalEffects(),
                data.effectAreas().subList(data.effects().size(), data.effectAreas().size()),
                template.targets()
            )
        );
    }
}
