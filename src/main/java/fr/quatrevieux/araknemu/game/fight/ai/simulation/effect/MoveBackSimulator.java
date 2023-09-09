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

package fr.quatrevieux.araknemu.game.fight.ai.simulation.effect;

import fr.arakne.utils.maps.MapCell;
import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Decoder;
import fr.arakne.utils.value.Interval;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.CastSimulation;
import fr.quatrevieux.araknemu.game.fight.castable.CastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.util.NullnessUtil;

import java.util.Optional;

/**
 * Simulator for damage caused by move back effect
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.MoveBackHandler
 */
public final class MoveBackSimulator implements EffectSimulator {
    @Override
    public void simulate(CastSimulation simulation, AI ai, CastScope.EffectScope<? extends FighterData, ? extends BattlefieldCell> effect) {
        final BattlefieldCell from = ai.fighter().cell();
        final FighterData caster = ai.fighter();
        final Decoder<BattlefieldCell> decoder = new Decoder<>(ai.map());

        for (FighterData target : effect.targets()) {
            int distance = effect.effect().min();
            final Direction direction = from.coordinate().directionTo(target.cell());
            BattlefieldCell destination = target.cell();

            // Check if a cell block the movement
            for (; distance > 0; --distance) {
                final Optional<BattlefieldCell> nextCell = decoder
                    .nextCellByDirection(destination, direction)
                    .filter(MapCell::walkable)
                ;

                if (!nextCell.isPresent()) {
                    break;
                }

                destination = nextCell.get();
            }

            if (distance > 0) {
                applyBlockingDamageChain(
                    simulation,
                    target,
                    computeDamage(caster, distance),
                    destination,
                    direction
                );
            }
        }
    }

    /**
     * Apply blocked move back damage on each fighter on the path
     * The damage are divided by two to each following fighter
     *
     * @param target The targeted fighter
     * @param damage The damage to apply
     * @param lastCell The destination cell (where the target is blocked)
     * @param direction The move direction
     */
    private void applyBlockingDamageChain(CastSimulation simulation, FighterData target, Interval damage, BattlefieldCell lastCell, Direction direction) {
        final Decoder<BattlefieldCell> decoder = new Decoder<>(lastCell.map());

        simulation.addDamage(damage, target);

        // Divide damage by 2 on each fighter
        for (;;) {
            damage = new Interval(damage.min() / 2, damage.max() / 2);

            if (damage.max() <= 0) {
                return;
            }

            final Optional<BattlefieldCell> nextCell = decoder
                .nextCellByDirection(lastCell, direction)
                .filter(BattlefieldCell::hasFighter)
            ;

            // Out of map, or no player is present here : stop the chain
            if (!nextCell.isPresent()) {
                return;
            }

            lastCell = nextCell.get();
            simulation.addDamage(damage, NullnessUtil.castNonNull(lastCell.fighter()));
        }
    }

    /**
     * Compute the damage in life point for a blocked move back
     *
     * https://www.dofus.com/fr/forum/1750-dofus/234052-version-beta-1-27?page=3#entry1657700
     *
     * @param caster The spell caster
     * @param distance The remaining distance
     *
     * @return The damage
     */
    private Interval computeDamage(FighterData caster, @NonNegative int distance) {
        return new Interval(
            9 * caster.level() * distance / 50,
            16 * caster.level() * distance / 50
        );
    }
}
