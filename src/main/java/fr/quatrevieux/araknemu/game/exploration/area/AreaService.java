package fr.quatrevieux.araknemu.game.exploration.area;

import fr.quatrevieux.araknemu.data.living.entity.environment.SubArea;
import fr.quatrevieux.araknemu.data.living.repository.environment.SubAreaRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.event.ListenerAggregate;
import fr.quatrevieux.araknemu.game.event.listener.service.RegisterAreaListeners;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for handle areas
 */
final public class AreaService implements PreloadableService {
    final private SubAreaRepository repository;
    final private ListenerAggregate dispatcher;

    final private Map<Integer, SubArea> subAreas = new HashMap<>();

    public AreaService(SubAreaRepository repository, ListenerAggregate dispatcher) {
        this.repository = repository;
        this.dispatcher = dispatcher;
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

        dispatcher.add(new RegisterAreaListeners(this));

        for (SubArea subArea : repository.all()) {
            subAreas.put(subArea.id(), subArea);
        }

        logger.info("Areas loaded");
    }
}
