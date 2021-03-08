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

package fr.quatrevieux.araknemu.network.game.out.account;

/**
 * Error during generating name
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L1205
 */
public final class RandomNameGenerationError {
    public enum Error {
        UNDEFINED,
        CANNOT_GENERATE_PASSWORD
    }

    private final Error error;

    public RandomNameGenerationError(Error error) {
        this.error = error;
    }

    @Override
    public String toString() {
        return "APE" + (error.ordinal() + 1);
    }
}
