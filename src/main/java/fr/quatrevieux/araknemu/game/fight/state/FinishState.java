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

package fr.quatrevieux.araknemu.game.fight.state;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.ending.EndFightResults;
import fr.quatrevieux.araknemu.game.fight.ending.reward.FightRewardsSheet;
import fr.quatrevieux.araknemu.game.fight.event.FightFinished;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.team.FightTeam;
import fr.quatrevieux.araknemu.network.game.out.fight.FightEnd;

import java.util.ArrayList;
import java.util.List;

/**
 * The fight is terminated
 */
public final class FinishState implements FightState {
    @Override
    public void start(Fight fight) {
        final FightRewardsSheet rewardsSheet = fight.type().rewards().generate(results(fight));

        rewardsSheet.rewards().forEach(reward -> reward.fighter().dispatch(new FightFinished(reward)));

        fight.send(new FightEnd(rewardsSheet));

        fight.destroy();
    }

    @Override
    public int id() {
        return 4;
    }

    /**
     * Compute the end fight results
     */
    private EndFightResults results(Fight fight) {
        final List<Fighter> winners = new ArrayList<>();
        final List<Fighter> loosers = new ArrayList<>();

        for (FightTeam team : fight.teams()) {
            if (team.alive()) {
                winners.addAll(team.fighters());
            } else {
                loosers.addAll(team.fighters());
            }
        }

        return new EndFightResults(fight, winners, loosers);
    }
}
