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

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.Interaction;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.NpcQuestion;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.Response;
import fr.quatrevieux.araknemu.network.game.out.dialog.DialogCreated;
import fr.quatrevieux.araknemu.network.game.out.dialog.DialogCreationError;
import fr.quatrevieux.araknemu.network.game.out.dialog.DialogLeaved;
import fr.quatrevieux.araknemu.network.game.out.dialog.DialogQuestion;

import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * Interaction for dialog with NPC
 */
public final class NpcDialog implements Interaction {
    private final ExplorationPlayer player;
    private final GameNpc npc;

    /**
     * Store the current question
     */
    private NpcQuestion question;

    /**
     * Store available responses
     */
    private Collection<Response> responses;

    public NpcDialog(ExplorationPlayer player, GameNpc npc) {
        this.player = player;
        this.npc = npc;
    }

    @Override
    public Interaction start() {
        return npc.question(player)
            .map(this::success)
            .orElseGet(this::error)
        ;
    }

    @Override
    public void stop() {
        player.interactions().remove();
        player.send(new DialogLeaved());
    }

    /**
     * Start next dialog question
     */
    public void next(NpcQuestion question) {
        this.question = question;
        this.responses = question.responses(player);

        player.send(new DialogQuestion(question, responses, player));
    }

    /**
     * Check the current dialog question id
     *
     * @throws IllegalArgumentException When invalid question id is given
     */
    public NpcDialog forQuestion(int id) {
        if (question.id() != id) {
            throw new IllegalArgumentException("Invalid question id");
        }

        return this;
    }

    /**
     * Perform the response action
     *
     * @param id The chosen response
     *
     * @throws NoSuchElementException When response is not available
     */
    public void answer(int id) {
        for (Response response : responses) {
            if (response.id() == id) {
                response.apply(player);
                return;
            }
        }

        throw new NoSuchElementException("Invalid response id");
    }

    /**
     * The dialog is successfully started
     */
    private Interaction success(NpcQuestion question) {
        player.send(new DialogCreated(npc));
        next(question);

        return this;
    }

    /**
     * Cannot start the dialog
     */
    private Interaction error() {
        player.send(new DialogCreationError());

        return null;
    }
}
