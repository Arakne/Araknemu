package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.DialogService;
import org.slf4j.Logger;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Manage living NPCs
 */
final public class NpcService implements EventsSubscriber, PreloadableService {
    final private DialogService dialogService;
    final private NpcTemplateRepository templateRepository;
    final private NpcRepository npcRepository;

    final private ConcurrentMap<Integer, GameNpc> npcByEntityId = new ConcurrentHashMap<>();

    public NpcService(DialogService dialogService, NpcTemplateRepository templateRepository, NpcRepository npcRepository) {
        this.dialogService = dialogService;
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
                        .forEach(npc -> npc.join(event.map()))
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
     * Get the NPC by the entity ID
     *
     * @param id The entity ID. /!\ Not same as sprite id
     */
    public GameNpc get(int id) {
        if (npcByEntityId.containsKey(id)) {
            return npcByEntityId.get(id);
        }

        return createByEntity(npcRepository.get(id));
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

        GameNpc npc = new GameNpc(
            entity,
            templateRepository.get(entity.templateId()),
            dialogService.forNpc(entity)
        );

        npcByEntityId.put(entity.id(), npc);

        return npc;
    }
}
