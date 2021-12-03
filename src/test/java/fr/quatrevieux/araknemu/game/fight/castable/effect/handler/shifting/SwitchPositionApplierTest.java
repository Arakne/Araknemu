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
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertSame;

class SwitchPositionApplierTest extends FightBaseCase {
    private Fight fight;
    private SwitchPositionApplier applier;

    @Test
    void applySuccess() {
        configureFight(fb -> fb
            .addSelf(b -> b.cell(150))
            .addEnemy(b -> b.cell(250))
        );

        PlayerFighter caster = player.fighter();
        Fighter other = fight.fighters().get(1);

        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(effect.target()).thenReturn(SpellEffectTarget.DEFAULT);
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        FightCell lastCell = caster.cell();
        FightCell target = fight.map().get(250);

        applier.apply(caster, target.fighter().get());

        requestStack.assertAll(
            ActionEffect.teleport(caster, caster, target),
            ActionEffect.teleport(caster, other, lastCell)
        );

        assertSame(caster, target.fighter().get());
        assertSame(target, caster.cell());
        assertSame(other, lastCell.fighter().get());
        assertSame(lastCell, other.cell());
    }

    private void configureFight(Consumer<FightBuilder> configurator) {
        FightBuilder builder = fightBuilder();
        configurator.accept(builder);

        fight = builder.build(true);
        applier = new SwitchPositionApplier(fight);

        fight.nextState();

        requestStack.clear();
    }
}