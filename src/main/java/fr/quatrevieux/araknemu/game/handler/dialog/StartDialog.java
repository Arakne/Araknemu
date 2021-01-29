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

package fr.quatrevieux.araknemu.game.handler.dialog;

import fr.quatrevieux.araknemu.core.network.parser.PacketHandler;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;
import fr.quatrevieux.araknemu.game.exploration.interaction.dialog.NpcDialog;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.dialog.CreateDialogRequest;

/**
 * Start a new dialog with NPC
 */
final public class StartDialog implements PacketHandler<GameSession, CreateDialogRequest> {
    @Override
    public void handle(GameSession session, CreateDialogRequest packet) {
        final ExplorationPlayer exploration = session.exploration();

        exploration.map().creature(packet.npcId()).apply(new Operation() {
            @Override
            public void onExplorationPlayer(ExplorationPlayer player) {
                throw new IllegalArgumentException("Cannot start a dialog with a player");
            }

            @Override
            public void onNpc(GameNpc npc) {
                exploration.interactions().start(new NpcDialog(exploration, npc));
            }
        });
    }

    @Override
    public Class<CreateDialogRequest> packet() {
        return CreateDialogRequest.class;
    }
}
