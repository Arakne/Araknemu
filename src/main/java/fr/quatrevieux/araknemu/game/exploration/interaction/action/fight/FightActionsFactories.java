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
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.spectator.SpectatorFactory;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;

/**
 * Register fight actions
 */
public final class FightActionsFactories implements ExplorationActionRegistry.SelfRegisterable {
    private final FightService fightService;
    private final FighterFactory fighterFactory;
    private final SpectatorFactory spectatorFactory;

    public FightActionsFactories(FightService fightService, FighterFactory fighterFactory, SpectatorFactory spectatorFactory) {
        this.fightService = fightService;
        this.fighterFactory = fighterFactory;
        this.spectatorFactory = spectatorFactory;
    }

    @Override
    public void register(ExplorationActionRegistry factory) {
        factory.register(ActionType.JOIN_FIGHT, this::join);
    }

    private Action join(ExplorationPlayer player, ActionType action, String[] arguments) {
        final Fight fight = fightService.getFromMap(player.map().id(), Integer.parseInt(arguments[0]));

        if (arguments.length == 1) {
            return joinAsSpectator(player, fight);
        }

        return joinFightTeam(player, fight, Integer.parseInt(arguments[1]));
    }

    private JoinFight joinFightTeam(ExplorationPlayer player, Fight fight, int teamId) {
        return new JoinFight(player, fight, findTeamById(fight, teamId), fighterFactory);
    }

    private JoinFightAsSpectator joinAsSpectator(ExplorationPlayer player, Fight fight) {
        return new JoinFightAsSpectator(spectatorFactory, player, fight);
    }

    private FightTeam findTeamById(Fight fight, int id) {
        return fight.teams().stream().filter(team -> team.id() == id).findFirst().get();
    }
}
