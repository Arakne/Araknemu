package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FightHandlerTest extends GameBaseCase {
    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();
    }

    @Test
    void withChallengeFight() throws ContainerException {
        FightHandler<ChallengeBuilder> handler = new FightHandler<>(
            new ChallengeBuilder(container.get(FightService.class))
        );

        Fight fight = handler.start(
            builder -> {
                try {
                    builder
                        .map(container.get(ExplorationMapService.class).load(10340))
                        .fighter(gamePlayer())
                        .fighter(makeOtherPlayer())
                    ;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        );

        assertInstanceOf(ChallengeType.class, fight.type());
        assertInstanceOf(PlacementState.class, fight.state());
        assertCount(2, fight.teams());
        assertCount(2, fight.fighters());
    }
}
