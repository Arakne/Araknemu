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

package fr.quatrevieux.araknemu.game.world.map.path;

import fr.quatrevieux.araknemu.game.world.map.Direction;
import fr.quatrevieux.araknemu.game.world.map.GameMap;
import fr.quatrevieux.araknemu.game.world.map.MapCell;
import fr.quatrevieux.araknemu.util.Base64;

import java.util.Optional;

/**
 * Decode map data like paths or directions
 */
final public class Decoder<C extends MapCell> {
    final private GameMap<C> map;

    public Decoder(GameMap<C> map) {
        this.map = map;
    }

    /**
     * Get the immediately next cell if we move by the given direction
     * If the next cell is out of the map, an empty optional is returned
     */
    public Optional<C> nextCellByDirection(C start, Direction direction) {
        int nextId = start.id() + direction.nextCellIncrement(map.dimensions().width());

        if (nextId >= map.size() || nextId < 0) {
            return Optional.empty();
        }

        return Optional.of(map.get(nextId));
    }

    /**
     * Decode compressed path
     *
     * @param encoded The encoded path
     *
     * @return List of cells ids
     *
     * @throws PathException When an invalid path is given
     */
    public Path<C> decode(String encoded, C start) throws PathException {
        if (encoded.length() % 3 != 0) {
            throw new PathException("Invalid path : bad length");
        }

        Path<C> path = new Path<>(this);

        path.add(new PathStep<>(start, Direction.EAST));

        for (int i = 0; i < encoded.length(); i += 3) {
            Direction direction = Direction.byChar(encoded.charAt(i));
            int cell = (Base64.ord(encoded.charAt(i + 1)) & 15) << 6 | Base64.ord(encoded.charAt(i + 2));

            if (cell < 0 || cell >= map.size()) {
                throw new PathException("Invalid cell number");
            }

            expandRectilinearMove(
                path,
                path.target(),
                map.get(cell),
                direction
            );
        }

        return path;
    }

    /**
     * Encode the computed path
     */
    public String encode(Path<C> path) {
        StringBuilder encoded = new StringBuilder(path.size() * 3);

        // #69 : The start cell must be added to path without compression
        encoded
            .append(Direction.EAST.toChar())
            .append(Base64.encode(path.get(0).cell().id(), 2))
        ;

        // Start the path at step 1 : The step 0 is the start cell
        for (int i = 1; i < path.size(); ++i) {
            PathStep step = path.get(i);

            encoded.append(step.direction().toChar());

            while (
                i + 1 < path.size()
                && path.get(i + 1).direction() == step.direction()
            ) {
                ++i;
            }

            encoded.append(Base64.encode(path.get(i).cell().id(), 2));
        }

        return encoded.toString();
    }

    /**
     * Get the pathfinder related to this decoder
     */
    public Pathfinder<C> pathfinder() {
        return new Pathfinder<>(this);
    }

    private void expandRectilinearMove(Path<C> path, C start, C target, Direction direction) throws PathException {
        int stepsLimit =  2 * map.dimensions().width() + 1;

        while (!start.equals(target)) {
            start = nextCellByDirection(start, direction).orElseThrow(() -> new PathException("Invalid cell number"));
            path.add(new PathStep<>(start, direction));

            if (--stepsLimit < 0) {
                throw new PathException("Invalid path : bad direction");
            }
        }
    }
}
