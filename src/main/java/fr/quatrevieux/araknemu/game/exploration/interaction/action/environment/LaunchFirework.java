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

package fr.quatrevieux.araknemu.game.exploration.interaction.action.environment;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.BlockingAction;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Launch a firework to the map
 */
public final class LaunchFirework implements BlockingAction {
    private final ExplorationPlayer player;

    private final int cell;
    private final int animation;
    private final int size;

    private int id;

    public LaunchFirework(ExplorationPlayer player, int cell, int animation, int size) {
        this.player = player;
        this.cell = cell;
        this.animation = animation;
        this.size = size;
    }

    @Override
    public void start(ActionQueue queue) {
        final ExplorationMap map = player.map();

        // Because actions are delayed, we cannot ensure that the player is still on map
        if (map != null) {
            queue.setPending(this);
            map.send(new GameActionResponse(this));
        }
    }

    @Override
    public void cancel(@Nullable String argument) {
        // No-op method : nothing is done at end of firework
        // Because cancel is called when stopping actions, no exception should be thrown
    }

    @Override
    public void end() {
        // No-op method : nothing is done at end of firework
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public ExplorationPlayer performer() {
        return player;
    }

    @Override
    public ActionType type() {
        return ActionType.FIREWORK;
    }

    @Override
    public Object[] arguments() {
        return new Object[] {cell + "," + animation + ",11,8," + size};
    }
}
