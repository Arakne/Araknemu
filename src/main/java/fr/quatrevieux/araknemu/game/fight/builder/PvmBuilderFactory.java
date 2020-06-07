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

package fr.quatrevieux.araknemu.game.fight.builder;

import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.fight.FightService;
import fr.quatrevieux.araknemu.game.fight.fighter.FighterFactory;
import fr.quatrevieux.araknemu.game.fight.type.PvmType;
import org.apache.logging.log4j.Logger;

/**
 * Create the pvm builder
 */
final public class PvmBuilderFactory implements FightBuilderFactory<PvmBuilder> {
    final private FighterFactory fighterFactory;
    final private RandomUtil random;
    final private PvmType type;
    final private Logger logger;

    public PvmBuilderFactory(FighterFactory fighterFactory, PvmType type, Logger logger) {
        this.fighterFactory = fighterFactory;
        this.random = new RandomUtil();
        this.type = type;
        this.logger = logger;
    }

    @Override
    public Class<PvmBuilder> type() {
        return PvmBuilder.class;
    }

    @Override
    public PvmBuilder create(FightService service) {
        return new PvmBuilder(service, fighterFactory, random, type, logger);
    }
}
