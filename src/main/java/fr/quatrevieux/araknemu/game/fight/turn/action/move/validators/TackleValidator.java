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
 * Copyright (c) 2017-2021 Vincent Quatrevieux, Jean-Alexandre Valentin
 */

package fr.quatrevieux.araknemu.game.fight.turn.action.move.validators;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveFailed;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveResult;

/**
 * Apply tackle to the fighter when enemies are present on adjacent cells
 * If a tackle is performed, a {@link MoveFailed} result will be returned
 */
public final class TackleValidator implements FightPathValidator {
    public static final int STATE_ROOTED = 6;

    private final int[] ignoredStates;
    private final RandomUtil random = new RandomUtil();

    public TackleValidator() {
        this(new int[] {STATE_ROOTED});
    }

    /**
     * @param ignoredStates List of fighter states to ignore for apply the tackle
     * @see fr.quatrevieux.araknemu.game.fight.fighter.States
     */
    public TackleValidator(int[] ignoredStates) {
        this.ignoredStates = ignoredStates;
    }

    @Override
    public MoveResult validate(Move move, MoveResult result) {
        final Fighter performer = move.performer();

        // Ignore tackle if he has at least on of those states
        if (performer.states().hasOne(ignoredStates)) {
            return result;
        }

        final FightCell currentCell = result.path().first().cell();
        final Decoder<FightCell> decoder = performer.cell().map().decoder();

        // The escape probability (i.e. between 0 and 1)
        double escapeProbability = 1d;

        // Combine escape probability from all adjacent enemies
        for (Direction direction : Direction.restrictedDirections()) {
            escapeProbability *= decoder.nextCellByDirection(currentCell, direction)
                .map(FightCell::fighter)
                .filter(fighter -> !fighter.team().equals(performer.team()))
                .filter(fighter -> !fighter.states().hasOne(ignoredStates))
                .map(adjacentEnemy -> computeTackle(performer, adjacentEnemy))
                .orElse(1d)
            ;
        }

        if (random.nextDouble() > escapeProbability) {
            final int lostPa = (int) (performer.turn().points().actionPoints() * (1d - escapeProbability));
            return new MoveFailed(performer, lostPa);
        }

        return result;
    }

    /**
     * Compute the tackle for a single enemy
     *
     * See: https://wiki-dofus.eu/w/Cat%C3%A9gorie:MAJ_1.27.0#L.27esquive.2Ftacle
     *
     * @param fighter The fighter which perform the move action
     * @param enemy The adjacent enemy
     *
     * @return The dodge chance between 0 and 1.
     *     0 means that it's impossible to escape, 1 means that it's certain to escape
     */
    private double computeTackle(Fighter fighter, FighterData enemy) {
        final int fighterAgility = fighter.characteristics().get(Characteristic.AGILITY);
        final int enemyAgility = enemy.characteristics().get(Characteristic.AGILITY);

        final double a = fighterAgility + 25;
        final double b = Math.max(enemyAgility + fighterAgility + 50, 1);

        final double chance = (3 * a / b) - 1;

        if (chance < 0) {
            return 0;
        }

        if (chance > 1) {
            return 1;
        }

        return chance;
    }
}
