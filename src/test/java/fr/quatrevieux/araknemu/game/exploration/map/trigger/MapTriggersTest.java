package fr.quatrevieux.araknemu.game.exploration.map.trigger;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MapTriggersTest extends GameBaseCase {
    private MapTriggers triggers;
    private Map<CellAction, CellActionPerformer> actions;
    private List<MapTrigger> realmTriggers;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        triggers = new MapTriggers(
            realmTriggers = Arrays.asList(
                new MapTrigger(1, 10300, 120, CellAction.TELEPORT, "123,45", ""),
                new MapTrigger(2, 10300, 120, CellAction.TELEPORT, "678,90", ""),
                new MapTrigger(3, 10300, 258, CellAction.TELEPORT, "123,45", "")
            ),
            actions = new HashMap<>()
        );
    }

    @Test
    void performOnCellWithoutTriggers() throws SQLException, ContainerException {
        explorationPlayer().changeCell(1);

        triggers.perform(explorationPlayer());
    }

    @Test
    void performWithOneAction() throws Exception {
        explorationPlayer().move(258);

        CellActionPerformer performer = Mockito.mock(CellActionPerformer.class);
        actions.put(CellAction.TELEPORT, performer);

        triggers.perform(explorationPlayer());

        Mockito.verify(performer).perform(realmTriggers.get(2), explorationPlayer());
    }

    @Test
    void performWithTwoActions() throws Exception {
        explorationPlayer().move(120);

        CellActionPerformer performer = Mockito.mock(CellActionPerformer.class);
        actions.put(CellAction.TELEPORT, performer);

        triggers.perform(explorationPlayer());

        Mockito.verify(performer).perform(realmTriggers.get(0), explorationPlayer());
        Mockito.verify(performer).perform(realmTriggers.get(1), explorationPlayer());
    }
}
