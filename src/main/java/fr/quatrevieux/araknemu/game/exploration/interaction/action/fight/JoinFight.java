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

package fr.quatrevieux.araknemu.game.exploration.interaction.action.fight;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.JoinFightError;
import fr.quatrevieux.araknemu.game.fight.exception.InvalidFightStateException;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;

/**
 * Try to join a fight
 */
public final class JoinFight implements Action {
    private final ExplorationPlayer player;
    private final Fight fight;
    private final FightTeam team;
    private final FighterFactory fighterFactory;

    public JoinFight(ExplorationPlayer player, Fight fight, FightTeam team, FighterFactory fighterFactory) {
        this.player = player;
        this.fight = fight;
        this.team = team;
        this.fighterFactory = fighterFactory;
    }

    @Override
    public void start(ActionQueue queue) {
        if (player.interactions().busy()) {
            error(JoinFightError.CANT_YOU_R_BUSY);
            return;
        }

        if (!(fight.state() instanceof PlacementState)) {
            error(JoinFightError.CANT_DO_TOO_LATE);
            return;
        }

        final ExplorationMap map = player.map();

        if (map == null || map.id() != fight.map().id()) {
            error(JoinFightError.CANT_BECAUSE_MAP);
            return;
        }

        fight.execute(() -> {
            try {
                fight.state(PlacementState.class).joinTeam(fighterFactory.create(player.player()), team);
            } catch (JoinFightException e) {
                error(e.error());
            } catch (InvalidFightStateException e) {
                error(JoinFightError.CANT_DO_TOO_LATE);
            }
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
