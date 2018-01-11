package fr.quatrevieux.araknemu.game.handler.basic;

import fr.quatrevieux.araknemu.network.game.GameSession;
import fr.quatrevieux.araknemu.network.game.in.basic.AskDate;
import fr.quatrevieux.araknemu.network.game.out.basic.ServerDate;
import fr.quatrevieux.araknemu.network.game.out.basic.ServerTime;
import fr.quatrevieux.araknemu.network.in.PacketHandler;

import java.util.Calendar;

/**
 * Send the current server date
 */
final public class SendDateAndTime implements PacketHandler<GameSession, AskDate> {
    @Override
    public void handle(GameSession session, AskDate packet) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, -1370);

        session.write(new ServerDate(calendar));
        session.write(new ServerTime(calendar));
    }

    @Override
    public Class<AskDate> packet() {
        return AskDate.class;
    }
}
