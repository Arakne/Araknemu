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

package fr.quatrevieux.araknemu.game.account.generator;

import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;

/**
 * Check the generated name to be available
 */
public final class NameCheckerGenerator implements NameGenerator {
    private final NameGenerator generator;
    private final PlayerRepository repository;
    private final GameConfiguration configuration;

    public NameCheckerGenerator(NameGenerator generator, PlayerRepository repository, GameConfiguration configuration) {
        this.generator = generator;
        this.repository = repository;
        this.configuration = configuration;
    }

    @Override
    public String generate() throws NameGenerationException {
        for (int i = 0; i < 15; ++i) {
            final String generated = generator.generate();

            if (!repository.nameExists(configuration.id(), generated)) {
                return generated;
            }
        }

        throw new NameGenerationException("Reach the maximum try number");
    }
}
