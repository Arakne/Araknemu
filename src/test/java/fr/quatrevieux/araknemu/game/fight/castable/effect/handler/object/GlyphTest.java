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
import fr.quatrevieux.araknemu.network.game.out.fight.battlefield.RemoveZone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GlyphTest extends FightBaseCase {
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
        Glyph glyph = new Glyph(
            fight,
            fight.map().get(123),
            caster,
            2,
            4,
            3,
            container.get(SpellService.class).get(183).level(3)
        );

        assertSame(fight.map().get(123), glyph.cell());
        assertSame(caster, glyph.caster());
        assertEquals(2, glyph.size());
        assertEquals(4, glyph.color());
        assertTrue(glyph.visible());
        assertTrue(glyph.visible(caster));
        assertEquals(0, glyph.cellsProperties().length);
        assertFalse(glyph.shouldStopMovement());
    }

    @Test
    void refresh() {
        Glyph glyph = new Glyph(
            fight,
            fight.map().get(123),
            caster,
            2,
            4,
            3,
            container.get(SpellService.class).get(183).level(3)
        );

        assertTrue(glyph.refresh());
        assertTrue(glyph.refresh());
        assertFalse(glyph.refresh());
        assertFalse(glyph.refresh());
        assertFalse(glyph.refresh());
    }

    @Test
    void refreshWithInfiniteDuration() {
        Glyph glyph = new Glyph(
            fight,
            fight.map().get(123),
            caster,
            2,
            4,
            -1,
            container.get(SpellService.class).get(183).level(3)
        );

        assertTrue(glyph.refresh());
        assertTrue(glyph.refresh());
    }

    @Test
    void disappear() {
        Glyph glyph = new Glyph(
            fight,
            fight.map().get(123),
            caster,
            2,
            4,
            3,
            container.get(SpellService.class).get(183).level(3)
        );

        glyph.disappear();

        requestStack.assertLast(new RemoveZone(glyph));
    }

    @Test
    void onStartTurnInArea() {
        Glyph glyph = new Glyph(
            fight,
            fight.map().get(123),
            caster,
            2,
            4,
            3,
            container.get(SpellService.class).get(183).level(3)
        );

        glyph.onStartTurnInArea(target);

        requestStack.assertOne(ActionEffect.glyphTriggered(caster, target, fight.map().get(123), container.get(SpellService.class).get(183).level(3)));
        assertBetween(9, 15, target.life().max() - target.life().current());
    }

    @Test
    void isOnArea() {
        Glyph glyph = new Glyph(
            fight,
            fight.map().get(123),
            caster,
            2,
            4,
            3,
            container.get(SpellService.class).get(183).level(3)
        );

        assertTrue(glyph.isOnArea(fight.map().get(123)));
        assertTrue(glyph.isOnArea(fight.map().get(138)));
        assertTrue(glyph.isOnArea(fight.map().get(124)));
        assertTrue(glyph.isOnArea(fight.map().get(93)));

        assertFalse(glyph.isOnArea(fight.map().get(166)));
        assertFalse(glyph.isOnArea(fight.map().get(241)));

        target.move(fight.map().get(138));
        assertTrue(glyph.isOnArea(target));

        target.move(fight.map().get(166));
        assertFalse(glyph.isOnArea(target));
    }
}
