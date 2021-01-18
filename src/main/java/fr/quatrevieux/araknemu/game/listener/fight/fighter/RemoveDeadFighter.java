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

package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterDie;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;

/**
 * Remove the dead fighter from fight
 */
final public class RemoveDeadFighter implements Listener<FighterDie> {
    final private Fight fight;

    public RemoveDeadFighter(Fight fight) {
        this.fight = fight;
    }

    @Override
    public void on(FighterDie event) {
        event.fighter().cell().removeFighter();

        // Stop turn if it's the playing fighter
        fight.turnList().current()
            .filter(turn -> turn.fighter().equals(event.fighter()))
            .ifPresent(FightTurn::stop)
        ;
        
        // Kills and remove from team all invocations of event.fighter()
        fight.fighters().forEach(fighter -> {
            fighter.invoker().ifPresent(invoker -> {
                if(invoker.equals(event.fighter())) {
                    fighter.life().kill(invoker);
                    removeFromTeamAndTurnList(fighter);
                }
            });
        });
        
        // Remove from team and turnlist if even.fighter() is an invocation
        if (event.fighter().invoker().isPresent()) {
            removeFromTeamAndTurnList((Fighter)event.fighter());
        }
    }

    @Override
    public Class<FighterDie> event() {
        return FighterDie.class;
    }

    private void removeFromTeamAndTurnList(Fighter fighter) {
        fight.team(fighter.team().number()).kick(fighter);
        fight.turnList().remove(fighter);
    }
}
