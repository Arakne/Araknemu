package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FighterRemoved;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.state.PlacementState;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.RemoveTeamFighters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class SendTeamFighterRemovedTest extends FightBaseCase {
    private Fight fight;
    private ExplorationMap map;
    private SendTeamFighterRemoved listener;
    private Fighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        map = container.get(ExplorationMapService.class).load(10340);
        explorationPlayer().join(map);

        fight = createSimpleFight(map);
        listener = new SendTeamFighterRemoved(map);

        fighter = new PlayerFighter(makeSimpleGamePlayer(10));
        fight.state(PlacementState.class).joinTeam(fighter, fight.team(0));

        requestStack.clear();
    }

    @Test
    void onFighterRemovedDuringPlacement() {
        listener.on(new FighterRemoved(fighter, fight));

        requestStack.assertLast(new RemoveTeamFighters(fight.team(0), Collections.singleton(fighter)));
    }

    @Test
    void onFighterRemovedNotPlacementState() {
        fight.state(PlacementState.class).startFight();
        requestStack.clear();

        listener.on(new FighterRemoved(fighter, fight));

        requestStack.assertEmpty();
    }
}