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
final public class DialogQuestion {
    final private NpcQuestion question;
    final private Collection<Response> responses;
    final private ExplorationPlayer interlocutor;

    public DialogQuestion(NpcQuestion question, Collection<Response> responses, ExplorationPlayer interlocutor) {
        this.question = question;
        this.responses = responses;
        this.interlocutor = interlocutor;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(64);

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
