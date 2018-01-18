package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.common.Disconnected;
import fr.quatrevieux.araknemu.game.event.common.PlayerLoaded;
import fr.quatrevieux.araknemu.game.event.listener.player.SavePlayer;
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
final public class PlayerService {
    final private PlayerRepository repository;
    final private PlayerRaceRepository raceRepository;
    final private GameConfiguration configuration;
    final private Dispatcher dispatcher;

    final private ConcurrentMap<Integer, GamePlayer> onlinePlayers = new ConcurrentHashMap<>();
    final private ConcurrentMap<String, GamePlayer> playersByName  = new ConcurrentHashMap<>();

    public PlayerService(PlayerRepository repository, PlayerRaceRepository raceRepository, GameConfiguration configuration, Dispatcher dispatcher) {
        this.repository = repository;
        this.raceRepository = raceRepository;
        this.configuration = configuration;
        this.dispatcher = dispatcher;
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

        Player player = repository.getForGame(
            Player.forGame(
                id,
                session.account().id(),
                configuration.id()
            )
        );

        GamePlayer gamePlayer = new GamePlayer(
            session.account(),
            player,
            raceRepository.get(player.race()),
            session,
            this
        );

        gamePlayer.dispatcher().add(Disconnected.class, e -> logout(gamePlayer));
        dispatcher.dispatch(new PlayerLoaded(gamePlayer));
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
        repository.save(player.entity);
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
