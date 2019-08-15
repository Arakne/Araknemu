package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.exchange;

import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.bank.BankService;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.exchange.ExchangeType;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeDialog;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;
import fr.quatrevieux.araknemu.network.game.out.exchange.StorageList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class OpenBankTest extends GameBaseCase {
    private OpenBank.Factory factory;
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .use(AccountBank.class, BankItem.class)
        ;

        factory = new OpenBank.Factory(container.get(BankService.class));
        player = explorationPlayer();

        requestStack.clear();
    }

    @Test
    void factory() {
        assertInstanceOf(OpenBank.class, factory.create(new ResponseAction(1, "BANK", "")));
    }

    @Test
    void check() {
        assertTrue(factory.create(new ResponseAction(1, "BANK", "")).check(player));
    }

    @Test
    void applySuccess() {
        requestStack.clear();
        factory.create(new ResponseAction(1, "BANK", "")).apply(player);

        requestStack.assertAll(
            new ExchangeCreated(ExchangeType.BANK),
            new StorageList(container.get(BankService.class).load(player.account()))
        );

        assertTrue(player.interactions().busy());
        assertInstanceOf(ExchangeDialog.class, player.interactions().get(Interaction.class));
    }
}
