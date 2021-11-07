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

package fr.quatrevieux.araknemu.game.monster;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterTemplate;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterTemplateRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.monster.reward.MonsterRewardService;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Handle loading monsters
 */
public final class MonsterService implements PreloadableService {
    private final SpellService spellService;
    private final MonsterRewardService rewardService;
    private final MonsterTemplateRepository repository;

    /**
     * Monster grade indexed by monster id
     */
    private final ConcurrentMap<Integer, GradeSet> monsters = new ConcurrentHashMap<>();

    public MonsterService(SpellService spellService, MonsterRewardService rewardService, MonsterTemplateRepository repository) {
        this.spellService = spellService;
        this.rewardService = rewardService;
        this.repository = repository;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading monsters...");

        repository.all().forEach(this::createMonsterGrades);

        logger.info("{} monsters loaded", monsters.size());
    }

    /**
     * Load monster grades
     *
     * @param id The monster template id
     *
     * @return The monster grades
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException When the monster template is not found
     */
    public GradeSet load(int id) {
        return monsters.containsKey(id)
            ? monsters.get(id)
            : createMonsterGrades(repository.get(id))
        ;
    }

    /**
     * Create all monster grades related to a monster template
     *
     * @param template The template to load
     */
    private GradeSet createMonsterGrades(MonsterTemplate template) {
        final List<Monster> monsterGrades = new ArrayList<>(template.grades().length);

        for (int grade = 0; grade < template.grades().length; ++grade) {
            monsterGrades.add(createMonster(template, grade));
        }

        final GradeSet grades = new GradeSet(monsterGrades);

        monsters.put(template.id(), grades);

        return grades;
    }

    /**
     * Create a single monster level
     */
    private Monster createMonster(MonsterTemplate template, int grade) {
        final MonsterTemplate.Grade gradeData = template.grades()[grade];
        final Map<Integer, Spell> spells = new HashMap<>(gradeData.spells().size());

        for (Map.Entry<Integer, Integer> entry : gradeData.spells().entrySet()) {
            spells.put(entry.getKey(), spellService.get(entry.getKey()).level(entry.getValue()));
        }

        return new Monster(
            template,
            new MonsterSpellList(spells),
            rewardService.get(template.id(), grade + 1),
            grade
        );
    }
}
