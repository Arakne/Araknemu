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
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.ConditionalGenerator;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Build a condition generator
 *
 * @see ConditionalGenerator The built generator
 * @see GeneratorBuilder#when(Predicate, Consumer) For get the conditional builder
 */
public final class ConditionalBuilder<F extends ActiveFighter> {
    private final Predicate<AI<F>> condition;
    private final GeneratorBuilder<F> success;
    private final GeneratorBuilder<F> otherwise;

    public ConditionalBuilder(Predicate<AI<F>> condition) {
        this.condition = condition;
        this.success = new GeneratorBuilder<>();
        this.otherwise = new GeneratorBuilder<>();
    }

    /**
     * Configure the success action generator
     *
     * Note: this method can be called multiple times
     *
     * Usage:
     * <pre>{code
     * builder.when(ai -> checkCanPerform(ai), cb -> cb
     *     .success(b2 -> b2.attack(simulator).boostSelf(simulator))
     * )
     * }</pre>
     *
     * @param configurator The builder configurator
     *
     * @return The builder instance
     */
    public ConditionalBuilder<F> success(Consumer<GeneratorBuilder<F>> configurator) {
        configurator.accept(success);

        return this;
    }

    /**
     * Add a new action generator to the success generator
     *
     * Usage:
     * <pre>{code
     * builder.when(ai -> checkCanPerform(ai), cb -> cb
     *     .success(new MySuccessAction())
     * )
     * }</pre>
     *
     * @param generator Action generator to append
     *
     * @return The builder instance
     */
    public ConditionalBuilder<F> success(ActionGenerator<F> generator) {
        success.add(generator);

        return this;
    }

    /**
     * Configure the otherwise action generator
     *
     * Note: this method can be called multiple times
     *
     * Usage:
     * <pre>{code
     * builder.when(ai -> checkCanPerform(ai), cb -> cb
     *     .otherwise(b2 -> b2.attack(simulator).boostSelf(simulator))
     * )
     * }</pre>
     *
     * @param configurator The builder configurator
     *
     * @return The builder instance
     */
    public ConditionalBuilder<F> otherwise(Consumer<GeneratorBuilder<F>> configurator) {
        configurator.accept(otherwise);

        return this;
    }

    /**
     * Add a new action generator to the otherwise generator
     *
     * Usage:
     * <pre>{code
     * builder.when(ai -> checkCanPerform(ai), cb -> cb
     *     .otherwise(new MySuccessAction())
     * )
     * }</pre>
     *
     * @param generator Action generator to append
     *
     * @return The builder instance
     */
    public ConditionalBuilder<F> otherwise(ActionGenerator<F> generator) {
        otherwise.add(generator);

        return this;
    }

    /**
     * Build the {@link ConditionalGenerator} object
     */
    public ActionGenerator<F> build() {
        return new ConditionalGenerator<>(condition, success.build(), otherwise.build());
    }
}
