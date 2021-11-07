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

package fr.quatrevieux.araknemu.network.game.out.exchange;

/**
 * The exchange is terminated (cancelled or accepted)
 *
 * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Exchange.as#L284
 */
public final class ExchangeLeaved {
    private final boolean accepted;

    public ExchangeLeaved(boolean accepted) {
        this.accepted = accepted;
    }

    @Override
    public String toString() {
        return "EV" + (accepted ? "a" : "");
    }

    /**
     * The exchange is cancelled
     */
    public static ExchangeLeaved cancelled() {
        return new ExchangeLeaved(false);
    }

    /**
     * The exchange is accepted
     */
    public static ExchangeLeaved accepted() {
        return new ExchangeLeaved(true);
    }
}
