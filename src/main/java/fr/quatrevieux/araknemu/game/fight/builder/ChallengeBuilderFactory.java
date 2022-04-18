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
import fr.quatrevieux.araknemu.game.fight.type.FightType;

/**
 * Create the challenge builder
 */
public final class ChallengeBuilderFactory implements FightBuilderFactory<ChallengeBuilder> {
    private final FighterFactory fighterFactory;
    private final FightType type;
    private final RandomUtil random = new RandomUtil();

    public ChallengeBuilderFactory(FighterFactory fighterFactory, FightType type) {
        this.fighterFactory = fighterFactory;
        this.type = type;
    }

    @Override
    public Class<ChallengeBuilder> type() {
        return ChallengeBuilder.class;
    }

    @Override
    public ChallengeBuilder create(FightService service) {
        return new ChallengeBuilder(service, fighterFactory, random, type);
    }
}
