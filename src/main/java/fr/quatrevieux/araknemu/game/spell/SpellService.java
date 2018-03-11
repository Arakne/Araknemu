package fr.quatrevieux.araknemu.game.spell;

import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.data.world.repository.SpellTemplateRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Service for handle spells
 */
final public class SpellService implements PreloadableService {
    final private SpellTemplateRepository repository;
    final private ConcurrentMap<Integer, SpellLevels> spells = new ConcurrentHashMap<>();

    public SpellService(SpellTemplateRepository repository) {
        this.repository = repository;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading spells...");

        for (SpellTemplate template : repository.load()) {
            spells.put(template.id(), new SpellLevels(template));
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
            spells.put(spellId, new SpellLevels(repository.get(spellId)));
        }

        return spells.get(spellId);
    }
}
