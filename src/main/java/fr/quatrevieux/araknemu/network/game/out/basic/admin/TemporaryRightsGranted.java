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
 * Copyright (c) 2017-2021 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.network.game.out.basic.admin;

/**
 * Temporary admin rights has been granted to the account
 * This packet allows the client to open admin console
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Basics.as#L67
 */
public final class TemporaryRightsGranted {
    private final String performer;

    /**
     * @param performer The admin performer pseudo
     */
    public TemporaryRightsGranted(String performer) {
        this.performer = performer;
    }

    @Override
    public String toString() {
        return "BAIO" + performer;
    }
}
