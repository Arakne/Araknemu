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

import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.constraint.player.PlayerConstraints;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerSpell;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.event.CharacterCreated;
import fr.quatrevieux.araknemu.game.account.event.CharacterCreationStarted;
import fr.quatrevieux.araknemu.game.account.event.PlayerDeleted;
import fr.quatrevieux.araknemu.game.account.exception.CharacterCreationException;
import fr.quatrevieux.araknemu.game.world.creature.accessory.AccessoryType;
import fr.quatrevieux.araknemu.game.world.creature.accessory.EmptyAccessories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CharactersServiceTest extends GameBaseCase {
    private CharactersService service;
    private PlayerRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new CharactersService(
            repository = container.get(PlayerRepository.class),
            container.get(PlayerConstraints.class),
            container.get(PlayerRaceRepository.class),
            container.get(Dispatcher.class),
            container.get(PlayerItemRepository.class)
        );

        dataSet
            .pushRaces()
            .pushSpells()
            .use(Player.class)
            .use(PlayerItem.class)
            .use(PlayerSpell.class)
        ;
    }

    @Test
    void list() throws ContainerException {
        Player first = dataSet.push(Player.forCreation(1, 1, "first", Race.ECAFLIP, Gender.MALE, new Colors(-1, -1, -1)));
        Player second = dataSet.push(Player.forCreation(1, 1, "second", Race.FECA, Gender.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(2, 1, "not_my_account", Race.FECA, Gender.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 2, "not_my_server", Race.FECA, Gender.MALE, new Colors(-1, -1, -1)));

        GameAccount account = new GameAccount(
            new Account(1),
            container.get(AccountService.class),
            1
        );

        List<AccountCharacter> characters = service.list(account);

        assertEquals(2, characters.size());

        assertEquals(first.id(), characters.get(0).id());
        assertEquals(second.id(), characters.get(1).id());

        assertSame(account, characters.get(0).account());
        assertSame(account, characters.get(1).account());
    }

    @Test
    void listWithItems() throws ContainerException, SQLException {
        Player first = dataSet.push(Player.forCreation(1, 1, "first", Race.ECAFLIP, Gender.MALE, new Colors(-1, -1, -1)));
        Player second = dataSet.push(Player.forCreation(1, 1, "second", Race.FECA, Gender.MALE, new Colors(-1, -1, -1)));

        dataSet.push(new PlayerItem(first.id(), 1, 12, null, 1, 1));
        dataSet.push(new PlayerItem(first.id(), 2, 23, null, 1, 6));

        GameAccount account = new GameAccount(
            new Account(1),
            container.get(AccountService.class),
            1
        );

        List<AccountCharacter> characters = service.list(account);

        assertEquals(2, characters.size());

        assertEquals(first.id(), characters.get(0).id());
        assertEquals(second.id(), characters.get(1).id());

        assertInstanceOf(CharacterAccessories.class, characters.get(0).spriteInfo().accessories());
        assertEquals(12, characters.get(0).spriteInfo().accessories().get(AccessoryType.WEAPON).appearance());
        assertEquals(23, characters.get(0).spriteInfo().accessories().get(AccessoryType.HELMET).appearance());
        assertInstanceOf(EmptyAccessories.class, characters.get(1).spriteInfo().accessories());
    }

    @Test
    void createSuccess() throws ContainerException, CharacterCreationException {
        ListenerAggregate dispatcher = container.get(ListenerAggregate.class);

        AtomicReference<AccountCharacter> reference = new AtomicReference<>();
        dispatcher.add(
            new Listener<CharacterCreationStarted>() {
                @Override
                public void on(CharacterCreationStarted event) {
                    reference.set(event.character());
                }

                @Override
                public Class<CharacterCreationStarted> event() {
                    return CharacterCreationStarted.class;
                }
            }
        );

        AtomicReference<AccountCharacter> ref2 = new AtomicReference<>();
        dispatcher.add(
            new Listener<CharacterCreated>() {
                @Override
                public void on(CharacterCreated event) {
                    ref2.set(event.character());
                }

                @Override
                public Class<CharacterCreated> event() {
                    return CharacterCreated.class;
                }
            }
        );

        GameAccount account = new GameAccount(
            new Account(5, "test", "test", "test"),
            container.get(AccountService.class),
            1
        );

        AccountCharacter created = service.create(
            new AccountCharacter(
                account,
                Player.forCreation(5, 1, "Bob", Race.ECAFLIP, Gender.MALE, new Colors(123, 456, 789))
            )
        );

        assertSame(account, created.account());

        Player db = repository.get(created.character());

        assertEquals(5, db.accountId());
        assertEquals(1, db.serverId());
        assertEquals("Bob", db.name());
        assertEquals(Race.ECAFLIP, db.race());
        assertEquals(Gender.MALE, db.gender());
        assertArrayEquals(new int[]{123, 456, 789}, db.colors().toArray());
        assertEquals(new Position(10300, 320), db.position());
        assertEquals(new Position(10300, 320), db.savedPosition());
        assertEquals(0, db.kamas());
        assertNotNull(reference.get());
        assertEquals("Bob", reference.get().character().name());
        assertSame(created, ref2.get());
    }

    @Test
    void createError() throws ContainerException, CharacterCreationException {
        GameAccount account = new GameAccount(
            new Account(5),
            container.get(AccountService.class),
            1
        );

        assertThrows(CharacterCreationException.class, () -> service.create(
            new AccountCharacter(
                account,
                Player.forCreation(5, 1, "--invalid-name", Race.ECAFLIP, Gender.MALE, new Colors(123, 456, 789))
            )
        ));

        assertTrue(service.list(account).isEmpty());
    }

    @Test
    void getNotFound() {
        assertThrows(EntityNotFoundException.class, () -> service.get(
            new GameAccount(
                new Account(1),
                container.get(AccountService.class),
                2
            ),
            12
        ));
    }

    @Test
    void getBadServer() throws ContainerException {
        Player player = dataSet.pushPlayer("test", 5, 1);

        assertThrows(EntityNotFoundException.class, () -> service.get(
            new GameAccount(
                new Account(5),
                container.get(AccountService.class),
                2
            ),
            player.id()
        ));
    }

    @Test
    void getBadAccount() throws ContainerException {
        Player player = dataSet.pushPlayer("test", 5, 2);

        assertThrows(EntityNotFoundException.class, () -> service.get(
            new GameAccount(
                new Account(2),
                container.get(AccountService.class),
                2
            ),
            player.id()
        ));
    }

    @Test
    void getSuccess() throws ContainerException {
        Player player = dataSet.pushPlayer("test", 5, 2);

        AccountCharacter character = service.get(
            new GameAccount(
                new Account(5),
                container.get(AccountService.class),
                2
            ),
            player.id()
        );

        assertEquals("test", character.character().name());
    }

    @Test
    void deleteNotFound() {
        assertThrows(
            EntityNotFoundException.class,
            () -> service.delete(
                new AccountCharacter(
                    new GameAccount(
                        new Account(5),
                        container.get(AccountService.class),
                        2
                    ),
                    new Player(5)
                )
            )
        );
    }

    @Test
    void deleteSuccess() throws Exception {
        Player player = dataSet.pushPlayer("test", 5, 2);

        AccountCharacter character = service.get(
            new GameAccount(
                new Account(5),
                container.get(AccountService.class),
                2
            ),
            player.id()
        );

        AtomicReference<AccountCharacter> ref = new AtomicReference<>();
        container.get(ListenerAggregate.class).add(PlayerDeleted.class, evt -> ref.set(evt.character()));

        service.delete(character);

        assertSame(character, ref.get());
        assertFalse(container.get(PlayerRepository.class).has(player));
    }
}
