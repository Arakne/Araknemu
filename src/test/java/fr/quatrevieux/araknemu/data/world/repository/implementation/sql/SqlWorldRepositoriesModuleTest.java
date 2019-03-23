package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.core.di.ItemPoolContainer;
import fr.quatrevieux.araknemu.data.world.repository.implementation.local.*;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupDataRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterGroupPositionRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterRewardRepository;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterTemplateRepository;
import fr.quatrevieux.araknemu.game.GameBaseCase;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

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
    }

    public void assertInstanceOf(Class type, Object obj) {
        assertTrue(type.isInstance(obj));
    }
}
