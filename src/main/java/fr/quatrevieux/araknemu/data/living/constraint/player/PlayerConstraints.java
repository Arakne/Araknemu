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

package fr.quatrevieux.araknemu.data.living.constraint.player;

import fr.quatrevieux.araknemu.data.living.constraint.ConstraintBuilder;
import fr.quatrevieux.araknemu.data.living.constraint.AbstractConstraintBuilderFactory;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.game.GameConfiguration;

/**
 * Constraints for {@link Player} entity
 */
public final class PlayerConstraints extends AbstractConstraintBuilderFactory<Player, PlayerConstraints.Error> {
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

        private final String code;

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

    private final PlayerRepository repository;
    private final GameConfiguration.PlayerConfiguration configuration;

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
            .not(b -> b.entityCheck(entity -> repository.nameExists(entity.serverId(), entity.name())))

            .error(Error.CREATE_CHARACTER_FULL)
            .value(repository::accountCharactersCount)
            .max(configuration.maxPerAccount() - 1)
        ;
    }
}
