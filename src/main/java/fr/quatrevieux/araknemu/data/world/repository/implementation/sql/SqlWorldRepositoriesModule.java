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
            RaceBaseStatsTransformer.class,
            container -> new RaceBaseStatsTransformer(
                new ImmutableCharacteristicsTransformer()
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
    }
}
