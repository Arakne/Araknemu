package fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player.InitiatorExchangeRequestDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player.PlayerExchangeRequest;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player.TargetExchangeRequestDialog;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class TargetExchangeRequestDialogTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    private TargetExchangeRequestDialog dialog;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        other = makeOtherExplorationPlayer();

        other.join(player.map());

        PlayerExchangeRequest request = new PlayerExchangeRequest(other, player);

        dialog = new TargetExchangeRequestDialog(request.invitation(other, player));
        player.interactions().start(dialog);
        other.interactions().start(new InitiatorExchangeRequestDialog(request.invitation(other, player)));
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

    @Test
    void accept() {
        dialog.accept();

        requestStack.assertLast(new ExchangeCreated(ExchangeType.PLAYER_EXCHANGE));

        assertInstanceOf(ExchangeDialog.class, player.interactions().get(Interaction.class));
        assertInstanceOf(ExchangeDialog.class, other.interactions().get(Interaction.class));
    }
}
