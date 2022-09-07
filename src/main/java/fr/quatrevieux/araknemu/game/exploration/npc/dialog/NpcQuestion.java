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

package fr.quatrevieux.araknemu.game.exploration.npc.dialog;

import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Question;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.parameter.ParametersResolver;
import org.checkerframework.common.value.qual.MinLen;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Question of GameNpc
 */
public final class NpcQuestion {
    private final Question entity;
    private final Collection<Response> responses;
    private final ParametersResolver parametersResolver;

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
        return Arrays.<@MinLen(1) String>stream(entity.parameters())
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
