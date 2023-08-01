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

package fr.quatrevieux.araknemu.game.fight.ai.action;

import fr.arakne.utils.maps.CoordinateCell;
import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.util.SpellCaster;
import fr.quatrevieux.araknemu.game.fight.ai.util.SpellsHelper;
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldCell;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.spell.Spell;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Try to teleport near enemy
 */
public final class TeleportNearEnemy implements ActionGenerator {
    private List<Spell> teleportSpells = Collections.emptyList();

    @Override
    public void initialize(AI ai) {
        final SpellsHelper helper = ai.helper().spells();

        teleportSpells = helper
            .withEffect(4)
            .sorted(Comparator.comparingInt(Castable::apCost))
            .collect(Collectors.toList())
        ;
    }

    @Override
    public <A extends Action> Optional<A> generate(AI ai, AiActionFactory<A> actions) {
        if (teleportSpells.isEmpty()) {
            return Optional.empty();
        }

        final int actionPoints = ai.turn().points().actionPoints();

        if (actionPoints < 1) {
            return Optional.empty();
        }

        final Optional<? extends FighterData> enemy = ai.enemy();

        if (!enemy.isPresent()) {
            return Optional.empty();
        }

        final SpellCaster caster = ai.helper().spells().caster(actions.castSpellValidator());
        final Selector selector = new Selector(enemy.get().cell(), ai.fighter().cell());

        // Already at adjacent cell of the enemy
        if (selector.adjacent()) {
            return Optional.empty();
        }

        // Spells are ordered by AP cost : the first spell which can reach an accessible adjacent is necessarily the best spell
        for (Spell spell : teleportSpells) {
            if (spell.apCost() > actionPoints) {
                break; // Following spells have an higher cost
            }

            if (selectBestTeleportTargetForSpell(caster, selector, ai.map(), spell)) {
                return selector.action(actions);
            }
        }

        return selector.action(actions);
    }

    /**
     * Select the best possible target for the given spell
     * The result will be push()'ed into selector
     *
     * @return true if the spell can reach an adjacent cell
     */
    private boolean selectBestTeleportTargetForSpell(SpellCaster caster, Selector selector, BattlefieldMap map, Spell spell) {
        for (BattlefieldCell cell : map) {
            // Target or launch is not valid
            if (!cell.walkable() || !caster.validate(spell, cell)) {
                continue;
            }

            // Adjacent cell found : no need to continue
            if (selector.push(spell, cell)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Select the best spell and cell couple for teleport
     */
    private class Selector {
        private final CoordinateCell<BattlefieldCell> enemyCell;
        private int distance;
        private @MonotonicNonNull BattlefieldCell cell;
        private @MonotonicNonNull Spell spell;

        public Selector(BattlefieldCell enemyCell, BattlefieldCell currentCell) {
            this.enemyCell = enemyCell.coordinate();
            this.distance = this.enemyCell.distance(currentCell);
        }

        /**
         * Check if the current cell is adjacent to the enemy cell
         */
        public boolean adjacent() {
            return distance == 1;
        }

        /**
         * Push the teleport parameters and check if there are better than the previous
         *
         * @return true if the new cell is adjacent to the target
         */
        public boolean push(Spell spell, BattlefieldCell cell) {
            final int currentDistance = enemyCell.distance(cell);

            if (currentDistance < distance) {
                this.spell = spell;
                this.cell = cell;
                this.distance = currentDistance;
            }

            return adjacent();
        }

        /**
         * Get the best cast action
         * May returns an empty optional if no teleport spell can be found, or if the fighter is already on the best cell
         */
        public <A extends Action> Optional<A> action(AiActionFactory<A> actions) {
            if (spell == null || cell == null) {
                return Optional.empty();
            }

            return Optional.of(actions.cast(spell, cell));
        }
    }
}
