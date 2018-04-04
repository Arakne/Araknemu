package fr.quatrevieux.araknemu.game.handler.fight;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.state.ActiveState;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.in.fight.FighterReady;
import fr.quatrevieux.araknemu.network.game.out.fight.BeginFight;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterReadyState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChangeFighterReadyStateTest extends GameBaseCase {
    private ChangeFighterReadyState handler;
    private Fight fight;
    private GamePlayer other;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        handler = new ChangeFighterReadyState();

        GamePlayer player = gamePlayer(true);
        other = makeOtherPlayer();
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        fight = container.get(FightService.class).handler(ChallengeBuilder.class)
            .start(
                builder -> {
                    builder
                        .fighter(player)
                        .fighter(other)
                        .map(map);
                }
            );
    }

    @Test
    void setReady() {
        handler.handle(session, new FighterReady(true));

        requestStack.assertLast(new FighterReadyState(session.fighter()));
        assertTrue(session.fighter().ready());
    }

    @Test
    void unsetReady() {
        session.fighter().setReady(true);
        requestStack.clear();

        handler.handle(session, new FighterReady(false));

        requestStack.assertLast(new FighterReadyState(session.fighter()));
        assertFalse(session.fighter().ready());
    }

    @Test
    void setReadyAndStartFight() {
        other.fighter().setReady(true);

        handler.handle(session, new FighterReady(true));
        requestStack.assertOne(new BeginFight());

        assertInstanceOf(ActiveState.class, fight.state());
    }
}