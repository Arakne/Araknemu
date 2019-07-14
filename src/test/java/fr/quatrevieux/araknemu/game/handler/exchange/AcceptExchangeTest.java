package fr.quatrevieux.araknemu.game.handler.exchange;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.PlayerExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeDialog;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.in.exchange.ExchangeReady;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeAccepted;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AcceptExchangeTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        other = makeOtherExplorationPlayer();

        other.join(player.map());

        for (PlayerExchangeParty party : PlayerExchangeParty.make(player, other)) {
            party.start();
        }
    }

    @Test
    void acceptShouldToggleState() throws Exception {
        handlePacket(new ExchangeReady());
        requestStack.assertLast(new ExchangeAccepted(true, player));

        handlePacket(new ExchangeReady());
        requestStack.assertLast(new ExchangeAccepted(false, player));
    }

    @Test
    void acceptForProcessExchange() throws Exception {
        player.interactions().get(ExchangeDialog.class).kamas(1000);
        other.interactions().get(ExchangeDialog.class).accept();

        handlePacket(new ExchangeReady());

        requestStack.assertLast(new ExchangeLeaved(true));
        assertEquals(1000, other.inventory().kamas());
        assertEquals(14225, player.inventory().kamas());
    }

    @Test
    void notExploring() {
        session.setExploration(null);

        assertThrows(CloseImmediately.class, () -> handlePacket(new ExchangeReady()));
    }
}
