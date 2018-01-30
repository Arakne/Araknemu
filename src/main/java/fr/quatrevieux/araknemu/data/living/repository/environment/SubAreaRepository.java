package fr.quatrevieux.araknemu.data.living.repository.environment;

import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.data.living.entity.environment.SubArea;

import java.util.Collection;

/**
 * Repository for sub areas
 * This repository is not mutable because data can only be updated, but not created
 */
public interface SubAreaRepository extends Repository<SubArea> {
    /**
     * Get all subareas
     */
    public Collection<SubArea> all();
}
