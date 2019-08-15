package fr.quatrevieux.araknemu.game.exploration.interaction.exchange;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeParty;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.exchange.PlayerExchangeParty;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeAccepted;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.local.LocalExchangeKamas;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.local.LocalExchangeObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;

class ExchangeDialogTest extends GameBaseCase {
    private ExplorationPlayer player;
    private ExplorationPlayer other;

    private ExchangeParty local;
    private ExchangeParty distant;

    private ExchangeDialog dialog;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        player = explorationPlayer();
        other = makeOtherExplorationPlayer();

        other.join(player.map());

        ExchangeParty[] parties = PlayerExchangeParty.make(player, other);

        local = parties[0];
        distant = parties[1];

        dialog = new ExchangeDialog(local);
    }

    @Test
    void start() {
        player.interactions().start(dialog);

        requestStack.assertLast(new ExchangeCreated(ExchangeType.PLAYER_EXCHANGE));

        assertSame(dialog, player.interactions().get(ExchangeDialog.class));
    }

    @Test
    void stop() {
        player.interactions().start(dialog);
        distant.start();

        dialog.stop();

        requestStack.assertLast(new ExchangeLeaved(false));
        assertFalse(player.interactions().interacting());
    }

    @Test
    void leave() {
        player.interactions().start(dialog);
        distant.start();

        dialog.leave();

        requestStack.assertLast(new ExchangeLeaved(false));
        assertFalse(player.interactions().interacting());
    }

    @Test
    void accept() {
        player.interactions().start(dialog);

        dialog.accept();
        requestStack.assertLast(new ExchangeAccepted(true, player));

        dialog.accept();
        requestStack.assertLast(new ExchangeAccepted(false, player));
    }

    @Test
    void kamas() {
        player.interactions().start(dialog);

        dialog.kamas(100);
        requestStack.assertLast(new LocalExchangeKamas(100));
    }

    @Test
    void item() throws SQLException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
        ;

        player.interactions().start(dialog);

        InventoryEntry entry = player.inventory().add(container.get(ItemService.class).create(2422));

        dialog.item(entry.id(), 1);
        requestStack.assertLast(new LocalExchangeObject(entry, 1));
    }
}
