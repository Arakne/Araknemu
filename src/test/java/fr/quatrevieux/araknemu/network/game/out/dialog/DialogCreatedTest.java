package fr.quatrevieux.araknemu.network.game.out.dialog;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.world.creature.Creature;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DialogCreatedTest extends GameBaseCase {
    @Test
    void generate() throws SQLException, ContainerException {
        dataSet
            .pushNpcs()
            .pushMaps()
        ;

        Creature npc = container.get(ExplorationMapService.class).load(10340).creature(-47204);

        assertEquals("DCK-47204", new DialogCreated(npc).toString());
    }
}
