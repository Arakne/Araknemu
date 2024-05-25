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
 * Copyright (c) 2017-2024 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.ai.factory.scripting;

import fr.quatrevieux.araknemu.game.fight.ai.AI;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.action.builder.ConditionalBuilder;
import fr.quatrevieux.araknemu.game.fight.ai.action.builder.GeneratorBuilder;
import fr.quatrevieux.araknemu.game.fight.ai.factory.NamedAiFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import groovy.lang.Closure;
import groovy.lang.DelegatesTo;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Base class to use for create AI on groovy scripts
 *
 * It allows to call generator builder methods directly on the factory, by overriding the configure() method,
 * and also simplify the creation of conditional actions.
 *
 * One of the configure() or configure(PlayableFighter fighter) method must be implemented.
 * By default, the AI name will be the class name in uppercase, without package.
 *
 * Example:
 * <pre>{@code
 * class MyAI extends AbstractScriptingAiBuilderFactory {
 *    Simulator simulator
 *
 *    // Inject dependencies on the constructor (any services can be injected)
 *    MyAI(Simulator simulator) {
 *        this.simulator = simulator
 *    }
 *
 *    // Configure the AI
 *    @Override
 *    protected void configure() {
 *        attackFromBestCell() // You can call generator builder methods directly
 *        add(new MyCustomAction()) // Use add() to add custom actions
 *
 *        add { ai, actions -> // Add can be used with a closure (takes as argument the AI and the actions factory)
 *            // ...
 *            return Optional.of(actions.cast().create(ai.fighter(), spell, target))
 *        }
 *
 *        whether({ fighter().life().current() < 50 }) { // Use whether() to create conditional actions
 *            heal() // Actions defined in the body will be executed if the condition is true
 *            moveFarEnemies()
 *
 *            otherwise { // Use otherwise() to define actions executed if the condition is false
 *                boostSelf()
 *                moveNearEnemy()
 *            }
 *        }
 *    }
 * }
 * }</pre>
 *
 * @see fr.quatrevieux.araknemu.game.fight.ai.factory.AbstractAiBuilderFactory To define AI without groovy
 */
public abstract class AbstractScriptingAiBuilderFactory extends GeneratorBuilder implements NamedAiFactory<PlayableFighter>, Cloneable {
    @Override
    public final Optional<AI> create(PlayableFighter fighter) {
        final AbstractScriptingAiBuilderFactory self = clone();

        self.configure(fighter);

        return Optional.of(new FighterAI(fighter, fighter.fight(), self.build()));
    }

    /**
     * Configure the AI for the given fighter
     */
    protected void configure(PlayableFighter fighter) {
        configure();
    }

    /**
     * Build the AI actions pipeline
     */
    protected void configure() {
        throw new UnsupportedOperationException("One of configure() or configure(PlayableFighter fighter) method must be implemented");
    }

    /**
     * Build a conditional action
     *
     * Usage:
     * <pre>{@code
     * // Define the condition
     * whether({ turn().points().actionPoints() > 10 }) {
     *     // Actions to execute if the condition is true
     *     teleportNearEnemy()
     *     add(new CastGivenSpell(147))
     *
     *     // Define the "else" part
     *     otherwise {
     *         // Actions to execute if the condition is false
     *         boostSelf()
     *         moveFarEnemies()
     *     }
     * }
     *
     * // which is equivalent to
     * when({ it.turn().points().actionPoints() > 10 }) {
     *     it.success { GeneratorBuilder b ->
     *         b.teleportNearEnemy()
     *         b.add(new CastGivenSpell(147))
     *     }
     *
     *     it.otherwise { GeneratorBuilder b ->
     *         b.boostSelf()
     *         b.moveFarEnemies()
     *     }
     * }
     * }</pre>
     *
     * @param condition The condition predicate. Takes as argument the AI, which is the delegate of the closure.
     * @param body The conditional actions builder. Takes as argument the builder, which is the delegate of the closure.
     *
     * @return the current builder instance for chaining
     *
     * @see GeneratorBuilder#when(Predicate, Consumer) The equivalent method for non-groovy code
     */
    public final AbstractScriptingAiBuilderFactory whether(@DelegatesTo(AI.class) Closure<Boolean> condition, @DelegatesTo(WhetherBody.class) Closure<?> body) {
        when(
            ai -> {
                condition.setDelegate(ai);
                condition.setResolveStrategy(Closure.DELEGATE_FIRST);

                return condition.call(ai);
            },
            cb -> new WhetherBody(cb).configure(body)
        );

        return this;
    }

    @Override
    protected AbstractScriptingAiBuilderFactory clone() {
        try {
            return (AbstractScriptingAiBuilderFactory) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    public static final class WhetherBody extends GeneratorBuilder {
        private final ConditionalBuilder builder;

        public WhetherBody(ConditionalBuilder builder) {
            this.builder = builder;
        }

        private void configure(@DelegatesTo(WhetherBody.class) Closure<?> configurator) {
            configurator.setDelegate(this);
            configurator.setResolveStrategy(Closure.DELEGATE_FIRST);
            configurator.call(this);

            builder.success(this.build());
        }

        /**
         * Build the "else" part of the conditional action
         *
         * The configurator closure will be called with the builder as delegate,
         * so you can call generator builder methods directly
         *
         * @param configurator Configuration closure. Takes as argument the builder.
         */
        public void otherwise(@DelegatesTo(GeneratorBuilder.class) Closure<?> configurator) {
            builder.otherwise(b -> {
                configurator.setDelegate(b);
                configurator.setResolveStrategy(Closure.DELEGATE_FIRST);
                configurator.call(b);
            });
        }
    }
}
