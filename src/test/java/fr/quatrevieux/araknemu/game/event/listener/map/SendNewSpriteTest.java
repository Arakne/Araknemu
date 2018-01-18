package fr.quatrevieux.araknemu.game.event.listener.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.account.Account;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.AccountService;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.event.exploration.NewSpriteOnMap;
import fr.quatrevieux.araknemu.game.event.listener.map.SendNewSprite;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.game.player.PlayerService;
import fr.quatrevieux.araknemu.game.player.PlayerSprite;
import fr.quatrevieux.araknemu.game.world.creature.Sprite;
import fr.quatrevieux.araknemu.network.adapter.util.DummyChannel;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import org.apache.mina.core.session.DummySession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;

class SendNewSpriteTest extends GameBaseCase {
    private SendNewSprite listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        listener = new SendNewSprite(
            explorationPlayer().map()
        );
    }

    @Test
    void onSelfSprite() throws SQLException, ContainerException {
        requestStack.clear();

        listener.on(
            new NewSpriteOnMap(
                new PlayerSprite(gamePlayer())
            )
        );

        requestStack.assertEmpty();
    }

    @Test
    void onOtherSprite() throws ContainerException {
        Sprite sprite = new PlayerSprite(
            new GamePlayer(
                new GameAccount(
                    new Account(2),
                    container.get(AccountService.class),
                    1
                ),
                new Player(5, 2, 1, "Other", Race.CRA, Sex.MALE, new Colors(-1, -1, -1)),
                dataSet.refresh(new PlayerRace(Race.CRA)),
                new GameSession(new DummyChannel()),
                container.get(PlayerService.class)
            )
        );

        listener.on(
            new NewSpriteOnMap(sprite)
        );

        requestStack.assertLast(
            new AddSprites(
                Collections.singleton(sprite)
            )
        );
    }
}
