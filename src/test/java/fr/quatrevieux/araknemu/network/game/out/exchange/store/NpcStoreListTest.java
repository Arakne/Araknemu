package fr.quatrevieux.araknemu.network.game.out.exchange.store;

import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class NpcStoreListTest extends GameBaseCase {
    @Test
    void generate() throws SQLException {
        dataSet.pushItemTemplates();

        ItemTemplateRepository repository = container.get(ItemTemplateRepository.class);

        assertEquals(
            "EL39;7e#2#0#0#0d0+2|2422;8a#1#f#0#1d15+0,7d#1#21#0#1d33+0",
            new NpcStoreList(
                Arrays.asList(
                    repository.get(39),
                    repository.get(2422)
                )
            ).toString()
        );
    }
}
