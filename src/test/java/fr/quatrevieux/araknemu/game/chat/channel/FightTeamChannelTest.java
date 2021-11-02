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

package fr.quatrevieux.araknemu.game.chat.channel;

import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.chat.ChatException;
import fr.quatrevieux.araknemu.game.chat.ChatService;
import fr.quatrevieux.araknemu.game.chat.event.BroadcastedMessage;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.network.game.in.chat.Message;
import fr.quatrevieux.araknemu.network.game.out.chat.MessageSent;
import fr.quatrevieux.araknemu.network.game.out.info.Information;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FightTeamChannelTest extends FightBaseCase {
    private FightTeamChannel channel;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        channel = new FightTeamChannel();
    }

    @Test
    void type() {
        assertSame(ChannelType.FIGHT_TEAM, channel.type());
    }

    @Test
    void authorized() throws Exception {
        assertFalse(channel.authorized(gamePlayer()));

        createFight();
        assertTrue(channel.authorized(gamePlayer()));
    }

    @Test
    void sendDuringExploration() {
        assertThrows(ChatException.class, () -> new ChatService(configuration.chat(), new Channel[] {channel}).send(gamePlayer(), new Message(ChannelType.FIGHT_TEAM, null, "hello", "")));
    }

    @Test
    void sendItem() throws Exception {
        createFight();

        channel.send(
            player,
            new Message(ChannelType.FIGHT_TEAM, null, "hello °0", "2443!76#12#0#0#0d0+18,7e#1b#0#0#0d0+27")
        );

        requestStack.assertLast(Information.cannotPostItemOnChannel());
    }

    @Test
    void sendToFight() throws Exception {
        Fight fight = createFight();

        PlayerFighter teammate = makePlayerFighter(makeSimpleGamePlayer(10));
        fight.state(PlacementState.class).joinTeam(teammate, player.fighter().team());
        teammate.player().start(teammate);

        requestStack.clear();

        List<PlayerFighter> fighters = new ArrayList<>();

        player.fighter().dispatcher().add(BroadcastedMessage.class, m -> fighters.add(player.fighter()));
        other.fighter().dispatcher().add(BroadcastedMessage.class, m -> fighters.add(other.fighter()));
        teammate.dispatcher().add(BroadcastedMessage.class, m -> fighters.add(teammate));

        channel.send(player, new Message(ChannelType.FIGHT_TEAM, null, "hello", ""));

        requestStack.assertLast(new MessageSent(player, ChannelType.FIGHT_TEAM, "hello", ""));
        assertCollectionEquals(fighters, player.fighter(), teammate);
    }
}
