package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.data.transformer.ImmutableCharacteristicsTransformer;
import fr.quatrevieux.araknemu.data.transformer.MutableCharacteristicsTransformer;

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
            container -> new AccountRepository(connection)
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository.class,
            container -> new PlayerRepository(
                connection,
                container.get(MutableCharacteristicsTransformer.class)
            )
        );

        configurator.persist(
            MutableCharacteristicsTransformer.class,
            container -> new MutableCharacteristicsTransformer()
        );
    }
}
