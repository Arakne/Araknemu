package fr.quatrevieux.araknemu.game.fight;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.ActiveState;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.*;
import fr.quatrevieux.araknemu.network.game.out.game.RemoveSprite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FunctionalFromExplorationMapTest extends FightBaseCase {
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        map = container.get(ExplorationMapService.class).load(10340);

        explorationPlayer().join(map);
        requestStack.clear();
    }

    @Test
    void createFightWillShowFight() throws SQLException, ContainerException {
        Fight fight = createSimpleFight(map);

        requestStack.assertAll(
            new ShowFight(fight),
            new AddTeamFighters(fight.team(0)),
            new AddTeamFighters(fight.team(1)),
            new FightsCount(1)
        );
    }

    @Test
    void startFightWillHideFight() throws SQLException, ContainerException {
        Fight fight = createSimpleFight(map);
        requestStack.clear();

        fight.state(PlacementState.class).startFight();

        requestStack.assertLast(new HideFight(fight));
    }

    @Test
    void joinFightWillRemoveSpriteFromMap() throws Exception {
        Fight fight = createSimpleFight(map);

        ExplorationPlayer explorationPlayer = makeExplorationPlayer(other);
        explorationPlayer.join(map);
        requestStack.clear();

        PlayerFighter otherFighter = makePlayerFighter(other);
        fight.state(PlacementState.class).joinTeam(otherFighter, fight.team(0));

        assertFalse(other.isExploring());
        assertFalse(map.creatures().contains(explorationPlayer));
        assertTrue(other.isFighting());
        assertContains(otherFighter, fight.fighters());
        assertContains(otherFighter, fight.team(0).fighters());

        requestStack.assertAll(
            new RemoveSprite(explorationPlayer.sprite()),
            new AddTeamFighters(fight.team(0), Collections.singleton(otherFighter))
        );
    }

    @Test
    void leaveFightWillRemoveFromTeam() throws Exception {
        Fight fight = createSimpleFight(map);

        ExplorationPlayer explorationPlayer = makeExplorationPlayer(other);
        explorationPlayer.join(map);

        PlayerFighter otherFighter = makePlayerFighter(other);
        fight.state(PlacementState.class).joinTeam(otherFighter, fight.team(0));
        requestStack.clear();

        fight.state(PlacementState.class).leave(otherFighter);

        assertFalse(other.isFighting());
        assertFalse(fight.fighters().contains(otherFighter));

        requestStack.assertAll(
            new RemoveTeamFighters(fight.team(0), Collections.singleton(otherFighter))
        );
    }

    @Test
    void leaveFightLeaderWillCancelFight() throws Exception {
        Fight fight = createSimpleFight(map);
        requestStack.clear();

        Fighter fighter = fight.team(0).leader();
        fight.state(PlacementState.class).leave(fighter);

        requestStack.assertAll(
            new RemoveTeamFighters(fighter.team(), Collections.singleton(fighter)),
            new HideFight(fight),
            new FightsCount(0)
        );
    }

    @Test
    void finishFightWillRemoveFight() throws Exception {
        Fight fight = createSimpleFight(map);
        fight.state(PlacementState.class).startFight();
        requestStack.clear();
        fight.state(ActiveState.class).terminate();

        requestStack.assertAll(
            new FightsCount(0)
        );
    }
}
