package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Manage living NPCs
 */
final public class NpcService implements EventsSubscriber, PreloadableService {
    final private NpcTemplateRepository templateRepository;
    final private NpcRepository npcRepository;

    final private ConcurrentMap<Integer, GameNpc> npcByEntityId = new ConcurrentHashMap<>();

    public NpcService(NpcTemplateRepository templateRepository, NpcRepository npcRepository) {
        this.templateRepository = templateRepository;
        this.npcRepository = npcRepository;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading NPCs...");

        logger.info("{} NPC templates loaded", templateRepository.all().size());

        npcRepository.all().forEach(this::createByEntity);
        logger.info("{} NPCs loaded", npcByEntityId.size());
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<MapLoaded>() {
                @Override
                public void on(MapLoaded event) {
                    npcRepository.byMapId(event.map().id()).stream()
                        .map(NpcService.this::createByEntity)
                        .forEach(npc -> event.map().add(npc))
                    ;
                }

                @Override
                public Class<MapLoaded> event() {
                    return MapLoaded.class;
                }
            }
        };
    }

    /**
     * Create the GameNpc from entity if not yet created
     *
     * @param entity The NPC entity to create
     */
    private GameNpc createByEntity(Npc entity) {
        if (npcByEntityId.containsKey(entity.id())) {
            return npcByEntityId.get(entity.id());
        }

        GameNpc npc = new GameNpc(entity, templateRepository.get(entity.templateId()));

        npcByEntityId.put(entity.id(), npc);

        return npc;
    }
}
