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
import fr.quatrevieux.araknemu.game.fight.event.FightFinished;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

/**
 * Apply reward when fight is terminated
 */
public final class ApplyEndFightReward implements Listener<FightFinished> {
    private final PlayerFighter fighter;

    public ApplyEndFightReward(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void on(FightFinished event) {
        fighter.player().stop(fighter);
        event.reward().apply();
        fighter.player().save();
    }

    @Override
    public Class<FightFinished> event() {
        return FightFinished.class;
    }
}
