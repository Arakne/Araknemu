package fr.quatrevieux.araknemu.data.world.repository.character;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerExperience;

import java.util.List;

/**
 * Repository for experience data {@link PlayerExperience}
 */
public interface PlayerExperienceRepository extends Repository<PlayerExperience> {
    /**
     * Get all experience
     */
    public List<PlayerExperience> all();
}
