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

package fr.quatrevieux.araknemu.game.fight.ending.reward.drop.pvm.provider;

import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.drop.DropReward;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.operation.FighterOperation;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.team.MonsterGroupTeam;

/**
 * Add end fight actions
 *
 * Actually only teleport action is available
 */
public final class PvmEndFightActionProvider implements DropRewardProvider {
    @Override
    public DropRewardProvider.Scope initialize(EndFightResults results) {
        return results.loosers().stream()
            .map(Fighter::team)
            .filter(team -> team instanceof MonsterGroupTeam)
            .map(team -> ((MonsterGroupTeam) team).group().winFightTeleportPosition())
            .findFirst()
            .filter(position -> !position.isNull())
            .<DropRewardProvider.Scope>map(Scope::new)
            .orElse(DropRewardProvider.Scope.NOOP)
        ;
    }

    private static class Scope implements DropRewardProvider.Scope {
        private final Position position;

        public Scope(Position position) {
            this.position = position;
        }

        @Override
        public void provide(DropReward reward) {
            reward.addAction((toApply, fighter) -> fighter.apply(new FighterOperation() {
                @Override
                public void onPlayer(PlayerFighter fighter) {
                    fighter.player().setPosition(position);
                }
            }));
        }
    }
}
