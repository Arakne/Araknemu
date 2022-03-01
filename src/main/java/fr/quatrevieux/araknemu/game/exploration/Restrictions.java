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

package fr.quatrevieux.araknemu.game.exploration;

import fr.quatrevieux.araknemu.game.exploration.event.RestrictionsChanged;
import fr.quatrevieux.araknemu.game.exploration.map.ExplorationMap;
import fr.quatrevieux.araknemu.util.BitSet;

/**
 * Handle exploration player restrictions
 */
public final class Restrictions {
    public static enum Restriction {
        /** Value : 1 */
        DENY_ASSAULT,
        /** Value : 2 */
        DENY_CHALLENGE,
        /** Value : 4 */
        DENY_EXCHANGE,
        /** Value : 8 */
        DENY_ATTACK,
        /** Value : 16 */
        FORCE_WALK,
        /** Value : 32 */
        IS_SLOW,
        /** Value : 64 */
        DENY_CREATURE_MODE,
        /** Value : 128 */
        IS_TOMB,
    }

    private static final LocalToExplorationMapping[] LOCAL_TO_EXPLORATION_MAPPING = new LocalToExplorationMapping[] {
        new LocalToExplorationMapping(fr.quatrevieux.araknemu.game.player.Restrictions::canAssault,   Restriction.DENY_ASSAULT),
        new LocalToExplorationMapping(fr.quatrevieux.araknemu.game.player.Restrictions::canChallenge, Restriction.DENY_CHALLENGE),
        new LocalToExplorationMapping(fr.quatrevieux.araknemu.game.player.Restrictions::canAttack,    Restriction.DENY_ATTACK),
        new LocalToExplorationMapping(fr.quatrevieux.araknemu.game.player.Restrictions::canExchange,  Restriction.DENY_EXCHANGE),
    };

    private final ExplorationPlayer player;
    private final BitSet<Restriction> set;

    public Restrictions(ExplorationPlayer player) {
        this.player = player;
        this.set = new BitSet<>();
    }

    /**
     * Get the integer value of restrictions
     */
    public int toInt() {
        return set.toInt();
    }

    /**
     * Check if the player can be assaulted (alignment fight)
     */
    public boolean canAssault() {
        return !set.check(Restriction.DENY_ASSAULT);
    }

    /**
     * Check if the can ask a duel to the player
     */
    public boolean canChallenge() {
        return !set.check(Restriction.DENY_CHALLENGE);
    }

    /**
     * Check if exchange is allowed with this player
     */
    public boolean canExchange() {
        return !set.check(Restriction.DENY_EXCHANGE);
    }

    /**
     * Check if the player (mutant) can be attacked
     */
    public boolean canAttack() {
        return !set.check(Restriction.DENY_ATTACK);
    }

    /**
     * Check if the player is forced to walk (run is denied)
     */
    public boolean forceWalk() {
        return set.check(Restriction.FORCE_WALK);
    }

    /**
     * Check if the player is move slowly
     */
    public boolean isSlow() {
        return set.check(Restriction.IS_SLOW);
    }

    /**
     * Check if the player is a tomb
     */
    public boolean isTomb() {
        return set.check(Restriction.IS_TOMB);
    }

    /**
     * Refresh the exploration restrictions according to the local player restrictions
     */
    public void refresh() {
        final fr.quatrevieux.araknemu.game.player.Restrictions localRestrictions = player.player().restrictions();
        final ExplorationMap map = player.map();

        boolean hasChanged = false;

        for (LocalToExplorationMapping mapping : LOCAL_TO_EXPLORATION_MAPPING) {
            if (!mapping.checker.check(localRestrictions)) {
                hasChanged |= set.set(mapping.restriction);
            } else {
                hasChanged |= set.unset(mapping.restriction);
            }
        }

        if (hasChanged && map != null) {
            map.dispatch(new RestrictionsChanged(player, this));
        }
    }

    private static final class LocalToExplorationMapping {
        private final Checker checker;
        private final Restriction restriction;

        public LocalToExplorationMapping(Checker checker, Restriction restriction) {
            this.checker = checker;
            this.restriction = restriction;
        }

        interface Checker {
            public boolean check(fr.quatrevieux.araknemu.game.player.Restrictions restrictions);
        }
    }
}
