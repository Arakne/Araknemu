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

package fr.quatrevieux.araknemu.game.fight.castable.effect.hook;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.target.SpellEffectTarget;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class HookedSpellEffectTest extends GameBaseCase {
    @Test
    void values() throws SQLException {
        dataSet.pushFunctionalSpells();

        Spell spell = container.get(SpellService.class).get(157).level(5);
        SpellEffect effect = spell.effects().get(0);
        HookedSpellEffect hookedSpellEffect = new HookedSpellEffect(effect);

        assertEquals(effect.effect(), hookedSpellEffect.effect());
        assertEquals(effect.min(), hookedSpellEffect.min());
        assertEquals(effect.max(), hookedSpellEffect.max());
        assertEquals(effect.special(), hookedSpellEffect.special());
        assertEquals(effect.probability(), hookedSpellEffect.probability());
        assertEquals(effect.text(), hookedSpellEffect.text());
        assertEquals(effect.trap(), hookedSpellEffect.trap());
        assertEquals(effect.duration(), hookedSpellEffect.duration());
        assertEquals(effect.boost(), hookedSpellEffect.boost());
        assertInstanceOf(CellArea.class, hookedSpellEffect.area());
        assertSame(SpellEffectTarget.DEFAULT, hookedSpellEffect.target());
    }
}
