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

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeProcessor;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.exchange.player.AbstractPlayerExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.exchange.player.PlayerExchangePartyProcessor;
import fr.quatrevieux.araknemu.game.exploration.exchange.player.PlayerExchangeStorage;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.exchange.GameNpcExchange;
import fr.quatrevieux.araknemu.game.listener.player.exchange.SendDistantPackets;
import fr.quatrevieux.araknemu.game.listener.player.exchange.SendLocalPackets;
import fr.quatrevieux.araknemu.game.world.creature.Creature;

/**
 * Player party for npc exchange
 *
 * @see ExchangeType#NPC_EXCHANGE
 */
final public class NpcExchangeParty extends AbstractPlayerExchangeParty {
    final private GameNpc npc;

    /**
     * Construct the exchange party
     *
     * @param player The exchange initiator
     * @param npc The target npc
     * @param exchange The npc exchange
     */
    public NpcExchangeParty(ExplorationPlayer player, GameNpc npc, GameNpcExchange exchange) {
        this(player, npc, exchange, new PlayerExchangeStorage(player));
    }

    private NpcExchangeParty(ExplorationPlayer player, GameNpc npc, NpcExchangePartyProcessor npcExchangePartyProcessor, PlayerExchangeStorage storage) {
        super(
            player,
            new ExchangeProcessor(
                new PlayerExchangePartyProcessor(player, storage),
                npcExchangePartyProcessor
            ),
            storage
        );

        this.npc = npc;

        storage.dispatcher().register(npcExchangePartyProcessor);
        storage.dispatcher().register(new SendLocalPackets(this));
        npcExchangePartyProcessor.dispatcher().register(new SendDistantPackets(player));
    }

    private NpcExchangeParty(ExplorationPlayer player, GameNpc npc, GameNpcExchange exchange, PlayerExchangeStorage storage) {
        this(player, npc, new NpcExchangePartyProcessor(npc, exchange), storage);
    }

    @Override
    public ExchangeType type() {
        return ExchangeType.NPC_EXCHANGE;
    }

    @Override
    public Creature target() {
        return npc;
    }
}
