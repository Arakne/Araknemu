package fr.quatrevieux.araknemu.game.exploration.npc;

import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.world.creature.Operation;
import fr.quatrevieux.araknemu.game.world.map.Direction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class GameNpcTest extends GameBaseCase {
    private GameNpc npc;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        npc = new GameNpc(
            new Npc(472, 878, new Position(10340, 82), Direction.SOUTH_EAST),
            new NpcTemplate(878, 40, 100, 100, Sex.MALE, new Colors(8158389, 13677665, 3683117), "0,20f9,2a5,1d5e,1b9e", 4, 9092)
        );
    }

    @Test
    void getters() {
        assertInstanceOf(NpcSprite.class, npc.sprite());
        assertEquals(82, npc.cell());
        assertEquals(-47204, npc.id());
        assertEquals(Direction.SOUTH_EAST, npc.orientation());
        assertEquals(878, npc.template().id());
    }

    @Test
    void apply() {
        Operation operation = Mockito.mock(Operation.class);

        npc.apply(operation);

        Mockito.verify(operation).onNpc(npc);
    }
}
