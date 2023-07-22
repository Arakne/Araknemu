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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.shifting;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterMoved;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

class CarryingApplierTest extends FightBaseCase {
    private CarryingApplier applier;
    private Fight fight;
    private Fighter caster;
    private Fighter target;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight(true);
        fight.nextState();
        applier = new CarryingApplier(fight, 3, 8);

        caster = player.fighter();
        target = other.fighter();

        requestStack.clear();
    }

    @Test
    void carry() {
        FightCell lastCell = target.cell();

        assertFalse(applier.active(caster));
        assertFalse(applier.active(target));

        applier.carry(caster, target);

        assertTrue(caster.states().has(3));
        assertTrue(target.states().has(8));
        assertTrue(applier.active(caster));
        assertTrue(applier.active(target));
        assertSame(caster.cell(), target.cell());
        assertSame(caster, caster.cell().fighter());
        assertFalse(lastCell.hasFighter());

        requestStack.assertAll(
            ActionEffect.addState(caster, 3),
            ActionEffect.addState(target, 8),
            new ActionEffect(50, caster, target.id())
        );
    }

    @Test
    void carryShouldIgnoredWhenTargetIsAlreadyCarried() throws SQLException {
        PlayerFighter otherCarrier = makePlayerFighter(makeSimpleGamePlayer(42));
        otherCarrier.setTeam(target.team());
        fight.fighters().joinTurnList(otherCarrier, fight.map().get(298));
        otherCarrier.init();
        applier.carry(otherCarrier, target);
        requestStack.clear();

        applier.carry(caster, target);

        assertFalse(applier.active(caster));
        assertFalse(caster.states().has(3));
        assertNotSame(caster.cell(), target.cell());
        assertSame(otherCarrier.cell(), target.cell());
        requestStack.assertEmpty();
    }

    @Test
    void carryShouldIgnoredWhenCasterAlreadyCarry() throws SQLException {
        PlayerFighter other = makePlayerFighter(makeSimpleGamePlayer(42));
        other.setTeam(target.team());
        fight.fighters().joinTurnList(other, fight.map().get(298));
        other.init();
        applier.carry(caster, other);
        requestStack.clear();

        applier.carry(caster, target);

        assertFalse(applier.active(target));
        assertFalse(target.states().has(8));
        assertNotSame(caster.cell(), target.cell());
        assertSame(other.cell(), caster.cell());
        requestStack.assertEmpty();
    }

    @Test
    void stopWithCarrier() {
        applier.carry(caster, target);
        applier.stop(caster);

        assertFalse(applier.active(caster));
        assertFalse(applier.active(target));
        assertFalse(caster.states().has(3));
        assertFalse(target.states().has(8));
        assertSame(caster.cell(), target.cell()); // stop will not move the target because cell is not free
        assertSame(caster, caster.cell().fighter());
    }

    @Test
    void stopWithCarried() {
        applier.carry(caster, target);
        applier.stop(target);

        assertFalse(applier.active(caster));
        assertFalse(applier.active(target));
        assertFalse(caster.states().has(3));
        assertFalse(target.states().has(8));
        assertSame(caster.cell(), target.cell()); // stop will not move the target because cell is not free
        assertSame(caster, caster.cell().fighter());
    }

    @Test
    void stopWithFreeCellShouldAddCarried() {
        AtomicReference<FighterMoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterMoved.class, ref::set);

        applier.carry(caster, target);
        caster.move(null); // free cell
        applier.stop(target);

        assertFalse(applier.active(caster));
        assertFalse(applier.active(target));
        assertFalse(caster.states().has(3));
        assertFalse(target.states().has(8));
        assertSame(target, target.cell().fighter());
        assertSame(target, ref.get().fighter());
        assertSame(target.cell(), ref.get().cell());
    }

    @Test
    void stopWithDeadCarrierShouldNotMoveItEvenIfCellIsFree() {
        AtomicReference<FighterMoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterMoved.class, ref::set);

        FightCell cell = caster.cell();

        applier.carry(caster, target);
        caster.move(null); // free cell
        target.life().kill(target);
        applier.stop(target);

        assertFalse(applier.active(caster));
        assertFalse(applier.active(target));
        assertFalse(caster.states().has(3));
        assertFalse(target.states().has(8));
        assertFalse(cell.hasFighter());
        assertNull(ref.get());
    }

    @Test
    void stopEffectNotActiveShouldDoNothing() {
        applier.stop(caster);

        requestStack.assertEmpty();
    }

    @Test
    void throwCarriedToCell() {
        AtomicReference<FighterMoved> ref = new AtomicReference<>();
        fight.dispatcher().add(FighterMoved.class, ref::set);

        applier.carry(caster, target);
        requestStack.clear();
        applier.throwCarriedToCell(caster, fight.map().get(298));

        assertFalse(applier.active(caster));
        assertFalse(applier.active(target));
        assertFalse(caster.states().has(3));
        assertFalse(target.states().has(8));
        assertSame(fight.map().get(298), target.cell());
        assertSame(target, target.cell().fighter());
        assertSame(target, ref.get().fighter());
        assertSame(fight.map().get(298), ref.get().cell());

        requestStack.assertAll(
            ActionEffect.removeState(caster, 3),
            ActionEffect.removeState(target, 8),
            new ActionEffect(51, caster, 298)
        );
    }

    @Test
    void throwCarriedToCellEffectNotActiveShouldDoNothing() {
        applier.throwCarriedToCell(caster, fight.map().get(298));

        requestStack.assertEmpty();
    }

    @Test
    void synchronizeMoveWithCarrier() {
        applier.carry(caster, target);
        requestStack.clear();

        caster.move(fight.map().get(298));
        applier.synchronizeMove(caster);

        assertTrue(applier.active(caster));
        assertTrue(applier.active(target));
        assertSame(caster.cell(), target.cell());
        assertSame(caster, caster.cell().fighter());
    }

    @Test
    void synchronizeMoveWithCarriedShouldStopEffect() {
        applier.carry(caster, target);
        requestStack.clear();

        target.move(fight.map().get(298));
        applier.synchronizeMove(target);

        assertFalse(applier.active(caster));
        assertFalse(applier.active(target));
        assertFalse(caster.states().has(3));
        assertFalse(target.states().has(8));
        assertSame(fight.map().get(298), target.cell());
        assertSame(target, target.cell().fighter());
        assertSame(caster, caster.cell().fighter());
    }
}
