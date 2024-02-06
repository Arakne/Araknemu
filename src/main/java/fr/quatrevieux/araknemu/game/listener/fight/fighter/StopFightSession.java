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

import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.game.fight.event.FightFinished;
import fr.quatrevieux.araknemu.game.fight.event.FightLeaved;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

/**
 * Stop the fight session on leave or fight end, and save the player
 */
public final class StopFightSession implements EventsSubscriber {
    private final PlayerFighter fighter;
    private final Listener[] listeners;

    public StopFightSession(PlayerFighter fighter) {
        this.fighter = fighter;
        this.listeners = new Listener[] { new OnLeave(), new OnFinish() };
    }

    private void apply() {
        fighter.player().stop(fighter);
        fighter.player().save();
    }

    @Override
    public Listener[] listeners() {
        return listeners;
    }

    public final class OnLeave implements Listener<FightLeaved> {
        @Override
        public void on(FightLeaved event) {
            apply();
        }

        @Override
        public Class<FightLeaved> event() {
            return FightLeaved.class;
        }
    }

    public final class OnFinish implements Listener<FightFinished> {
        @Override
        public void on(FightFinished event) {
            apply();
        }

        @Override
        public Class<FightFinished> event() {
            return FightFinished.class;
        }
    }
}
