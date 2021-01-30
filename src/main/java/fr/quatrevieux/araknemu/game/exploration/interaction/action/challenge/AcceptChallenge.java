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
import fr.quatrevieux.araknemu.game.exploration.interaction.action.Action;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionQueue;
import fr.quatrevieux.araknemu.game.exploration.interaction.action.ActionType;
import fr.quatrevieux.araknemu.game.exploration.interaction.challenge.ChallengerDialog;

/**
 * Accept the challenge invitation
 */
final public class AcceptChallenge implements Action {
    final private ExplorationPlayer player;
    final private int target;

    public AcceptChallenge(ExplorationPlayer player, int target) {
        this.player = player;
        this.target = target;
    }

    @Override
    public void start(ActionQueue queue) {
        final ChallengerDialog dialog = player.interactions().get(ChallengerDialog.class);

        if (dialog.initiator().id() != target) {
            throw new IllegalArgumentException("Invalid challenge target");
        }

        dialog.accept();
    }

    @Override
    public ExplorationPlayer performer() {
        return player;
    }

    @Override
    public ActionType type() {
        return ActionType.ACCEPT_CHALLENGE;
    }

    @Override
    public Object[] arguments() {
        return new Object[] {target};
    }
}
