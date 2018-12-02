package fr.quatrevieux.araknemu.game.handler.spell;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.spell.SpellMove;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Move the spell from spell book
 */
final public class MoveSpell implements PacketHandler<GameSession, SpellMove> {
    @Override
    public void handle(GameSession session, SpellMove packet) throws Exception {
        session.player().properties().spells()
            .entry(packet.spellId())
            .move(packet.position())
        ;
    }

    @Override
    public Class<SpellMove> packet() {
        return SpellMove.class;
    }
}
