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

package fr.quatrevieux.araknemu.game.monster.environment;

import fr.arakne.utils.maps.CoordinateCell;
import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.maps.path.PathException;
import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.activity.ActivityService;
import fr.quatrevieux.araknemu.game.activity.Task;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.cell.ExplorationMapCell;
import fr.quatrevieux.araknemu.game.monster.group.MonsterGroup;
import org.apache.logging.log4j.Logger;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Task for random move monsters on exploration maps
 *
 * At most one group move per map per execution.
 * The move is performed if its randomly selected with moveChange and if the path is not too complex
 */
public final class MoveMonsters implements Task {
    private final MonsterEnvironmentService service;
    private final Duration delay;
    private final int moveChance;
    private final int maxDistance;

    private final RandomUtil random = new RandomUtil();

    /**
     * Initialise the move task
     *
     * @param service The environment server
     * @param delay  The period delay
     * @param moveChance Move chance for each groups, in percent
     * @param maxDistance The maximum move distance in cell count
     */
    public MoveMonsters(MonsterEnvironmentService service, Duration delay, int moveChance, int maxDistance) {
        this.service = service;
        this.delay = delay;
        this.moveChance = moveChance;
        this.maxDistance = maxDistance;
    }

    @Override
    public void execute(Logger logger) {
        service.groups()
            .filter(position -> !position.available().isEmpty())
            .filter(position -> !position.fixed())
            .filter(position -> random.bool(moveChance))
            .forEach(this::move)
        ;
    }

    @Override
    public Duration delay() {
        return delay;
    }

    @Override
    public boolean retry(ActivityService service) {
        return false;
    }

    @Override
    public String toString() {
        return "Move monsters";
    }

    /**
     * Try to move a group from the given position
     */
    private void move(LivingMonsterGroupPosition position) {
        final MonsterGroup group = random.of(position.available());

        targetCell(group.cell())
            .map(target -> {
                try {
                    return new Decoder<>(target.map())
                        .pathfinder()
                        .exploredCellLimit(50)
                        .findPath(group.cell(), target)
                    ;
                } catch (PathException e) {
                    // Ignore exception
                    return null;
                }
            })
            .ifPresent(group::move)
        ;
    }

    /**
     * Get a random target cell
     *
     * @param currentCell The current group cell
     *
     * @return The target cell, if available
     */
    private Optional<ExplorationMapCell> targetCell(ExplorationMapCell currentCell) {
        final CoordinateCell<ExplorationMapCell> currentCoordinates = currentCell.coordinate();
        final List<ExplorationMapCell> cells = new ArrayList<>();
        final ExplorationMap map = currentCell.map();

        for (int id = 0; id < map.size(); ++id) {
            final ExplorationMapCell cell = map.get(id);

            if (cell.free() && currentCoordinates.distance(cell) <= maxDistance) {
                cells.add(cell);
            }
        }

        return cells.isEmpty() ? Optional.empty() : Optional.of(random.of(cells));
    }
}
