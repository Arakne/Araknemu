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

package fr.quatrevieux.araknemu.game.connector;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.realm.host.HostService;

/**
 * DI module for configure local connector
 */
public final class LocalModule implements ContainerModule {
    private final Container realmContainer;

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
