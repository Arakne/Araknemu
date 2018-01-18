package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountCharacter;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.network.game.out.account.CharactersList;
import fr.quatrevieux.araknemu.network.realm.out.ServerList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ListCharactersTest extends GameBaseCase {
    private ListCharacters handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new ListCharacters(
            container.get(CharactersService.class)
        );

        login();
        dataSet.use(Player.class);
    }

    @Test
    void noCharacters() throws Exception {
        handler.handle(session, new AskCharacterList(false));

        requestStack.assertLast(new CharactersList(ServerList.ONE_YEAR, Collections.EMPTY_LIST));
    }

    @Test
    void withCharacters() throws Exception {
        Player first = dataSet.push(Player.forCreation(1, 2, "first", Race.ECAFLIP, Sex.MALE, new Colors(-1, -1, -1)));
        Player second = dataSet.push(Player.forCreation(1, 2, "second", Race.FECA, Sex.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(2, 2, "not_my_account", Race.FECA, Sex.MALE, new Colors(-1, -1, -1)));
        dataSet.push(Player.forCreation(1, 1, "not_my_server", Race.FECA, Sex.MALE, new Colors(-1, -1, -1)));

        handler.handle(session, new AskCharacterList(false));

        requestStack.assertLast(new CharactersList(ServerList.ONE_YEAR, Arrays.asList(
            new AccountCharacter(
                session.account(),
                first
            ),
            new AccountCharacter(
                session.account(),
                second
            )
        )));
    }
}
