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
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.fight.castable.spell;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *
 */
class LaunchedSpellsTest extends FightBaseCase {
    private Fight fight;
    private LaunchedSpells launchedSpells;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        launchedSpells = new LaunchedSpells();
    }

    @Test
    void validNotLaunched() {
        Spell spell = Mockito.mock(Spell.class);

        launchedSpells.valid(spell, fight.map().get(123));
    }

    @Test
    void validAlreadyLaunchedWithoutConstraints() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.launchDelay()).thenReturn(0);
        Mockito.when(constraints.launchPerTarget()).thenReturn(0);
        Mockito.when(constraints.launchPerTurn()).thenReturn(0);

        launchedSpells.push(spell, fight.map().get(123));

        assertTrue(launchedSpells.valid(spell, fight.map().get(123)));
    }

    @Test
    void validWithCooldown() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.launchDelay()).thenReturn(10);
        Mockito.when(constraints.launchPerTarget()).thenReturn(0);
        Mockito.when(constraints.launchPerTurn()).thenReturn(0);

        launchedSpells.push(spell, fight.map().get(123));

        assertFalse(launchedSpells.valid(spell, fight.map().get(123)));
    }

    @Test
    void validWithMaxPerTurn() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.launchDelay()).thenReturn(0);
        Mockito.when(constraints.launchPerTarget()).thenReturn(0);
        Mockito.when(constraints.launchPerTurn()).thenReturn(2);

        assertTrue(launchedSpells.valid(spell, fight.map().get(123)));

        launchedSpells.push(spell, fight.map().get(123));
        assertTrue(launchedSpells.valid(spell, fight.map().get(123)));

        launchedSpells.push(spell, fight.map().get(123));
        assertFalse(launchedSpells.valid(spell, fight.map().get(123)));
    }

    @Test
    void validWithMaxPerTarget() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.launchDelay()).thenReturn(0);
        Mockito.when(constraints.launchPerTarget()).thenReturn(2);
        Mockito.when(constraints.launchPerTurn()).thenReturn(0);

        assertTrue(launchedSpells.valid(spell, other.fighter().cell()));

        launchedSpells.push(spell, other.fighter().cell());
        assertTrue(launchedSpells.valid(spell, other.fighter().cell()));

        launchedSpells.push(spell, other.fighter().cell());
        assertFalse(launchedSpells.valid(spell, other.fighter().cell()));
        assertTrue(launchedSpells.valid(spell, fight.map().get(123)));
        assertTrue(launchedSpells.valid(spell, player.fighter().cell()));
    }

    @Test
    void refreshWillRemoveSpellsIfCooldownReachZero() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.launchDelay()).thenReturn(2);
        Mockito.when(constraints.launchPerTarget()).thenReturn(0);
        Mockito.when(constraints.launchPerTurn()).thenReturn(0);

        launchedSpells.push(spell, fight.map().get(123));
        assertFalse(launchedSpells.valid(spell, fight.map().get(123)));

        launchedSpells.refresh();
        assertFalse(launchedSpells.valid(spell, fight.map().get(123)));

        launchedSpells.refresh();
        assertTrue(launchedSpells.valid(spell, fight.map().get(123)));
    }
}
