package fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge.AskChallenge;
import fr.quatrevieux.araknemu.game.exploration.interaction.challenge.ChallengerDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.challenge.InitiatorDialog;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.item.ItemService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryEntry;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class AskChallengeTest extends GameBaseCase {
    @Test
    void otherPlayerIsBusy() throws Exception {
        ExplorationPlayer current = explorationPlayer();
        ExplorationPlayer other = new ExplorationPlayer(makeOtherPlayer());

        other.join(current.map());

        Interaction interaction = Mockito.mock(Interaction.class);
        Mockito.when(interaction.start()).thenReturn(interaction);
        other.interactions().start(interaction);

        AskChallenge action = new AskChallenge(current, other.id(), container.get(FightService.class));

        action.start(new ActionQueue());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, "1", "z")
        );
    }

    @Test
    void badMap() throws Exception {
        ExplorationPlayer current = explorationPlayer();
        ExplorationPlayer other = new ExplorationPlayer(makeOtherPlayer());

        other.join(current.map());

        AskChallenge action = new AskChallenge(current, other.id(), container.get(FightService.class));

        other.join(
            container.get(ExplorationMapService.class).load(10540)
        );

        action.start(new ActionQueue());

        requestStack.assertLast(
            new GameActionResponse("", ActionType.JOIN_FIGHT, "1", "p")
        );
    }
    @Test
    void notExplorationPlayer() throws Exception {
        dataSet.pushNpcs();
        ExplorationPlayer current = explorationPlayer();

        GameNpc npc = container.get(NpcService.class).get(457);

        npc.join(current.map());
        requestStack.clear();

        AskChallenge action = new AskChallenge(current, npc.id(), container.get(FightService.class));

        action.start(new ActionQueue());

        requestStack.assertEmpty();
        assertFalse(current.interactions().interacting());
    }

    @Test
    void success() throws Exception {
        ExplorationPlayer current = explorationPlayer();
        ExplorationPlayer other = new ExplorationPlayer(makeOtherPlayer());

        current.join(container.get(ExplorationMapService.class).load(10340));
        other.join(current.map());

        AskChallenge action = new AskChallenge(current, other.id(), container.get(FightService.class));

        action.start(new ActionQueue());

        requestStack.assertLast(new GameActionResponse(action));

        assertTrue(current.interactions().interacting());
        assertTrue(other.interactions().interacting());

        assertSame(other, current.interactions().get(InitiatorDialog.class).interlocutor());
        assertSame(current, other.interactions().get(ChallengerDialog.class).interlocutor());
    }
}
