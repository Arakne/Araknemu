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
import fr.quatrevieux.araknemu.game.fight.castable.BaseCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.castable.effect.handler.EffectHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.util.Asserter;

/**
 * Add a trap on the target cell
 * The trap will trigger the related spell when a fighter enter it
 *
 * Arguments:
 * - min: spell ID
 * - max: spell level
 * - special: trap color
 *
 * Two traps cannot be on the same cell, otherwise the GA 151 (CANT_DO_INVISIBLE_OBSTACLE) is sent and the trap is not added
 *
 * @see Trap
 */
public final class AddTrapHandler implements EffectHandler {
    private final Fight fight;
    private final SpellService spellService;

    public AddTrapHandler(Fight fight, SpellService spellService) {
        this.fight = fight;
        this.spellService = spellService;
    }

    @Override
    public void handle(FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        final Fighter caster = cast.caster();
        final SpellEffect spellEffect = effect.effect();
        final Spell spell = cast.spell();
        final FightCell target = cast.target();

        // Trap can only be cast using a spell
        if (spell == null) {
            throw new UnsupportedOperationException("Trap effect can only be used with a spell");
        }

        if (hasTrapOnCell(target)) {
            fight.send(ActionEffect.spellBlockedByInvisibleObstacle(caster, spell));
            return;
        }

        final Trap trap = new Trap(
            fight,
            target,
            caster,
            spellEffect.area().size(),
            spellEffect.special(),
            spell,
            spellService
                .get(spellEffect.min())
                .level(Asserter.assertPositive(spellEffect.max()))
        );

        fight.map().objects().add(trap);
        trap.sendPackets(caster.team(), caster);
    }

    @Override
    public void buff(FightCastScope cast, BaseCastScope<Fighter, FightCell>.EffectScope effect) {
        throw new UnsupportedOperationException("Trap effect cannot be used as buff");
    }

    /**
     * Check if there is a trap on the given cell
     *
     * @param cell The cell to check
     * @return true if there is a trap on the cell
     */
    private boolean hasTrapOnCell(FightCell cell) {
        return fight.map().objects().anyMatch(o -> o.cell().equals(cell) && o instanceof Trap);
    }
}
