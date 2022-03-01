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
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.player.spell.event.SpellUpgraded;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookEntry;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SaveUpgradedSpellTest extends GameBaseCase {
    private SaveUpgradedSpell listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SaveUpgradedSpell(
            gamePlayer(true),
            container.get(PlayerSpellRepository.class)
        );
    }

    @Test
    void onSpellUpgraded() throws ContainerException, SQLException, NoSuchFieldException, IllegalAccessException {
        PlayerSpell spell = dataSet.push(new PlayerSpell(1, 202, false, 2, 3));

        spell.setLevel(3);
        this.<Player>readField(gamePlayer(), "entity").setSpellPoints(5);

        SpellBookEntry entry = new SpellBookEntry(null, spell, container.get(SpellService.class).get(202));

        listener.on(new SpellUpgraded(entry));

        assertEquals(3, dataSet.refresh(spell).level());
        assertEquals(5, dataSet.refresh(new Player(1)).spellPoints());
    }
}