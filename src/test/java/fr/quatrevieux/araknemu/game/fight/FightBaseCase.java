package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.*;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import org.junit.jupiter.api.BeforeEach;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class FightBaseCase extends GameBaseCase {
    protected GamePlayer player;
    protected GamePlayer other;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        player = gamePlayer(true);
        other  = makeOtherPlayer();
    }

    public Fight createFight(boolean init) throws Exception {
        Fight fight = new Fight(
            new ChallengeType(),
            container.get(FightService.class).map(
                container.get(ExplorationMapService.class).load(10340)
            ),
            Arrays.asList(
                createTeam0(),
                createTeam1()
            ),
            new StatesFlow(
                new NullState(),
                new InitialiseState(false),
                new PlacementState(),
                new ActiveState()
            )
        );

        if (init) {
            fight.nextState();
        }

        return fight;
    }

    public Fight createFight() throws Exception {
        return createFight(true);
    }

    public FightTeam createTeam0() {
        return new SimpleTeam(
            new PlayerFighter(player),
            Arrays.asList(122, 123, 124),
            0
        );
    }

    public FightTeam createTeam1() {
        return new SimpleTeam(
            new PlayerFighter(other),
            Arrays.asList(125, 126, 127),
            0
        );
    }
}
