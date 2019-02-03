package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.data.living.repository.environment.SubAreaRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository;
import fr.quatrevieux.araknemu.data.living.transformer.ChannelsTransformer;
import fr.quatrevieux.araknemu.data.living.transformer.PermissionsTransformer;
import fr.quatrevieux.araknemu.data.transformer.MutableCharacteristicsTransformer;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;

/**
 * DI module for living repositories
 */
final public class SqlLivingRepositoriesModule implements ContainerModule {
    final private ConnectionPool connection;

    public SqlLivingRepositoriesModule(ConnectionPool connection) {
        this.connection = connection;
    }

    @Override
    public void configure(ContainerConfigurator configurator) {
        configurator.persist(
            AccountRepository.class,
            container -> new SqlAccountRepository(
                connection,
                container.get(PermissionsTransformer.class)
            )
        );

        configurator.persist(
            PlayerRepository.class,
            container -> new SqlPlayerRepository(
                connection,
                container.get(MutableCharacteristicsTransformer.class),
                container.get(ChannelsTransformer.class)
            )
        );

        configurator.persist(
            SubAreaRepository.class,
            container -> new SqlSubAreaRepository(connection)
        );

        configurator.persist(
            PlayerItemRepository.class,
            container -> new SqlPlayerItemRepository(
                connection,
                container.get(ItemEffectsTransformer.class)
            )
        );

        configurator.persist(
            PlayerSpellRepository.class,
            container -> new SqlPlayerSpellRepository(connection)
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
    }
}
