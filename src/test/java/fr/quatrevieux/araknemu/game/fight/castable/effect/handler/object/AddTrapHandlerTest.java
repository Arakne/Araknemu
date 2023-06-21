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

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.weapon.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldObject;
import fr.quatrevieux.araknemu.game.fight.module.CommonEffectsModule;
import fr.quatrevieux.araknemu.game.fight.module.IndirectSpellApplyEffectsModule;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.type.Weapon;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AddTrapHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private AddTrapHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.register(new CommonEffectsModule(fight));
        fight.register(new IndirectSpellApplyEffectsModule(fight, container.get(SpellService.class)));
        fight.nextState();
        fight.turnList().start();

        caster = player.fighter();
        target = other.fighter();

        target.move(fight.map().get(123));

        handler = new AddTrapHandler(fight, container.get(SpellService.class));
        dataSet.pushFunctionalSpells();

        requestStack.clear();
    }

    @Test
    void buffNotSupported() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(401);
        Mockito.when(effect.min()).thenReturn(183);
        Mockito.when(effect.max()).thenReturn(2);
        Mockito.when(effect.special()).thenReturn(4);
        Mockito.when(effect.duration()).thenReturn(4);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 3)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        assertThrows(UnsupportedOperationException.class, () -> handler.buff(scope, scope.effects().get(0)));
    }

    @Test
    void handleWillAddTrapToMap() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(401);
        Mockito.when(effect.min()).thenReturn(183);
        Mockito.when(effect.max()).thenReturn(2);
        Mockito.when(effect.special()).thenReturn(4);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 3)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(124));
        handler.handle(scope, scope.effects().get(0));

        Optional<BattlefieldObject> found = fight.map().objects().stream().filter(o -> o.caster().equals(caster)).findFirst();

        assertTrue(found.isPresent());
        assertEquals(caster, found.get().caster());
        assertEquals(124, found.get().cell().id());
        assertEquals(3, found.get().size());
        assertEquals(4, found.get().color());

        requestStack.assertOne(ActionEffect.packet(caster, "GDZ+124;3;4"));
        requestStack.assertOne(ActionEffect.packet(caster, "GDC124;aaaaaaaaaz4;1"));
        requestStack.clear();

        // Move target to trap
        target.move(fight.map().get(124));

        requestStack.assertOne("GA;306;2;0,124,0,0,0,1"); // trap is triggered
        assertEquals(50-12, 50-9, target.life().current());

        // Trap is removed from map
        requestStack.assertOne("GDZ-124;3;4");
        requestStack.assertOne("GDC124");
        assertFalse(fight.map().objects().stream().anyMatch(o -> o.caster().equals(caster)));
    }

    @Test
    void handleCannotAddTwoTrapOnSameCell() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(401);
        Mockito.when(effect.min()).thenReturn(183);
        Mockito.when(effect.max()).thenReturn(2);
        Mockito.when(effect.special()).thenReturn(4);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 3)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(spell.id()).thenReturn(120);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(124));

        // Add trap
        handler.handle(scope, scope.effects().get(0));
        assertEquals(1, fight.map().objects().stream().filter(o -> o.caster().equals(caster)).count());
        requestStack.clear();

        // Effect is ignored
        handler.handle(scope, scope.effects().get(0));
        assertEquals(1, fight.map().objects().stream().filter(o -> o.caster().equals(caster)).count());
        requestStack.assertLast("GA;151;1;120");
    }

    @Test
    void handleWithoutSpellIsUnsupported() throws SQLException {
        dataSet
            .pushItemTemplates()
            .pushWeaponTemplates()
            .pushItemSets()
        ;

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        CastableWeapon weapon = new CastableWeapon((Weapon) container.get(ItemService.class).create(40));

        Mockito.when(effect.effect()).thenReturn(401);
        Mockito.when(effect.min()).thenReturn(183);
        Mockito.when(effect.max()).thenReturn(2);
        Mockito.when(effect.special()).thenReturn(4);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 3)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);

        FightCastScope scope = makeCastScope(caster, weapon, effect, fight.map().get(124));
        assertThrows(UnsupportedOperationException.class, () -> handler.handle(scope, scope.effects().get(0)));

        Optional<BattlefieldObject> found = fight.map().objects().stream().filter(o -> o.caster().equals(caster)).findFirst();
        assertFalse(found.isPresent());
    }
}
