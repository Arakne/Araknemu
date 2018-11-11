package fr.quatrevieux.araknemu.game.exploration.interaction.action.fight;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.FightService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FightActionsFactoriesTest extends FightBaseCase {
    private ExplorationActionRegistry factory;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        factory = new ExplorationActionRegistry(new FightActionsFactories(container.get(FightService.class)));

        dataSet.pushMaps();
    }

    @Test
    void joinFight() throws Exception {
        ExplorationPlayer player = explorationPlayer();

        ExplorationMap map = container.get(ExplorationMapService.class).load(10340);
        player.join(map);

        Fight fight = createSimpleFight(map);

        Action action = factory.create(player, ActionType.JOIN_FIGHT, new String[] {fight.id() + "", fight.team(0).id() + ""});

        assertInstanceOf(JoinFight.class, action);
    }
}
