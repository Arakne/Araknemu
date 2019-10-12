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

package fr.quatrevieux.araknemu.game.exploration.interaction.dialog;

import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.NpcService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.DialogService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.NpcQuestion;
import fr.quatrevieux.araknemu.network.game.out.dialog.DialogCreated;
import fr.quatrevieux.araknemu.network.game.out.dialog.DialogCreationError;
import fr.quatrevieux.araknemu.network.game.out.dialog.DialogLeaved;
import fr.quatrevieux.araknemu.network.game.out.dialog.DialogQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

class NpcDialogTest extends GameBaseCase {
    private ExplorationPlayer player;
    private NpcService service;
    private DialogService dialogService;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        dataSet.pushNpcs();

        player = explorationPlayer();
        service = container.get(NpcService.class);
        dialogService = container.get(DialogService.class);

        requestStack.clear();
    }

    @Test
    void startSuccess() {
        GameNpc npc = service.get(472);
        player.interactions().start(new NpcDialog(player, npc));

        assertTrue(player.interactions().busy());
        assertTrue(player.interactions().interacting());

        requestStack.assertAll(
            new DialogCreated(npc),
            new DialogQuestion(
                npc.question(player).get(),
                npc.question(player).get().responses(player),
                player
            )
        );
    }

    @Test
    void startNoQuestionFound() throws ContainerException {
        GameNpc npc = new GameNpc(
            dataSet.refresh(new Npc(472, 0, null, null, null)),
            dataSet.refresh(new NpcTemplate(878, 0, 0, 0, null, null, null, 0, 0, null)),
            Collections.emptyList(),
            Collections.emptyList()
        );
        player.interactions().start(new NpcDialog(player, npc));

        assertFalse(player.interactions().busy());
        assertFalse(player.interactions().interacting());

        requestStack.assertAll(new DialogCreationError());
    }

    @Test
    void stop() {
        GameNpc npc = service.get(472);
        player.interactions().start(new NpcDialog(player, npc));

        assertTrue(player.interactions().busy());
        assertTrue(player.interactions().interacting());

        player.interactions().stop();

        assertFalse(player.interactions().busy());
        assertFalse(player.interactions().interacting());

        requestStack.assertLast(new DialogLeaved());
    }

    @Test
    void next() {
        GameNpc npc = service.get(472);
        player.interactions().start(new NpcDialog(player, npc));

        NpcQuestion question = dialogService.byIds(new int[] {3787}).stream().findFirst().get();

        player.interactions().get(NpcDialog.class).next(question);

        assertSame(player.interactions().get(NpcDialog.class), player.interactions().get(NpcDialog.class).forQuestion(3787));
        requestStack.assertLast(new DialogQuestion(question, question.responses(player), player));
    }

    @Test
    void answerNextQuestion() {
        GameNpc npc = service.get(472);
        player.interactions().start(new NpcDialog(player, npc));

        NpcDialog dialog = player.interactions().get(NpcDialog.class);

        dialog
            .forQuestion(3786)
            .answer(3323)
        ;

        NpcQuestion question = dialogService.byIds(new int[] {3787}).stream().findFirst().get();
        requestStack.assertLast(new DialogQuestion(question, question.responses(player), player));
    }

    @Test
    void answerLeave() {
        GameNpc npc = service.get(472);
        player.interactions().start(new NpcDialog(player, npc));

        NpcDialog dialog = player.interactions().get(NpcDialog.class);

        dialog
            .forQuestion(3786)
            .answer(3324)
        ;

        requestStack.assertLast(new DialogLeaved());
        assertFalse(player.interactions().busy());
    }

    @Test
    void answerInvalidResponse() {
        GameNpc npc = service.get(472);
        player.interactions().start(new NpcDialog(player, npc));

        NpcDialog dialog = player.interactions().get(NpcDialog.class);

        assertThrows(NoSuchElementException.class, () -> dialog.forQuestion(3786).answer(1111));
    }

    @Test
    void forQuestionInvalid() {
        GameNpc npc = service.get(472);
        player.interactions().start(new NpcDialog(player, npc));

        assertThrows(IllegalArgumentException.class, () -> player.interactions().get(NpcDialog.class).forQuestion(1111));
    }
}
