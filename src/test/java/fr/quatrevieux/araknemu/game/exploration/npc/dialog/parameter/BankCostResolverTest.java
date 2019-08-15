package fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter;

import fr.quatrevieux.araknemu.data.living.entity.account.AccountBank;
import fr.quatrevieux.araknemu.data.living.entity.account.BankItem;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.account.bank.Bank;
import fr.quatrevieux.araknemu.game.account.bank.BankService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class BankCostResolverTest extends GameBaseCase {
    private BankCostResolver resolver;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushItemTemplates()
            .pushItemSets()
            .use(AccountBank.class, BankItem.class)
        ;

        resolver = new BankCostResolver(container.get(BankService.class));
    }

    @Test
    void name() {
        assertEquals("bankCost", resolver.name());
    }

    @Test
    void valueEmpty() throws SQLException {
        assertEquals(0L, resolver.value(explorationPlayer()));
    }

    @Test
    void valueNotEmpty() throws SQLException {
        Bank bank = container.get(BankService.class).load(explorationPlayer().account());

        bank.add(container.get(ItemService.class).create(2422));
        bank.add(container.get(ItemService.class).create(39), 4);

        assertEquals(2L, resolver.value(explorationPlayer()));
    }
}
