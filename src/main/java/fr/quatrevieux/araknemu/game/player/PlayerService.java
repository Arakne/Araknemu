package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.common.PlayerLoaded;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Service for handle {@link GamePlayer}
 */
final public class PlayerService {
    final private PlayerRepository repository;
    final private PlayerRaceRepository raceRepository;
    final private GameConfiguration configuration;
    final private Dispatcher dispatcher;

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
            session
        );

        dispatcher.dispatch(new PlayerLoaded(gamePlayer));

        return gamePlayer;
    }
}
