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

import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.account.event.CharacterCreated;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import fr.quatrevieux.araknemu.game.spell.SpellLevels;

/**
 * Set the default positions for start spells
 */
public final class SetDefaultPositionSpellBook implements Listener<CharacterCreated> {
    private final PlayerRaceService service;
    private final PlayerSpellRepository repository;

    public SetDefaultPositionSpellBook(PlayerRaceService service, PlayerSpellRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @Override
    public void on(CharacterCreated event) {
        final Player player = event.character().character();

        int position = 1;

        for (SpellLevels spell : service.get(player.race()).spells()) {
            if (spell.level(1).minPlayerLevel() > player.level() || position > 63) {
                break;
            }

            repository.add(
                new PlayerSpell(player.id(), spell.id(), true, 1, position++)
            );
        }
    }

    @Override
    public Class<CharacterCreated> event() {
        return CharacterCreated.class;
    }
}
