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

package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.core.dbal.executor.LoggedQueryExecutor;
import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.data.living.repository.BanIpRepository;
import fr.quatrevieux.araknemu.data.living.repository.account.*;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.data.living.transformer.ChannelsTransformer;
import fr.quatrevieux.araknemu.data.living.transformer.InstantTransformer;
import fr.quatrevieux.araknemu.data.living.transformer.IpAddressTransformer;
import fr.quatrevieux.araknemu.data.living.transformer.PermissionsTransformer;
import fr.quatrevieux.araknemu.data.transformer.MutableCharacteristicsTransformer;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import org.apache.logging.log4j.LogManager;

/**
 * DI module for living repositories
 */
final public class SqlLivingRepositoriesModule implements ContainerModule {
    final private QueryExecutor executor;

    public SqlLivingRepositoriesModule(ConnectionPool connection) {
        this.executor = new LoggedQueryExecutor(
            new ConnectionPoolExecutor(connection),
            LogManager.getLogger(SqlLivingRepositoriesModule.class)
        );
    }

    @Override
    public void configure(ContainerConfigurator configurator) {
        configurator.persist(
            AccountRepository.class,
            container -> new SqlAccountRepository(
                executor,
                container.get(PermissionsTransformer.class)
            )
        );

        configurator.persist(
            PlayerRepository.class,
            container -> new SqlPlayerRepository(
                executor,
                container.get(MutableCharacteristicsTransformer.class),
                container.get(ChannelsTransformer.class)
            )
        );

        configurator.persist(
            PlayerItemRepository.class,
            container -> new SqlPlayerItemRepository(
                executor,
                container.get(ItemEffectsTransformer.class)
            )
        );

        configurator.persist(
            PlayerSpellRepository.class,
            container -> new SqlPlayerSpellRepository(executor)
        );

        configurator.persist(
            AccountBankRepository.class,
            container -> new SqlAccountBankRepository(executor)
        );

        configurator.persist(
            BankItemRepository.class,
            container -> new SqlBankItemRepository(
                executor,
                container.get(ItemEffectsTransformer.class)
            )
        );

        configurator.persist(
            ConnectionLogRepository.class,
            container -> new SqlConnectionLogRepository(
                executor,
                container.get(InstantTransformer.class)
            )
        );

        configurator.persist(
            BanishmentRepository.class,
            container -> new SqlBanishmentRepository(
                executor,
                container.get(InstantTransformer.class)
            )
        );

        configurator.persist(
         BanIpRepository.class,
            container -> new SqlBanIpRepository(
                executor,
                container.get(IpAddressTransformer.class),
                container.get(InstantTransformer.class)
            )
        );

        configurator.persist(
            MutableCharacteristicsTransformer.class,
            container -> new MutableCharacteristicsTransformer()
        );

        configurator.persist(
            PermissionsTransformer.class,
            container -> new PermissionsTransformer()
        );

        configurator.persist(
            ChannelsTransformer.class,
            container -> new ChannelsTransformer()
        );

        configurator.persist(
            ItemEffectsTransformer.class,
            container -> new ItemEffectsTransformer()
        );

        configurator.persist(InstantTransformer.class, container -> new InstantTransformer());
        configurator.persist(IpAddressTransformer.class, container -> new IpAddressTransformer());
    }
}
