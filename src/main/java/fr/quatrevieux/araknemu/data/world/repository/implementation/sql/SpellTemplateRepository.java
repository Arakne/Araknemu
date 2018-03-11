package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;

/**
 * SQL implementation for spell template repository
 */
final class SpellTemplateRepository implements fr.quatrevieux.araknemu.data.world.repository.SpellTemplateRepository {
    private class Loader implements RepositoryUtils.Loader<SpellTemplate> {
        @Override
        public SpellTemplate create(ResultSet rs) throws SQLException {
            return new SpellTemplate(
                rs.getInt("SPELL_ID"),
                rs.getString("SPELL_NAME"),
                rs.getInt("SPELL_SPRITE"),
                rs.getString("SPELL_SPRITE_ARG"),
                new SpellTemplate.Level[]{
                    levelTransformer.unserialize(rs.getString("SPELL_LVL_1")),
                    levelTransformer.unserialize(rs.getString("SPELL_LVL_2")),
                    levelTransformer.unserialize(rs.getString("SPELL_LVL_3")),
                    levelTransformer.unserialize(rs.getString("SPELL_LVL_4")),
                    levelTransformer.unserialize(rs.getString("SPELL_LVL_5")),
                    levelTransformer.unserialize(rs.getString("SPELL_LVL_6"))
                },
                Arrays.stream(StringUtils.split(rs.getString("SPELL_TARGET"), ";"))
                    .mapToInt(Integer::parseInt)
                    .toArray()
            );
        }

        @Override
        public SpellTemplate fillKeys(SpellTemplate entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<SpellTemplate> utils;

    final private Transformer<SpellTemplate.Level> levelTransformer;

    public SpellTemplateRepository(ConnectionPool pool, Transformer<SpellTemplate.Level> levelTransformer) {
        this.levelTransformer = levelTransformer;
        this.pool = new ConnectionPoolUtils(pool);
        utils = new RepositoryUtils<>(this.pool, new SpellTemplateRepository.Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE `SPELL` (" +
                    "`SPELL_ID` INTEGER PRIMARY KEY," +
                    "`SPELL_NAME` VARCHAR(100)," +
                    "`SPELL_SPRITE` INTEGER," +
                    "`SPELL_SPRITE_ARG` VARCHAR(20)," +
                    "`SPELL_LVL_1` TEXT," +
                    "`SPELL_LVL_2` TEXT," +
                    "`SPELL_LVL_3` TEXT," +
                    "`SPELL_LVL_4` TEXT," +
                    "`SPELL_LVL_5` TEXT," +
                    "`SPELL_LVL_6` TEXT," +
                    "`SPELL_TARGET` VARCHAR(32)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE SPELL");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public SpellTemplate get(SpellTemplate entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public SpellTemplate get(int id) {
        return utils.findOne(
            "SELECT * FROM SPELL WHERE SPELL_ID = ?",
            stmt -> stmt.setInt(1, id)
        );
    }

    @Override
    public boolean has(SpellTemplate entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM SPELL WHERE SPELL_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<SpellTemplate> load() {
        return utils.findAll(
            "SELECT * FROM SPELL",
            stmt -> {}
        );
    }
}
