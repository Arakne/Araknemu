package fr.quatrevieux.araknemu.network.game.out.exchange;

import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.bank.Bank;
import fr.quatrevieux.araknemu.game.account.bank.BankService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class StorageListTest extends GameBaseCase {
    @Test
    void generate() throws SQLException {
        dataSet
            .pushItemTemplates()
            .pushItemSets()
            .use(AccountBank.class, BankItem.class)
        ;

        final ItemService itemService = container.get(ItemService.class);

        Bank bank = container.get(BankService.class).load(explorationPlayer().account());

        bank.add(itemService.create(2422, true), 2);
        bank.add(itemService.create(284, true), 10);
        bank.addKamas(5000);

        assertEquals(
            "ELO1~976~2~~8a#f#0#0#0d0+15,7d#21#0#0#0d0+33;O2~11c~a~~;G5000",
            new StorageList(bank).toString()
        );
    }
}
