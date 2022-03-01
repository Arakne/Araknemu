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
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.challenge.ChallengeInvitationHandler;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.builder.ChallengeBuilder;

/**
 * Ask for challenge
 */
public final class AskChallenge implements Action {
    private final ExplorationPlayer player;
    private final int target;
    private final FightService fightService;

    public AskChallenge(ExplorationPlayer player, int target, FightService fightService) {
        this.player = player;
        this.target = target;
        this.fightService = fightService;
    }

    @Override
    public void start(ActionQueue queue) {
        final ExplorationMap map = player.map();

        if (map != null) {
            map.creature(target).apply(new Operation() {
                @Override
                public void onExplorationPlayer(ExplorationPlayer challenger) {
                    player
                        .interactions()
                        .start(new ChallengeInvitationHandler(fightService.handler(ChallengeBuilder.class)).invitation(player, challenger))
                    ;
                }
            });
        }
    }

    @Override
    public ExplorationPlayer performer() {
        return player;
    }

    @Override
    public ActionType type() {
        return ActionType.CHALLENGE;
    }

    @Override
    public Object[] arguments() {
        return new Object[] {target};
    }
}
