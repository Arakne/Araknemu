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

package fr.quatrevieux.araknemu.game.exploration.exchange.npc;

import fr.quatrevieux.araknemu.game.exploration.exchange.*;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;

/**
 * Factories for npc exchanges
 */
final public class NpcExchangeFactories extends ExchangeFactoryAggregate<GameNpc> {
    @SafeVarargs
    public NpcExchangeFactories(ExchangeTypeFactory<GameNpc>... factories) {
        super(factories);

        register(new SimpleExchangeTypeFactory<>(ExchangeType.NPC_STORE, (initiator, target) -> new NpcStoreExchange(initiator, target, target.store()).dialog()));
    }
}
