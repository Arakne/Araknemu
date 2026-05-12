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

import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;

/**
 * Synchronize exploration player life points with the fighter
 */
public final class SynchronizeLife implements DropRewardAction {

    private final boolean shouldFullHeal;
    private static SetDeadOp setDeadOp = new SetDeadOp();
    private static SyncLifeOp syncLifeOp = new SyncLifeOp();
    private static FullHealOp fullHealOp = new FullHealOp();

    public SynchronizeLife(boolean shouldFullHeal) {
        this.shouldFullHeal = shouldFullHeal;
    }

    @Override
    public void apply(DropReward reward, Fighter fighter) {
        if (shouldFullHeal) {
            fighter.apply(fullHealOp);
            return;
        }

        if (reward.type().equals(RewardType.WINNER)) {
            fighter.apply(syncLifeOp);
        }

        if (reward.type().equals(RewardType.LOOSER)) {
            fighter.apply(setDeadOp);
        }
    }

    private static class SetDeadOp implements FighterOperation {
        @Override
        public void onPlayer(PlayerFighter fighter) {
            fighter.player().properties().life().set(0);
        }
    }

    private static class SyncLifeOp implements FighterOperation {
        @Override
        public void onPlayer(PlayerFighter fighter) {
            // @todo set percent
            fighter.player().properties().life().set(
                fighter.player().properties().life().max() * fighter.life().current() / fighter.life().max()
            );
        }
    }

    private static class FullHealOp implements FighterOperation {
        @Override
        public void onPlayer(PlayerFighter fighter) {
            fighter.player().properties().life().set(
                fighter.player().properties().life().max()
            );
        }
    }
}
