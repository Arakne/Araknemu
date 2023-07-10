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
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import org.checkerframework.checker.index.qual.GTENegativeOne;
import org.checkerframework.checker.index.qual.NonNegative;

/**
 * Glyph object which is triggered at end of turn, created by {@link AddEndTurnGlyphHandler}
 */
final class EndTurnGlyph extends AbstractGlyph {
    /**
     * @param fight The current fight
     * @param cell The cell where the glyph is
     * @param caster The caster of the glyph
     * @param size The size of the glyph. 0 is for a single cell glyph
     * @param color The color of the glyph. Used by the client as layer for identifying the glyph
     * @param remainingTurns The remaining turns of the glyph. -1 for infinite.
     * @param spell The spell that is applied when a fighter is on the glyph at the start of his turn
     */
    public EndTurnGlyph(Fight fight, FightCell cell, Fighter caster, @NonNegative int size, int color, @GTENegativeOne int remainingTurns, Spell spell) {
        super(fight, cell, caster, size, color, remainingTurns, spell);
    }

    @Override
    public void onEndTurnInArea(Fighter fighter) {
        trigger(fighter);
    }
}
