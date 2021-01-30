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

package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.data.living.constraint.EntityConstraint;
import fr.quatrevieux.araknemu.data.living.constraint.player.PlayerConstraints;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.account.event.CharacterCreated;
import fr.quatrevieux.araknemu.game.account.event.CharacterCreationStarted;
import fr.quatrevieux.araknemu.game.account.event.PlayerDeleted;
import fr.quatrevieux.araknemu.game.account.exception.CharacterCreationException;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import fr.quatrevieux.araknemu.game.world.creature.accessory.EmptyAccessories;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for handle account characters
 */
final public class CharactersService {
    final private PlayerRepository repository;
    final private EntityConstraint<Player, PlayerConstraints.Error> constraint;
    final private PlayerRaceRepository playerRaceRepository;
    final private Dispatcher dispatcher;
    final private PlayerItemRepository itemRepository;

    public CharactersService(PlayerRepository repository, EntityConstraint<Player, PlayerConstraints.Error> constraint, PlayerRaceRepository playerRaceRepository, Dispatcher dispatcher, PlayerItemRepository itemRepository) {
        this.repository = repository;
        this.constraint = constraint;
        this.playerRaceRepository = playerRaceRepository;
        this.dispatcher = dispatcher;
        this.itemRepository = itemRepository;
    }

    /**
     * Create a new character
     *
     * @param character Character to create
     */
    public AccountCharacter create(AccountCharacter character) throws CharacterCreationException {
        if (!constraint.check(character.character())) {
            throw new CharacterCreationException(constraint.error());
        }

        // @todo configure into a listener
        character.character().setPosition(
            playerRaceRepository.get(character.character().race()).startPosition()
        );
        character.character().setSavedPosition(
            playerRaceRepository.get(character.character().race()).startPosition()
        );

        try {
            dispatcher.dispatch(new CharacterCreationStarted(character));

            character = new AccountCharacter(
                character.account(),
                repository.add(
                    character.character()
                )
            );

            dispatcher.dispatch(new CharacterCreated(character));

            return character;
        } catch (RepositoryException e) {
            throw new CharacterCreationException(e);
        }
    }

    /**
     * Get list of characters
     *
     * @param account The account
     */
    public List<AccountCharacter> list(GameAccount account) {
        final Map<Integer, List<PlayerItem>> items = itemRepository.forCharacterList(
            account.serverId(),
            account.id(),
            AccessoryType.slots()
        );

        return repository.findByAccount(account.id(), account.serverId())
            .stream()
            .map(player -> new AccountCharacter(
                account,
                player,
                items.containsKey(player.id())
                    ? new CharacterAccessories(items.get(player.id()))
                    : new EmptyAccessories()
            ))
            .collect(Collectors.toList())
        ;
    }

    /**
     * Get account character
     *
     * @param account The account
     * @param id The character id
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException When cannot found the character
     */
    public AccountCharacter get(GameAccount account, int id) {
        return new AccountCharacter(
            account,
            repository.getForGame(
                Player.forGame(id, account.id(), account.serverId())
            )
        );
    }

    /**
     * Delete the character
     */
    public void delete(AccountCharacter character) {
        repository.delete(character.character());

        dispatcher.dispatch(new PlayerDeleted(character));
    }
}
