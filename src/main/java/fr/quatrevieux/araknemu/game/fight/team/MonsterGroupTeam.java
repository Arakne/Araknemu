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

package fr.quatrevieux.araknemu.game.fight.team;

import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.game.fight.JoinFightError;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.monster.Monster;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Fight team for {@link MonsterGroup}
 *
 * This team is read only, and contains exactly all group monsters as {@link MonsterFighter}
 * This team has no leader
 */
public final class MonsterGroupTeam implements FightTeam {
    private final MonsterGroup monsterGroup;
    private final int number;
    private final List<FightCell> startPlaces;

    private final List<Fighter> fighters;
    private final TeamOptions options;

    @SuppressWarnings({"assignment", "argument"})
    public MonsterGroupTeam(MonsterGroup monsterGroup, List<FightCell> startPlaces, int number, FighterFactory fighterFactory) {
        this.monsterGroup = monsterGroup;
        this.number = number;
        this.startPlaces = startPlaces;

        this.fighters = makeFighters(this, monsterGroup.monsters(), fighterFactory);
        this.options = new DefaultTeamOptions(this);
    }

    @Override
    public @Nullable Fighter leader() {
        return null;
    }

    @Override
    public int id() {
        return monsterGroup.id();
    }

    @Override
    public int cell() {
        return monsterGroup.cell().id();
    }

    @Override
    public int type() {
        return 1;
    }

    @Override
    public Alignment alignment() {
        return Alignment.NONE;
    }

    @Override
    public int number() {
        return number;
    }

    @Override
    public List<FightCell> startPlaces() {
        return startPlaces;
    }

    @Override
    public Collection<Fighter> fighters() {
        return Collections.unmodifiableCollection(fighters);
    }

    @Override
    public void send(Object packet) {
        // No op : monster do not receive packets
    }

    @Override
    public boolean alive() {
        return fighters.stream().anyMatch(fighter -> !fighter.dead());
    }

    @Override
    public TeamOptions options() {
        return options;
    }

    @Override
    public void join(Fighter fighter) throws JoinFightException {
        throw new JoinFightException(JoinFightError.TEAM_CLOSED);
    }

    @Override
    public void kick(Fighter fighter) {
        throw new UnsupportedOperationException("Read-only team");
    }

    /**
     * Get the monster group related to this team
     */
    public MonsterGroup group() {
        return monsterGroup;
    }

    /**
     * Creates fighters from monsters of the group
     * Ids of monsters are negative integer sequence (starting at -1 for the first monster)
     */
    private static List<Fighter> makeFighters(MonsterGroupTeam team, List<Monster> monsters, FighterFactory fighterFactory) {
        final List<Fighter> fighters = new ArrayList<>(monsters.size());

        for (Monster monster : monsters) {
            fighters.add(fighterFactory.create(monster, team));
        }

        return fighters;
    }
}
