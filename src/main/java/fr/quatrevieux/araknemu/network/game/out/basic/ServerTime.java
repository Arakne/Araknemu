package fr.quatrevieux.araknemu.network.game.out.basic;

import java.util.Calendar;

/**
 * Server time in milliseconds
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Basics.as#L153
 */
final public class ServerTime {
    final private Calendar calendar;

    public ServerTime(Calendar calendar) {
        this.calendar = calendar;
    }

    @Override
    public String toString() {
        return "BT" + (calendar.getTimeInMillis() + calendar.get(Calendar.ZONE_OFFSET));
    }
}
