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

package fr.quatrevieux.araknemu.network.game;

import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.network.session.AbstractDelegatedSession;
import fr.quatrevieux.araknemu.core.network.session.Session;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.player.GamePlayer;

/**
 * Session wrapper for game server
 */
final public class GameSession extends AbstractDelegatedSession implements Session, Dispatcher {
    private GameAccount account;
    private GamePlayer player;
    private ExplorationPlayer exploration;
    private PlayerFighter fighter;

    public GameSession(Session session) {
        super(session);
    }

    /**
     * Attach an game account to the session
     *
     * @param account Account to attach
     */
    public void attach(GameAccount account) {
        this.account = account;
    }

    /**
     * Get the attached account
     */
    public GameAccount account() {
        return account;
    }

    /**
     * Check if an account is attached
     */
    public boolean isLogged() {
        return account != null;
    }

    /**
     * Remove the attached account
     * @return The attached account
     */
    public GameAccount detach() {
        return account = null;
    }

    /**
     * Set the logged player
     *
     * @throws IllegalStateException When a player is already set
     */
    public void setPlayer(GamePlayer player) {
        if (this.player != null && player != null) {
            throw new IllegalStateException("A player is already loaded");
        }

        this.player = player;
    }

    /**
     * Get the logged player
     *
     * @return The player instance, or null is not in game
     */
    public GamePlayer player() {
        return player;
    }

    /**
     * Get the exploration player
     *
     * @return The player instance, or null if not on exploration
     */
    public ExplorationPlayer exploration() {
        return exploration;
    }

    /**
     * Set the exploration player
     */
    public void setExploration(ExplorationPlayer exploration) {
        this.exploration = exploration;
    }

    /**
     * Get the fighter
     *
     * @return The fighter or null is not fighting
     */
    public PlayerFighter fighter() {
        return fighter;
    }

    /**
     * Set the fighter
     */
    public void setFighter(PlayerFighter fighter) {
        this.fighter = fighter;
    }

    @Override
    public void dispatch(Object event) {
        if (player != null) {
            player.dispatcher().dispatch(event);
        }

        if (exploration != null) {
            exploration.dispatcher().dispatch(event);
        }

        if (fighter != null) {
            fighter.dispatcher().dispatch(event);
        }
    }
}
