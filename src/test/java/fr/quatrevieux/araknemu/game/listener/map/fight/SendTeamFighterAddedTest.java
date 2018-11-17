package fr.quatrevieux.araknemu.game.listener.map.fight;

import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.event.FighterAdded;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.network.game.out.fight.exploration.AddTeamFighters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

class SendTeamFighterAddedTest extends FightBaseCase {
    private Fight fight;
    private ExplorationMap map;
    private SendTeamFighterAdded listener;
    private PlayerFighter fighter;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushMaps();

        map = container.get(ExplorationMapService.class).load(10340);
        explorationPlayer().join(map);

        fight = createSimpleFight(map);
        listener = new SendTeamFighterAdded(map);

        fighter = makePlayerFighter(makeSimpleGamePlayer(10));

        requestStack.clear();
    }

    @Test
    void onFighterAdded() {
        fighter.setTeam(fight.team(0));

        listener.on(new FighterAdded(fighter));

        requestStack.assertLast(new AddTeamFighters(fight.team(0), Collections.singleton(fighter)));
    }
}