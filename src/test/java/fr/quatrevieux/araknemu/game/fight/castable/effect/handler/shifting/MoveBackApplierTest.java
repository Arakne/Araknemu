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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

class MoveBackApplierTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private MoveBackApplier applier;

    @Test
    void applySuccess() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(165))
            .addEnemy(b -> b.player(other).cell(150))
        );

        caster = player.fighter();
        target = other.fighter();

        FightCell lastCell = target.cell();
        FightCell destination = fight.map().get(105);

        applier.apply(caster, target, 3);

        requestStack.assertLast(ActionEffect.slide(caster, target, destination));

        assertFalse(lastCell.hasFighter());
        assertSame(target, destination.fighter());
        assertSame(destination, target.cell());
    }

    @ParameterizedTest
    @MethodSource("provideDistances")
    void applyBlocked(int distance, int min, int max) {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(210))
            .addEnemy(b -> b.player(other).cell(182).currentLife(1000).maxLife(1000))
        );

        caster = player.fighter();
        target = other.fighter();

        applier.apply(caster, target, distance);
        FightCell destination = fight.map().get(168);

        int damage = target.life().max() - target.life().current();
        assertBetween(min, max, damage);

        requestStack.assertOne(ActionEffect.slide(caster, target, destination));
        requestStack.assertOne(ActionEffect.alterLifePoints(caster, target, -damage));
    }

    public static Stream<Arguments> provideDistances() {
        return Stream.of(
            Arguments.of(2, 9, 16),
            Arguments.of(3, 18, 32),
            Arguments.of(4, 27, 48),
            Arguments.of(10, 81, 144)
        );
    }

    @Test
    void applyBlockedByFighterShouldGetHalfDamage() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(210))
            .addEnemy(b -> b.cell(182).currentLife(1000).maxLife(1000))
            .addEnemy(b -> b.cell(168).currentLife(1000).maxLife(1000))
        );

        caster = player.fighter();

        List<Fighter> enemies = new ArrayList<>(fight.team(1).fighters());

        applier.apply(caster, enemies.get(0), 3);

        int damage = enemies.get(0).life().max() - enemies.get(0).life().current();
        assertBetween(27, 48, damage);

        assertEquals(damage / 2, enemies.get(1).life().max() - enemies.get(1).life().current());
    }

    @Test
    void applyBlockedChainDamage() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(305))
            .addEnemy(b -> b.cell(291))
            .addEnemy(b -> b.cell(277))
            .addEnemy(b -> b.cell(263))
            .addEnemy(b -> b.cell(249))
            .addEnemy(b -> b.cell(235))
        );

        caster = player.fighter();

        List<Fighter> enemies = new ArrayList<>(fight.team(1).fighters());

        // Remove random damage
        new MoveBackApplier(fight, 8, 1).apply(caster, enemies.get(0), 1);

        assertEquals(9, enemies.get(0).life().max() - enemies.get(0).life().current());
        assertEquals(4, enemies.get(1).life().max() - enemies.get(1).life().current());
        assertEquals(2, enemies.get(2).life().max() - enemies.get(2).life().current());
        assertEquals(1, enemies.get(3).life().max() - enemies.get(3).life().current());
        assertEquals(0, enemies.get(4).life().max() - enemies.get(4).life().current());
    }

    @Test
    void applyBlockedWithoutMovementShouldNotPerformMove() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(210))
            .addEnemy(b -> b.cell(168).currentLife(1000).maxLife(1000))
        );

        caster = player.fighter();

        List<Fighter> enemies = new ArrayList<>(fight.team(1).fighters());

        applier.apply(caster, enemies.get(0), 3);

        int damage = enemies.get(0).life().max() - enemies.get(0).life().current();

        requestStack.assertAll(ActionEffect.alterLifePoints(caster, enemies.get(0), -damage));
        assertSame(fight.map().get(168), enemies.get(0).cell());
    }

    private void configureFight(Consumer<FightBuilder> configurator) {
        FightBuilder builder = fightBuilder();
        configurator.accept(builder);

        fight = builder.build(true);
        applier = new MoveBackApplier(fight);

        fight.nextState();

        requestStack.clear();
    }
}
