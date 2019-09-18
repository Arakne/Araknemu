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

package fr.quatrevieux.araknemu.game.exploration.exchange.player;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangePartyProcessor;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeProcessor;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.listener.player.exchange.SendDistantPackets;
import fr.quatrevieux.araknemu.game.listener.player.exchange.SendLocalPackets;
import fr.quatrevieux.araknemu.game.world.creature.Creature;

/**
 * Party for player exchange
 *
 * The party is linked to the distant party (other)
 */
final public class PlayerExchangeParty extends AbstractPlayerExchangeParty {
    private PlayerExchangeParty(ExplorationPlayer player, ExchangeProcessor processor, PlayerExchangeStorage storage) {
        super(player, processor, storage);
    }

    @Override
    public ExchangeType type() {
        return ExchangeType.PLAYER_EXCHANGE;
    }

    @Override
    public Creature target() {
        // @todo Does the target player should be sent ?
        return null;
    }

    /**
     * Start the exchange interaction
     */
    public void start() {
        actor().interactions().start(dialog());
    }

    /**
     * Make exchange parties between two players
     */
    static public PlayerExchangeParty[] make(ExplorationPlayer player1, ExplorationPlayer player2) {
        final PlayerExchangeStorage storage1 = new PlayerExchangeStorage(player1);
        final PlayerExchangeStorage storage2 = new PlayerExchangeStorage(player2);

        final ExchangePartyProcessor partyProcessor1 = new PlayerExchangePartyProcessor(player1, storage1);
        final ExchangePartyProcessor partyProcessor2 = new PlayerExchangePartyProcessor(player2, storage2);

        final ExchangeProcessor processor = new ExchangeProcessor(partyProcessor1, partyProcessor2);

        final PlayerExchangeParty party1 = new PlayerExchangeParty(player1, processor, storage1);
        final PlayerExchangeParty party2 = new PlayerExchangeParty(player2, processor, storage2);

        storage1.dispatcher().register(new SendLocalPackets(party1));
        storage1.dispatcher().register(new SendDistantPackets(player2));

        storage2.dispatcher().register(new SendLocalPackets(party2));
        storage2.dispatcher().register(new SendDistantPackets(player1));

        return new PlayerExchangeParty[] {party1, party2};
    }
}
