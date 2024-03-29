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

package fr.quatrevieux.araknemu.game.fight.builder;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

/**
 * Builder for pvm fight
 *
 * @see PvmBuilder
 * @see PvmType
 */
public final class PvmBuilder implements FightBuilder {
    private final FightService service;
    private final FighterFactory fighterFactory;
    private final RandomUtil random;
    private final PvmType type;

    private @MonotonicNonNull ExplorationMap map;
    private @MonotonicNonNull GamePlayer initiator;
    private @MonotonicNonNull MonsterGroup group;
    private boolean randomize = true;

    public PvmBuilder(FightService service, FighterFactory fighterFactory, RandomUtil random, PvmType type) {
        this.service = service;
        this.fighterFactory = fighterFactory;
        this.random = random;
        this.type = type;
    }

    @Override
    public Fight build(int fightId) {
        final ExplorationMap map = this.map;
        final GamePlayer initiator = this.initiator;
        final MonsterGroup group = this.group;

        if (map == null || initiator == null || group == null) {
            throw new IllegalStateException("Missing mandatory data for build the fight");
        }

        final BaseBuilder builder = new BaseBuilder(service, randomize ? random : null, type);

        builder.map(map);
        builder.addTeam((fight, number, startPlaces) -> new SimpleTeam(
            fight,
            fighterFactory.create(initiator),
            startPlaces,
            number
        ));
        builder.addTeam((fight, number, startPlaces) -> new MonsterGroupTeam(/*fight, */group, startPlaces, number, fighterFactory));

        return builder.build(fightId);
    }

    /**
     * Set the fight map
     */
    public PvmBuilder map(ExplorationMap map) {
        this.map = map;

        return this;
    }

    /**
     * Set the initiator fighter
     */
    public PvmBuilder initiator(GamePlayer initiator) {
        this.initiator = initiator;

        return this;
    }

    /**
     * Set the attacked group
     */
    public PvmBuilder monsterGroup(MonsterGroup monsterGroup) {
        this.group = monsterGroup;

        return this;
    }

    /**
     * Does the teams should be randomized or not
     * If not, the initiator will always join the team 0, and the monster group, the team 1
     */
    public PvmBuilder randomize(boolean randomize) {
        this.randomize = randomize;

        return this;
    }
}
