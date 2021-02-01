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

package fr.quatrevieux.araknemu.network.game.out.dialog;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.NpcQuestion;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.Response;

import java.util.Collection;

/**
 * Send question and responses for a dialog
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Dialog.as#L60
 */
public final class DialogQuestion {
    private final NpcQuestion question;
    private final Collection<Response> responses;
    private final ExplorationPlayer interlocutor;

    public DialogQuestion(NpcQuestion question, Collection<Response> responses, ExplorationPlayer interlocutor) {
        this.question = question;
        this.responses = responses;
        this.interlocutor = interlocutor;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder(64);

        sb.append("DQ").append(question.id());

        boolean first = true;
        for (Object parameter : question.parameters(interlocutor)) {
            sb.append(first ? ';' : ',').append(parameter);
            first = false;
        }

        first = true;
        for (Response response : responses) {
            sb.append(first ? '|' : ';').append(response.id());
            first = false;
        }

        return sb.toString();
    }
}
