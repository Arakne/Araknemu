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

package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.living.transformer.InstantTransformer;
import fr.quatrevieux.araknemu.data.living.transformer.PermissionsTransformer;
import fr.quatrevieux.araknemu.realm.RealmBaseCase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlLivingRepositoriesModuleTest extends RealmBaseCase {
    @Test
    void instances() throws SQLException, ContainerException {
        Container container = new ItemPoolContainer();

        container.register(new SqlLivingRepositoriesModule(
            app.database().get("realm")
        ));

        assertInstanceOf(SqlAccountRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.account.AccountRepository.class));
        assertInstanceOf(SqlPlayerRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.player.PlayerRepository.class));
        assertInstanceOf(SqlPlayerItemRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository.class));
        assertInstanceOf(SqlPlayerSpellRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.player.PlayerSpellRepository.class));
        assertInstanceOf(SqlAccountBankRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.account.AccountBankRepository.class));
        assertInstanceOf(SqlBankItemRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.account.BankItemRepository.class));
        assertInstanceOf(SqlConnectionLogRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.account.ConnectionLogRepository.class));
        assertInstanceOf(SqlBanishmentRepository.class, container.get(fr.quatrevieux.araknemu.data.living.repository.account.BanishmentRepository.class));

        assertInstanceOf(PermissionsTransformer.class, container.get(PermissionsTransformer.class));
        assertInstanceOf(InstantTransformer.class, container.get(InstantTransformer.class));
    }

    public void assertInstanceOf(Class type, Object obj) {
        assertTrue(type.isInstance(obj));
    }
}