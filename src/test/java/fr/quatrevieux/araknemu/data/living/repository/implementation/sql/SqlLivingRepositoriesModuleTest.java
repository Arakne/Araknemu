package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.living.transformer.PermissionsTransformer;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class SqlLivingRepositoriesModuleTest extends RealmBaseCase {
    @Test
    void instances() throws SQLException, ContainerException {
        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(
            app.database().get("realm")
        ));

        assertInstanceOf(SqlAccountRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository.class));
        assertInstanceOf(SqlPlayerRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository.class));
        assertInstanceOf(SqlSubAreaRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.environment.SubAreaRepository.class));
        assertInstanceOf(SqlPlayerItemRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository.class));
        assertInstanceOf(SqlPlayerSpellRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository.class));
        assertInstanceOf(SqlAccountBankRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.account.AccountBankRepository.class));
        assertInstanceOf(SqlBankItemRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.account.BankItemRepository.class));

        assertInstanceOf(PermissionsTransformer.class, container.get(PermissionsTransformer.class));
    }

    public void assertInstanceOf(Class type, Object obj) {
        assertTrue(type.isInstance(obj));
    }
}