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

package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.provider;

import fr.arakne.utils.value.Interval;
import fr.arakne.utils.value.helper.RandomUtil;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.fighter.monster.MonsterFighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;

/**
 * Provide win kamas on Pvm fight
 */
public final class PvmKamasProvider implements DropRewardProvider {
    private final RandomUtil random = new RandomUtil();

    @Override
    public Scope initialize(EndFightResults results) {
        return reward -> reward.setKamas(
            random.rand(
                results.applyToLoosers(new ExtractKamas()).get()
            )
        );
    }

    private static class ExtractKamas implements FighterOperation {
        private int minKamas = 0;
        private int maxKamas = 0;

        @Override
        public void onMonster(MonsterFighter fighter) {
            minKamas += fighter.reward().kamas().min();
            maxKamas += fighter.reward().kamas().max();
        }

        public Interval get() {
            return new Interval(minKamas, maxKamas);
        }
    }
}
