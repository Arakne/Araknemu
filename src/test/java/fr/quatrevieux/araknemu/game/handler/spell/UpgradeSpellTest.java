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

package fr.quatrevieux.araknemu.game.handler.spell;

import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;
import fr.quatrevieux.araknemu.core.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.spell.SpellUpgrade;
import fr.quatrevieux.araknemu.network.game.out.account.Stats;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellUpgradeError;
import fr.quatrevieux.araknemu.network.game.out.spell.UpdateSpell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class UpgradeSpellTest extends FightBaseCase {
    private UpgradeSpell handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new UpgradeSpell();

        gamePlayer(true);

        requestStack.clear();
    }

    @Test
    void upgradeSpellNotFound() throws Exception {
        try {
            handler.handle(session, new SpellUpgrade(-1));

            fail("ErrorPacket should be thrown");
        } catch (ErrorPacket e) {
            assertInstanceOf(SpellUpgradeError.class, e.packet());
        }
    }

    @Test
    void upgradeNotEnoughPoints() throws Exception {
        this.<Player>readField(gamePlayer(), "entity").setSpellPoints(0);

        try {
            handler.handle(session, new SpellUpgrade(3));

            fail("ErrorPacket should be thrown");
        } catch (ErrorPacket e) {
            assertInstanceOf(SpellUpgradeError.class, e.packet());
        }
    }

    @Test
    void upgradeSuccess() throws Exception {
        this.<Player>readField(gamePlayer(), "entity").setSpellPoints(5);

        handler.handle(session, new SpellUpgrade(3));

        SpellBookEntry entry = gamePlayer().properties().spells().entry(3);
        assertEquals(4, gamePlayer().properties().spells().upgradePoints());
        assertEquals(2, entry.spell().level());

        requestStack.assertAll(
            new UpdateSpell(entry),
            new Stats(gamePlayer().properties())
        );

        assertEquals(4, dataSet.refresh(new Player(1)).spellPoints());
        assertEquals(2, dataSet.refresh(entry.entity()).level());
    }

    @Test
    void functionalErrorOnActiveFight() throws Exception {
        this.<Player>readField(gamePlayer(), "entity").setSpellPoints(5);

        Fight fight = createFight();
        fight.start();

        assertErrorPacket(Error.cantDoDuringFight(), () -> handlePacket(new SpellUpgrade(3)));
    }

    @Test
    void functionalSuccess() throws Exception {
        this.<Player>readField(gamePlayer(), "entity").setSpellPoints(5);

        handlePacket(new SpellUpgrade(3));

        SpellBookEntry entry = gamePlayer().properties().spells().entry(3);
        assertEquals(4, gamePlayer().properties().spells().upgradePoints());
        assertEquals(2, entry.spell().level());

        requestStack.assertAll(
            new UpdateSpell(entry),
            new Stats(gamePlayer().properties())
        );
    }
}
