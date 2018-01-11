package fr.quatrevieux.araknemu.network.game.out.basic;

import java.util.Calendar;

/**
 * The server date
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Basics.as#L158
 */
final public class ServerDate {
    final private Calendar calendar;

    public ServerDate(Calendar calendar) {
        this.calendar = calendar;
    }

    @Override
    public String toString() {
        return String.format(
            "BD%04d|%02d|%02d",
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH) + 1,
            calendar.get(Calendar.DAY_OF_MONTH)
        );
    }
}
