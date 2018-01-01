package fr.quatrevieux.araknemu.network.game.out.game;

/**
 * Packet sent when map packets are all sent, and the map is ready for exploration
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Game.as#L426
 */
final public class MapReady {
    @Override
    public String toString() {
        return "GDK";
    }
}
