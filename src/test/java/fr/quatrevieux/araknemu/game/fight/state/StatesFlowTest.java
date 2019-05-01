package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.data.value.Dimensions;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.helpers.NOPLogger;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class StatesFlowTest extends GameBaseCase {
    @Test
    void flow() {
        FightState s1 = Mockito.mock(FightState.class);
        FightState s2 = Mockito.mock(FightState.class);

        StatesFlow flow = new StatesFlow(s1, s2);

        assertSame(s1, flow.current());

        Fight fight = new Fight(
            1,
            new ChallengeType(),
            new FightMap(
                new MapTemplate(0, "", new Dimensions(0, 0), "", new ArrayList<>(), new List[0])
            ),
            new ArrayList<>(),
            new StatesFlow(),
            NOPLogger.NOP_LOGGER
        );

        flow.next(fight);

        assertSame(s2, flow.current());
        Mockito.verify(s2).start(fight);
    }
}