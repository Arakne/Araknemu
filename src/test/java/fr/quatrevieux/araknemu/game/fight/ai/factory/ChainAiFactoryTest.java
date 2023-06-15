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

package fr.quatrevieux.araknemu.game.fight.ai.factory;

import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ai.FighterAI;
import fr.quatrevieux.araknemu.game.fight.ai.action.ActionGenerator;
import fr.quatrevieux.araknemu.game.fight.ai.action.logic.NullGenerator;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayableFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

class ChainAiFactoryTest extends FightBaseCase {
    private PlayableFighter fighter;

    @BeforeEach
    @Override
    public void setUp() throws Exception {
        super.setUp();

        createFight();

        fighter = player.fighter();
    }

    @Test
    void createEmpty() {
        assertFalse(new ChainAiFactory().create(fighter).isPresent());
    }

    @Test
    void createNoneMatch() {
        assertFalse(new ChainAiFactory(fighter -> Optional.empty()).create(fighter).isPresent());
    }

    @Test
    void createSuccess() {
        final FighterAI ai = new FighterAI(fighter, fighter.fight(), NullGenerator.get());

        assertSame(ai, new ChainAiFactory(
            fighter -> Optional.empty(),
            fighter -> Optional.of(ai)
        ).create(fighter).get());
    }
}
