package fr.quatrevieux.araknemu.game.handler.account;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.account.AskRegionalVersion;
import fr.quatrevieux.araknemu.network.game.out.account.RegionalVersion;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

/**
 * Send the regional version of the server
 */
final public class SendRegionalVersion implements PacketHandler<GameSession, AskRegionalVersion> {
    @Override
    public void handle(GameSession session, AskRegionalVersion packet) throws Exception {
        session.write(new RegionalVersion(0)); // @todo configure
    }

    @Override
    public Class<AskRegionalVersion> packet() {
        return AskRegionalVersion.class;
    }
}
