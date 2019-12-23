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

package fr.quatrevieux.araknemu.game.handler.dialog;

import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.dialog.NpcDialog;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMapService;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.core.network.exception.CloseImmediately;
import fr.quatrevieux.araknemu.network.game.in.dialog.CreateDialogRequest;
import fr.quatrevieux.araknemu.network.game.out.dialog.DialogCreated;
import fr.quatrevieux.araknemu.network.game.out.dialog.DialogQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class StartDialogTest extends GameBaseCase {
    private ExplorationPlayer player;
    private StartDialog handler;
    private ExplorationMap map;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet
            .pushNpcs()
            .pushMaps()
            .pushAreas()
            .pushSubAreas()
        ;

        map = container.get(ExplorationMapService.class).load(10340);
        player = explorationPlayer();
        player.join(map);

        handler = new StartDialog();
        requestStack.clear();
    }

    @Test
    void handleSuccess() {
        handler.handle(session, new CreateDialogRequest(-47204));

        assertTrue(player.interactions().interacting());
        player.interactions().get(NpcDialog.class).forQuestion(3786);

        GameNpc npc = (GameNpc) map.creature(-47204);

        requestStack.assertAll(
            new DialogCreated(npc),
            new DialogQuestion(npc.question(player).get(), npc.question(player).get().responses(player), player)
        );
    }

    @Test
    void handleNotFound() {
        assertThrows(NoSuchElementException.class, () -> handler.handle(session, new CreateDialogRequest(404)));
        assertFalse(player.interactions().interacting());
    }

    @Test
    void handleNotNpc() throws Exception {
        ExplorationPlayer other = makeOtherExplorationPlayer();
        other.join(map);

        assertThrows(IllegalArgumentException.class, () -> handler.handle(session, new CreateDialogRequest(other.id())));
        assertFalse(player.interactions().interacting());
    }

    @Test
    void functionalSuccess() throws Exception {
        handlePacket(new CreateDialogRequest(-47204));

        assertTrue(player.interactions().interacting());
    }

    @Test
    void functionalNotExploring() {
        session.setExploration(null);

        assertThrows(CloseImmediately.class, () -> handlePacket(new CreateDialogRequest(-47204)));
    }
}
