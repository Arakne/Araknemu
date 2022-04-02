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
 * Copyright (c) 2017-2020 Vincent Quatrevieux
 */

package fr.quatrevieux.araknemu.realm.authentication.password;

import org.checkerframework.checker.initialization.qual.UnderInitialization;
import org.checkerframework.checker.initialization.qual.UnknownInitialization;
import org.checkerframework.checker.nullness.qual.KeyFor;
import org.checkerframework.checker.nullness.qual.RequiresNonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Handle password hash algorithms
 */
public final class PasswordManager {
    private final List<@KeyFor("algorithms") String> availableAlgorithms;
    private final Map<String, HashAlgorithm> algorithms = new HashMap<>();

    public PasswordManager(List<String> availableAlgorithms, HashAlgorithm... algorithms) {
        for (HashAlgorithm algorithm : algorithms) {
            register(algorithm);
        }

        this.availableAlgorithms = checkAlgorithms(availableAlgorithms);
    }

    /**
     * Wrap an hashed password loaded from database
     */
    public Password get(String hashedPassword) {
        for (String name : availableAlgorithms) {
            final HashAlgorithm algorithm = algorithms.get(name);

            if (algorithm.supports(hashedPassword)) {
                return algorithm.parse(hashedPassword);
            }
        }

        throw new IllegalArgumentException("Cannot found any valid hash algorithm");
    }

    /**
     * Hash an plain password
     */
    public Password hash(String inputPassword) {
        return algorithms.get(availableAlgorithms.get(0)).hash(inputPassword);
    }

    /**
     * Rehash the password if needed
     *
     * The rehash is performed when :
     * - The default algorithm change
     * - The hash algorithm needs a rehash
     *
     * @param password The current hashed password
     * @param input The plain password
     * @param newPasswordAction Action to perform with new password (not called if the password is not rehashed)
     */
    public void rehash(Password password, String input, Consumer<Password> newPasswordAction) {
        if (!password.algorithm().name().equals(availableAlgorithms.get(0)) || password.needRehash()) {
            newPasswordAction.accept(hash(input));
        }
    }

    /**
     * Register a new algorithm
     */
    @RequiresNonNull("algorithms")
    public void register(@UnknownInitialization PasswordManager this, HashAlgorithm algorithm) {
        algorithms.put(algorithm.name(), algorithm);
    }

    @RequiresNonNull("algorithms")
    private List<@KeyFor("algorithms") String> checkAlgorithms(@UnderInitialization PasswordManager this, List<String> availableAlgorithms) {
        if (!algorithms.keySet().containsAll(availableAlgorithms)) {
            final List<String> invalidAlgorithms = new ArrayList<>(availableAlgorithms);
            invalidAlgorithms.removeAll(algorithms.keySet());

            throw new IllegalArgumentException("Hash algorithms " + invalidAlgorithms + " are not registered");
        }

        return (List<@KeyFor("algorithms") String>) availableAlgorithms;
    }
}
