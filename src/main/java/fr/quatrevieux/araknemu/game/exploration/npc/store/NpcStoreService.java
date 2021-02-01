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

package fr.quatrevieux.araknemu.game.exploration.npc.store;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.exploration.npc.ExchangeProvider;
import fr.quatrevieux.araknemu.game.item.ItemService;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Handle Npc stores
 */
public final class NpcStoreService implements ExchangeProvider {
    private final ItemService itemService;
    private final GameConfiguration.EconomyConfiguration configuration;
    private final ItemTemplateRepository itemTemplateRepository;

    /**
     * Already loaded stores
     * Indexed by the npc template id
     */
    private final Map<Integer, NpcStore> stores = new ConcurrentHashMap<>();

    public NpcStoreService(ItemService itemService, ItemTemplateRepository itemTemplateRepository, GameConfiguration.EconomyConfiguration configuration) {
        this.itemService = itemService;
        this.itemTemplateRepository = itemTemplateRepository;
        this.configuration = configuration;
    }

    @Override
    public Optional<NpcStore> load(NpcTemplate template) {
        if (stores.containsKey(template.id())) {
            return Optional.of(stores.get(template.id()));
        }

        return template.storeItems()
            .map(items -> {
                final NpcStore store = new NpcStore(
                    itemService,
                    configuration,
                    Arrays.stream(items)
                        .mapToObj(itemTemplateRepository::get)
                        .collect(Collectors.toList())
                );

                stores.put(template.id(), store);

                return store;
            })
        ;
    }
}
