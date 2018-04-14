package fr.quatrevieux.araknemu.game.listener.player.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FightJoined;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class AttachFighterTest extends GameBaseCase {
    private AttachFighter listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        listener = new AttachFighter(gamePlayer());
    }

    @Test
    void onFightJoined() throws ContainerException, SQLException {
        PlayerFighter fighter = new PlayerFighter(gamePlayer());

        listener.on(
            new FightJoined(
                new Fight(
                    new ChallengeType(),
                    container.get(FightService.class).map(
                        container.get(ExplorationMapService.class).load(10340)
                    ),
                    new ArrayList<>()
                ),
                fighter
            )
        );

        assertTrue(gamePlayer().isFighting());
        assertSame(fighter, gamePlayer().fighter());
    }
}
