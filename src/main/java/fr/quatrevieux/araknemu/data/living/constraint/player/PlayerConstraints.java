package fr.quatrevieux.araknemu.data.living.constraint.player;

import fr.quatrevieux.araknemu.data.living.constraint.ConstraintBuilder;
import fr.quatrevieux.araknemu.data.living.constraint.ConstraintBuilderFactory;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;

/**
 * Constraints for {@link Player} entity
 */
final public class PlayerConstraints extends ConstraintBuilderFactory<Player, PlayerConstraints.Error> {
    /**
     * List of error codes
     * https://github.com/Emudofus/Dofus/blob/1.29/dofus/aks/Account.as#L580
     */
    public enum Error {
        SUBSCRIPTION_OUT("s"),
        CREATE_CHARACTER_FULL("f"),
        NAME_ALEREADY_EXISTS("a"),
        CREATE_CHARACTER_BAD_NAME("n"),
        CREATE_CHARACTER_ERROR;

        final private String code;

        Error(String code) {
            this.code = code;
        }

        Error() {
            this("");
        }

        public String code() {
            return code;
        }
    }

    final private PlayerRepository repository;
    final private GameConfiguration.PlayerConfiguration configuration;

    public PlayerConstraints(PlayerRepository repository, GameConfiguration.PlayerConfiguration configuration) {
        this.repository = repository;
        this.configuration = configuration;
    }

    @Override
    public void build(ConstraintBuilder<Player, Error> builder) {
        builder
            .error(Error.CREATE_CHARACTER_BAD_NAME)
            .value(Player::name)
            .notEmpty()
            .regex(configuration.nameRegex())
            .minLength(configuration.nameMinLength())
            .maxLength(configuration.nameMaxLength())

            .error(Error.NAME_ALEREADY_EXISTS)
            .not(b -> b.entityCheck(repository::nameExists))

            .error(Error.CREATE_CHARACTER_FULL)
            .value(repository::accountCharactersCount)
            .max(configuration.maxPerAccount() - 1)
        ;
    }
}
