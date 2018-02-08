package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.living.transformer.PermissionsTransformer;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class LivingRepositoriesModuleTest extends RealmBaseCase {
    @Test
    void instances() throws SQLException, ContainerException {
        Container container = new ItemPoolContainer();

        container.register(new LivingRepositoriesModule(
            app.database().get("realm")
        ));

        assertInstanceOf(AccountRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository.class));
        assertInstanceOf(PlayerRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository.class));
        assertInstanceOf(SubAreaRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.environment.SubAreaRepository.class));
        assertInstanceOf(PlayerItemRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository.class));

        assertInstanceOf(PermissionsTransformer.class, container.get(PermissionsTransformer.class));
    }

    public void assertInstanceOf(Class type, Object obj) {
        assertTrue(type.isInstance(obj));
    }
}