package fr.quatrevieux.araknemu.network.game.out.account;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CharacterSelectedTest extends GameBaseCase {
    @Test
    void generate() throws ContainerException {
        GamePlayer player = new GamePlayer(
            new GameAccount(
                new Account(5),
                container.get(AccountService.class),
                2
            ),
            new Player(123, 5, 2, "Bob", Race.FECA, Sex.MALE, new Colors(123, 456, 789), 23),
            session
        );

        assertEquals("ASK123|Bob|23||0|10|7b|1c8|315|", new CharacterSelected(player).toString());
    }
}
