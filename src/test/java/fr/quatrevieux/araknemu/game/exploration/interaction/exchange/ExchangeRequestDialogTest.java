package fr.quatrevieux.araknemu.game.exploration.interaction.exchange;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRequestDialogTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    private ExchangeRequestDialog dialog;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        other = makeOtherExplorationPlayer();

        other.join(player.map());

        PlayerExchangeRequest request = new PlayerExchangeRequest(player, other);

        dialog = new ExchangeRequestDialog(request);
        player.interactions().start(dialog);
        other.interactions().start(new TargetExchangeRequestDialog(request));
    }

    @Test
    void stop() {
        dialog.stop();

        requestStack.assertLast(new ExchangeLeaved(false));

        assertFalse(player.interactions().interacting());
        assertFalse(other.interactions().interacting());
    }

    @Test
    void leave() {
        dialog.leave();

        requestStack.assertLast(new ExchangeLeaved(false));

        assertFalse(player.interactions().interacting());
        assertFalse(other.interactions().interacting());
    }
}
