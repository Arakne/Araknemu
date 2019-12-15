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

package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.world.repository.environment.area.SubAreaRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcExchangeRepository;
import fr.quatrevieux.araknemu.data.world.repository.implementation.local.*;
import fr.quatrevieux.araknemu.data.world.repository.monster.*;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SqlWorldRepositoriesModuleTest extends GameBaseCase {
    @Test
    void instances() throws SQLException, ContainerException {
        Container container = new ItemPoolContainer();

        container.register(new SqlWorldRepositoriesModule(
            app.database().get("game")
        ));

        assertInstanceOf(PlayerRaceRepositoryCache.class, container.get(fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository.class));
        assertInstanceOf(SqlMapTemplateRepository.class, container.get(fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository.class));
        assertInstanceOf(SqlMapTriggerRepository.class, container.get(fr.quatrevieux.araknemu.data.world.repository.environment.MapTriggerRepository.class));
        assertInstanceOf(ItemTemplateRepositoryCache.class, container.get(fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository.class));
        assertInstanceOf(ItemSetRepositoryCache.class, container.get(fr.quatrevieux.araknemu.data.world.repository.item.ItemSetRepository.class));
        assertInstanceOf(ItemTypeRepositoryCache.class, container.get(fr.quatrevieux.araknemu.data.world.repository.item.ItemTypeRepository.class));
        assertInstanceOf(SqlSpellTemplateRepository.class, container.get(fr.quatrevieux.araknemu.data.world.repository.SpellTemplateRepository.class));
        assertInstanceOf(SqlPlayerExperienceRepository.class, container.get(fr.quatrevieux.araknemu.data.world.repository.character.PlayerExperienceRepository.class));
        assertInstanceOf(NpcRepositoryCache.class, container.get(fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcRepository.class));
        assertInstanceOf(NpcTemplateRepositoryCache.class, container.get(fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository.class));
        assertInstanceOf(SqlQuestionRepository.class, container.get(fr.quatrevieux.araknemu.data.world.repository.environment.npc.QuestionRepository.class));
        assertInstanceOf(SqlResponseActionRepository.class, container.get(fr.quatrevieux.araknemu.data.world.repository.environment.npc.ResponseActionRepository.class));
        assertInstanceOf(SqlMonsterTemplateRepository.class, container.get(MonsterTemplateRepository.class));
        assertInstanceOf(MonsterGroupDataRepositoryCache.class, container.get(MonsterGroupDataRepository.class));
        assertInstanceOf(SqlMonsterGroupPositionRepository.class, container.get(MonsterGroupPositionRepository.class));
        assertInstanceOf(SqlMonsterRewardRepository.class, container.get(MonsterRewardRepository.class));
        assertInstanceOf(SqlMonsterRewardItemRepository.class, container.get(MonsterRewardItemRepository.class));
        assertInstanceOf(SqlNpcExchangeRepository.class, container.get(NpcExchangeRepository.class));
        assertInstanceOf(SqlSubAreaRepository.class, container.get(SubAreaRepository.class));
    }

    public void assertInstanceOf(Class type, Object obj) {
        assertTrue(type.isInstance(obj));
    }
}
