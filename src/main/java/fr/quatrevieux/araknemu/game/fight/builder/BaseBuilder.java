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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.builder;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.state.ActiveState;
import fr.quatrevieux.araknemu.game.fight.state.FinishState;
import fr.quatrevieux.araknemu.game.fight.state.InitialiseState;
import fr.quatrevieux.araknemu.game.fight.state.NullState;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.state.StatesFlow;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.type.FightType;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Base builder for fight
 */
final public class BaseBuilder implements FightBuilder {
    public interface TeamFactory {
        /**
         * Creates the fight team
         *
         * @param number The team number
         * @param startPlaces The available start places
         */
        public FightTeam create(int number, List<Integer> startPlaces);
    }

    final private FightService service;
    final private RandomUtil random;
    final private FightType type;
    final private Logger logger;

    private FightMap map;
    final private List<TeamFactory> teamFactories = new ArrayList<>();

    public BaseBuilder(FightService service, RandomUtil random, FightType type, Logger logger) {
        this.service = service;
        this.random = random;
        this.type = type;
        this.logger = logger;
    }

    @Override
    public Fight build(int fightId) {
        return new Fight(fightId, type, map, buildTeams(), statesFlow(), logger);
    }

    /**
     * Set the fight map
     */
    public void map(ExplorationMap map) {
        this.map = service.map(map);
    }

    public void addTeam(TeamFactory factory) {
        teamFactories.add(factory);
    }

    private List<FightTeam> buildTeams() {
        List<TeamFactory> factories = random != null ? random.shuffle(teamFactories) : teamFactories;
        List<FightTeam> teams = new ArrayList<>(factories.size());

        for (int number = 0; number < factories.size(); ++number) {
            teams.add(factories.get(number).create(number, map.startPlaces(number)));
        }

        return teams;
    }

    private StatesFlow statesFlow() {
        return new StatesFlow(
            new NullState(),
            new InitialiseState(),
            new PlacementState(),
            new ActiveState(),
            new FinishState()
        );
    }
}
