package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.exception.CharacterCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CharactersServiceTest extends GameBaseCase {
    private CharactersService service;
    private PlayerRepository repository;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        service = new CharactersService(
            repository = container.get(PlayerRepository.class)
        );

        dataSet.use(Player.class);
    }

    @Test
    void list() throws ContainerException {
        Player first = dataSet.push(Player.forCreation(1, 1, "first", Race.ECAFLIP, Sex.MALE, new Colors(-1, -1, -1)));
        Player second = dataSet.push(Player.forCreation(1, 1, "second", Race.FECA, Sex.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(2, 1, "not_my_account", Race.FECA, Sex.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 2, "not_my_server", Race.FECA, Sex.MALE, new Colors(-1, -1, -1)));

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
    void createSuccess() throws ContainerException, CharacterCreationException {
        GameAccount account = new GameAccount(
            new Account(5),
            container.get(AccountService.class),
            1
        );

        AccountCharacter created = service.create(
            new AccountCharacter(
                account,
                Player.forCreation(5, 1, "Bob", Race.ECAFLIP, Sex.MALE, new Colors(123, 456, 789))
            )
        );

        assertSame(account, created.account());

        Player db = repository.get(created.character());

        assertEquals(5, db.accountId());
        assertEquals(1, db.serverId());
        assertEquals("Bob", db.name());
        assertEquals(Race.ECAFLIP, db.race());
        assertEquals(Sex.MALE, db.sex());
        assertArrayEquals(new int[]{123, 456, 789}, db.colors().toArray());
    }
}
