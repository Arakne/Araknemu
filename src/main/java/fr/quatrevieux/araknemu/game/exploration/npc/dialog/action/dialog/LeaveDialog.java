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
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.Action;
import fr.quatrevieux.araknemu.game.exploration.npc.dialog.action.ActionFactory;

/**
 * Leave current NPC dialog
 */
public final class LeaveDialog implements Action {
    public static final class Factory implements ActionFactory {
        private static final LeaveDialog INSTANCE = new LeaveDialog();

        @Override
        public String type() {
            return "LEAVE";
        }

        @Override
        public Action create(ResponseAction entity) {
            return INSTANCE;
        }
    }

    @Override
    public boolean check(ExplorationPlayer player) {
        return true;
    }

    @Override
    public void apply(ExplorationPlayer player) {
        player.interactions().get(NpcDialog.class).stop();
    }
}
