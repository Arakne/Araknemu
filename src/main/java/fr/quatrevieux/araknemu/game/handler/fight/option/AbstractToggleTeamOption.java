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

package fr.quatrevieux.araknemu.game.handler.fight.option;

import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.core.network.parser.Packet;
import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.ConfigurableTeamOptions;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.out.basic.Noop;
import org.checkerframework.checker.nullness.util.NullnessUtil;

/**
 * Handle the toggle of a team option
 *
 * If an operation is not authorized, a {@link Noop} packet will be sent to silently ignore the error
 * The toggle can only be performed if the current fighter is the team leader and the options are configurable
 *
 * To add custom checks, you should override the method {@link AbstractToggleTeamOption#check(GameSession, PlayerFighter)}
 *
 * @param <P> The input packet
 */
public abstract class AbstractToggleTeamOption<P extends Packet> implements PacketHandler<GameSession, P> {
    @Override
    public final void handle(GameSession session, P packet) throws Exception {
        final PlayerFighter fighter = NullnessUtil.castNonNull(session.fighter());

        if (!fighter.isTeamLeader() || !(fighter.team().options() instanceof ConfigurableTeamOptions) || !check(session, fighter)) {
            // If the option cannot be changed, the request must be silently ignored : so send a noop packet
            throw new ErrorPacket(new Noop());
        }

        toggleOption((ConfigurableTeamOptions) fighter.team().options());
    }

    /**
     * Check if the toggle can be performed in the current state
     * If the returned value is false, a "Noop" packet will be sent and the operation will be aborted
     *
     * Note: you should override this method to add custom check
     *
     * @param session The current session
     * @param fighter The fighter
     *
     * @return true if the operation can be performed
     */
    protected boolean check(GameSession session, PlayerFighter fighter) {
        return true;
    }

    /**
     * Perform the toggle on the team options
     *
     * @param options Team option instance
     */
    protected abstract void toggleOption(ConfigurableTeamOptions options);
}
