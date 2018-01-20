package fr.quatrevieux.araknemu.network.game.out.game;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.network.adapter.util.DummyChannel;
import fr.quatrevieux.araknemu.network.game.GameSession;
import org.apache.mina.core.session.DummySession;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.EnumSet;

import static org.junit.jupiter.api.Assertions.*;

class AddSpritesTest extends GameBaseCase {
    @Test
    void generateWithPlayer() throws SQLException, ContainerException {
        ExplorationPlayer p1 = explorationPlayer();
        ExplorationPlayer p2 = new ExplorationPlayer(
            new GamePlayer(
                new GameAccount(
                    new Account(2),
                    container.get(AccountService.class),
                    1
                ),
                new Player(5, 2, 1, "Other", Race.CRA, Sex.MALE, new Colors(-1, -1, -1), 1, new DefaultCharacteristics(), new Position(10540, 210), EnumSet.allOf(ChannelType.class)),
                dataSet.refresh(new PlayerRace(Race.CRA)),
                new GameSession(new DummyChannel()),
                container.get(PlayerService.class)
            )
        );

        assertEquals(
            "GM|+279;0;0;1;Bob;1;10^100x100;0;;-1;-1;-1;;;;;;;;|+210;0;0;5;Other;9;90^100x100;0;;-1;-1;-1;;;;;;;;",
            new AddSprites(Arrays.asList(p1.sprite(), p2.sprite())).toString()
        );
    }
}
