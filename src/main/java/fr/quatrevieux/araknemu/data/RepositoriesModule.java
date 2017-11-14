package fr.quatrevieux.araknemu.data;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository;

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
    }
}
