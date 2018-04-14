package fr.quatrevieux.araknemu.game.listener.player.exploration;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FightJoined;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class LeaveExplorationForFightTest extends GameBaseCase {
    private LeaveExplorationForFight listener;
    private ExplorationPlayer player;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        player = explorationPlayer();

        listener = new LeaveExplorationForFight(player);
    }

    @Test
    void onFightJoined() throws ContainerException {
        listener.on(
            new FightJoined(
                new Fight(
                    new ChallengeType(),
                    container.get(FightService.class).map(
                        container.get(ExplorationMapService.class).load(10340)
                    ),
                    new ArrayList<>()
                ),
                new PlayerFighter(player.player())
            )
        );

        assertFalse(player.player().isExploring());
        assertNull(session.exploration());
        assertFalse(player.map().players().contains(player));
    }
}
