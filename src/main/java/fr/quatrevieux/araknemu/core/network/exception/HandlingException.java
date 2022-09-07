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
 * Base exception which occurs during a normal handling of a packet
 * The sub-class exceptions are usually related to an action
 */
public class HandlingException extends RuntimeException {
    public HandlingException() {
    }

    public HandlingException(String message) {
        super(message);
    }

    public HandlingException(String message, @Nullable Throwable cause) {
        super(message, cause);
    }

    public HandlingException(@Nullable Throwable cause) {
        super(cause);
    }

    public HandlingException(String message, @Nullable Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
