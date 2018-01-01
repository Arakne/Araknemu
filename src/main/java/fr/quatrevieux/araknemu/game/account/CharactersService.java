package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.living.constraint.EntityConstraint;
import fr.quatrevieux.araknemu.data.living.constraint.player.PlayerConstraints;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.game.account.exception.CharacterCreationException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handle account characters
 */
final public class CharactersService {
    final private PlayerRepository repository;
    final private EntityConstraint<Player, PlayerConstraints.Error> constraint;
    final private PlayerRaceRepository playerRaceRepository;

    public CharactersService(PlayerRepository repository, EntityConstraint<Player, PlayerConstraints.Error> constraint, PlayerRaceRepository playerRaceRepository) {
        this.repository = repository;
        this.constraint = constraint;
        this.playerRaceRepository = playerRaceRepository;
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

        character.character().setPosition(
            playerRaceRepository.get(character.character().race()).startPosition()
        );

        try {
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
}
