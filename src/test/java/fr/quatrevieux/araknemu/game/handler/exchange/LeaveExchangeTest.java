package fr.quatrevieux.araknemu.game.handler.exchange;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.PlayerExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player.PlayerExchangeRequest;
import fr.quatrevieux.araknemu.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.in.exchange.LeaveExchangeRequest;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LeaveExchangeTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        other = makeOtherExplorationPlayer();

        other.join(player.map());
    }

    @Test
    void functionalOnRequest() throws Exception {
        player.interactions().start(new PlayerExchangeRequest(player, other));

        handlePacket(new LeaveExchangeRequest());

        requestStack.assertLast(new ExchangeLeaved(false));

        assertFalse(player.interactions().busy());
        assertFalse(other.interactions().busy());
    }

    @Test
    void functionalOnExchange() throws Exception {
        for (PlayerExchangeParty party : PlayerExchangeParty.make(player, other)) {
            party.start();
        }

        handlePacket(new LeaveExchangeRequest());

        requestStack.assertLast(new ExchangeLeaved(false));

        assertFalse(player.interactions().busy());
        assertFalse(other.interactions().busy());
    }

    @Test
    void functionalNotExploring() {
        session.setExploration(null);

        assertThrows(CloseImmediately.class, () -> handlePacket(new LeaveExchangeRequest()));
    }
}
