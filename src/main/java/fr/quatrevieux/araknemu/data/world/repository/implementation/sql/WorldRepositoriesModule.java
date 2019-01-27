package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.di.ContainerConfigurator;
import fr.quatrevieux.araknemu.core.di.ContainerModule;
import fr.quatrevieux.araknemu.data.transformer.ImmutableCharacteristicsTransformer;
import fr.quatrevieux.araknemu.data.world.repository.implementation.local.*;
import fr.quatrevieux.araknemu.data.world.transformer.*;

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
                    container.get(RaceBaseStatsTransformer.class),
                    container.get(BoostStatsDataTransformer.class)
                )
            )
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.world.repository.environment.MapTemplateRepository.class,
            container -> new MapTemplateRepository(
                connection,
                container.get(MapCellTransformer.class),
                container.get(FightPlacesTransformer.class)
            )
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.world.repository.environment.MapTriggerRepository.class,
            container -> new MapTriggerRepository(connection)
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.world.repository.item.ItemTemplateRepository.class,
            container -> new ItemTemplateRepositoryCache(
                    new ItemTemplateRepository(
                    connection,
                    container.get(ItemEffectsTransformer.class)
                )
            )
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.world.repository.item.ItemSetRepository.class,
            container -> new ItemSetRepositoryCache(
                    new ItemSetRepository(
                    connection,
                    container.get(ItemSetBonusTransformer.class)
                )
            )
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.world.repository.item.ItemTypeRepository.class,
            container -> new ItemTypeRepositoryCache(
                    new ItemTypeRepository(
                    connection,
                    container.get(EffectAreaTransformer.class)
                )
            )
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.world.repository.SpellTemplateRepository.class,
            container -> new SpellTemplateRepository(
                connection,
                container.get(SpellTemplateLevelTransformer.class)
            )
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.world.repository.character.PlayerExperienceRepository.class,
            container -> new PlayerExperienceRepository(connection)
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository.class,
            container -> new NpcTemplateRepositoryCache(new NpcTemplateRepository(connection))
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcRepository.class,
            container -> new NpcRepositoryCache(new NpcRepository(connection))
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.world.repository.environment.npc.QuestionRepository.class,
            container -> new QuestionRepository(connection)
        );

        configurator.persist(
            fr.quatrevieux.araknemu.data.world.repository.environment.npc.ResponseActionRepository.class,
            container -> new ResponseActionRepository(connection)
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
