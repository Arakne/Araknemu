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
import fr.quatrevieux.araknemu.game.fight.castable.FightCastScope;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.BattlefieldObject;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.network.game.out.fight.action.ActionEffect;
import fr.quatrevieux.araknemu.network.game.out.fight.battlefield.RemoveZone;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Base glyph object.
 * Implementations must call {@link #trigger(Fighter)} on the hook method corresponding to the glyph type (start or end turn)
 */
abstract class AbstractGlyph implements BattlefieldObject {
    private final Fight fight;
    private final FightCell cell;
    private final Fighter caster;
    private final @NonNegative int size;
    private final int color;
    private final Spell spell;
    private @GTENegativeOne int remainingTurns;

    /**
     * @param fight The current fight
     * @param cell The cell where the glyph is
     * @param caster The caster of the glyph
     * @param size The size of the glyph. 0 is for a single cell glyph
     * @param color The color of the glyph. Used by the client as layer for identifying the glyph
     * @param remainingTurns The remaining turns of the glyph. -1 for infinite.
     * @param spell The spell that is applied when a fighter is on the glyph at the start of his turn
     */
    public AbstractGlyph(Fight fight, FightCell cell, Fighter caster, @NonNegative int size, int color, @GTENegativeOne int remainingTurns, Spell spell) {
        this.fight = fight;
        this.cell = cell;
        this.caster = caster;
        this.size = size;
        this.color = color;
        this.remainingTurns = remainingTurns;
        this.spell = spell;
    }

    @Override
    public final FightCell cell() {
        return cell;
    }

    @Override
    public final Fighter caster() {
        return caster;
    }

    @Override
    public final @NonNegative int size() {
        return size;
    }

    @Override
    public final int color() {
        return color;
    }

    @Override
    public final boolean refresh() {
        if (remainingTurns == -1) {
            return true;
        }

        if (remainingTurns == 0) {
            return false;
        }

        return --remainingTurns > 0;
    }

    @Override
    public final void disappear() {
        fight.send(new RemoveZone(this));
    }

    /**
     * Trigger the glyph effects on the given fighter
     */
    protected final void trigger(Fighter fighter) {
        final FightCastScope castScope = FightCastScope.fromCell(spell, caster, cell, fighter.cell(), spell.effects());

        fight.send(ActionEffect.glyphTriggered(caster, fighter, cell, spell));
        fight.effects().apply(castScope);
    }
}
