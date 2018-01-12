package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.world.repository.implementation.local.PlayerRaceRepositoryCache;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class WorldRepositoriesModuleTest extends GameBaseCase {
    @Test
    void instances() throws SQLException, ContainerException {
        Container container = new ItemPoolContainer();

        container.register(new WorldRepositoriesModule(
            app.database().get("game")
        ));

        assertInstanceOf(PlayerRaceRepositoryCache.class, container.get(fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository.class));
        assertInstanceOf(MapTemplateRepository.class, container.get(fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository.class));
    }

    public void assertInstanceOf(Class type, Object obj) {
        assertTrue(type.isInstance(obj));
    }
}