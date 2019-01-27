package fr.quatrevieux.araknemu.game.exploration.npc.dialog;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Question;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.ParametersResolver;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Question of GameNpc
 */
final public class NpcQuestion {
    final private Question entity;
    final private Collection<Response> responses;
    final private ParametersResolver parametersResolver;

    public NpcQuestion(Question entity, Collection<Response> responses, ParametersResolver parametersResolver) {
        this.entity = entity;
        this.responses = responses;
        this.parametersResolver = parametersResolver;
    }

    /**
     * Get the question id
     * The question text is stored on swf, and linked with this id
     */
    public int id() {
        return entity.id();
    }

    /**
     * Check if the question can be asked to the player
     *
     * @return true if the question is available, or false for ask the next one
     */
    public boolean check(ExplorationPlayer player) {
        return true;
    }

    /**
     * Extract the question parameters (like name) from the player
     *
     * @return List of parameters. Must be stringifiable
     */
    public Object[] parameters(ExplorationPlayer player) {
        return Arrays.stream(entity.parameters())
            .map(parameter -> parametersResolver.resolve(parameter, player))
            .toArray()
        ;
    }

    /**
     * Get list of available responses
     */
    public Collection<Response> responses(ExplorationPlayer player) {
        return responses
            .stream()
            .filter(response -> response.check(player))
            .collect(Collectors.toList())
        ;
    }
}
