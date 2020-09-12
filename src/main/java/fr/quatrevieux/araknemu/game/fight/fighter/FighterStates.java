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

package fr.quatrevieux.araknemu.game.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.fighter.event.FighterStateChanged;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Handler fighter states
 */
final public class FighterStates implements States {
    final private Fighter fighter;

    final private Map<Integer, Integer> states = new HashMap<>();

    public FighterStates(Fighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void push(int state, int duration) {
        boolean defined = states.containsKey(state);

        if (defined && !higherDuration(states.get(state), duration)) {
            return;
        }

        states.put(state, duration);

        fighter.fight().dispatch(
            new FighterStateChanged(
                fighter,
                state,
                defined ? FighterStateChanged.Type.UPDATE : FighterStateChanged.Type.ADD
            )
        );
    }

    @Override
    public void remove(int state) {
        if (states.remove(state) != null) {
            fighter.fight().dispatch(new FighterStateChanged(fighter, state, FighterStateChanged.Type.REMOVE));
        }
    }

    @Override
    public boolean has(int state) {
        return states.containsKey(state);
    }

    @Override
    public boolean hasAll(int[] states) {
        for (int state : states) {
            if (!has(state)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean hasOne(int[] states) {
        for (int state : states) {
            if (has(state)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void refresh() {
        for (int state : new ArrayList<>(states.keySet())) {
            int turns = states.get(state);

            switch (turns) {
                case -1:
                    break;

                case 0:
                case 1:
                    remove(state);
                    break;

                default:
                    states.put(state, turns - 1);
            }
        }
    }

    private boolean higherDuration(int baseDuration, int newDuration) {
        if (baseDuration == -1) {
            return false;
        }

        if (newDuration == -1) {
            return true;
        }

        return newDuration > baseDuration;
    }
}
