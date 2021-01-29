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

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.util.EnumMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Argon2 hash algorithm implementation for password
 */
final public class Argon2Hash implements HashAlgorithm {
    class Argon2Password implements Password {
        final private String hash;
        final private Argon2Factory.Argon2Types hashType;

        public Argon2Password(String hash) {
            this.hash = hash;
            this.hashType = typeOf(hash);
        }

        @Override
        public boolean check(String input) {
            return argon2(hashType).verify(hash, input.getBytes());
        }

        @Override
        public boolean needRehash() {
            return argon2(hashType).needsRehash(hash, iterations, memory, parallelism) || hashType != type;
        }

        @Override
        public HashAlgorithm algorithm() {
            return Argon2Hash.this;
        }

        @Override
        public String toString() {
            return hash;
        }
    }

    final private Map<Argon2Factory.Argon2Types, Argon2> algorithms = new EnumMap<>(Argon2Factory.Argon2Types.class);

    private int iterations = 4;
    private int memory = 64 * 1024;
    private int parallelism = 8;
    private Argon2Factory.Argon2Types type = Argon2Factory.Argon2Types.ARGON2id;

    @Override
    public Password parse(String hashedValue) {
        return new Argon2Password(hashedValue);
    }

    @Override
    public boolean supports(String hashedValue) {
        return hashedValue.startsWith("$argon2");
    }

    @Override
    public Password hash(String inputValue) {
        return new Argon2Password(argon2(type).hash(iterations, memory, parallelism, inputValue.getBytes()));
    }

    @Override
    public String name() {
        return "argon2";
    }

    /**
     * Define number of iterations (higher value is longer for hash)
     */
    public Argon2Hash setIterations(int iterations) {
        this.iterations = iterations;
        return this;
    }

    /**
     * Define memory usage for hashing, in kilobits
     * Higher value takes longer time to hash
     */
    public Argon2Hash setMemory(int memory) {
        this.memory = memory;
        return this;
    }

    /**
     * Define the parallelism (i.e. number of core used to compute hash)
     */
    public Argon2Hash setParallelism(int parallelism) {
        this.parallelism = parallelism;
        return this;
    }

    /**
     * Define the argon2 type (i, d, or id)
     */
    public Argon2Hash setType(Argon2Factory.Argon2Types type) {
        if (this.type != type) {
            this.type = type;
        }

        return this;
    }

    /**
     * Get the argon2 algorithm for the given type
     */
    private Argon2 argon2(Argon2Factory.Argon2Types type) {
        return algorithms.computeIfAbsent(type, Argon2Factory::create);
    }

    /**
     * Parse the argon2 type from the hashed value
     */
    private Argon2Factory.Argon2Types typeOf(String hashedValue) {
        return typeByName(hashedValue.substring(1, hashedValue.indexOf('$', "$argon2".length())));
    }

    /**
     * Get the argon2 type from the type name
     *
     * @param typeName The type name, in case insensitive
     * @throws NoSuchElementException When the given name is invalid
     */
    static public Argon2Factory.Argon2Types typeByName(String typeName) {
        switch (typeName.toLowerCase()) {
            case "argon2i":
                return Argon2Factory.Argon2Types.ARGON2i;
            case "argon2d":
                return Argon2Factory.Argon2Types.ARGON2d;
            case "argon2id":
                return Argon2Factory.Argon2Types.ARGON2id;
            default:
                throw new NoSuchElementException("Invalid argon2 type " + typeName);
        }
    }
}
