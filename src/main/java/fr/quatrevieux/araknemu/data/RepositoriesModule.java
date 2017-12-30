package fr.quatrevieux.araknemu.data;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository;
import fr.quatrevieux.araknemu.data.transformer.CharacteristicsTransformer;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;

/**
 * DI module for repositories
 *
 * A {@link ConnectionPool} instance MUST be provided
 */
final public class RepositoriesModule implements ContainerModule {
    @Override
    public void configure(ContainerConfigurator configurator) {
        configurator.persist(
            AccountRepository.class,
            container -> new AccountRepository(container.get(ConnectionPool.class))
        );

        configurator.persist(
            PlayerRepository.class,
            container -> new PlayerRepository(
                container.get(ConnectionPool.class),
                container.get(CharacteristicsTransformer.class)
            )
        );

        configurator.persist(
            PlayerRaceRepository.class,
            container -> new PlayerRaceRepository(
                container.get(ConnectionPool.class),
                container.get(CharacteristicsTransformer.class)
            )
        );

        configurator.persist(
            CharacteristicsTransformer.class,
            container -> new CharacteristicsTransformer()
        );
    }
}
