package fr.quatrevieux.araknemu.game.listener.map;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.event.CellChanged;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;

class SendPlayerChangeCellTest extends GameBaseCase {
    private SendPlayerChangeCell listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        listener = new SendPlayerChangeCell(
            container.get(ExplorationMapService.class).load(10300)
        );
    }

    @Test
    void onCellChanged() throws SQLException, ContainerException {
        listener.on(new CellChanged(explorationPlayer(), 123));

        requestStack.assertLast(
            new AddSprites(
                Collections.singleton(explorationPlayer().sprite())
            )
        );
    }
}
