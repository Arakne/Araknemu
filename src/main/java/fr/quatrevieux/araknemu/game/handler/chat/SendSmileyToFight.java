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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.handler.chat;

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.chat.UseSmiley;
import fr.quatrevieux.araknemu.network.game.out.chat.Smiley;
import org.checkerframework.checker.nullness.util.NullnessUtil;

/**
 * Send the player smiley to the fight
 */
public final class SendSmileyToFight implements PacketHandler<GameSession, UseSmiley> {
    @Override
    public void handle(GameSession session, UseSmiley packet) throws Exception {
        final PlayerFighter fighter = NullnessUtil.castNonNull(session.fighter());

        fighter.fight().send(new Smiley(fighter, packet.smiley()));
    }

    @Override
    public Class<UseSmiley> packet() {
        return UseSmiley.class;
    }
}
