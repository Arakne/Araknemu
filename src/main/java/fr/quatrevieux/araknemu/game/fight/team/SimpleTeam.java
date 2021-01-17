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
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Simple fight team for player fighters
 */
final public class SimpleTeam implements FightTeam {
    final private PlayerFighter leader;
    final private List<Fighter> fighters;
    final private List<Integer> startPlaces;
    final private int number;

    public SimpleTeam(PlayerFighter leader, List<Integer> startPlaces, int number) {
        this.leader = leader;
        this.fighters = new ArrayList<>();
        this.fighters.add(leader);
        this.startPlaces = startPlaces;
        this.number = number;

        leader.setTeam(this);
    }

    @Override
    public Fighter leader() {
        return leader;
    }

    @Override
    public int id() {
        return leader.id();
    }

    @Override
    public int cell() {
        return leader.player().position().cell();
    }

    @Override
    public int type() {
        return 0;
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
    public List<Integer> startPlaces() {
        return startPlaces;
    }

    @Override
    public Collection<Fighter> fighters() {
        return Collections.unmodifiableCollection(fighters);
    }

    @Override
    public void send(Object packet) {
        fighters.forEach(fighter -> {
            if(fighter instanceof PlayerFighter) {
                ((PlayerFighter)fighter).send(packet);
            }
        });
    }

    @Override
    public boolean alive() {
        return fighters.stream().anyMatch(fighter -> !fighter.dead());
    }

    @Override
    public void join(Fighter fighter) throws JoinFightException {
        fighter.apply(new FighterOperation() {
            @Override
            public void onPlayer(PlayerFighter fighter) {
                if (fighters.size() >= startPlaces.size()) {
                    throw new JoinFightException(JoinFightError.TEAM_FULL);
                }

                fighter.setTeam(SimpleTeam.this);
                fighters.add(fighter);
            }

            @Override
            public void onGenericFighter(Fighter fighter) {
                throw new JoinFightException(JoinFightError.TEAM_CLOSED);
            }

            @Override
            public void onMonster(MonsterFighter fighter) {
                fighters.add(fighter);
            }
        });
    }

    @Override
    public void kick(Fighter fighter) {
        fighters.remove(fighter);
    }
}
