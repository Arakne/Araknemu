package fr.quatrevieux.araknemu.game.fight.fighter.operation;

import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

/**
 * Try to send a packet to a fighter
 */
final public class SendPacket implements FighterOperation {
    final private String packet;

    public SendPacket(Object packet) {
        this.packet = packet.toString();
    }

    @Override
    public void onPlayer(PlayerFighter fighter) {
        fighter.send(packet);
    }
}
