package fr.quatrevieux.araknemu.game.monster;

import fr.quatrevieux.araknemu.data.world.entity.monster.MonsterTemplate;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterTemplateRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Handle loading monsters
 */
final public class MonsterService implements PreloadableService {
    final private SpellService spellService;
    final private MonsterTemplateRepository repository;

    /**
     * Monster grade indexed by monster id
     */
    final private ConcurrentMap<Integer, GradeSet> monsters = new ConcurrentHashMap<>();

    public MonsterService(SpellService spellService, MonsterTemplateRepository repository) {
        this.spellService = spellService;
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
        GradeSet grades = new GradeSet(
            Arrays.stream(template.grades())
                .map(grade -> createMonster(template, grade))
                .collect(Collectors.toList())
        );

        monsters.put(template.id(), grades);

        return grades;
    }

    /**
     * Create a single monster level
     */
    private Monster createMonster(MonsterTemplate template, MonsterTemplate.Grade grade) {
        Map<Integer, Spell> spells = new HashMap<>(grade.spells().size());

        for (Map.Entry<Integer, Integer> entry : grade.spells().entrySet()) {
            spells.put(entry.getKey(), spellService.get(entry.getKey()).level(entry.getValue()));
        }

        return new Monster(template, grade, new MonsterSpellList(spells));
    }
}
