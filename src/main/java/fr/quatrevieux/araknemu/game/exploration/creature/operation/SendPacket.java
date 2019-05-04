package fr.quatrevieux.araknemu.game.exploration.creature.operation;

import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.exploration.npc.GameNpc;
import fr.quatrevieux.araknemu.game.exploration.creature.Operation;

/**
 * Send the packet to creatures which supports sending packet
 */
final public class SendPacket implements Operation {
    final private String packet;

    public SendPacket(Object packet) {
        this.packet = packet.toString();
    }

    @Override
    public void onExplorationPlayer(ExplorationPlayer player) {
        player.send(packet);
    }

    @Override
    public void onNpc(GameNpc npc) {}
}
