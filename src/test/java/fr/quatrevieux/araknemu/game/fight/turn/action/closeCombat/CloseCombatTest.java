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

package fr.quatrevieux.araknemu.game.fight.turn.action.closeCombat;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.weapon.WeaponConstraintsValidator;
import fr.quatrevieux.araknemu.game.fight.exception.FightException;
import fr.quatrevieux.araknemu.game.fight.fighter.ActiveFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.module.CommonEffectsModule;
import fr.quatrevieux.araknemu.game.fight.turn.FightTurn;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionResult;
import fr.quatrevieux.araknemu.game.fight.turn.action.ActionType;
import fr.quatrevieux.araknemu.game.fight.turn.action.util.CriticalityStrategy;
import fr.quatrevieux.araknemu.game.item.inventory.exception.InventoryException;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class CloseCombatTest extends FightBaseCase {
    private Fight fight;
    private FightTurn turn;
    private Fighter fighter;
    private CloseCombat action;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        fight.register(new CommonEffectsModule(fight));
        turn = new FightTurn(fighter = player.fighter(), fight, Duration.ofSeconds(30));

        fighter.move(fight.map().get(171));
        other.fighter().move(fight.map().get(186));

        turn.start();

        requestStack.clear();
    }

    @Test
    void values() {
        action = new CloseCombat(
            fighter,
            fight.map().get(186)
        );

        assertSame(fighter, action.performer());
        assertSame(ActionType.CLOSE_COMBAT, action.type());
        assertEquals("CloseCombat{target=186}", action.toString());
    }

    @Test
    void validateNoWeapon() {
        action = new CloseCombat(
            fighter,
            fight.map().get(186)
        );

        assertThrows(FightException.class, () -> action.validate(turn));
        requestStack.assertEmpty();
    }

    @Test
    void validateNotEnoughAp() throws InventoryException, ContainerException, SQLException {
        action = new CloseCombat(
            fighter,
            fight.map().get(186)
        );

        equipWeapon(player);
        turn.points().useActionPoints(4);

        assertFalse(action.validate(turn));

        requestStack.assertLast(Error.cantCastNotEnoughActionPoints(2, 4));
    }

    @Test
    void validateInvalidTargetCell() throws InventoryException, ContainerException, SQLException {
        action = new CloseCombat(
            fighter,
            fight.map().get(0)
        );

        equipWeapon(player);

        assertFalse(action.validate(turn));

        requestStack.assertLast(Error.cantCastCellNotAvailable());
    }

    @Test
    void validateSuccess() throws InventoryException, ContainerException, SQLException {
        action = new CloseCombat(
            fighter,
            fight.map().get(186)
        );

        equipWeapon(player);

        assertTrue(action.validate(turn));
    }

    @Test
    void startCriticalFailure() throws InventoryException, ContainerException, SQLException {
        action = new CloseCombat(
            fighter,
            fight.map().get(186),
            new WeaponConstraintsValidator(),
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return false; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return true; }
            }
        );

        equipWeapon(player);

        ActionResult result = action.start();

        assertInstanceOf(CloseCombatFailed.class, result);
        assertEquals(305, result.action());
        assertFalse(result.success());
        assertFalse(result.secret());
        assertEquals(fighter, result.performer());
        assertArrayEquals(new Object[0], result.arguments());
    }

    @Test
    void startNormalHit() throws InventoryException, ContainerException, SQLException {
        action = new CloseCombat(
            fighter,
            fight.map().get(186),
            new WeaponConstraintsValidator(),
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return false; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return false; }
            }
        );

        equipWeapon(player);

        ActionResult result = action.start();

        assertInstanceOf(CloseCombatSuccess.class, result);
        assertEquals(303, result.action());
        assertTrue(result.success());
        assertFalse(result.secret());
        assertEquals(fighter, result.performer());
        assertArrayEquals(new Object[] {186}, result.arguments());
        assertFalse(CloseCombatSuccess.class.cast(result).critical());
        assertEquals(1, CloseCombatSuccess.class.cast(result).effects().get(0).min());
    }

    @Test
    void startCriticalHit() throws InventoryException, ContainerException, SQLException {
        action = new CloseCombat(
            fighter,
            fight.map().get(186),
            new WeaponConstraintsValidator(),
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return true; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return false; }
            }
        );

        equipWeapon(player);

        ActionResult result = action.start();

        assertInstanceOf(CloseCombatSuccess.class, result);
        assertEquals(303, result.action());
        assertTrue(result.success());
        assertFalse(result.secret());
        assertEquals(fighter, result.performer());
        assertArrayEquals(new Object[] {186}, result.arguments());
        assertTrue(CloseCombatSuccess.class.cast(result).critical());
        assertEquals(6, CloseCombatSuccess.class.cast(result).effects().get(0).min());
    }

    @Test
    void endNormalHit() throws InventoryException, ContainerException, SQLException {
        action = new CloseCombat(
            fighter,
            fight.map().get(186),
            new WeaponConstraintsValidator(),
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return false; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return false; }
            }
        );

        equipWeapon(player);
        requestStack.clear();
        action.start().apply(turn);

        int damage = other.fighter().life().max() - other.fighter().life().current();

        assertEquals(2, turn.points().actionPoints());
        assertBetween(1, 10, damage);

        requestStack.assertAll(
            ActionEffect.usedActionPoints(fighter, 4),
            ActionEffect.alterLifePoints(fighter, other.fighter(), -damage)
        );
    }

    @Test
    void endCriticalHit() throws InventoryException, ContainerException, SQLException {
        action = new CloseCombat(
            fighter,
            fight.map().get(186),
            new WeaponConstraintsValidator(),
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return true; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return false; }
            }
        );

        equipWeapon(player);
        requestStack.clear();
        action.start().apply(turn);

        int damage = other.fighter().life().max() - other.fighter().life().current();

        assertEquals(2, turn.points().actionPoints());
        assertBetween(9, 18, damage);

        requestStack.assertAll(
            ActionEffect.criticalHitCloseCombat(fighter),
            ActionEffect.usedActionPoints(fighter, 4),
            ActionEffect.alterLifePoints(fighter, other.fighter(), -damage)
        );
    }

    @Test
    void failed() throws InventoryException, ContainerException, SQLException {
        action = new CloseCombat(
            fighter,
            fight.map().get(186)
        );

        equipWeapon(player);
        requestStack.clear();

        action.start().apply(turn);

        assertFalse(turn.active());
        assertEquals(2, turn.points().actionPoints());
        requestStack.assertOne(ActionEffect.usedActionPoints(fighter, 4));
    }

    @Test
    void hammerWeaponWillNotTargetTheCaster() throws InventoryException, ContainerException, SQLException {
        dataSet.pushItemSets();

        action = new CloseCombat(
            fighter,
            fight.map().get(186),
            new WeaponConstraintsValidator(),
            new CriticalityStrategy() {
                public int hitRate(ActiveFighter fighter, int base) { return 0; }
                public int failureRate(ActiveFighter fighter, int base) { return 0; }
                public boolean hit(ActiveFighter fighter, int baseRate) { return false; }
                public boolean failed(ActiveFighter fighter, int baseRate) { return false; }
            }
        );

        equipWeapon(player, 2416);
        requestStack.clear();
        action.start().apply(turn);

        int damage = other.fighter().life().max() - other.fighter().life().current();

        assertBetween(6 + 10, 12 + 20, damage);
        assertEquals(player.fighter().life().max(), player.fighter().life().current());
    }
}
