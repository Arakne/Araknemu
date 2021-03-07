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

package fr.quatrevieux.araknemu.game.exploration.exchange;

/**
 * Processor for exchange between two parties
 * The processor instance must be shared between parties for synchronization purpose
 */
public final class ExchangeProcessor {
    private final ExchangePartyProcessor first;
    private final ExchangePartyProcessor second;

    public ExchangeProcessor(ExchangePartyProcessor first, ExchangePartyProcessor second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Process the exchange for the two parties
     */
    public synchronized void process() {
        // Exchange is not valid : clean the exchange and reset the accept
        // Use boolean operator | instead of logical one ||
        // to ensure that validate is called on both parties
        if (!first.validate() | !second.validate()) {
            resetAccept();
            return;
        }

        first.process(second);
        second.process(first);

        first.terminate(true);
        second.terminate(true);
    }

    /**
     * Leave and terminate the exchange
     */
    public synchronized void cancel() {
        first.terminate(false);
        second.terminate(false);
    }

    /**
     * Check if the both parties has accepted the exchange
     */
    public boolean accepted() {
        return first.accepted() && second.accepted();
    }

    /**
     * Reset parties acceptations on change
     */
    public synchronized void resetAccept() {
        first.resetAccept();
        second.resetAccept();
    }

    /**
     * Ensure that the exchange is not accepted
     */
    public void assertNotAccepted() {
        if (accepted()) {
            throw new IllegalStateException("Exchange is already accepted");
        }
    }
}
