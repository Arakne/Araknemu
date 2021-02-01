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
import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.fighter.PassiveFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldMap;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.action.Action;
import fr.quatrevieux.araknemu.game.spell.Spell;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Try to teleport near enemy
 */
public final class TeleportNearEnemy implements ActionGenerator {
    private SpellCaster caster;
    private List<Spell> teleportSpells;

    /**
     * Select the best spell and cell couple for teleport
     */
    private class Selector {
        private final CoordinateCell<FightCell> enemyCell;
        private int distance;
        private FightCell cell;
        private Spell spell;

        public Selector(FightCell enemyCell, FightCell currentCell) {
            this.enemyCell = new CoordinateCell<>(enemyCell);
            this.distance = this.enemyCell.distance(new CoordinateCell<>(currentCell));
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
        public boolean push(Spell spell, FightCell cell) {
            final int currentDistance = new CoordinateCell<>(cell).distance(enemyCell);

            if (currentDistance < distance) {
                this.spell = spell;
                this.cell = cell;
                this.distance = currentDistance;
            }

            return adjacent();
        }

        /**
         * Get the best cast action
         * May return an empty optional if no teleport spell can be found, or if the fighter is already on the best cell
         */
        public Optional<Action> action() {
            if (spell == null) {
                return Optional.empty();
            }

            return Optional.of(caster.create(spell, cell));
        }
    }

    @Override
    public void initialize(AI ai) {
        caster = new SpellCaster(ai);
        teleportSpells = new ArrayList<>();

        for (Spell spell : ai.fighter().spells()) {
            if (spell.effects().stream().anyMatch(spellEffect -> spellEffect.effect() == 4)) {
                teleportSpells.add(spell);
            }
        }

        teleportSpells.sort(Comparator.comparingInt(Castable::apCost));
    }

    @Override
    public Optional<Action> generate(AI ai) {
        if (teleportSpells.isEmpty()) {
            return Optional.empty();
        }

        final int actionPoints = ai.turn().points().actionPoints();

        if (actionPoints < 1) {
            return Optional.empty();
        }

        final Optional<? extends PassiveFighter> enemy = ai.enemy();

        if (!enemy.isPresent()) {
            return Optional.empty();
        }

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

            if (selectBestTeleportTargetForSpell(selector, ai.map(), spell)) {
                return selector.action();
            }
        }

        return selector.action();
    }

    /**
     * Select the best possible target for the given spell
     * The result will be push()'ed into selector
     *
     * @return true if the spell can reach an adjacent cell
     */
    private boolean selectBestTeleportTargetForSpell(Selector selector, BattlefieldMap map, Spell spell) {
        for (FightCell cell : map) {
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
}
