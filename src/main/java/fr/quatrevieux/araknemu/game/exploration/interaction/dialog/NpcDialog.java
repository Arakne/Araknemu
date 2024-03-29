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
import org.checkerframework.checker.nullness.qual.EnsuresNonNull;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.checkerframework.dataflow.qual.Pure;

import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * Interaction for dialog with NPC
 */
public final class NpcDialog implements Interaction {
    private final ExplorationPlayer player;
    private final GameNpc npc;

    /**
     * Store the current dialog
     */
    private @MonotonicNonNull Current current;

    public NpcDialog(ExplorationPlayer player, GameNpc npc) {
        this.player = player;
        this.npc = npc;
    }

    @Override
    @SuppressWarnings("methodref.return") // orElseGet(null) is not supported
    public @Nullable Interaction start() {
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
        this.current = new Current(question, question.responses(player));

        player.send(new DialogQuestion(question, current.responses, player));
    }

    /**
     * Check the current dialog question id
     *
     * @throws IllegalArgumentException When invalid question id is given
     */
    @Pure
    @EnsuresNonNull({"current", "this.forQuestion(#1).current"})
    @SuppressWarnings("contracts.postcondition") // checker is so dumb...
    public @This NpcDialog forQuestion(int id) {
        if (current == null || current.question.id() != id) {
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
    @RequiresNonNull("current")
    public void answer(int id) {
        for (Response response : current.responses) {
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
    private @Nullable Interaction error() {
        player.send(new DialogCreationError());

        return null;
    }

    /**
     * Store the current dialog choices (question + responses)
     */
    private static final class Current {
        private final NpcQuestion question;
        private final Collection<Response> responses;

        public Current(NpcQuestion question, Collection<Response> responses) {
            this.question = question;
            this.responses = responses;
        }
    }
}
