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

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.data.transformer.ImmutableCharacteristicsTransformer;
import fr.quatrevieux.araknemu.data.world.repository.SpellTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerExperienceRepository;
import fr.quatrevieux.araknemu.data.world.repository.character.PlayerRaceRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.MapTriggerRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.QuestionRepository;
import fr.quatrevieux.araknemu.data.world.repository.environment.npc.ResponseActionRepository;
import fr.quatrevieux.araknemu.data.world.repository.implementation.local.*;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemSetRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository;
import fr.quatrevieux.araknemu.data.world.repository.item.ItemTypeRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.*;
import fr.quatrevieux.araknemu.data.world.transformer.*;

/**
 * DI module for world repositories
 */
final public class SqlWorldRepositoriesModule implements ContainerModule {
    final private ConnectionPool connection;

    public SqlWorldRepositoriesModule(ConnectionPool connection) {
        this.connection = connection;
    }

    @Override
    public void configure(ContainerConfigurator configurator) {
        configurator.persist(
            PlayerRaceRepository.class,
            container -> new PlayerRaceRepositoryCache(
                new SqlPlayerRaceRepository(
                    connection,
                    container.get(RaceBaseStatsTransformer.class),
                    container.get(BoostStatsDataTransformer.class)
                )
            )
        );

        configurator.persist(
            MapTemplateRepository.class,
            container -> new SqlMapTemplateRepository(
                connection,
                container.get(MapCellTransformer.class),
                container.get(FightPlacesTransformer.class)
            )
        );

        configurator.persist(
            MapTriggerRepository.class,
            container -> new SqlMapTriggerRepository(connection)
        );

        configurator.persist(
            ItemTemplateRepository.class,
            container -> new ItemTemplateRepositoryCache(
                    new SqlItemTemplateRepository(
                    connection,
                    container.get(ItemEffectsTransformer.class)
                )
            )
        );

        configurator.persist(
            ItemSetRepository.class,
            container -> new ItemSetRepositoryCache(
                    new SqlItemSetRepository(
                    connection,
                    container.get(ItemSetBonusTransformer.class)
                )
            )
        );

        configurator.persist(
            ItemTypeRepository.class,
            container -> new ItemTypeRepositoryCache(
                    new SqlItemTypeRepository(
                    connection,
                    container.get(EffectAreaTransformer.class)
                )
            )
        );

        configurator.persist(
            SpellTemplateRepository.class,
            container -> new SqlSpellTemplateRepository(
                connection,
                container.get(SpellTemplateLevelTransformer.class)
            )
        );

        configurator.persist(
            PlayerExperienceRepository.class,
            container -> new SqlPlayerExperienceRepository(connection)
        );

        configurator.persist(
            NpcTemplateRepository.class,
            container -> new NpcTemplateRepositoryCache(new SqlNpcTemplateRepository(connection))
        );

        configurator.persist(
            NpcRepository.class,
            container -> new NpcRepositoryCache(new SqlNpcRepository(connection))
        );

        configurator.persist(
            QuestionRepository.class,
            container -> new SqlQuestionRepository(connection)
        );

        configurator.persist(
            ResponseActionRepository.class,
            container -> new SqlResponseActionRepository(connection)
        );

        configurator.persist(
            MonsterTemplateRepository.class,
            container -> new SqlMonsterTemplateRepository(
                connection,
                container.get(ColorsTransformer.class),
                container.get(ImmutableCharacteristicsTransformer.class)
            )
        );

        configurator.persist(
            MonsterGroupDataRepository.class,
            container -> new MonsterGroupDataRepositoryCache(
                new SqlMonsterGroupDataRepository(
                    connection,
                    container.get(MonsterListTransformer.class)
                )
            )
        );

        configurator.persist(
            MonsterGroupPositionRepository.class,
            container -> new SqlMonsterGroupPositionRepository(connection)
        );

        configurator.persist(
            MonsterRewardRepository.class,
            container -> new SqlMonsterRewardRepository(connection)
        );

        configurator.persist(
            MonsterRewardItemRepository.class,
            container -> new SqlMonsterRewardItemRepository(connection)
        );

        configurator.persist(
            RaceBaseStatsTransformer.class,
            container -> new RaceBaseStatsTransformer(
                container.get(ImmutableCharacteristicsTransformer.class)
            )
        );

        configurator.persist(
            MapCellTransformer.class,
            container -> new MapCellTransformer()
        );

        configurator.persist(
            ItemEffectsTransformer.class,
            container -> new ItemEffectsTransformer()
        );

        configurator.persist(
            BoostStatsDataTransformer.class,
            container -> new BoostStatsDataTransformer()
        );

        configurator.persist(
            ItemSetBonusTransformer.class,
            container -> new ItemSetBonusTransformer()
        );

        configurator.persist(
            SpellTemplateLevelTransformer.class,
            container -> new SpellTemplateLevelTransformer(
                container.get(EffectAreaTransformer.class)
            )
        );

        configurator.persist(
            FightPlacesTransformer.class,
            container -> new FightPlacesTransformer()
        );

        configurator.persist(
            EffectAreaTransformer.class,
            container -> new EffectAreaTransformer()
        );

        configurator.persist(
            ColorsTransformer.class,
            container -> new ColorsTransformer()
        );

        configurator.persist(
            ImmutableCharacteristicsTransformer.class,
            container -> new ImmutableCharacteristicsTransformer()
        );

        configurator.persist(
            MonsterListTransformer.class,
            container -> new MonsterListTransformer()
        );
    }
}
