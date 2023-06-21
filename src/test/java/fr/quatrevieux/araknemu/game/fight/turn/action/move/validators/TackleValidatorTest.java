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
 * Copyright (c) 2017-2021 Vincent Quatrevieux Jean-Alexandre Valentin
 */

package fr.quatrevieux.araknemu.game.fight.turn.action.move.validators;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.maps.path.Path;
import fr.arakne.utils.maps.path.PathStep;
import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterData;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.invocation.StaticInvocationFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.Move;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveFailed;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.move.MoveSuccess;
import fr.quatrevieux.araknemu.game.fight.turn.order.AlternateTeamFighterOrder;
import fr.quatrevieux.araknemu.game.monster.MonsterService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.time.Duration;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TackleValidatorTest extends FightBaseCase {
    private Fight fight;
    private FightTurn turn;
    private PlayableFighter fighter;
    private TackleValidator validator;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        validator = new TackleValidator();
        fight = createFight();
        fighter = player.fighter();
        fighter.move(fight.map().get(185));
        turn = new FightTurn(player.fighter(), fight, Duration.ofSeconds(30));
        turn.start();
    }

    @Test
    void validatePathWithNoEnnemy() {
        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(198), Direction.NORTH_WEST)
            )
        );
        
        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);
        
        MoveResult result = new MoveSuccess(fighter, path);

        MoveResult result2 = validator.validate(move, result);

        assertSame(result, result2);
    }

    @Test
    void validatePathWithEnnemyNotOnFirstStep() throws SQLException {
        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        );

        other.fighter().move(fight.map().get(198));
        
        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);

        MoveResult result = new MoveSuccess(fighter, path);

        MoveResult result2 = validator.validate(move, result);

        assertSame(result, result2);
    }

    @Test
    void validatePathWithEnnemyOnFirstStepWithLessAgilityShouldNotBlock() throws SQLException {
        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        );

        fighter.characteristics().alter(Characteristic.AGILITY, 500);
        other.fighter().move(fight.map().get(170));

        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);

        MoveResult result = new MoveSuccess(fighter, path);

        MoveResult result2 = validator.validate(move, result);

        assertSame(result, result2);
    }

    @Test
    void validatePathWithEnnemyOnFirstStepWithMoreAgilityShouldBlockAndRemoveAP() throws SQLException {
        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        );

        other.fighter().characteristics().alter(Characteristic.AGILITY, 500);
        other.fighter().move(fight.map().get(170));

        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);

        MoveResult result = new MoveSuccess(fighter, path);
        MoveResult result2 = validator.validate(move, result);

        assertNotSame(result, result2);
        assertInstanceOf(MoveFailed.class, result2);
        assertEquals(3, result2.lostMovementPoints());
        assertEquals(6, result2.lostActionPoints());
        assertEquals(104, result2.action());
        assertEquals(185, result2.target().id());
    }

    @Test
    void validatePathWithStaticEnnemyOnFirstStepShouldBeIgnored() throws SQLException {
        dataSet.pushMonsterTemplateInvocations();

        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        );

        StaticInvocationFighter invoc = new StaticInvocationFighter(
            -50,
            container.get(MonsterService.class).load(282).get(5),
            other.fighter().team(),
            other.fighter()
        );
        fight.fighters().join(invoc, fight.map().get(170));
        invoc.init();
        invoc.characteristics().alter(Characteristic.AGILITY, 500);

        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);

        MoveResult result = new MoveSuccess(fighter, path);
        MoveResult result2 = validator.validate(move, result);

        assertSame(result, result2);
    }

    @Test
    void validateWithRootedStateShouldIgnoreTackle() throws SQLException {
        fighter.states().push(TackleValidator.STATE_ROOTED);

        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        );

        other.fighter().characteristics().alter(Characteristic.AGILITY, 500);
        other.fighter().move(fight.map().get(170));

        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);

        MoveResult result = validator.validate(move, new MoveSuccess(fighter, path));

        assertTrue(result.success());
        assertEquals(227, result.target().id());
    }

    @Test
    void validateWithRootedStateEnemyShouldIgnoreTackle() throws SQLException {

        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        );

        other.fighter().characteristics().alter(Characteristic.AGILITY, 500);
        other.fighter().states().push(TackleValidator.STATE_ROOTED);
        other.fighter().move(fight.map().get(170));

        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);

        MoveResult result = validator.validate(move, new MoveSuccess(fighter, path));

        assertTrue(result.success());
        assertEquals(227, result.target().id());
    }

    @Test
    void validatePathWithEnnemyOnFirstStepWithSameAgilityShouldBlockHalfChance() throws SQLException {
        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        );

        other.fighter().move(fight.map().get(170));

        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);

        int blockedCount = 0;

        for (int i = 0; i < 100; ++i) {
            MoveResult result = validator.validate(move, new MoveSuccess(fighter, path));

            if (!result.success()) {
                ++blockedCount;

                assertEquals(3, result.lostActionPoints());
                assertEquals(3, result.lostMovementPoints());
            }
        }

        assertBetween(40, 60, blockedCount);
    }

    @Test
    void validatePathWithEnnemyOnFirstStepWithSlightlyLessAgilityShouldBlockLessThanHalfChance() throws SQLException {
        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        );

        fighter.characteristics().alter(Characteristic.AGILITY, 10);
        other.fighter().move(fight.map().get(170));

        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);

        int blockedCount = 0;

        for (int i = 0; i < 100; ++i) {
            MoveResult result = validator.validate(move, new MoveSuccess(fighter, path));

            if (!result.success()) {
                ++blockedCount;

                assertEquals(1, result.lostActionPoints());
                assertEquals(3, result.lostMovementPoints());
            }
        }

        assertBetween(20, 40, blockedCount);
    }

    @Test
    void validatePathWithEnnemyOnFirstStepWithSlightlyMoreAgilityShouldBlockMoreThanHalfChance() throws SQLException {
        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        );

        other.fighter().characteristics().alter(Characteristic.AGILITY, 10);
        other.fighter().move(fight.map().get(170));

        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);

        int blockedCount = 0;

        for (int i = 0; i < 100; ++i) {
            MoveResult result = validator.validate(move, new MoveSuccess(fighter, path));

            if (!result.success()) {
                ++blockedCount;

                assertEquals(4, result.lostActionPoints());
                assertEquals(3, result.lostMovementPoints());
            }
        }

        assertBetween(70, 80, blockedCount);
    }

    @MethodSource("provideEnemiesAndChances")
    @ParameterizedTest
    void validateWithMultipleEnemiesShouldCombineChances(int[] enemiesCells, int chance) {
        FightBuilder builder = fightBuilder().addSelf(fb -> fb.cell(185));

        for (int cellId : enemiesCells) {
            builder.addEnemy(fb -> fb.cell(cellId));
        }

        fight = builder.build(true);
        fight.start(new AlternateTeamFighterOrder());

        fighter = player.fighter();
        turn = new FightTurn(fighter, fight, Duration.ofSeconds(30));
        turn.start();

        Path<FightCell> path = new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        );

        Move move = new Move(turn.fighter(), path, new FightPathValidator[0]);

        int successCount = 0;

        for (int i = 0; i < 100; ++i) {
            MoveResult result = validator.validate(move, new MoveSuccess(fighter, path));

            if (result.success()) {
                ++successCount;
            }
        }

        assertBetween(chance - 10, chance + 10, successCount);
    }

    public static Stream<Arguments> provideEnemiesAndChances() {
        return Stream.of(
            Arguments.of(new int[] {200}, 50),
            Arguments.of(new int[] {200, 171}, 25),
            Arguments.of(new int[] {200, 171, 170}, 12)
        );
    }

    @MethodSource("provideAgilityAndChance")
    @ParameterizedTest
    void computeTackle(int performerAgility, int enemyAgility, double escapeProbability) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        FightBuilder builder = fightBuilder()
            .addSelf(fb -> fb.cell(185).charac(Characteristic.AGILITY, performerAgility).charac(Characteristic.INTELLIGENCE, 1000))
            .addEnemy(fb -> fb.cell(170).charac(Characteristic.AGILITY, enemyAgility))
        ;

        fight = builder.build(true);
        fight.start(new AlternateTeamFighterOrder());

        fighter = player.fighter();
        turn = new FightTurn(fighter, fight, Duration.ofSeconds(30));
        turn.start();

        Method method = validator.getClass().getDeclaredMethod("computeTackle", Fighter.class, FighterData.class);
        method.setAccessible(true);

        assertEquals(escapeProbability, (double) method.invoke(validator, fighter, getFighter(1)), 0.01);
    }

    public static Stream<Arguments> provideAgilityAndChance() {
        return Stream.of(
            Arguments.of(0, 0, 0.5),
            Arguments.of(50, 0, 1),
            Arguments.of(50, 50, 0.5),
            Arguments.of(100, 50, 0.88),
            Arguments.of(50, 100, 0.13),
            Arguments.of(450, 700, 0.19),
            Arguments.of(150, 350, 0.0),
            Arguments.of(350, 150, 1),
            Arguments.of(350, 200, 0.88),
            Arguments.of(350, 250, 0.73),
            Arguments.of(350, 300, 0.61),
            Arguments.of(350, 350, 0.50),
            Arguments.of(350, 400, 0.41),
            Arguments.of(350, 450, 0.32),
            Arguments.of(350, 500, 0.25),
            Arguments.of(350, 550, 0.18),
            Arguments.of(350, 600, 0.13),
            Arguments.of(350, 650, 0.07),
            Arguments.of(350, 700, 0.02)
        );
    }

    @Test
    void functionalStopped() {
        other.fighter().move(fight.map().get(170));
        other.fighter().characteristics().alter(Characteristic.AGILITY, 500);

        Move move = (Move) fight.actions().move().create(fighter, new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        ));

        fight.start(new AlternateTeamFighterOrder());

        turn.perform(move);
        turn.terminate();

        assertEquals(185, fighter.cell().id());
        assertEquals(0, fighter.turn().points().movementPoints());
        assertEquals(0, fighter.turn().points().actionPoints());
    }

    @Test
    void functionalNotStopped() {
        other.fighter().move(fight.map().get(170));
        fighter.characteristics().alter(Characteristic.AGILITY, 500);

        Move move = (Move) fight.actions().move().create(fighter, new Path<FightCell>(
            fight.map().decoder(),
            Arrays.asList(
                new PathStep<FightCell>(fight.map().get(185), Direction.EAST),
                new PathStep<FightCell>(fight.map().get(199), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(213), Direction.SOUTH_WEST),
                new PathStep<FightCell>(fight.map().get(227), Direction.NORTH_WEST)
            )
        ));

        fight.start(new AlternateTeamFighterOrder());

        turn.perform(move);
        turn.terminate();

        assertEquals(227, fighter.cell().id());
        assertEquals(0, fighter.turn().points().movementPoints());
        assertEquals(6, fighter.turn().points().actionPoints());
    }
}
