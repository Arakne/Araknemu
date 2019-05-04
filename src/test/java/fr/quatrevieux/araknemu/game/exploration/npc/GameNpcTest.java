package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.DialogService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.NpcQuestion;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.SQLException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class GameNpcTest extends GameBaseCase {
    private GameNpc npc;
    private Npc entity;
    private NpcTemplate template;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushQuestions()
            .pushResponseActions()
        ;

        entity = new Npc(472, 878, new Position(10340, 82), Direction.SOUTH_EAST, new int[] {3786});
        npc = new GameNpc(
            entity,
            template = new NpcTemplate(878, 40, 100, 100, Sex.MALE, new Colors(8158389, 13677665, 3683117), "0,20f9,2a5,1d5e,1b9e", 4, 9092),
            container.get(DialogService.class).forNpc(entity)
        );
    }

    @Test
    void getters() {
        assertInstanceOf(NpcSprite.class, npc.sprite());
        assertEquals(82, npc.position().cell());
        assertEquals(10340, npc.position().map());
        assertEquals(-47204, npc.id());
        assertEquals(Direction.SOUTH_EAST, npc.orientation());
        assertEquals(878, npc.template().id());
    }

    @Test
    void join() throws SQLException {
        dataSet.pushMaps();

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        npc.join(map);

        assertEquals(map.get(82), npc.cell());
        assertTrue(map.creatures().contains(npc));
    }

    @Test
    void apply() {
        Operation operation = Mockito.mock(Operation.class);

        npc.apply(operation);

        Mockito.verify(operation).onNpc(npc);
    }

    @Test
    void questionSuccess() throws SQLException, ContainerException {
        NpcQuestion question = npc.question(explorationPlayer()).get();

        assertEquals(3786, question.id());
        assertTrue(question.check(explorationPlayer()));
    }

    @Test
    void questionNotFound() throws SQLException, ContainerException {
        npc = new GameNpc(entity, template, Collections.emptyList());

        assertFalse(npc.question(explorationPlayer()).isPresent());
    }
}
