package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountCharacter;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.CharacterAccessories;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CharactersListTest extends GameBaseCase {
    @Test
    void generateNoCharacters() {
        assertEquals("ALK123|0", new CharactersList(123, Collections.EMPTY_LIST).toString());
    }

    @Test
    void generateWithOneCharacter() throws ContainerException {
        assertEquals(
            "ALK123|1|5;name;15;90;-1;33;2b;;;1;;;",
            new CharactersList(
                123,
                Collections.singleton(
                    new AccountCharacter(
                        new GameAccount(
                            new Account(12),
                            container.get(AccountService.class),
                            1
                        ),
                        new Player(5, 12, 1, "name", Race.CRA, Sex.MALE, new Colors(-1, 51, 43), 15, null)
                    )
                )
            ).toString()
        );
    }

    @Test
    void generateWithAccessories() throws ContainerException {
        assertEquals(
            "ALK123|1|5;name;15;90;-1;33;2b;,c,17,,;;1;;;",
            new CharactersList(
                123,
                Collections.singleton(
                    new AccountCharacter(
                        new GameAccount(
                            new Account(12),
                            container.get(AccountService.class),
                            1
                        ),
                        new Player(5, 12, 1, "name", Race.CRA, Sex.MALE, new Colors(-1, 51, 43), 15, null),
                        new CharacterAccessories(
                            Arrays.asList(
                                new PlayerItem(1, 1, 12, null, 1, 6),
                                new PlayerItem(1, 1, 23, null, 1, 7)
                            )
                        )
                    )
                )
            ).toString()
        );
    }
}
