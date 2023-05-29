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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.object;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.module.CommonEffectsModule;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.battlefield.AddZones;
import fr.quatrevieux.araknemu.network.game.out.fight.battlefield.RemoveZone;
import fr.quatrevieux.araknemu.network.game.out.game.UpdateCells;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrapTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.register(new CommonEffectsModule(fight));
        fight.nextState();
        fight.turnList().start();

        caster = player.fighter();
        target = other.fighter();

        target.move(fight.map().get(123));

        dataSet.pushFunctionalSpells();
        requestStack.clear();
    }

    @Test
    void getters() {
        Trap trap = new Trap(
            fight,
            fight.map().get(123),
            caster,
            2,
            4,
            container.get(SpellService.class).get(65).level(3),
            container.get(SpellService.class).get(183).level(3)
        );

        assertSame(fight.map().get(123), trap.cell());
        assertSame(caster, trap.caster());
        assertEquals(2, trap.size());
        assertEquals(4, trap.color());
        assertFalse(trap.visible());
        assertTrue(trap.visible(caster));
        assertEquals(1, trap.cellsProperties().length);
        assertEquals(25, trap.cellsProperties()[0].value());
        assertEquals(UpdateCells.LAYER_2_OBJECT_NUMBER.mask(), trap.cellsProperties()[0].mask());
        assertTrue(trap.shouldStopMovement());
    }

    @Test
    void refresh() {
        Trap trap = new Trap(
            fight,
            fight.map().get(123),
            caster,
            2,
            4,
            container.get(SpellService.class).get(65).level(3),
            container.get(SpellService.class).get(183).level(3)
        );

        assertTrue(trap.refresh());
    }

    @Test
    void disappear() {
        requestStack.clear();
        Trap trap = new Trap(
            fight,
            fight.map().get(123),
            caster,
            2,
            4,
            container.get(SpellService.class).get(65).level(3),
            container.get(SpellService.class).get(183).level(3)
        );

        trap.disappear();

        requestStack.assertAll(
            new RemoveZone(trap),
            new UpdateCells(UpdateCells.Data.reset(123))
        );
    }

    @Test
    void onStartTurnInAreaShouldDoNothing() {
        requestStack.clear();
        Trap trap = new Trap(
            fight,
            fight.map().get(123),
            caster,
            2,
            4,
            container.get(SpellService.class).get(65).level(3),
            container.get(SpellService.class).get(183).level(3)
        );

        trap.onStartTurnInArea(target);

        requestStack.assertEmpty();
    }

    @Test
    void onEnterInArea() {
        requestStack.clear();
        Trap trap = new Trap(
            fight,
            fight.map().get(123),
            caster,
            2,
            4,
            container.get(SpellService.class).get(65).level(3),
            container.get(SpellService.class).get(183).level(3)
        );
        fight.map().objects().add(trap);

        trap.onEnterInArea(target);

        int damage = target.life().max() - target.life().current();

        assertBetween(9, 15, damage);
        assertEquals(0, fight.map().objects().stream().count());

        requestStack.assertAll(
            ActionEffect.trapTriggered(caster, target, fight.map().get(123), container.get(SpellService.class).get(65).level(3)),
            new RemoveZone(trap),
            new UpdateCells(UpdateCells.Data.reset(123)),
            ActionEffect.alterLifePoints(caster, target, -damage)
        );
    }

    @Test
    void isOnArea() {
        Trap trap = new Trap(
            fight,
            fight.map().get(123),
            caster,
            2,
            4,
            container.get(SpellService.class).get(65).level(3),
            container.get(SpellService.class).get(183).level(3)
        );

        assertTrue(trap.isOnArea(fight.map().get(123)));
        assertTrue(trap.isOnArea(fight.map().get(138)));
        assertTrue(trap.isOnArea(fight.map().get(124)));
        assertTrue(trap.isOnArea(fight.map().get(93)));

        assertFalse(trap.isOnArea(fight.map().get(166)));
        assertFalse(trap.isOnArea(fight.map().get(241)));

        target.move(fight.map().get(138));
        assertTrue(trap.isOnArea(target));

        target.move(fight.map().get(166));
        assertFalse(trap.isOnArea(target));
    }

    @Test
    void show() {
        requestStack.clear();
        Trap trap = new Trap(
            fight,
            fight.map().get(123),
            caster,
            2,
            4,
            container.get(SpellService.class).get(65).level(3),
            container.get(SpellService.class).get(183).level(3)
        );

        assertFalse(trap.visible());
        assertFalse(trap.visible(target));

        trap.show(target);

        assertTrue(trap.visible());
        assertTrue(trap.visible(target));

        requestStack.assertAll(
            ActionEffect.packet(target, new AddZones(trap)),
            ActionEffect.packet(target, new UpdateCells(UpdateCells.Data.fromProperties(123, true, trap.cellsProperties())))
        );
    }
}
