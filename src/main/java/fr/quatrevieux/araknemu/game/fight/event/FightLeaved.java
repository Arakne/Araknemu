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

package fr.quatrevieux.araknemu.game.fight.event;

import fr.quatrevieux.araknemu.game.fight.ending.reward.FightReward;

import java.util.Optional;

/**
 * The fighter has leave the fight
 */
public final class FightLeaved {
    private final FightReward reward;

    public FightLeaved() {
        this(null);
    }

    public FightLeaved(FightReward reward) {
        this.reward = reward;
    }

    public Optional<FightReward> reward() {
        return Optional.ofNullable(reward);
    }
}
