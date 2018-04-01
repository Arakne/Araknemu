package fr.quatrevieux.araknemu.game.event.listener.fight;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.event.fight.FightJoined;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilderFactory;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightMap;
import fr.quatrevieux.araknemu.game.fight.team.SimpleTeam;
import fr.quatrevieux.araknemu.game.fight.type.ChallengeType;
import fr.quatrevieux.araknemu.network.game.out.fight.JoinFight;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import fr.quatrevieux.araknemu.network.game.out.game.FightStartPositions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SendFightJoinedTest extends GameBaseCase {
    private SendFightJoined listener;
    private Fight fight;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        fighter = new PlayerFighter(gamePlayer());
        FightMap fightMap = container.get(FightService.class).map(
            container.get(ExplorationMapService.class).load(10340)
        );

        fight = new Fight(
            new ChallengeType(),
            fightMap,
            Arrays.asList(
                new SimpleTeam(fighter, fightMap.startPlaces(0), 0),
                new SimpleTeam(new PlayerFighter(makeOtherPlayer()), fightMap.startPlaces(1), 1)
            )
        );

        fight.nextState();

        listener = new SendFightJoined(fighter);
    }

    @Test
    void onFightJoined() {
        listener.on(new FightJoined(fight, fighter));

        requestStack.assertAll(
            new JoinFight(fight),
            new AddSprites(
                Arrays.asList(
                    fighter.sprite(),
                    new ArrayList<>(fight.team(1).fighters()).get(0).sprite()
                )
            ),
            new FightStartPositions(
                new List[]{
                    fight.team(0).startPlaces(),
                    fight.team(1).startPlaces(),
                },
                0
            )
        );
    }
}
