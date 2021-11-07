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

package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.game.player.event.RestrictionsChanged;
import fr.quatrevieux.araknemu.util.BitSet;

/**
 * Handle current player restrictions
 */
public final class Restrictions {
    public static enum Restriction {
        /** value: 1 */
        DENY_ASSAULT,
        /** value: 2 */
        DENY_CHALLENGE,
        /** value: 4 */
        DENY_EXCHANGE,
        /** value: 8 */
        ALLOW_ATTACK,
        /** value: 16 */
        DENY_CHAT,
        /** value: 32 */
        DENY_MERCHANT,
        /** value: 64 */
        DENY_USE_OBJECT,
        /** value: 128 */
        DENY_INTERACT_COLLECTOR,
        /** value: 256 */
        DENY_USE_IO,
        /** value: 512 */
        DENY_SPEAK_NPC,
        UNUSED_1024,
        UNUSED_2048,
        /** value: 4096 */
        ALLOW_DUNGEON_MUTANT,
        /** value: 8192 */
        ALLOW_MOVE_ALL_DIRECTION,
        /** value: 16384 */
        ALLOW_ATTACK_MONSTERS_MUTANT,
        /** value: 32768 */
        DENY_INTERACT_PRISM,
    }

    private final Dispatcher dispatcher;
    private final BitSet<Restriction> set;

    public Restrictions(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
        this.set = new BitSet<>();
    }

    /**
     * Add new restrictions
     */
    public void set(Restriction... restrictions) {
        boolean hasChanged = false;

        for (Restriction restriction : restrictions) {
            hasChanged |= set.set(restriction);
        }

        if (hasChanged) {
            dispatcher.dispatch(new RestrictionsChanged(this));
        }
    }

    /**
     * Remove restrictions
     */
    public void unset(Restriction... restrictions) {
        boolean hasChanged = false;

        for (Restriction restriction : restrictions) {
            hasChanged |= set.unset(restriction);
        }

        if (hasChanged) {
            dispatcher.dispatch(new RestrictionsChanged(this));
        }
    }

    /**
     * Get the integer value of restrictions
     */
    public int toInt() {
        return set.toInt();
    }

    /**
     * Check if the current player can assault an other player (alignment fight)
     */
    public boolean canAssault() {
        return !set.check(Restriction.DENY_ASSAULT);
    }

    /**
     * Check if the current player can ask a duel
     */
    public boolean canChallenge() {
        return !set.check(Restriction.DENY_CHALLENGE);
    }

    /**
     * Check if the current player can start an exchange with an other player
     */
    public boolean canExchange() {
        return !set.check(Restriction.DENY_EXCHANGE);
    }

    /**
     * Check if the player can attack a mutant player
     */
    public boolean canAttack() {
        return set.check(Restriction.ALLOW_ATTACK);
    }

    /**
     * Check if the player can use the chat
     */
    public boolean canChat() {
        return !set.check(Restriction.DENY_CHAT);
    }

    /**
     * Check if the player can organize its shop and be in merchant mode
     */
    public boolean canBeMerchant() {
        return !set.check(Restriction.DENY_MERCHANT);
    }

    /**
     * Check if the player can use on object from its inventory
     */
    public boolean canUseObject() {
        return !set.check(Restriction.DENY_USE_OBJECT);
    }

    /**
     * Check if the player can speak and interact with collectors
     */
    public boolean canInteractCollector() {
        return !set.check(Restriction.DENY_INTERACT_COLLECTOR);
    }

    /**
     * Check if the player can use interactive objects
     */
    public boolean canUseIO() {
        return !set.check(Restriction.DENY_USE_IO);
    }

    /**
     * Check if the player can speak with npc
     */
    public boolean canSpeakNPC() {
        return !set.check(Restriction.DENY_SPEAK_NPC);
    }

    /**
     * Check if the mutant can attack monsters or join fight with other players
     */
    public boolean canAttackDungeonWhenMutant() {
        return set.check(Restriction.ALLOW_DUNGEON_MUTANT);
    }

    /**
     * Check if the current player sprite has no directions restrictions (has 8 directions instead of 4)
     */
    public boolean canMoveAllDirections() {
        return set.check(Restriction.ALLOW_MOVE_ALL_DIRECTION);
    }

    /**
     * Check if the mutant can attack monsters
     * For join fights with players, see {@link Restrictions#canAttackDungeonWhenMutant()}
     */
    public boolean canAttackMonsterWhenMutant() {
        return set.check(Restriction.ALLOW_ATTACK_MONSTERS_MUTANT);
    }

    /**
     * Check if the player can use prisms
     */
    public boolean canInteractWithPrism() {
        return !set.check(Restriction.DENY_INTERACT_PRISM);
    }

    /**
     * Initialize restrictions when game player is loaded
     */
    void init(GamePlayer player) {
        set.set(Restriction.ALLOW_MOVE_ALL_DIRECTION);

        // @todo to remove when implemented
        set.set(Restriction.DENY_MERCHANT);
    }
}
