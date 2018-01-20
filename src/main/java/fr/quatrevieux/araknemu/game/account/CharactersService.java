package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.living.constraint.EntityConstraint;
import fr.quatrevieux.araknemu.data.living.constraint.player.PlayerConstraints;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.account.exception.CharacterCreationException;
import fr.quatrevieux.araknemu.game.event.Dispatcher;
import fr.quatrevieux.araknemu.game.event.common.PlayerDeleted;
import fr.quatrevieux.araknemu.game.event.manage.CharacterCreationStarted;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handle account characters
 */
final public class CharactersService {
    final private PlayerRepository repository;
    final private EntityConstraint<Player, PlayerConstraints.Error> constraint;
    final private PlayerRaceRepository playerRaceRepository;
    final private Dispatcher dispatcher;

    public CharactersService(PlayerRepository repository, EntityConstraint<Player, PlayerConstraints.Error> constraint, PlayerRaceRepository playerRaceRepository, Dispatcher dispatcher) {
        this.repository = repository;
        this.constraint = constraint;
        this.playerRaceRepository = playerRaceRepository;
        this.dispatcher = dispatcher;
    }

    /**
     * Create a new character
     *
     * @param character Character to create
     */
    public AccountCharacter create(AccountCharacter character) throws CharacterCreationException {
        if (!constraint.check(character.character())) {
            throw new CharacterCreationException(constraint.error());
        }

        // @todo configure into a listener
        character.character().setPosition(
            playerRaceRepository.get(character.character().race()).startPosition()
        );

        try {
            dispatcher.dispatch(new CharacterCreationStarted(character));

            return new AccountCharacter(
                character.account(),
                repository.add(
                    character.character()
                )
            );
        } catch (RepositoryException e) {
            throw new CharacterCreationException(e);
        }
    }

    /**
     * Get list of characters
     *
     * @param account The account
     */
    public List<AccountCharacter> list(GameAccount account) {
        return repository.findByAccount(account.id(), account.serverId())
            .stream()
            .map(player -> new AccountCharacter(account, player))
            .collect(Collectors.toList())
        ;
    }

    /**
     * Get account character
     *
     * @param account The account
     * @param id The character id
     *
     * @throws fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException When cannot found the character
     */
    public AccountCharacter get(GameAccount account, int id) {
        return new AccountCharacter(
            account,
            repository.getForGame(
                Player.forGame(id, account.id(), account.serverId())
            )
        );
    }

    /**
     * Delete the character
     */
    public void delete(AccountCharacter character) {
        repository.delete(character.character());

        dispatcher.dispatch(new PlayerDeleted(character));
    }
}
