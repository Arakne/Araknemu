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

package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.action;

import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import org.checkerframework.checker.index.qual.Positive;

/**
 * Add kamas to fighter
 */
public final class AddKamas implements DropRewardAction {
    @Override
    public void apply(DropReward reward, Fighter fighter) {
        final long kamas = reward.kamas();

        if (kamas > 0) {
            fighter.apply(new Operation(kamas));
        }
    }

    private static class Operation implements FighterOperation {
        private final @Positive long kamas;

        public Operation(@Positive long kamas) {
            this.kamas = kamas;
        }

        @Override
        public void onPlayer(PlayerFighter fighter) {
            fighter.player().inventory().addKamas(kamas);
        }

        @Override
        public void onGenericFighter(Fighter fighter) {
            if (!fighter.invoked()) {
                return;
            }

            final Fighter invoker = fighter.invoker();

            if (invoker == null) {
                return;
            }

            invoker.apply(this);
        }
    }
}
