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

package fr.quatrevieux.araknemu.core.network.exception;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Simple error which result of an error response
 */
public class ErrorPacket extends HandlingException implements WritePacket {
    private final Object packet;

    public ErrorPacket(String message, Object packet) {
        super(message);
        this.packet = packet;
    }

    public ErrorPacket(Object packet) {
        this.packet = packet;
    }

    public ErrorPacket(Object packet, @Nullable Throwable cause) {
        super(cause);
        this.packet = packet;
    }

    @Override
    public Object packet() {
        return packet;
    }
}
