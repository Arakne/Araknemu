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

package fr.quatrevieux.araknemu.game.exploration.interaction.action.fight;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.JoinFightError;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.game.fight.spectator.SpectatorFactory;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import fr.quatrevieux.araknemu.network.game.out.info.Error;

/**
 * Try to join a fight as spectator
 */
public final class JoinFightAsSpectator implements Action {
    private final SpectatorFactory spectatorFactory;
    private final ExplorationPlayer player;
    private final Fight fight;

    public JoinFightAsSpectator(SpectatorFactory spectatorFactory, ExplorationPlayer player, Fight fight) {
        this.spectatorFactory = spectatorFactory;
        this.player = player;
        this.fight = fight;
    }

    @Override
    public void start(ActionQueue queue) {
        if (player.interactions().busy()) {
            error(JoinFightError.CANT_YOU_R_BUSY);
            return;
        }

        final ExplorationMap map = player.map();

        if (map == null || map.id() != fight.map().id()) {
            error(JoinFightError.CANT_BECAUSE_MAP);
            return;
        }

        if (!fight.active()) {
            player.send(Error.cantJoinFightAsSpectator());
            return;
        }

        final Spectator spectator = spectatorFactory.create(player.player(), fight);

        fight.execute(() -> {
            if (!fight.spectators().canJoin()) {
                player.send(Error.cantJoinFightAsSpectator());
                return;
            }

            player.player().stop(player);
            spectator.join();
        });
    }

    @Override
    public ExplorationPlayer performer() {
        return player;
    }

    @Override
    public ActionType type() {
        return ActionType.JOIN_FIGHT;
    }

    @Override
    public Object[] arguments() {
        return new Object[0];
    }

    private void error(JoinFightError error) {
        player.send(
            new GameActionResponse("", ActionType.JOIN_FIGHT, player.id(), Character.toString(error.error()))
        );
    }
}
