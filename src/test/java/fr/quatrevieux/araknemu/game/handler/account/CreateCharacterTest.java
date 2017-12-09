package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountCharacter;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.network.game.in.account.AddCharacterRequest;
import fr.quatrevieux.araknemu.network.game.out.account.CharacterCreated;
import fr.quatrevieux.araknemu.network.game.out.account.CharactersList;
import fr.quatrevieux.araknemu.network.realm.out.ServerList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CreateCharacterTest extends GameBaseCase {
    private CreateCharacter handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new CreateCharacter(
            container.get(CharactersService.class)
        );

        dataSet.use(Player.class);
        login();
    }

    @Test
    void success() throws Exception {
        handler.handle(session, new AddCharacterRequest(
            "Bob",
            Race.ECAFLIP,
            Sex.MALE,
            new Colors(123, 456, 789)
        ));

        Player player = dataSet.repository(Player.class).get(new Player(1));

        assertEquals(1, player.accountId());
        assertEquals(1, player.serverId());
        assertEquals("Bob", player.name());
        assertEquals(Race.ECAFLIP, player.race());
        assertEquals(Sex.MALE, player.sex());
        assertArrayEquals(new int[]{123, 456, 789}, player.colors().toArray());

        requestStack.assertAll(
            new CharacterCreated(),
            new CharactersList(ServerList.ONE_YEAR, Collections.singleton(
                new AccountCharacter(
                    session.account(),
                    player
                )
            ))
        );
    }
}
