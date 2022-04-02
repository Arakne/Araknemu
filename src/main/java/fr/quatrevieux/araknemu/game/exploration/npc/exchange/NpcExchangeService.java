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

package fr.quatrevieux.araknemu.game.exploration.npc.exchange;

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcExchange;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcExchangeRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.event.GameStarted;
import fr.quatrevieux.araknemu.game.exploration.npc.ExchangeProvider;
import fr.quatrevieux.araknemu.game.item.ItemService;
import org.apache.logging.log4j.Logger;
import org.checkerframework.checker.index.qual.Positive;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Manage the npc exchanges
 */
public final class NpcExchangeService implements PreloadableService, EventsSubscriber, ExchangeProvider {
    private final ItemService itemService;
    private final NpcExchangeRepository repository;
    private final ItemTemplateRepository templateRepository;

    private final Map<Integer, GameNpcExchange> exchangeByTemplateId = new ConcurrentHashMap<>();
    private boolean preloading = false;

    public NpcExchangeService(ItemService itemService, NpcExchangeRepository repository, ItemTemplateRepository templateRepository) {
        this.itemService = itemService;
        this.repository = repository;
        this.templateRepository = templateRepository;
    }

    @Override
    public void preload(Logger logger) {
        preloading = true;
        logger.info("Loading npc exchanges...");

        repository.all().stream()
            .collect(Collectors.groupingBy(NpcExchange::npcTemplateId))
            .values()
            .forEach(this::createNpcExchange)
        ;

        logger.info("{} npc exchanges loaded", exchangeByTemplateId.size());
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<GameStarted>() {
                @Override
                public void on(GameStarted event) {
                    preloading = false;
                }

                @Override
                public Class<GameStarted> event() {
                    return GameStarted.class;
                }
            },
        };
    }

    @Override
    public Optional<GameNpcExchange> load(NpcTemplate template) {
        final GameNpcExchange loadedExchange = exchangeByTemplateId.get(template.id());

        if (loadedExchange != null) {
            return Optional.of(loadedExchange);
        }

        // All exchanges are preloaded, but not found for the given npc
        // => the npc do not have exchange
        if (preloading) {
            return Optional.empty();
        }

        // Try loading from database
        final List<NpcExchange> exchangeEntities = repository.byNpcTemplate(template);

        if (exchangeEntities.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(createNpcExchange(exchangeEntities));
    }

    /**
     * Load the item template map
     */
    private Map<ItemTemplate, @Positive Integer> loadItemTemplates(Map<Integer, @Positive Integer> templateIdsAndQuantity) {
        final Map<ItemTemplate, @Positive Integer> templates = new HashMap<>(templateIdsAndQuantity.size());

        // @todo templateRepository#getAll
        templateIdsAndQuantity.forEach((templateId, quantity) -> templates.put(templateRepository.get(templateId), quantity));

        return templates;
    }

    /**
     * Creates the exchange from exchange entities
     */
    private GameNpcExchange createNpcExchange(List<NpcExchange> exchangeEntities) {
        final GameNpcExchange exchange = new GameNpcExchange(
            exchangeEntities.stream()
                .map(entity -> new NpcExchangeEntry(itemService, entity, loadItemTemplates(entity.exchangedItems())))
                .collect(Collectors.toList())
        );

        exchangeByTemplateId.put(exchangeEntities.get(0).npcTemplateId(), exchange);

        return exchange;
    }
}
