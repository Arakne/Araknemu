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

package fr.quatrevieux.araknemu.game.player.spell;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.game.listener.player.spell.SaveLearnedSpell;
import fr.quatrevieux.araknemu.game.listener.player.spell.SaveSpellPosition;
import fr.quatrevieux.araknemu.game.listener.player.spell.SaveUpgradedSpell;
import fr.quatrevieux.araknemu.game.listener.player.spell.SendAllSpellBoosts;
import fr.quatrevieux.araknemu.game.listener.player.spell.SendLearnedSpell;
import fr.quatrevieux.araknemu.game.listener.player.spell.SendSpellBoost;
import fr.quatrevieux.araknemu.game.listener.player.spell.SendSpellList;
import fr.quatrevieux.araknemu.game.listener.player.spell.SendUpgradedSpell;
import fr.quatrevieux.araknemu.game.listener.player.spell.SetDefaultPositionSpellBook;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import fr.quatrevieux.araknemu.game.spell.SpellLevels;
import fr.quatrevieux.araknemu.game.spell.SpellService;

/**
 * Service for handle player spells
 */
public final class SpellBookService implements EventsSubscriber {
    private final PlayerSpellRepository repository;
    private final SpellService service;
    private final PlayerRaceService playerRaceService;

    public SpellBookService(PlayerSpellRepository repository, SpellService service, PlayerRaceService playerRaceService) {
        this.repository = repository;
        this.service = service;
        this.playerRaceService = playerRaceService;
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new Listener<PlayerLoaded>() {
                @Override
                public void on(PlayerLoaded event) {
                    event.player().dispatcher().add(new SaveSpellPosition(repository));
                    event.player().dispatcher().add(new SaveLearnedSpell(repository));
                    event.player().dispatcher().add(new SaveUpgradedSpell(event.player(), repository));

                    event.player().dispatcher().add(new SendSpellList(event.player()));
                    event.player().dispatcher().add(new SendAllSpellBoosts(event.player()));
                    event.player().dispatcher().add(new SendLearnedSpell(event.player()));
                    event.player().dispatcher().add(new SendUpgradedSpell(event.player()));
                    event.player().dispatcher().add(new SendSpellBoost(event.player()));
                }

                @Override
                public Class<PlayerLoaded> event() {
                    return PlayerLoaded.class;
                }
            },
            new SetDefaultPositionSpellBook(playerRaceService, repository),
        };
    }

    /**
     * Load the spell book
     */
    public SpellBook load(Dispatcher dispatcher, Player player) {
        final SpellBook spellBook = new SpellBook(dispatcher, player);

        // Add player spells
        for (PlayerSpell spell : repository.byPlayer(player)) {
            spellBook.addEntry(spell, service.get(spell.spellId()));
        }

        // Add base race spells
        for (SpellLevels spell : playerRaceService.get(player.race()).spells()) {
            if (!spellBook.hasEntry(spell.id())) {
                spellBook.addEntry(
                    new PlayerSpell(player.id(), spell.id(), true),
                    spell
                );
            }
        }

        return spellBook;
    }
}
