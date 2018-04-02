package fr.quatrevieux.araknemu.game.exploration.area;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.entity.environment.SubArea;
import fr.quatrevieux.araknemu.data.living.repository.environment.SubAreaRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.listener.service.RegisterAreaListeners;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for handle areas
 */
final public class AreaService implements PreloadableService, EventsSubscriber {
    final private SubAreaRepository repository;

    final private Map<Integer, SubArea> subAreas = new HashMap<>();

    public AreaService(SubAreaRepository repository) {
        this.repository = repository;
    }

    /**
     * Get all available areas
     */
    public Collection<SubArea> list() {
        return subAreas.values();
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading areas...");

        for (SubArea subArea : repository.all()) {
            subAreas.put(subArea.id(), subArea);
        }

        logger.info("Areas loaded");
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new RegisterAreaListeners(this)
        };
    }
}
