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

package fr.quatrevieux.araknemu.game.fight.ai.util;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.action.DummyGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.NullGenerator;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.turn.action.cast.Cast;
import fr.quatrevieux.araknemu.game.spell.Spell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpellCasterTest extends FightBaseCase {
    private Fight fight;
    private SpellCaster caster;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        fighter = player.fighter();

        FighterAI ai = new FighterAI(fighter, fight, new DummyGenerator());
        fight.nextState();
        fight.turnList().start();
        ai.start(fight.turnList().current().get());

        caster = new SpellCaster(ai);
    }

    @Test
    void validate() {
        Spell spell = fighter.spells().get(3);

        assertFalse(caster.validate(spell, fight.map().get(5)));
        assertTrue(caster.validate(spell, fight.map().get(210)));
    }

    @Test
    void create() {
        Spell spell = fighter.spells().get(3);

        Cast cast = (Cast) caster.create(spell, fight.map().get(210));

        assertSame(spell, cast.spell());
        assertSame(fight.map().get(210), cast.target());
        assertSame(fighter, cast.caster());
    }
}
