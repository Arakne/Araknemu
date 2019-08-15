package fr.quatrevieux.araknemu.game.exploration.exchange;

import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.bank.Bank;
import fr.quatrevieux.araknemu.game.account.bank.BankService;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.exchange.ExchangeInteraction;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.game.player.inventory.slot.AmuletSlot;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeCreated;
import fr.quatrevieux.araknemu.network.game.out.exchange.ExchangeLeaved;
import fr.quatrevieux.araknemu.network.game.out.exchange.StorageList;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage.StorageKamas;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage.StorageMovementError;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage.StorageObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BankExchangePartyTest extends GameBaseCase {
    private BankExchangeParty party;
    private Bank bank;
    private ExplorationPlayer player;
    private ItemService itemService;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
            .use(AccountBank.class, BankItem.class)
        ;

        itemService = container.get(ItemService.class);
        bank = container.get(BankService.class).load(explorationPlayer().account());
        party = new BankExchangeParty(player = explorationPlayer(), bank);

        requestStack.clear();
    }

    @Test
    void getters() {
        assertSame(player, party.actor());
        assertEquals(ExchangeType.BANK, party.type());
    }

    @Test
    void start() {
        party.start();

        requestStack.assertAll(
            new ExchangeCreated(ExchangeType.BANK),
            new StorageList(bank)
        );

        assertTrue(player.interactions().busy());
        assertInstanceOf(ExchangeDialog.class, player.interactions().get(ExchangeInteraction.class));
    }

    @Test
    void leaveShouldSaveBank() {
        bank.addKamas(1000);

        party.start();
        party.leave();

        assertFalse(player.interactions().busy());
        requestStack.assertLast(ExchangeLeaved.accepted());
        assertEquals(1000, dataSet.refresh(new AccountBank(player.account().id(), player.account().serverId(), 0)).kamas());
    }

    @Test
    void toggleAccept() {
        assertThrows(UnsupportedOperationException.class, () -> party.toggleAccept());
    }

    @Test
    void kamasPositive() {
        party.start();

        party.kamas(1000);

        requestStack.assertLast(new StorageKamas(1000));
        assertEquals(1000, bank.kamas());
        assertEquals(14225, player.inventory().kamas());

        party.kamas(500);

        requestStack.assertLast(new StorageKamas(1500));
        assertEquals(1500, bank.kamas());
        assertEquals(13725, player.inventory().kamas());
    }

    @Test
    void kamasPositiveTooHighShouldLimitToCurrentKamasAmount() {
        party.start();

        party.kamas(100000);

        requestStack.assertLast(new StorageKamas(15225));
        assertEquals(15225, bank.kamas());
        assertEquals(0, player.inventory().kamas());
    }

    @Test
    void kamasNegative() {
        bank.addKamas(5000);
        party.start();

        party.kamas(-1000);

        requestStack.assertOne(new StorageKamas(4000));
        assertEquals(4000, bank.kamas());
        assertEquals(16225, player.inventory().kamas());

        party.kamas(-500);

        requestStack.assertOne(new StorageKamas(3500));
        assertEquals(3500, bank.kamas());
        assertEquals(16725, player.inventory().kamas());
    }

    @Test
    void kamasNegativeTooHighShouldLimitWithBankKamasQuantity() {
        bank.addKamas(5000);
        party.start();

        party.kamas(-10000);

        requestStack.assertOne(new StorageKamas(0));
        assertEquals(0, bank.kamas());
        assertEquals(20225, player.inventory().kamas());
    }

    @Test
    void kamasZero() {
        party.start();
        party.kamas(0);

        requestStack.assertLast(new StorageMovementError());
    }

    @Test
    void itemPositive() {
        ItemEntry entry = player.inventory().add(itemService.create(39), 5);
        party.start();

        party.item(entry.id(), 3);

        requestStack.assertLast(new StorageObject(bank.get(1)));
        assertEquals(2, entry.quantity());
        assertEquals(3, bank.get(1).quantity());

        party.item(entry.id(), 1);

        requestStack.assertLast(new StorageObject(bank.get(1)));
        assertEquals(1, entry.quantity());
        assertEquals(4, bank.get(1).quantity());
    }

    @Test
    void itemPositiveTooHighShouldLimitWithItemQuantity() {
        ItemEntry entry = player.inventory().add(itemService.create(39), 5);
        party.start();

        party.item(entry.id(), 10);

        requestStack.assertLast(new StorageObject(bank.get(1)));
        assertEquals(0, entry.quantity());
        assertEquals(5, bank.get(1).quantity());
    }

    @Test
    void itemPositiveEquipedItemShouldFailed() {
        ItemEntry entry = player.inventory().add(itemService.create(39), 1, AmuletSlot.SLOT_ID);
        party.start();

        party.item(entry.id(), 1);

        requestStack.assertLast(new StorageMovementError());
        assertEquals(1, entry.quantity());
    }

    @Test
    void itemNegative() {
        ItemEntry entry = bank.add(itemService.create(39), 5);
        party.start();

        party.item(entry.id(), -3);

        requestStack.assertOne(new StorageObject(entry));
        assertEquals(2, entry.quantity());
        assertEquals(3, player.inventory().get(1).quantity());

        party.item(entry.id(), -1);

        requestStack.assertOne(new StorageObject(entry));
        assertEquals(1, entry.quantity());
        assertEquals(4, player.inventory().get(1).quantity());
    }

    @Test
    void itemNegativeTooHighShouldLimitWithItemQuantity() {
        ItemEntry entry = bank.add(itemService.create(39), 5);
        party.start();

        party.item(entry.id(), -10);

        requestStack.assertOne(new StorageObject(entry));
        assertEquals(0, entry.quantity());
        assertEquals(5, player.inventory().get(1).quantity());
    }

    @Test
    void itemInvalid() {
        party.start();
        party.item(404, 1);

        requestStack.assertLast(new StorageMovementError());
    }

    @Test
    void itemZeroQuantity() {
        ItemEntry entry = player.inventory().add(itemService.create(39), 5);
        party.start();

        party.item(entry.id(), 0);

        requestStack.assertLast(new StorageMovementError());
    }

    @Test
    void send() {
        party.send("test");

        requestStack.assertLast("test");
    }
}
