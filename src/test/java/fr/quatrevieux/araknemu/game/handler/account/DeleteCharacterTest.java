package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.account.AccountCharacter;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.in.account.DeleteCharacterRequest;
import fr.quatrevieux.araknemu.network.game.out.account.CharacterDeleted;
import fr.quatrevieux.araknemu.network.game.out.account.CharactersList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class DeleteCharacterTest extends GameBaseCase {
    private DeleteCharacter handler;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        handler = new DeleteCharacter(
            container.get(CharactersService.class),
            container.get(GameConfiguration.class).player()
        );

        login();

        dataSet.use(PlayerItem.class);
    }

    @Test
    void deletePlayerNotFound() {
        assertThrows(ErrorPacket.class, () -> handler.handle(session, new DeleteCharacterRequest(123, "")));
    }

    @Test
    void deleteBadAccount() throws ContainerException {
        Player player = dataSet.pushPlayer("test", 78, 2);

        assertThrows(ErrorPacket.class, () -> handler.handle(session, new DeleteCharacterRequest(player.id(), "")));
    }

    @Test
    void deleteBadServer() throws ContainerException {
        Player player = dataSet.pushPlayer("test", session.account().id(), 45);

        assertThrows(ErrorPacket.class, () -> handler.handle(session, new DeleteCharacterRequest(player.id(), "")));
    }

    @Test
    void deleteBadAnswer() throws ContainerException {
        Player player = dataSet.pushPlayer("test", session.account().id(), 2);

        player.setLevel(50);
        container.get(PlayerRepository.class).save(player);

        assertThrows(ErrorPacket.class, () -> handler.handle(session, new DeleteCharacterRequest(player.id(), "")));
    }

    @Test
    void deleteSuccessLowLevel() throws Exception {
        Player other = dataSet.pushPlayer("test", session.account().id(), 2);
        Player player = dataSet.pushPlayer("to_delete", session.account().id(), 2);

        handler.handle(session, new DeleteCharacterRequest(player.id(), ""));

        requestStack.assertAll(
            new CharacterDeleted(true),
            new CharactersList(
                session.account().remainingTime(),
                Collections.singletonList(
                    new AccountCharacter(
                        session.account(),
                        other
                    )
                )
            )
        );

        assertFalse(container.get(PlayerRepository.class).has(player));
    }

    @Test
    void deleteSuccessHighLevel() throws Exception {
        Player other = dataSet.pushPlayer("test", session.account().id(), 2);
        Player player = dataSet.pushPlayer("to_delete", session.account().id(), 2);

        player.setLevel(50);
        container.get(PlayerRepository.class).save(player);

        handler.handle(session, new DeleteCharacterRequest(player.id(), "my response"));

        requestStack.assertAll(
            new CharacterDeleted(true),
            new CharactersList(
                session.account().remainingTime(),
                Collections.singletonList(
                    new AccountCharacter(
                        session.account(),
                        other
                    )
                )
            )
        );

        assertFalse(container.get(PlayerRepository.class).has(player));
    }
}
