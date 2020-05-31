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

package fr.quatrevieux.araknemu.game.player.race;

import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.PreloadableService;
import fr.quatrevieux.araknemu.game.spell.SpellService;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Handle player race data
 */
final public class PlayerRaceService implements PreloadableService {
    final private PlayerRaceRepository repository;
    final private SpellService spellService;

    final private Map<Race, GamePlayerRace> races = new EnumMap<>(Race.class);

    public PlayerRaceService(PlayerRaceRepository repository, SpellService spellService) {
        this.repository = repository;
        this.spellService = spellService;
    }

    @Override
    public void preload(Logger logger) {
        logger.info("Loading races...");

        for (PlayerRace race : repository.load()) {
            races.put(race.race(), create(race));
        }

        logger.info("{} races loaded", races.size());
    }

    /**
     * Get a player race data
     */
    public GamePlayerRace get(Race race) {
        if (!races.containsKey(race)) {
            races.put(race, create(repository.get(race)));
        }

        return races.get(race);
    }

    private GamePlayerRace create(PlayerRace entity) {
        return new GamePlayerRace(
            entity,
            Arrays.stream(entity.spells())
                .mapToObj(spellService::get)
                .collect(Collectors.toList())
        );
    }
}
