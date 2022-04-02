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

package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.dialog;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.dialog.NpcDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.DialogService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.NpcQuestion;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.ActionFactory;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;

import java.util.Arrays;
import java.util.Collection;

/**
 * Start next dialog question
 *
 * The check will failed if cannot found an available question (condition check)
 */
public final class NextQuestion implements Action {
    private final DialogService service;
    private final int[] questionIds;

    // Lazy loading of questions : prevent from stack overflow
    private @MonotonicNonNull Collection<NpcQuestion> questions;

    public NextQuestion(DialogService service, int[] questionIds) {
        this.service = service;
        this.questionIds = questionIds;
    }

    @Override
    public boolean check(ExplorationPlayer player) {
        return questions().stream().anyMatch(question -> question.check(player));
    }

    @Override
    public void apply(ExplorationPlayer player) {
        questions().stream()
            .filter(question -> question.check(player)).findFirst()
            .ifPresent(player.interactions().get(NpcDialog.class)::next)
        ;
    }

    /**
     * Lazy load questions : the first call will load questions, and next calls will always returns the same value
     */
    private Collection<NpcQuestion> questions() {
        if (questions != null) {
            return questions;
        }

        return questions = service.byIds(questionIds);
    }

    public static final class Factory implements ActionFactory {
        private final DialogService service;

        public Factory(DialogService service) {
            this.service = service;
        }

        @Override
        public String type() {
            return "NEXT";
        }

        @Override
        public Action create(ResponseAction entity) {
            return new NextQuestion(
                service,
                Arrays.stream(StringUtils.split(entity.arguments(), ";"))
                    .mapToInt(Integer::parseInt)
                    .toArray()
            );
        }
    }
}
