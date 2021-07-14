/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.challenge.ChallengerDialog;
import fr.quatrevieux.araknemu.game.exploration.interaction.challenge.InitiatorDialog;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.network.game.out.game.action.GameActionResponse;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AskChallengeTest extends GameBaseCase {
    @Test
    void otherPlayerIsBusy() throws Exception {
        ExplorationPlayer current = explorationPlayer();
        ExplorationPlayer other = new ExplorationPlayer(makeOtherPlayer());

        other.changeMap(current.map(), 123);

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

        other.changeMap(current.map(), 123);

        AskChallenge action = new AskChallenge(current, other.id(), container.get(FightService.class));

        other.changeMap(
            container.get(ExplorationMapService.class).load(10540),
            123
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

        current.changeMap(container.get(ExplorationMapService.class).load(10340), 123);
        other.changeMap(current.map(), 123);

        AskChallenge action = new AskChallenge(current, other.id(), container.get(FightService.class));

        action.start(new ActionQueue());

        requestStack.assertLast(new GameActionResponse(action));

        assertTrue(current.interactions().interacting());
        assertTrue(other.interactions().interacting());

        assertSame(other, current.interactions().get(InitiatorDialog.class).interlocutor());
        assertSame(current, other.interactions().get(ChallengerDialog.class).interlocutor());
    }
}
