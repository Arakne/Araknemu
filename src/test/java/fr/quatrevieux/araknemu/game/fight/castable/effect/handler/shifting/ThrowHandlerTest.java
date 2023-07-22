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
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ThrowHandlerTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;
    private ThrowHandler handler;

    @Test
    void buffNotSupported() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(165))
            .addEnemy(b -> b.player(other).cell(150))
        );

        caster = player.fighter();
        target = other.fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(effect.min()).thenReturn(3);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());

        assertThrows(UnsupportedOperationException.class, () -> handler.buff(scope, scope.effects().get(0)));
    }

    @Test
    void applySuccess() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(165))
            .addEnemy(b -> b.player(other).cell(150))
        );

        caster = player.fighter();
        target = other.fighter();

        new CarryingApplier(fight, 3, 8).carry(
            caster,
            target
        );

        requestStack.clear();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, fight.map().get(123));
        handler.handle(scope, scope.effects().get(0));

        requestStack.assertAll(
            ActionEffect.removeState(caster, 3),
            ActionEffect.removeState(target, 8),
            new ActionEffect(51, caster, 123)
        );

        assertNotSame(caster.cell(), target.cell());
        assertFalse(caster.states().has(3));
        assertFalse(target.states().has(8));
        assertSame(target, fight.map().get(123).fighter());
        assertSame(fight.map().get(123), target.cell());
        assertSame(caster, caster.cell().fighter());
    }

    @Test
    void applyNotCarryingShouldDoNothing() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(165))
            .addEnemy(b -> b.player(other).cell(150))
        );
        requestStack.clear();

        caster = player.fighter();
        target = other.fighter();

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCastScope scope = makeCastScope(caster, spell, effect, target.cell());
        handler.handle(scope, scope.effects().get(0));

        requestStack.assertEmpty();
    }

    private void configureFight(Consumer<FightBuilder> configurator) {
        FightBuilder builder = fightBuilder();
        configurator.accept(builder);

        fight = builder.build(true);
        handler = new ThrowHandler(new CarryingApplier(fight, 3, 8));

        fight.nextState();

        requestStack.clear();
    }
}
