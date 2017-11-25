package fr.quatrevieux.araknemu.game.connector;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.realm.host.HostService;

/**
 * DI module for configure local connector
 */
final public class LocalModule implements ContainerModule {
    final private Container realmContainer;

    public LocalModule(Container realmContainer) {
        this.realmContainer = realmContainer;
    }

    @Override
    public void configure(ContainerConfigurator configurator) {
        configurator.factory(
            RealmConnector.class,
            container -> new LocalRealmConnector(
                realmContainer.get(HostService.class),
                container.get(ConnectorService.class)
            )
        );
    }
}
