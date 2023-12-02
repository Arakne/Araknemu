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

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.arakne.utils.maps.MapCell;
import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Decoder;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.util.SpellsHelper;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import org.checkerframework.checker.nullness.qual.EnsuresNonNullIf;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.dataflow.qual.Pure;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Try to cast attracting spell on enemy to move it near the caster
 *
 * The selected spell is the one with the lowest enemy distance after cast,
 * and the lowest AP cost (if multiple spells have the same distance)
 *
 * If no cast is possible, or without effect, no action is generated
 * Also, this action will be ignored if the caster is already adjacent to an enemy
 *
 * @see fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting.MoveFrontHandler The simulated effect
 * @see MoveToAttractEnemy To generate a movement allowing to cast this effect (should be registered after this action)
 */
public final class AttractEnemy implements ActionGenerator {
    private final int effectId;
    private List<Spell> attractionSpells = Collections.emptyList();

    /**
     * @param effectId The spell effect ID for attraction
     */
    public AttractEnemy(int effectId) {
        this.effectId = effectId;
    }

    @Override
    public void initialize(AI ai) {
        final SpellsHelper helper = ai.helper().spells();

        attractionSpells = helper
            .withEffect(effectId)
            .sorted(Comparator.comparingInt(Castable::apCost))
            .collect(Collectors.toList())
        ;
    }

    @Override
    public <A extends Action> Optional<A> generate(AI ai, AiActionFactory<A> actions) {
        return compute(ai, actions.castSpellValidator()).toAction(actions);
    }

    /**
     * Check if the action can be performed
     *
     * If this method is false, {@link #generate(AI, AiActionFactory)} will always return an empty optional, and
     * {@link #compute(AI, CastConstraintValidator)} will always return {@link Result#EMPTY}.
     *
     * This value is true if :
     * - There is at least one spell with the attraction effect
     * - The current AP is greater than 0
     * - The caster is not adjacent to an enemy
     *
     * @param ai The AI to check
     *
     * @return true if the action can be performed, or false if the generator should be ignored
     */
    public boolean valid(AI ai) {
        return !attractionSpells.isEmpty()
            && ai.turn().points().actionPoints() > 0
            && ai.helper().enemies().adjacent().noneMatch(f -> true)
        ;
    }

    /**
     * Compute the best action for the current AI
     *
     * Note: {@link Result#valid()} should be checked before using the result
     *
     * @param ai The AI to compute for
     * @param validator The validator for check if the spell can be cast
     *
     * @return The best action, or {@link Result#EMPTY} if no action is possible
     */
    public Result compute(AI ai, CastConstraintValidator<Spell> validator) {
        if (!valid(ai)) {
            return Result.EMPTY;
        }

        final FighterData fighter = ai.fighter();
        final Decoder<BattlefieldCell> decoder = new Decoder<>(ai.fighter().cell().map());
        final int actionPoints = ai.turn().points().actionPoints();

        Result best = Result.EMPTY;

        for (Spell spell : attractionSpells) {
            if (spell.apCost() > actionPoints) {
                continue;
            }

            for (FighterData enemy : ai.helper().enemies()) {
                if (!ai.helper().spells().caster(validator).validate(spell, enemy.cell())) {
                    continue;
                }

                final Result result = computeForEnemy(decoder, spell, fighter, enemy);

                if (result.isBetterThan(best)) {
                    best = result;
                }
            }
        }

        return best;
    }

    private Result computeForEnemy(Decoder<BattlefieldCell> decoder, Spell spell, FighterData caster, FighterData enemy) {
        BattlefieldCell destination = enemy.cell();

        final int distance = spell.effects().stream().filter(e -> e.effect() == 6).findFirst().map(SpellEffect::min).orElse(0);
        final Direction direction = destination.coordinate().directionTo(caster.cell());

        // Check if a cell block the movement
        for (int i = 0; i < distance; ++i) {
            final Optional<BattlefieldCell> nextCell = decoder
                .nextCellByDirection(destination, direction)
                .filter(MapCell::walkable)
            ;

            if (!nextCell.isPresent()) {
                break;
            }

            destination = nextCell.get();
        }

        // The spell has no effect
        if (destination.equals(enemy.cell())) {
            return Result.EMPTY;
        }

        return new Result(
            spell,
            enemy.cell(),
            destination.coordinate().distance(caster.cell())
        );
    }

    /**
     * Represent the result of the computation, with the selected spell and target,
     * and the new distance between the caster and the enemy
     */
    public static final class Result {
        public static final Result EMPTY = new Result();

        private final @Nullable Spell spell;
        private final @Nullable BattlefieldCell target;
        private final int distance;

        private Result() {
            this.spell = null;
            this.target = null;
            this.distance = Integer.MAX_VALUE;
        }

        public Result(Spell spell, BattlefieldCell target, int distance) {
            this.spell = spell;
            this.target = target;
            this.distance = distance;
        }

        /**
         * Get the selected spell
         */
        public @Nullable Spell spell() {
            return spell;
        }

        /**
         * Get the selected cast target
         */
        public @Nullable BattlefieldCell target() {
            return target;
        }

        /**
         * The new distance between the caster and the enemy after cast
         */
        public int distance() {
            return distance;
        }

        /**
         * Check if the result is valid (i.e. a spell and a target are selected)
         * If the result is not valid, no action should be generated
         */
        @Pure
        @EnsuresNonNullIf(expression = {"this.spell", "this.target"}, result = true)
        public boolean valid() {
            return spell != null && target != null;
        }

        /**
         * Check if the current result is better than the other
         *
         * A result is better than another if:
         * - It is valid and the other is not
         * - It has a lower distance
         * - It has a lower AP cost (if the distance is the same)
         *
         * @param other The other result to compare with
         *
         * @return true if the current result is better than the other
         */
        @Pure
        public boolean isBetterThan(Result other) {
            if (!valid()) {
                return false;
            }

            if (!other.valid()) {
                return true;
            }

            if (distance > other.distance) {
                return false;
            }

            return distance < other.distance || spell.apCost() < other.spell.apCost();
        }

        /**
         * Convert the result to an action
         * If the result is not valid, an empty optional is returned
         *
         * @param actions The action factory
         *
         * @return The action wrapped in an optional
         */
        private <A extends Action> Optional<A>  toAction(AiActionFactory<A> actions) {
            if (!valid()) {
                return Optional.empty();
            }

            return Optional.of(actions.cast(spell, target));
        }
    }
}
