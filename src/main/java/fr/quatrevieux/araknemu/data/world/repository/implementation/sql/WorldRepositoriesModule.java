package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.data.transformer.ImmutableCharacteristicsTransformer;

/**
 * DI module for world repositories
 */
final public class WorldRepositoriesModule implements ContainerModule {
    final private ConnectionPool connection;

    public WorldRepositoriesModule(ConnectionPool connection) {
        this.connection = connection;
    }

    @Override
    public void configure(ContainerConfigurator configurator) {
        configurator.persist(
            fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository.class,
            container -> new PlayerRaceRepository(
                connection,
                container.get(ImmutableCharacteristicsTransformer.class)
            )
        );

        configurator.persist(
            ImmutableCharacteristicsTransformer.class,
            container -> new ImmutableCharacteristicsTransformer()
        );
    }
}
