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
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Party for player exchange
 *
 * The party is linked to the distant party (other)
 */
public final class PlayerExchangeParty extends AbstractPlayerExchangeParty {
    private PlayerExchangeParty(ExplorationPlayer player, ExchangeProcessor processor, PlayerExchangeStorage storage) {
        super(player, processor, storage);
    }

    @Override
    public ExchangeType type() {
        return ExchangeType.PLAYER_EXCHANGE;
    }

    @Override
    public @Nullable Creature target() {
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
    public static PlayerExchangeParty[] make(ExplorationPlayer initiator, ExplorationPlayer target) {
        final PlayerExchangeStorage initiatorStorage = new PlayerExchangeStorage(initiator);
        final PlayerExchangeStorage targetStorage = new PlayerExchangeStorage(target);

        final ExchangePartyProcessor initiatorPartyProcessor = new PlayerExchangePartyProcessor(initiator, initiatorStorage);
        final ExchangePartyProcessor targetPartyProcessor = new PlayerExchangePartyProcessor(target, targetStorage);

        final ExchangeProcessor processor = new ExchangeProcessor(initiatorPartyProcessor, targetPartyProcessor);

        final PlayerExchangeParty initiatorParty = new PlayerExchangeParty(initiator, processor, initiatorStorage);
        final PlayerExchangeParty targetParty = new PlayerExchangeParty(target, processor, targetStorage);

        initiatorStorage.dispatcher().register(new SendLocalPackets(initiatorParty));
        initiatorStorage.dispatcher().register(new SendDistantPackets(target));

        targetStorage.dispatcher().register(new SendLocalPackets(targetParty));
        targetStorage.dispatcher().register(new SendDistantPackets(initiator));

        return new PlayerExchangeParty[] {initiatorParty, targetParty};
    }
}
