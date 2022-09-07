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
 * Copyright (c) 2017-2022 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterCharacteristicChanged;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellBoostChanged;
import fr.quatrevieux.araknemu.game.spell.boost.SpellsBoosts;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellBoost;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SendSpellBoostedTest extends FightBaseCase {
    private SendSpellBoosted listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        createFight();

        listener = new SendSpellBoosted(player.fighter());
    }

    @Test
    void onSpellBoostChanged() {
        listener.on(new SpellBoostChanged(3, SpellsBoosts.Modifier.DAMAGE, 10));

        requestStack.assertLast(new SpellBoost(3, SpellsBoosts.Modifier.DAMAGE, 10));
    }

    @Test
    void onSpellBoostChangedWithBaseSpellBoost() {
        player.properties().spells().boosts().boost(3, SpellsBoosts.Modifier.DAMAGE, 5);
        listener.on(new SpellBoostChanged(3, SpellsBoosts.Modifier.DAMAGE, 10));

        requestStack.assertLast(new SpellBoost(3, SpellsBoosts.Modifier.DAMAGE, 15));
    }

    @Test
    void functional() {
        player.fighter().spells().boost(3, SpellsBoosts.Modifier.DAMAGE, 10);
        requestStack.assertLast(new SpellBoost(3, SpellsBoosts.Modifier.DAMAGE, 10));

        player.fighter().spells().boost(3, SpellsBoosts.Modifier.DAMAGE, 5);
        requestStack.assertLast(new SpellBoost(3, SpellsBoosts.Modifier.DAMAGE, 15));

        player.fighter().spells().boost(3, SpellsBoosts.Modifier.DAMAGE, -10);
        requestStack.assertLast(new SpellBoost(3, SpellsBoosts.Modifier.DAMAGE, 5));
    }
}
