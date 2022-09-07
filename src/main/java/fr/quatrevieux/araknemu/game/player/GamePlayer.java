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

import fr.quatrevieux.araknemu.core.event.DefaultListenerAggregate;
import fr.quatrevieux.araknemu.core.event.ListenerAggregate;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.game.account.GameAccount;
import fr.quatrevieux.araknemu.game.chat.ChannelSet;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.exploration.ExplorationPlayer;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.spectator.Spectator;
import fr.quatrevieux.araknemu.game.player.experience.GamePlayerExperience;
import fr.quatrevieux.araknemu.game.player.inventory.LoadedInventory;
import fr.quatrevieux.araknemu.game.player.inventory.PlayerInventory;
import fr.quatrevieux.araknemu.game.player.race.GamePlayerRace;
import fr.quatrevieux.araknemu.game.player.spell.SpellBook;
import fr.quatrevieux.araknemu.game.player.sprite.GamePlayerSpriteInfo;
import fr.quatrevieux.araknemu.game.player.sprite.SpriteInfo;
import fr.quatrevieux.araknemu.network.game.GameSession;
import org.checkerframework.dataflow.qual.Pure;

import java.util.Set;

/**
 * GamePlayer object
 * A player is a logged character, with associated game session
 */
public final class GamePlayer implements PlayerSessionScope {
    private final GameAccount account;
    private final Player entity;
    private final PlayerService service;
    private final GameSession session;
    private final GamePlayerRace race;
    private final Set<ChannelType> channels;
    private final PlayerInventory inventory;
    private final SpriteInfo spriteInfo;
    private final PlayerData data;
    private final Restrictions restrictions;

    private final ListenerAggregate dispatcher = new DefaultListenerAggregate();

    private PlayerSessionScope scope;

    @SuppressWarnings({"assignment", "argument"})
    public GamePlayer(GameAccount account, Player entity, GamePlayerRace race, GameSession session, PlayerService service, LoadedInventory inventory, SpellBook spells, GamePlayerExperience experience) {
        this.account = account;
        this.entity = entity;
        this.race = race;
        this.session = session;
        this.service = service;
        this.channels = new ChannelSet(entity.channels(), dispatcher);
        this.inventory = inventory.attach(this);
        this.data = new PlayerData(dispatcher, this, entity, spells, experience);
        this.spriteInfo = new GamePlayerSpriteInfo(entity, this.inventory);
        this.restrictions = new Restrictions(session);

        this.scope = this;

        this.data.build();
        this.restrictions.init(this);
    }

    @Override
    public void dispatch(Object event) {
        session.dispatch(event);
    }

    @Override
    @Pure
    public ListenerAggregate dispatcher() {
        return dispatcher;
    }

    /**
     * Get the base player data
     * The data should be used for change the player data
     * This value do not depends of the current player state (exploring / fighting) and should be modified carefully
     */
    @Override
    @Pure
    public PlayerData properties() {
        return data;
    }

    @Override
    public void send(Object packet) {
        session.send(packet);
    }

    @Override
    public void register(GameSession session) {
        session.setPlayer(this);
    }

    @Override
    public void unregister(GameSession session) {
        session.setPlayer(null);
    }

    @Pure
    public int id() {
        return entity.id();
    }

    @Pure
    public GameAccount account() {
        return account;
    }

    /**
     * Get the current player position
     */
    @Pure
    public Position position() {
        return entity.position();
    }

    /**
     * Set new position
     */
    public void setPosition(Position position) {
        entity.setPosition(position);
    }

    /**
     * Get the player saved position (teleport when die)
     */
    @Pure
    public Position savedPosition() {
        return entity.savedPosition();
    }

    /**
     * Set saved position (teleport when die)
     */
    public void setSavedPosition(Position position) {
        entity.setSavedPosition(position);
    }

    /**
     * Get the player race
     */
    @Pure
    public GamePlayerRace race() {
        return race;
    }

    @Pure
    public String name() {
        return entity.name();
    }

    /**
     * Get channels subscriptions
     */
    @Pure
    public Set<ChannelType> subscriptions() {
        return channels;
    }

    /**
     * Get the current session scope
     */
    @Pure
    public PlayerSessionScope scope() {
        return scope;
    }

    /**
     * Start a new session scope
     */
    public void start(PlayerSessionScope scope) {
        if (this.scope == scope) {
            throw new IllegalStateException("Scope already started");
        }

        scope.register(session);
        this.scope = scope;
    }

    /**
     * Stop the scope
     */
    public void stop(PlayerSessionScope scope) {
        scope.unregister(session);

        // Reset the scope only if we stop the active scope
        if (this.scope == scope) {
            this.scope = this;
        }
    }

    /**
     * Check if the player is exploring
     */
    @Pure
    public boolean isExploring() {
        return session.exploration() != null;
    }

    /**
     * Get the exploration player
     *
     * @throws IllegalStateException When the player is not on exploration state
     */
    @Pure
    public ExplorationPlayer exploration() {
        final ExplorationPlayer exploration = session.exploration();

        if (exploration != null) {
            return exploration;
        }

        throw new IllegalStateException("The current player is not an exploration state");
    }

    /**
     * Get the attached fighter
     */
    @Pure
    public PlayerFighter fighter() {
        final PlayerFighter fighter = session.fighter();

        if (fighter != null) {
            return fighter;
        }

        throw new IllegalStateException("The current player is not in fight");
    }

    /**
     * Check if the current player is in fight
     */
    @Pure
    public boolean isFighting() {
        return session.fighter() != null;
    }

    /**
     * Get the attached spectator
     */
    @Pure
    public Spectator spectator() {
        final Spectator spectator = session.spectator();

        if (spectator != null) {
            return spectator;
        }

        throw new IllegalStateException("The current player is not a spectator");
    }

    /**
     * Check if the current player is a spectator
     */
    @Pure
    public boolean isSpectator() {
        return session.spectator() != null;
    }

    /**
     * Save the player
     */
    public void save() {
        properties().life().setLifeWithCurrentRegeneration();
        service.save(this);
    }

    @Pure
    public PlayerInventory inventory() {
        return inventory;
    }

    /**
     * Get the player sprite info
     */
    @Pure
    public SpriteInfo spriteInfo() {
        return spriteInfo;
    }

    /**
     * Get the current player restrictions
     */
    @Pure
    public Restrictions restrictions() {
        return restrictions;
    }

    /**
     * Does the player is a newly created character ? (i.e. first connection)
     */
    public boolean isNew() {
        return session.log().map(log -> !log.hasAlreadyPlayed(entity)).orElse(false);
    }

    @Pure
    Player entity() {
        return entity;
    }
}
