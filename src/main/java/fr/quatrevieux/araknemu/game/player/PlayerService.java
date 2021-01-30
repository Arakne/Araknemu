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

package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.event.Dispatcher;
import fr.quatrevieux.araknemu.core.event.EventsSubscriber;
import fr.quatrevieux.araknemu.core.event.Listener;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.handler.event.Disconnected;
import fr.quatrevieux.araknemu.game.listener.player.ComputeLifePoints;
import fr.quatrevieux.araknemu.game.listener.player.InitializeRestrictions;
import fr.quatrevieux.araknemu.game.listener.player.SavePlayer;
import fr.quatrevieux.araknemu.game.listener.player.SendLifeChanged;
import fr.quatrevieux.araknemu.game.listener.player.SendRestrictions;
import fr.quatrevieux.araknemu.game.listener.player.SendShutdownScheduled;
import fr.quatrevieux.araknemu.game.listener.player.SendStats;
import fr.quatrevieux.araknemu.game.listener.player.StartTutorial;
import fr.quatrevieux.araknemu.game.player.event.PlayerLoaded;
import fr.quatrevieux.araknemu.game.player.experience.PlayerExperienceService;
import fr.quatrevieux.araknemu.game.player.inventory.InventoryService;
import fr.quatrevieux.araknemu.game.player.race.PlayerRaceService;
import fr.quatrevieux.araknemu.game.player.spell.SpellBookService;
import fr.quatrevieux.araknemu.network.game.GameSession;

import java.util.Collection;
import java.util.NoSuchElementException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Service for handle {@link GamePlayer}
 */
final public class PlayerService implements EventsSubscriber {
    final private PlayerRepository repository;
    final private GameConfiguration configuration;
    final private Dispatcher dispatcher;
    final private InventoryService inventoryService;
    final private PlayerRaceService playerRaceService;
    final private SpellBookService spellBookService;
    final private PlayerExperienceService experienceService;

    final private ConcurrentMap<Integer, GamePlayer> onlinePlayers = new ConcurrentHashMap<>();
    final private ConcurrentMap<String, GamePlayer> playersByName  = new ConcurrentHashMap<>();

    public PlayerService(PlayerRepository repository, GameConfiguration configuration, Dispatcher dispatcher, InventoryService inventoryService, PlayerRaceService playerRaceService, SpellBookService spellBookService, PlayerExperienceService experienceService) {
        this.repository = repository;
        this.configuration = configuration;
        this.dispatcher = dispatcher;
        this.inventoryService = inventoryService;
        this.playerRaceService = playerRaceService;
        this.spellBookService = spellBookService;
        this.experienceService = experienceService;
    }

    /**
     * Load the player for entering game
     *
     * @param session The current session
     * @param id The player race
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException When cannot found player on server
     * @throws RepositoryException For any other repository errors
     */
    public GamePlayer load(GameSession session, int id) throws RepositoryException {
        if (onlinePlayers.containsKey(id)) {
            throw new IllegalStateException("The player is already loaded");
        }

        final Player player = repository.getForGame(
            Player.forGame(
                id,
                session.account().id(),
                configuration.id()
            )
        );

        final GamePlayer gamePlayer = new GamePlayer(
            session.account(),
            player,
            playerRaceService.get(player.race()),
            session,
            this,
            inventoryService.load(player),
            spellBookService.load(session, player),
            experienceService.load(session, player)
        );

        gamePlayer.dispatcher().add(Disconnected.class, e -> logout(gamePlayer));
        gamePlayer.dispatcher().add(new ComputeLifePoints(gamePlayer));
        gamePlayer.dispatcher().add(new SendLifeChanged(gamePlayer));
        gamePlayer.dispatcher().add(new SendStats(gamePlayer));
        gamePlayer.dispatcher().add(new SendRestrictions(gamePlayer));
        gamePlayer.dispatcher().add(new InitializeRestrictions(gamePlayer));
        gamePlayer.dispatcher().add(new StartTutorial(gamePlayer)); // @todo Move to "tutorial" package when implemented
        this.dispatcher.dispatch(new PlayerLoaded(gamePlayer));
        gamePlayer.dispatcher().add(new SavePlayer(gamePlayer)); // After all events

        login(gamePlayer);

        return gamePlayer;
    }

    /**
     * Get all online players
     */
    public Collection<GamePlayer> online() {
        return onlinePlayers.values();
    }

    /**
     * Get filter stream over online players
     *
     * @param predicate The filter predicate
     */
    public Stream<GamePlayer> filter(Predicate<GamePlayer> predicate) {
        return online()
            .stream()
            .filter(predicate)
        ;
    }

    /**
     * Check if the player is online
     *
     * @param name The player name
     */
    public boolean isOnline(String name) {
        return playersByName.containsKey(name.toLowerCase());
    }

    /**
     * Get a player by its name
     *
     * @param name The player name
     *
     * @throws NoSuchElementException When the player is not online (or do not exists)
     */
    public GamePlayer get(String name) {
        name = name.toLowerCase();

        if (!isOnline(name)) {
            throw new NoSuchElementException("The player " + name + " cannot be found");
        }

        return playersByName.get(name);
    }

    /**
     * Save the player
     */
    public void save(GamePlayer player) {
        repository.save(player.entity());
    }

    @Override
    public Listener[] listeners() {
        return new Listener[] {
            new SendShutdownScheduled(this),
        };
    }

    private void login(GamePlayer player) {
        onlinePlayers.put(player.id(), player);
        playersByName.put(player.name().toLowerCase(), player);
    }

    private void logout(GamePlayer player) {
        onlinePlayers.remove(player.id());
        playersByName.remove(player.name().toLowerCase());
    }
}
