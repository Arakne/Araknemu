package fr.quatrevieux.araknemu.game.listener.fight;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.event.FighterReadyStateChanged;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterReadyState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class SendFighterReadyStateTest extends GameBaseCase {
    private SendFighterReadyState listener;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        fighter = new PlayerFighter(gamePlayer(true));
        Fight fight = new Fight(
            new ChallengeType(),
            container.get(FightService.class).map(
                container.get(ExplorationMapService.class).load(10340)
            ),
            Arrays.asList(
                new SimpleTeam(fighter, new ArrayList<>(), 0),
                new SimpleTeam(new PlayerFighter(makeOtherPlayer()), new ArrayList<>(), 1)
            )
        );

        listener = new SendFighterReadyState(fight);
    }

    @Test
    void onFighterReadyStateChanged() {
        listener.on(new FighterReadyStateChanged(fighter));

        requestStack.assertLast(new FighterReadyState(fighter));
    }
}