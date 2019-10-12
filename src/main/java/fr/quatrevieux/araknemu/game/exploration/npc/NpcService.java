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

package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.exploration.map.event.MapLoaded;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.DialogService;
import org.slf4j.Logger;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Manage living NPCs
 */
final public class NpcService implements EventsSubscriber, PreloadableService {
    final private DialogService dialogService;
    final private NpcTemplateRepository templateRepository;
    final private NpcRepository npcRepository;
    final private Collection<ExchangeProvider> exchangeProviders;

    final private ConcurrentMap<Integer, GameNpc> npcByEntityId = new ConcurrentHashMap<>();

    public NpcService(DialogService dialogService, NpcTemplateRepository templateRepository, NpcRepository npcRepository, Collection<ExchangeProvider> exchangeProviders) {
        this.dialogService = dialogService;
        this.templateRepository = templateRepository;
        this.npcRepository = npcRepository;
        this.exchangeProviders = exchangeProviders;
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

        NpcTemplate template = templateRepository.get(entity.templateId());

        GameNpc npc = new GameNpc(
            entity,
            template,
            dialogService.forNpc(entity),
            exchangeProviders.stream()
                .map(provider -> provider.load(template))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList())
        );

        npcByEntityId.put(entity.id(), npc);

        return npc;
    }
}
