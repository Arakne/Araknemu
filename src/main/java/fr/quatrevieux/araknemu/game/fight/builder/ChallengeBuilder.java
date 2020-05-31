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

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.util.RandomUtil;
import org.apache.logging.log4j.Logger;

/**
 * Builder for challenge fight
 */
final public class ChallengeBuilder implements FightBuilder {
    final private BaseBuilder builder;
    final private FighterFactory fighterFactory;

    public ChallengeBuilder(FightService service, FighterFactory fighterFactory, RandomUtil random, Logger logger) {
        this.builder = new BaseBuilder(service, random, new ChallengeType(), logger);
        this.fighterFactory = fighterFactory;
    }

    @Override
    public Fight build(int fightId) {
        return builder.build(fightId);
    }

    /**
     * Set the fight map
     */
    public ChallengeBuilder map(ExplorationMap map) {
        builder.map(map);

        return this;
    }

    /**
     * Add new fighter
     */
    public ChallengeBuilder fighter(GamePlayer player) {
        builder.addTeam((number, startPlaces) -> new SimpleTeam(
            fighterFactory.create(player),
            startPlaces,
            number
        ));

        return this;
    }
}
