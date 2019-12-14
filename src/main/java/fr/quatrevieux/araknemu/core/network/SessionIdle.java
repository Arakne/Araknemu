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

package fr.quatrevieux.araknemu.core.network;

import java.time.Duration;

/**
 * Pseudo packet used when the session raise its inactivity time
 *
 * Note: Only read idle is considered
 */
final public class SessionIdle implements InternalPacket {
    final private Duration duration;

    public SessionIdle(Duration duration) {
        this.duration = duration;
    }

    /**
     * The IDLE duration
     */
    public Duration duration() {
        return duration;
    }
}
