/*
 * This file is part of Araknemu.
 *
 * Araknemu is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Araknemu is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Araknemu.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2017-2019 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.util;

import java.util.Calendar;

/**
 * Handle date for Dofus server
 */
public final class DofusDate {
    public static final int YEAR_OFFSET = -1370;

    private final Calendar calendar;

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
    public static DofusDate fromDuration(long duration) {
        final DofusDate date = new DofusDate();

        date.calendar.setTimeInMillis(date.calendar.getTimeInMillis() - duration);

        return date;
    }
}
