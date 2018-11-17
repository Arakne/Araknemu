package fr.quatrevieux.araknemu.game.listener.player.exploration;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FightJoined;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;

class LeaveExplorationForFightTest extends FightBaseCase {
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
    void onFightJoined() throws Exception {
        listener.on(
            new FightJoined(
                createFight(false),
                makePlayerFighter(player.player())
            )
        );

        assertFalse(player.player().isExploring());
        assertNull(session.exploration());
        assertFalse(player.map().players().contains(player));
    }
}
