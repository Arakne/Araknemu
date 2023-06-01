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
 * Copyright (c) 2017-2023 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.exploration.interaction.map;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.move.ChangeMap;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Perform a teleportation
 *
 * This interaction handle both teleportation on same map and on another map
 * In case of teleportation on another map, it will push a {@link ChangeMap} interaction
 * Otherwise, it will change the player cell by calling {@link ExplorationPlayer#changeCell(int)}
 */
public final class Teleportation implements Interaction {
    private final ExplorationPlayer player;
    private final TeleportationTarget target;
    private final int cinematic;

    public Teleportation(ExplorationPlayer player, TeleportationTarget target) {
        this(player, target, 0);
    }

    public Teleportation(ExplorationPlayer player, TeleportationTarget target, int cinematic) {
        this.player = player;
        this.target = target;
        this.cinematic = cinematic;
    }

    @Override
    @SuppressWarnings("argument") // checkerframework does not take in account that target.map() and player.map() are equal
    public @Nullable Interaction start() {
        if (target.map().equals(player.map())) {
            // teleport on same map
            player.changeCell(target.cell());
        } else {
            // teleport on another map
            player.interactions().push(new ChangeMap(player, target.map(), target.cell(), cinematic));
        }

        return null;
    }

    @Override
    public void stop() {
        // nothing to do: this interaction is not blocking
    }
}
