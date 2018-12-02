package fr.quatrevieux.araknemu.game.handler.spell;

import fr.quatrevieux.araknemu.network.exception.ErrorPacket;
import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.spell.SpellUpgrade;
import fr.quatrevieux.araknemu.network.game.out.spell.SpellUpgradeError;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Upgrade the spell
 */
final public class UpgradeSpell implements PacketHandler<GameSession, SpellUpgrade> {
    @Override
    public void handle(GameSession session, SpellUpgrade packet) throws Exception {
        try {
            session.player().properties().spells()
                .entry(packet.spellId())
                .upgrade()
            ;
        } catch (Exception e) {
            throw new ErrorPacket(new SpellUpgradeError(), e);
        }
    }

    @Override
    public Class<SpellUpgrade> packet() {
        return SpellUpgrade.class;
    }
}
