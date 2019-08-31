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

package fr.quatrevieux.araknemu.game.listener.player.spell;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.event.GameJoined;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellBoost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

class SendAllSpellBoostsTest extends GameBaseCase {
    private SendAllSpellBoosts listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendAllSpellBoosts(gamePlayer(true));

        requestStack.clear();
    }

    @Test
    void withoutBoosts() {
        listener.on(new GameJoined());

        requestStack.assertEmpty();
    }

    @Test
    void withBoosts() throws SQLException, ContainerException {
        gamePlayer().properties().spells().boosts().boost(3, SpellsBoosts.Modifier.DAMAGE, 15);
        gamePlayer().properties().spells().boosts().boost(3, SpellsBoosts.Modifier.RANGE, 3);
        gamePlayer().properties().spells().boosts().boost(6, SpellsBoosts.Modifier.AP_COST, 2);
        requestStack.clear();

        listener.on(new GameJoined());

        requestStack.assertAll(
            new SpellBoost(3, SpellsBoosts.Modifier.RANGE, 3),
            new SpellBoost(3, SpellsBoosts.Modifier.DAMAGE, 15),
            new SpellBoost(6, SpellsBoosts.Modifier.AP_COST, 2)
        );
    }
}
