package fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player;

import fr.quatrevieux.araknemu.data.constant.Characteristic;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player.InitiatorExchangeRequestDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player.PlayerExchangeRequest;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.player.TargetExchangeRequestDialog;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.player.Restrictions;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeRequestError;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeRequested;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class PlayerExchangeRequestTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    private PlayerExchangeRequest request;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        other = makeOtherExplorationPlayer();

        other.join(player.map());

        request = new PlayerExchangeRequest(player, other);
        requestStack.clear();
    }

    @Test
    void startSuccess() {
        player.interactions().start(request);

        requestStack.assertLast(new ExchangeRequested(player, other, ExchangeType.PLAYER_EXCHANGE));

        assertInstanceOf(InitiatorExchangeRequestDialog.class, player.interactions().get(ExchangeInteraction.class));
        assertInstanceOf(TargetExchangeRequestDialog.class, other.interactions().get(ExchangeInteraction.class));
    }

    @Test
    void startSelfCantExchangeRestriction() {
        player.player().restrictions().set(Restrictions.Restriction.DENY_EXCHANGE);

        player.interactions().start(request);

        assertFalse(player.interactions().busy());
        assertFalse(other.interactions().busy());

        requestStack.assertLast(new ExchangeRequestError(ExchangeRequestError.Error.CANT_EXCHANGE));
    }

    @Test
    void startTargetCantExchangeRestriction() {
        other.player().restrictions().set(Restrictions.Restriction.DENY_EXCHANGE);

        player.interactions().start(request);

        assertFalse(player.interactions().busy());
        assertFalse(other.interactions().busy());

        requestStack.assertLast(new ExchangeRequestError(ExchangeRequestError.Error.CANT_EXCHANGE));
    }

    @Test
    void startTargetBusy() {
        Interaction interaction = Mockito.mock(Interaction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);

        other.interactions().start(interaction);

        player.interactions().start(request);

        assertFalse(player.interactions().busy());

        requestStack.assertLast(new ExchangeRequestError(ExchangeRequestError.Error.CANT_EXCHANGE));
    }

    @Test
    void startTargetExchanging() {
        Interaction interaction = Mockito.mock(ExchangeInteraction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);

        other.interactions().start(interaction);

        player.interactions().start(request);

        assertFalse(player.interactions().busy());

        requestStack.assertLast(new ExchangeRequestError(ExchangeRequestError.Error.ALREADY_EXCHANGE));
    }

    @Test
    void startBadMap() {
        other.join(
            container.get(ExplorationMapService.class).load(10540)
        );

        player.interactions().start(request);

        assertFalse(player.interactions().busy());
        assertFalse(other.interactions().busy());

        requestStack.assertLast(new ExchangeRequestError(ExchangeRequestError.Error.CANT_EXCHANGE));
    }

    @Test
    void startOverweight() {
        player.properties().characteristics().base().set(Characteristic.STRENGTH, -10000000);
        player.interactions().start(request);

        assertFalse(player.interactions().busy());
        assertFalse(other.interactions().busy());

        requestStack.assertLast(new ExchangeRequestError(ExchangeRequestError.Error.OVERWEIGHT));
    }

    @Test
    void leave() {
        player.interactions().start(request);

        request.leave();

        requestStack.assertLast(new ExchangeLeaved(false));

        assertFalse(player.interactions().busy());
        assertFalse(other.interactions().busy());
    }

    @Test
    void stop() {
        player.interactions().start(request);

        request.stop();

        assertFalse(player.interactions().busy());
        assertFalse(other.interactions().busy());
    }
}
