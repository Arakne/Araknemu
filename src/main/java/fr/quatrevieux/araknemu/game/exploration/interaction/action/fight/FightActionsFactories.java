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
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;

/**
 * Register fight actions
 */
final public class FightActionsFactories implements ExplorationActionRegistry.SelfRegisterable {
    final private FightService fightService;
    final private FighterFactory fighterFactory;

    public FightActionsFactories(FightService fightService, FighterFactory fighterFactory) {
        this.fightService = fightService;
        this.fighterFactory = fighterFactory;
    }

    @Override
    public void register(ExplorationActionRegistry factory) {
        factory.register(ActionType.JOIN_FIGHT, this::join);
    }

    private JoinFight join(ExplorationPlayer player, ActionType action, String[] arguments) {
        final Fight fight = fightService.getFromMap(player.map().id(), Integer.parseInt(arguments[0]));

        return new JoinFight(
            player,
            fight,
            findTeamById(fight, Integer.parseInt(arguments[1])),
            fighterFactory
        );
    }

    private FightTeam findTeamById(Fight fight, int id) {
        return fight.teams().stream().filter(team -> team.id() == id).findFirst().get();
    }
}
