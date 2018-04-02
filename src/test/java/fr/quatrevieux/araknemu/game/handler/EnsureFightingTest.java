package fr.quatrevieux.araknemu.game.handler;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayerFighter;
import fr.quatrevieux.araknemu.game.handler.fight.ChangeFighterStartPlace;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.in.account.AskCharacterList;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterChangePlace;
import fr.quatrevieux.araknemu.network.in.Packet;
import fr.quatrevieux.araknemu.network.in.PacketHandler;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EnsureFightingTest extends GameBaseCase {
    @Test
    void handleNotFighting() {
        PacketHandler inner = Mockito.mock(PacketHandler.class);
        EnsureFighting handler = new EnsureFighting<>(inner);

        assertThrows(CloseImmediately.class, () -> handler.handle(session, Mockito.mock(Packet.class)));
    }

    @Test
    void handleSuccess() throws Exception {
        PacketHandler inner = Mockito.mock(PacketHandler.class);
        EnsureFighting handler = new EnsureFighting<>(inner);
        gamePlayer(true).attachFighter(new PlayerFighter(gamePlayer()));

        Packet packet = new AskCharacterList(false);

        handler.handle(session, packet);

        Mockito.verify(inner).handle(session, packet);
    }

    @Test
    void packet() {
        assertEquals(
            FighterChangePlace.class,
            new EnsureFighting<>(new ChangeFighterStartPlace()).packet()
        );
    }
}