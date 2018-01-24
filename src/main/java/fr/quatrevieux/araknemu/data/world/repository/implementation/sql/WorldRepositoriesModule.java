package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.data.transformer.ImmutableCharacteristicsTransformer;
import fr.quatrevieux.araknemu.data.world.repository.implementation.local.PlayerRaceRepositoryCache;
import fr.quatrevieux.araknemu.data.world.transformer.MapCellTransformer;

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
            container -> new PlayerRaceRepositoryCache(
                new PlayerRaceRepository(
                    connection,
                    container.get(ImmutableCharacteristicsTransformer.class)
                )
            )
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository.class,
            container -> new MapTemplateRepository(
                connection,
                container.get(MapCellTransformer.class)
            )
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.world.repository.environment.MapTriggerRepository.class,
            container -> new MapTriggerRepository(connection)
        );

        configurator.persist(
            ImmutableCharacteristicsTransformer.class,
            container -> new ImmutableCharacteristicsTransformer()
        );

        configurator.persist(
            MapCellTransformer.class,
            container -> new MapCellTransformer()
        );
    }
}
