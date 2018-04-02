package fr.quatrevieux.araknemu.game.exploration.area;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.entity.environment.SubArea;
import fr.quatrevieux.araknemu.data.living.repository.environment.SubAreaRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.listener.player.InitializeAreas;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
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
            new Listener<PlayerLoaded>() {
                @Override
                public void on(PlayerLoaded event) {
                    event.player().dispatcher().add(new InitializeAreas(event.player(), AreaService.this));
                }

                @Override
                public Class<PlayerLoaded> event() {
                    return PlayerLoaded.class;
                }
            }
        };
    }
}
