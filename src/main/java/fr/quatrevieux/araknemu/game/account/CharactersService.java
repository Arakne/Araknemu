package fr.quatrevieux.araknemu.game.account;

import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.game.account.exception.CharacterCreationException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for handle account characters
 */
final public class CharactersService {
    final private PlayerRepository repository;

    public CharactersService(PlayerRepository repository) {
        this.repository = repository;
    }

    /**
     * Create a new character
     *
     * @param character Character to create
     *
     * @todo check name, etc...
     */
    public AccountCharacter create(AccountCharacter character) throws CharacterCreationException {
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
