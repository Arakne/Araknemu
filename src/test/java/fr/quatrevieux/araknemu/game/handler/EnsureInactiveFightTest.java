package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.account.CharactersService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.handler.account.ListCharacters;
import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.network.game.out.info.Error;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EnsureInactiveFightTest extends FightBaseCase {
    @Test
    void handleExploring() throws Exception {
        PacketHandler inner = Mockito.mock(PacketHandler.class);
        EnsureInactiveFight handler = new EnsureInactiveFight<>(inner);
        explorationPlayer();

        Packet packet = new AskCharacterList(false);

        handler.handle(session, packet);

        Mockito.verify(inner).handle(session, packet);
    }

    @Test
    void handleFightingNotActive() throws Exception {
        createFight();

        PacketHandler inner = Mockito.mock(PacketHandler.class);
        EnsureInactiveFight handler = new EnsureInactiveFight<>(inner);

        Packet packet = new AskCharacterList(false);

        handler.handle(session, packet);

        Mockito.verify(inner).handle(session, packet);
    }

    @Test
    void handleFightingActive() throws Exception {
        Fight fight = createFight();
        fight.start();

        PacketHandler inner = Mockito.mock(PacketHandler.class);
        EnsureInactiveFight handler = new EnsureInactiveFight<>(inner);

        Packet packet = new AskCharacterList(false);

        assertErrorPacket(Error.cantDoDuringFight(), () -> handler.handle(session, packet));

        Mockito.verify(inner, Mockito.never()).handle(session, packet);
    }

    @Test
    void packet() throws ContainerException {
        assertEquals(
            AskCharacterList.class,
            new EnsureInactiveFight<>(new ListCharacters(
                container.get(CharactersService.class)
            )).packet()
        );
    }
}