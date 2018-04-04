package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;
import fr.quatrevieux.araknemu.game.fight.builder.FightHandler;
import fr.quatrevieux.araknemu.game.fight.fighter.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.ActiveState;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.network.game.out.fight.BeginFight;
import fr.quatrevieux.araknemu.game.player.GamePlayer;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterPositions;
import fr.quatrevieux.araknemu.network.game.out.fight.FighterReadyState;
import fr.quatrevieux.araknemu.network.game.out.fight.JoinFight;
import fr.quatrevieux.araknemu.network.game.out.game.AddSprites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;

/**
 *
 */
public class FunctionalTest extends GameBaseCase {
    private FightService service;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        service = container.get(FightService.class);
    }

    @Test
    void challengeFight() throws Exception {
        FightHandler<ChallengeBuilder> handler = service.handler(ChallengeBuilder.class);

        GamePlayer player = gamePlayer(true);
        GamePlayer other = makeOtherPlayer();
        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);

        Fight fight = handler.start(
            builder -> {
                builder
                    .fighter(player)
                    .fighter(other)
                    .map(map)
                ;
            }
        );

        assertTrue(player.isFighting());
        assertTrue(other.isFighting());

        assertInstanceOf(PlacementState.class, fight.state());

        PlayerFighter fighter1, fighter2;

        if (player.fighter().team().number() == 0) {
            fighter1 = player.fighter();
            fighter2 = other.fighter();
        } else {
            fighter1 = other.fighter();
            fighter2 = player.fighter();
        }

        requestStack.assertOne(new JoinFight(fight));
        requestStack.assertOne(new AddSprites(Arrays.asList(fighter1.sprite(), fighter2.sprite())));

        if (fighter1.cell().id() != 185) {
            fight.state(PlacementState.class).changePlace(fighter1, fight.map().get(185));
            requestStack.assertLast(new FighterPositions(Arrays.asList(fighter1, fighter2)));
        }

        if (fighter2.cell().id() != 150) {
            fight.state(PlacementState.class).changePlace(fighter2, fight.map().get(150));
            requestStack.assertLast(new FighterPositions(Arrays.asList(fighter1, fighter2)));
        }

        assertEquals(185, fighter1.cell().id());
        assertEquals(150, fighter2.cell().id());

        fighter1.setReady(true);
        requestStack.assertLast(new FighterReadyState(fighter1));
        assertInstanceOf(PlacementState.class, fight.state());

        fighter1.setReady(false);
        requestStack.assertLast(new FighterReadyState(fighter1));

        fighter2.setReady(true);
        requestStack.assertLast(new FighterReadyState(fighter2));
        assertInstanceOf(PlacementState.class, fight.state());

        fighter1.setReady(true);
        assertInstanceOf(ActiveState.class, fight.state());
        requestStack.assertOne(new BeginFight());
    }
}
