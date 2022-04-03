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

package fr.quatrevieux.araknemu.game.item.effect.use;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.item.effect.UseEffect;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LearnSpellEffectTest extends GameBaseCase {
    private LearnSpellEffect effect;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushSpells();

        effect = new LearnSpellEffect(
            container.get(SpellService.class)
        );

        gamePlayer(true);
        requestStack.clear();
    }

    @Test
    void checkAlreadyLearned() throws SQLException, ContainerException {
        assertFalse(
            effect.check(
                new UseEffect(null, Effect.LEARN_SPELL, new int[] {0, 0, 3}),
                explorationPlayer()
            )
        );

        requestStack.assertLast(
            Error.cantLearnSpell(3)
        );
    }

    @Test
    void checkTooHighLevelSpell() throws SQLException, ContainerException {
        dataSet.pushHighLevelSpells();

        assertFalse(
            effect.check(
                new UseEffect(null, Effect.LEARN_SPELL, new int[] {0, 0, 1908}),
                explorationPlayer()
            )
        );

        requestStack.assertLast(
            Error.cantLearnSpell(1908)
        );
    }

    @Test
    void checkOk() throws SQLException, ContainerException {
        assertTrue(
            effect.check(
                new UseEffect(null, Effect.LEARN_SPELL, new int[] {0, 0, 202}),
                explorationPlayer()
            )
        );
    }

    @Test
    void checkTarget() throws SQLException {
        assertFalse(
            effect.checkTarget(
                new UseEffect(null, Effect.LEARN_SPELL, new int[] {0, 0, 202}),
                explorationPlayer(),
                null,
                null
            )
        );
    }

    @Test
    void apply() throws SQLException, ContainerException {
        effect.apply(
            new UseEffect(null, Effect.LEARN_SPELL, new int[] {0, 0, 202}),
            explorationPlayer()
        );

        assertTrue(gamePlayer().properties().spells().has(202));
        assertEquals(1, gamePlayer().properties().spells().entry(202).spell().level());
        assertEquals(63, gamePlayer().properties().spells().entry(202).position());
        assertFalse(gamePlayer().properties().spells().entry(202).classSpell());
    }
}
