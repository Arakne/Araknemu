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

package fr.quatrevieux.araknemu.game.fight.team;

import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.game.fight.exception.JoinFightException;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.world.util.Sender;

import java.util.List;

/**
 * Team for fight
 */
public interface FightTeam extends Sender, Team<Fighter> {
    /**
     * Get the team leader (the fight initiator)
     */
    public Fighter leader();

    /**
     * Get the team id (must be unique over the map)
     * Generally the team id is the team leader id
     */
    public int id();

    /**
     * Get the team cell id on the map
     *
     * @todo return cell object ?
     */
    public int cell();

    /**
     * Get the team type
     *
     * https://github.com/Emudofus/Dofus/blob/1.29/dofus/Constants.as#L25
     */
    public int type();

    /**
     * Get the team alignment
     */
    public Alignment alignment();

    /**
     * Get start places
     */
    public List<Integer> startPlaces();

    /**
     * Send packet to all players
     */
    @Override
    public void send(Object packet);

    /**
     * Check if there is at least one alive fighter in the team
     */
    public boolean alive();

    /**
     * Add a new fighter to the team
     * When the fighter join the team it must be linked to the team (Fighter.team() must return the joined team)
     *
     * @param fighter Fighter to add
     *
     * @throws JoinFightException
     */
    public void join(Fighter fighter) throws JoinFightException;

    /**
     * Remove the fighter from the team
     *
     * @param fighter Fighter to remove
     */
    public void kick(Fighter fighter);
}
