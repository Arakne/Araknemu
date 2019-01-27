package fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.dialog;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.ResponseAction;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.dialog.NpcDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.DialogService;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.NpcQuestion;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.ActionFactory;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Collection;

/**
 * Start next dialog question
 *
 * The check will failed if cannot found an available question (condition check)
 */
final public class NextQuestion implements Action {
    final static public class Factory implements ActionFactory {
        final private DialogService service;

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

    final private DialogService service;
    final private int[] questionIds;

    // Lazy loading of questions : prevent from stack overflow
    private Collection<NpcQuestion> questions;

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
}
