package fr.quatrevieux.araknemu.game.listener.player.exchange;

import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.bank.Bank;
import fr.quatrevieux.araknemu.game.account.bank.BankService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.item.inventory.ItemEntry;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage.StorageKamas;
import fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage.StorageObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SendStoragePacketsTest extends GameBaseCase {
    private Bank bank;
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

        bank = container.get(BankService.class).load(explorationPlayer().account());
        bank.dispatcher().register(new SendStoragePackets(explorationPlayer()));

        itemService = container.get(ItemService.class);
    }

    @Test
    void onObjectAdded() {
        ItemEntry entry = bank.add(itemService.create(39));

        requestStack.assertLast(new StorageObject(entry));
    }

    @Test
    void onObjectQuantityChanged() {
        ItemEntry entry = bank.add(itemService.create(39));
        requestStack.clear();

        entry.add(3);

        requestStack.assertLast(new StorageObject(entry));
    }

    @Test
    void onObjectRemoved() {
        ItemEntry entry = bank.add(itemService.create(39));
        requestStack.clear();

        entry.remove(1);

        requestStack.assertLast(new StorageObject(entry));
    }

    @Test
    void onKamasChanged() {
        bank.addKamas(5000);

        requestStack.assertLast(new StorageKamas(5000));
    }
}
