package fr.quatrevieux.araknemu.util;

import java.util.Calendar;

/**
 * Handle date for Dofus server
 */
final public class DofusDate {
    final static public int YEAR_OFFSET = -1370;

    final private Calendar calendar;

    public DofusDate() {
        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, YEAR_OFFSET);
    }

    /**
     * Get the date to milliseconds
     */
    public long toMilliseconds() {
        return calendar.getTimeInMillis() + calendar.get(Calendar.ZONE_OFFSET);
    }

    public int year() {
        return calendar.get(Calendar.YEAR);
    }

    public int month() {
        return calendar.get(Calendar.MONTH) + 1;
    }

    public int day() {
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * Create a new past date from duration in milliseconds
     *
     * @param duration The duration in milliseconds
     */
    static public DofusDate fromDuration(long duration) {
        DofusDate date = new DofusDate();
        date.calendar.setTimeInMillis(date.calendar.getTimeInMillis() - duration);

        return date;
    }
}
