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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.action.builder;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.action.Attack;
import fr.quatrevieux.araknemu.game.fight.ai.action.AttractEnemy;
import fr.quatrevieux.araknemu.game.fight.ai.action.BlockNearestEnemy;
import fr.quatrevieux.araknemu.game.fight.ai.action.Boost;
import fr.quatrevieux.araknemu.game.fight.ai.action.Debuff;
import fr.quatrevieux.araknemu.game.fight.ai.action.Heal;
import fr.quatrevieux.araknemu.game.fight.ai.action.Invoke;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveFarEnemies;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveNearAllies;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveNearEnemy;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveToAttack;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveToAttractEnemy;
import fr.quatrevieux.araknemu.game.fight.ai.action.MoveToBoost;
import fr.quatrevieux.araknemu.game.fight.ai.action.TeleportNearEnemy;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.GeneratorAggregate;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.NullGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.simulation.Simulator;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Build an action generator pipeline using a simple builder
 *
 * Note: This class is not marked as final to allow extends to add custom actions methods
 *
 * Usage:
 * <pre>{@code
 * Simulator simulator = xxx;
 * GeneratorBuilder builder = new GeneratorBuilder();
 *
 * builder
 *     .boostSelf(simulator)
 *     .attackFromBestCell(simulator)
 *     .when(ai -> isLowLife(ai), cb -> cb
 *         .success(GeneratorBuilder::moveFarEnemies)
 *         .otherwise(GeneratorBuilder::moveNearEnemy)
 *     )
 *     .boostAllies(simulator)
 * ;
 * }</pre>
 */
public class GeneratorBuilder {
    private final List<ActionGenerator> generators = new ArrayList<>();

    /**
     * Append a new action generator at the end of the pipeline
     *
     * Note: prefer use of helpers methods instead
     *
     * Usage:
     * <pre>{@code
     * builder
     *     .add(new MyProprietaryAction())
     *     .add(new NextAction())
     * ;
     * }</pre>
     *
     * @param generator The generator instance
     *
     * @return The builder instance
     */
    public final GeneratorBuilder add(ActionGenerator generator) {
        generators.add(generator);

        return this;
    }

    /**
     * Add a conditional action
     *
     * Usage:
     * <pre>{code
     * builder.when(ai -> checkCanPerform(ai), cb -> cb
     *     .success(GeneratorBuilder::attack)
     *     .otherwise(GeneratorBuilder::boostSelf)
     * )
     * }</pre>
     *
     * @param configurator The builder configurator
     *
     * @return The builder instance
     *
     * @see fr.quatrevieux.araknemu.game.fight.ai.util.Predicates For the condition parameter
     * @see ConditionalBuilder
     * @see fr.quatrevieux.araknemu.game.fight.ai.action.logic.ConditionalGenerator
     */
    public final GeneratorBuilder when(Predicate<AI> condition, Consumer<ConditionalBuilder> configurator) {
        final ConditionalBuilder builder = new ConditionalBuilder(condition);

        configurator.accept(builder);

        return add(builder.build());
    }

    /**
     * Try to attack from the best cell (i.e. move to maximize damages)
     *
     * Try to perform in order :
     * - Move to the best cell for cast attack
     * - Cast the attack spell
     *
     * Note: this method is equivalent to `{@code builder.moveToAttack(simulator).attack(simulator)}`
     *
     * @param simulator Simulator used by AI
     *
     * @return The builder instance
     *
     * @see GeneratorBuilder#attackFromNearestCell(Simulator) Same action (move and attack), but configured to use the minimal amount of MP
     * @see GeneratorBuilder#moveToAttack(Simulator) For only perform the move action, without attack
     * @see GeneratorBuilder#attack(Simulator) For only perform the attack, without move
     */
    public final GeneratorBuilder attackFromBestCell(Simulator simulator) {
        return moveToAttack(simulator).attack(simulator);
    }

    /**
     * Try to move to the best cell for cast an attack spell
     *
     * To ensure that the move will be performed, add the attack action after this one.
     * Otherwise, if an attack is possible from the current cell it will be performed,
     * which will results to sub-optimal action.
     *
     * The action will not be performed if there is a tackle chance and if an attack is possible from the current cell
     *
     * @param simulator Simulator used by AI
     *
     * @return The builder instance
     *
     * @see GeneratorBuilder#attackFromBestCell(Simulator) For perform move and attack
     * @see MoveToAttack#bestTarget(Simulator) The used action generator
     */
    public final GeneratorBuilder moveToAttack(Simulator simulator) {
        return add(MoveToAttack.bestTarget(simulator));
    }

    /**
     * Try to attack from the nearest cell
     *
     * Try to perform in order :
     * - Cast the attack spell
     * - Move to the nearest cell for cast attack
     *
     * If an attack can be performed from the current cell, the move will be ignored
     *
     * Note: this method is equivalent to `{@code builder.attack(simulator).add(MoveToAttack.nearest(simulator))}`
     *
     * @param simulator Simulator used by AI
     *
     * @return The builder instance
     *
     * @see GeneratorBuilder#attackFromBestCell(Simulator) Same action (move and attack), but configured to maximize damage
     * @see GeneratorBuilder#attack(Simulator) For only perform the attack, without move
     */
    public final GeneratorBuilder attackFromNearestCell(Simulator simulator) {
        return attack(simulator).add(MoveToAttack.nearest(simulator));
    }

    /**
     * Try to attack from the current cell
     *
     * @param simulator Simulator used by AI
     *
     * @return The builder instance
     *
     * @see Attack The used action generator
     * @see GeneratorBuilder#attackFromBestCell(Simulator) For perform move and attack action
     * @see GeneratorBuilder#attackFromNearestCell(Simulator) For perform move and attack action
     */
    public final GeneratorBuilder attack(Simulator simulator) {
        return add(new Attack(simulator));
    }

    /**
     * Try to boost oneself
     *
     * Note: The action will not boost only self fighter, but also allies, with lower priority
     *
     * @param simulator Simulator used by AI
     *
     * @return The builder instance
     *
     * @see Boost#self(Simulator) The used action generator
     * @see GeneratorBuilder#boostAllies(Simulator) To boost allies in priority
     */
    public final GeneratorBuilder boostSelf(Simulator simulator) {
        return add(Boost.self(simulator));
    }

    /**
     * Try to boost allies
     *
     * @param simulator Simulator used by AI
     *
     * @return The builder instance
     *
     * @see Boost#allies(Simulator) The used action generator
     * @see GeneratorBuilder#boostSelf(Simulator) To boost oneself in priority
     */
    public final GeneratorBuilder boostAllies(Simulator simulator) {
        return add(Boost.allies(simulator));
    }

    /**
     * Try to move to the best cell for cast a boost spell
     *
     * To ensure that the move will be performed, add the boost action after this one.
     * The action will not be performed if there is a tackle chance and if an boost is possible from the current cell
     *
     * @param simulator Simulator used by AI
     *
     * @return The builder instance
     *
     * @see MoveToBoost The used action generator
     */
    public final GeneratorBuilder moveToBoost(Simulator simulator) {
        return add(new MoveToBoost(simulator)).boostAllies(simulator);
    }

    /**
     * Try to heal allies or self
     *
     * @param simulator Simulator used by AI
     *
     * @return The builder instance
     *
     * @see Heal The used action generator
     */
    public final GeneratorBuilder heal(Simulator simulator) {
        return add(new Heal(simulator));
    }

    /**
     * Try to debuff (i.e. apply negative buff) enemies
     *
     * @param simulator Simulator used by AI
     *
     * @return The builder instance
     *
     * @see Debuff The used action generator
     */
    public final GeneratorBuilder debuff(Simulator simulator) {
        return add(new Debuff(simulator));
    }

    /**
     * Try to move near the selected enemy
     *
     * @return The builder instance
     *
     * @see MoveNearEnemy The used action generator
     * @see AI#enemy() The selected enemy
     * @see GeneratorBuilder#moveOrTeleportNearEnemy() The move using MP or teleport spell
     */
    public final GeneratorBuilder moveNearEnemy() {
        return add(new MoveNearEnemy());
    }

    /**
     * Try to move to the nearest enemy of the invoker
     *
     * @return The builder instance
     *
     * @see BlockNearestEnemy The used action generator
     */
    public final GeneratorBuilder blockNearestEnemy() {
        return add(new BlockNearestEnemy());
    }

    /**
     * Try to teleport near the selected enemy
     *
     * @return The builder instance
     *
     * @see TeleportNearEnemy The used action generator
     * @see AI#enemy() The selected enemy
     * @see GeneratorBuilder#moveOrTeleportNearEnemy() The move using MP or teleport spell
     */
    public final GeneratorBuilder teleportNearEnemy() {
        return add(new TeleportNearEnemy());
    }

    /**
     * Try to move or teleport near the selected enemy
     *
     * This is equivalent to `{@code builder.moveNearEnemy().teleportNearEnemy()}`
     *
     * @return The builder instance
     *
     * @see AI#enemy() The selected enemy
     * @see GeneratorBuilder#moveNearEnemy()
     * @see GeneratorBuilder#moveFarEnemies()
     */
    public final GeneratorBuilder moveOrTeleportNearEnemy() {
        return moveNearEnemy().teleportNearEnemy();
    }

    /**
     * Try to move far all enemies
     *
     * The selected cell is the cell with the highest minimal distance from enemies
     *
     * @return The builder instance
     *
     * @see MoveFarEnemies The used action generator
     */
    public final GeneratorBuilder moveFarEnemies() {
        return add(new MoveFarEnemies());
    }

    /**
     * Try to move near allies
     *
     * The selected cell is the cell with the lowest minimal and average distance from allies
     *
     * @return The builder instance
     *
     * @see MoveNearAllies The used action generator
     */
    public final GeneratorBuilder moveNearAllies() {
        return add(new MoveNearAllies());
    }

    /**
     * Try to invoke a monster
     *
     * @return The builder instance
     *
     * @see Invoke The used action generator
     */
    public final GeneratorBuilder invoke(Simulator simulator) {
        return add(new Invoke(simulator));
    }

    /**
     * Try to attract enemies, or move to attract them
     *
     * @see AttractEnemy The used action generator
     * @see MoveToAttractEnemy to perform the move action if needed
     */
    public final GeneratorBuilder attractEnemy() {
        final AttractEnemy attractEnemy = new AttractEnemy(6);

        return add(attractEnemy).add(new MoveToAttractEnemy(attractEnemy));
    }

    /**
     * Build the action generator object
     *
     * If the pipeline is empty, and {@link NullGenerator} will be returned
     * If there is only one action generator, it will be returned
     * Else, create a {@link GeneratorAggregate}
     */
    public final ActionGenerator build() {
        if (generators.isEmpty()) {
            return NullGenerator.get();
        }

        if (generators.size() == 1) {
            return generators.get(0);
        }

        return new GeneratorAggregate(generators.toArray(new ActionGenerator[0]));
    }
}
