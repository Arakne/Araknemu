package fr.quatrevieux.araknemu.game.player;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;
import fr.quatrevieux.araknemu.network.game.GameSession;

/**
 * Service for handle {@link GamePlayer}
 */
final public class PlayerService {
    final private PlayerRepository repository;
    final private GameConfiguration configuration;

    public PlayerService(PlayerRepository repository, GameConfiguration configuration) {
        this.repository = repository;
        this.configuration = configuration;
    }

    /**
     * Load the player for entering game
     *
     * @param session The current session
     * @param id The player id
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException When cannot found player on server
     * @throws RepositoryException For any other repository errors
     */
    public GamePlayer load(GameSession session, int id) throws RepositoryException {
        return new GamePlayer(
            session.account(),
            repository.getForGame(
                Player.forGame(
                    id,
                    session.account().id(),
                    configuration.id()
                )
            ),
            session
        );
    }
}
