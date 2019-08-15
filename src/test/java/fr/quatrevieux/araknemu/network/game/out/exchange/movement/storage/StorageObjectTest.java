package fr.quatrevieux.araknemu.network.game.out.exchange.movement.storage;

import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.bank.Bank;
import fr.quatrevieux.araknemu.game.account.bank.BankEntry;
import fr.quatrevieux.araknemu.game.account.bank.BankService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StorageObjectTest extends GameBaseCase {
    @Test
    void generateAdd() throws SQLException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
            .use(AccountBank.class, BankItem.class)
        ;

        Bank bank = container.get(BankService.class).load(explorationPlayer().account());
        BankEntry entry = bank.add(container.get(ItemService.class).create(2422, true), 2);

        assertEquals("EsKO+1|2|2422|8a#f#0#0#0d0+15,7d#21#0#0#0d0+33", new StorageObject(entry).toString());
    }

    @Test
    void generateRemove() throws SQLException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
            .use(AccountBank.class, BankItem.class)
        ;

        Bank bank = container.get(BankService.class).load(explorationPlayer().account());
        BankEntry entry = bank.add(container.get(ItemService.class).create(2422, true));
        entry.remove(1);

        assertEquals("EsKO-1", new StorageObject(entry).toString());
    }
}
