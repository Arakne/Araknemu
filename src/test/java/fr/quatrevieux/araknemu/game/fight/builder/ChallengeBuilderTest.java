package fr.quatrevieux.araknemu.game.fight.builder;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class ChallengeBuilderTest extends GameBaseCase {
    private ChallengeBuilder builder;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        builder = new ChallengeBuilder(container.get(FightService.class));
    }

    @Test
    void buildSimple() throws Exception {
        Fight fight = builder
            .fighter(gamePlayer())
            .fighter(makeOtherPlayer())
            .map(
                container.get(ExplorationMapService.class).load(10340)
            )
            .build()
        ;

        assertInstanceOf(ChallengeType.class, fight.type());
        assertCount(2, fight.teams());
        assertCount(2, fight.fighters());
        assertContainsOnly(SimpleTeam.class, fight.teams());
        assertContainsOnly(PlayerFighter.class, fight.fighters());
    }

    @Test
    void buildWillRandomizeTeamNumbers() throws Exception {
        builder
            .fighter(gamePlayer())
            .fighter(makeOtherPlayer())
            .map(container.get(ExplorationMapService.class).load(10340))
        ;

        int nbTeam0 = 0;

        for (int i = 0; i < 100; ++i) {
            Fight fight = builder.build();

            Fighter fighter = new ArrayList<>(fight.team(0).fighters()).get(0);

            if (fighter.id() == gamePlayer().id()) {
                ++nbTeam0;
            }
        }

        assertNotEquals(0, nbTeam0);
        assertNotEquals(100, nbTeam0);
    }
}
