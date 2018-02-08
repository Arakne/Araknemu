package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.data.living.transformer.ChannelsTransformer;
import fr.quatrevieux.araknemu.data.living.transformer.PermissionsTransformer;
import fr.quatrevieux.araknemu.data.transformer.MutableCharacteristicsTransformer;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;

/**
 * DI module for living repositories
 */
final public class LivingRepositoriesModule implements ContainerModule {
    final private ConnectionPool connection;

    public LivingRepositoriesModule(ConnectionPool connection) {
        this.connection = connection;
    }

    @Override
    public void configure(ContainerConfigurator configurator) {
        configurator.persist(
            fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository.class,
            container -> new AccountRepository(
                connection,
                container.get(PermissionsTransformer.class)
            )
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository.class,
            container -> new PlayerRepository(
                connection,
                container.get(MutableCharacteristicsTransformer.class),
                container.get(ChannelsTransformer.class)
            )
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.living.repository.environment.SubAreaRepository.class,
            container -> new SubAreaRepository(connection)
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository.class,
            container -> new PlayerItemRepository(
                connection,
                container.get(ItemEffectsTransformer.class)
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
    }
}
