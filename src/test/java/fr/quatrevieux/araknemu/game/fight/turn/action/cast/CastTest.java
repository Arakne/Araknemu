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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.turn.action.cast;

import fr.arakne.utils.maps.constant.Direction;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.spell.SpellConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.castable.validator.CastConstraintValidator;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import fr.quatrevieux.araknemu.game.fight.module.CommonEffectsModule;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.event.SpellCasted;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.BaseCriticalityStrategy;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CastTest extends FightBaseCase {
    private Fight fight;
    private FightTurn turn;
    private PlayableFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        fight.register(new CommonEffectsModule(fight));
        turn = new FightTurn(fighter = player.fighter(), fight, Duration.ofSeconds(30));

        fighter.move(fight.map().get(185));
        other.fighter().move(fight.map().get(186));

        turn.start();
    }

    @Test
    void values() {
        Cast cast = new Cast(
            fighter,
            fighter.spells().get(3),
            fight.map().get(186),
            new SpellConstraintsValidator(fight),
            new BaseCriticalityStrategy()
        );

        assertSame(fighter, cast.performer());
        assertSame(ActionType.CAST, cast.type());
        assertEquals("Cast{spell=3, target=186}", cast.toString());
    }

    @Test
    void validateBadCell() {
        Cast cast = new Cast(fighter, fighter.spells().get(3), fight.map().get(0), new SpellConstraintsValidator(fight), new BaseCriticalityStrategy());

        assertFalse(cast.validate(turn));
        requestStack.assertLast(Error.cantCastCellNotAvailable());
    }

    @Test
    void validateNotEnoughAp() {
        turn.points().useActionPoints(5);

        Cast cast = new Cast(fighter, fighter.spells().get(3), fight.map().get(0), new SpellConstraintsValidator(fight), new BaseCriticalityStrategy());

        assertFalse(cast.validate(turn));
        requestStack.assertLast(Error.cantCastNotEnoughActionPoints(1, 5));
    }

    @Test
    void validateBadState() {
        fighter.states().push(19);

        Cast cast = new Cast(fighter, fighter.spells().get(3), fight.map().get(123), new SpellConstraintsValidator(fight), new BaseCriticalityStrategy());

        assertFalse(cast.validate(turn));
        requestStack.assertLast(Error.cantCastBadState());
    }

    @Test
    void validateSuccess() {
        Cast cast = new Cast(fighter, fighter.spells().get(3), fight.map().get(186), new SpellConstraintsValidator(fight), new BaseCriticalityStrategy());

        assertTrue(cast.validate(turn));
    }

    @Test
    void startSuccess() {
        Cast cast = new Cast(
            fighter,
            fighter.spells().get(3),
            fight.map().get(186),
            new SpellConstraintsValidator(new CastConstraintValidator[0]),
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return false; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return false; }
            }
        );

        ActionResult result = cast.start();

        assertInstanceOf(CastSuccess.class, result);
        assertTrue(result.success());
        assertFalse(result.secret());
        assertEquals(300, result.action());
        assertSame(fighter, result.performer());
        assertArrayEquals(new Object[] {3, 186, 103, 1, "30,1,1"}, result.arguments());
        assertFalse(CastSuccess.class.cast(result).critical());
        assertEquals(fighter.spells().get(3).effects(), CastSuccess.class.cast(result).effects());
        assertEquals(Direction.SOUTH_EAST, fighter.orientation());
    }

    @Test
    void startTargetCurrentCellShouldNotChangeTheOrientation() {
        fighter.setOrientation(Direction.NORTH_EAST);

        Cast cast = new Cast(
            fighter,
            fighter.spells().get(3),
            fighter.cell(),
            new SpellConstraintsValidator(new CastConstraintValidator[0]),
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return false; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return false; }
            }
        );

        cast.start();
        assertEquals(Direction.NORTH_EAST, fighter.orientation());
    }

    @Test
    void startCriticalHit() {
        Cast cast = new Cast(
            fighter,
            fighter.spells().get(3),
            fight.map().get(186),
            new SpellConstraintsValidator(new CastConstraintValidator[0]),
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return true; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return false; }
            }
        );

        ActionResult result = cast.start();

        assertInstanceOf(CastSuccess.class, result);
        assertTrue(result.success());
        assertFalse(result.secret());
        assertEquals(300, result.action());
        assertSame(fighter, result.performer());
        assertArrayEquals(new Object[] {3, 186, 103, 1, "30,1,1"}, result.arguments());
        assertTrue(CastSuccess.class.cast(result).critical());
        assertEquals(fighter.spells().get(3).criticalEffects(), CastSuccess.class.cast(result).effects());
        assertEquals(Direction.SOUTH_EAST, fighter.orientation());
    }

    @Test
    void startCriticalFailure() {
        Cast cast = new Cast(
            fighter,
            fighter.spells().get(3),
            fight.map().get(186),
            new SpellConstraintsValidator(new CastConstraintValidator[0]),
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return false; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return true; }
            }
        );

        ActionResult result = cast.start();

        assertInstanceOf(CastFailed.class, result);
        assertFalse(result.success());
        assertFalse(result.secret());
        assertEquals(302, result.action());
        assertSame(fighter, result.performer());
        assertArrayEquals(new Object[] {3}, result.arguments());
        assertEquals(Direction.SOUTH_EAST, fighter.orientation());

        result.apply(turn);

        requestStack.assertLast(ActionEffect.usedActionPoints(fighter, 5));
        assertEquals(1, turn.points().actionPoints());
    }

    @Test
    void failedWithEndTurn() {
        Spell spell = Mockito.mock(Spell.class);

        Mockito.when(spell.apCost()).thenReturn(4);
        Mockito.when(spell.endsTurnOnFailure()).thenReturn(true);

        Cast cast = new Cast(
            fighter,
            spell,
            fight.map().get(186),
            new SpellConstraintsValidator(new CastConstraintValidator[0]),
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return false; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return true; }
            }
        );

        cast.start().apply(turn);

        assertEquals(2, turn.points().actionPoints());
        assertFalse(turn.active());
    }

    @Test
    void duration() {
        Cast cast = new Cast(
            fighter,
            fighter.spells().get(3),
            fight.map().get(186),
            new SpellConstraintsValidator(fight),
            new BaseCriticalityStrategy()
        );

        assertEquals(Duration.ofMillis(500), cast.duration());
    }

    @Test
    void endOnSuccess() {
        AtomicReference<SpellCasted> ref = new AtomicReference<>();
        fight.dispatcher().add(SpellCasted.class, ref::set);

        Cast cast = new Cast(
            fighter,
            fighter.spells().get(3),
            fight.map().get(186),
            new SpellConstraintsValidator(new CastConstraintValidator[0]),
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return false; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return false; }
            }
        );

        requestStack.clear();

        cast.start().apply(turn);

        int damage = other.fighter().life().max() - other.fighter().life().current();

        assertBetween(4, 15, damage);

        requestStack.assertAll(
            ActionEffect.usedActionPoints(fighter, 5),
            ActionEffect.alterLifePoints(fighter, other.fighter(), -damage)
        );

        assertEquals(1, turn.points().actionPoints());
        assertEquals(cast, ref.get().action());
    }

    @Test
    void endOnCriticalHit() {
        Cast cast = new Cast(
            fighter,
            fighter.spells().get(3),
            fight.map().get(186),
            new SpellConstraintsValidator(new CastConstraintValidator[0]),
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return true; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return false; }
            }
        );

        requestStack.clear();

        cast.start().apply(turn);

        int damage = other.fighter().life().max() - other.fighter().life().current();

        assertEquals(20, damage);

        requestStack.assertAll(
            ActionEffect.criticalHitSpell(fighter, fighter.spells().get(3)),
            ActionEffect.usedActionPoints(fighter, 5),
            ActionEffect.alterLifePoints(fighter, other.fighter(), -damage)
        );

        assertEquals(1, turn.points().actionPoints());
    }
}
