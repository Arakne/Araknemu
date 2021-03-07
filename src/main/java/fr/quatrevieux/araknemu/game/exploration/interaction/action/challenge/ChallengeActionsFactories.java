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

package fr.quatrevieux.araknemu.game.exploration.interaction.action.challenge;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ExplorationActionRegistry;
import fr.quatrevieux.araknemu.game.fight.FightService;

/**
 * Register challenge actions
 */
public final class ChallengeActionsFactories implements ExplorationActionRegistry.SelfRegisterable {
    private final FightService fightService;

    public ChallengeActionsFactories(FightService fightService) {
        this.fightService = fightService;
    }

    @Override
    public void register(ExplorationActionRegistry factory) {
        factory.register(ActionType.CHALLENGE, this::ask);
        factory.register(ActionType.ACCEPT_CHALLENGE, this::accept);
        factory.register(ActionType.REFUSE_CHALLENGE, this::refuse);
    }

    private AskChallenge ask(ExplorationPlayer player, ActionType action, String[] arguments) {
        return new AskChallenge(player, Integer.parseInt(arguments[0]), fightService);
    }

    private AcceptChallenge accept(ExplorationPlayer player, ActionType action, String[] arguments) {
        return new AcceptChallenge(player, Integer.parseInt(arguments[0]));
    }

    private RefuseChallenge refuse(ExplorationPlayer player, ActionType action, String[] arguments) {
        return new RefuseChallenge(player, Integer.parseInt(arguments[0]));
    }
}
