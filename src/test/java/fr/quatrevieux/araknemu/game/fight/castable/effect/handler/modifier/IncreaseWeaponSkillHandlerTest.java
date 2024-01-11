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

package fr.quatrevieux.araknemu.game.fight.castable.effect.handler.modifier;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.closeCombat.CastableWeapon;
import fr.quatrevieux.araknemu.game.fight.castable.effect.buff.Buff;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.race.DefaultCloseCombat;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class IncreaseWeaponSkillHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private IncreaseWeaponSkillHandler handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fight.nextState();
        fight.turnList().start();

        caster = player.fighter();
        target = other.fighter();

        target.move(fight.map().get(123));

        handler = new IncreaseWeaponSkillHandler();

        requestStack.clear();
    }

    @Test
    void handleNotSupported() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        assertThrows(UnsupportedOperationException.class, () -> handler.handle(scope, scope.effects().get(0)));
    }

    @Test
    void buff() throws SQLException {
        equipWeapon(player);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(165);
        Mockito.when(effect.min()).thenReturn(3);
        Mockito.when(effect.max()).thenReturn(30);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.buff(scope, scope.effects().get(0));

        Optional<Buff> buff1 = caster.buffs().stream().filter(buff -> buff.effect().effect() == 165).findFirst();
        Optional<Buff> buff2 = target.buffs().stream().filter(buff -> buff.effect().effect() == 165).findFirst();

        assertTrue(buff1.isPresent());
        assertTrue(buff2.isPresent());

        assertEquals(3, buff1.get().effect().min());
        assertEquals(3, buff2.get().effect().min());
    }

    @Test
    void functional() throws SQLException {
        equipWeapon(player);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(165);
        Mockito.when(effect.min()).thenReturn(6);
        Mockito.when(effect.max()).thenReturn(30);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.buff(scope, scope.effects().get(0));

        CastableWeapon weapon = (CastableWeapon) caster.closeCombat().get();
        assertEquals(120, weapon.ability());
        assertEquals(1, weapon.effects().get(0).min());
        assertEquals(8, weapon.effects().get(0).max());
        assertEquals(7, weapon.criticalEffects().get(0).min());
        assertEquals(14, weapon.criticalEffects().get(0).max());

        caster.buffs().removeAll();

        assertEquals(90, weapon.ability());
        assertEquals(1, weapon.effects().get(0).min());
        assertEquals(6, weapon.effects().get(0).max());
        assertEquals(5, weapon.criticalEffects().get(0).min());
        assertEquals(10, weapon.criticalEffects().get(0).max());
    }

    @Test
    void functionalWeaponTypeDoNotCorrespond() throws SQLException {
        equipWeapon(player);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(165);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.max()).thenReturn(30);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.buff(scope, scope.effects().get(0));

        CastableWeapon weapon = (CastableWeapon) caster.closeCombat().get();
        assertEquals(90, weapon.ability());
        assertEquals(1, weapon.effects().get(0).min());
        assertEquals(6, weapon.effects().get(0).max());
        assertEquals(5, weapon.criticalEffects().get(0).min());
        assertEquals(10, weapon.criticalEffects().get(0).max());

        caster.buffs().removeAll();

        assertEquals(90, weapon.ability());
        assertEquals(1, weapon.effects().get(0).min());
        assertEquals(6, weapon.effects().get(0).max());
        assertEquals(5, weapon.criticalEffects().get(0).min());
        assertEquals(10, weapon.criticalEffects().get(0).max());
    }

    @Test
    void functionalNoWeaponEquipped() throws SQLException {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.effect()).thenReturn(165);
        Mockito.when(effect.min()).thenReturn(2);
        Mockito.when(effect.max()).thenReturn(30);
        Mockito.when(effect.duration()).thenReturn(5);
        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, caster.cell());
        handler.buff(scope, scope.effects().get(0));

        Assertions.assertInstanceOf(DefaultCloseCombat.class, caster.closeCombat().get());

        caster.buffs().removeAll();
        Assertions.assertInstanceOf(DefaultCloseCombat.class, caster.closeCombat().get());
    }
}
