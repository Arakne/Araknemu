package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.NpcTemplate;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Map repository implementation for SQL database
 */
final class NpcTemplateRepository implements fr.quatrevieux.araknemu.data.world.repository.environment.npc.NpcTemplateRepository {
    private class Loader implements RepositoryUtils.Loader<NpcTemplate> {
        @Override
        public NpcTemplate create(ResultSet rs) throws SQLException {
            return new NpcTemplate(
                rs.getInt("NPC_TEMPLATE_ID"),
                rs.getInt("GFXID"),
                rs.getInt("SCALE_X"),
                rs.getInt("SCALE_Y"),
                Sex.values()[rs.getInt("SEX")],
                new Colors(
                    rs.getInt("COLOR1"),
                    rs.getInt("COLOR2"),
                    rs.getInt("COLOR3")
                ),
                rs.getString("ACCESSORIES"),
                rs.getInt("EXTRA_CLIP"),
                rs.getInt("CUSTOM_ARTWORK")
            );
        }

        @Override
        public NpcTemplate fillKeys(NpcTemplate entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<NpcTemplate> utils;

    public NpcTemplateRepository(ConnectionPool pool) {
        this.pool = new ConnectionPoolUtils(pool);

        utils = new RepositoryUtils<>(this.pool, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE NPC_TEMPLATE(" +
                    "NPC_TEMPLATE_ID INTEGER PRIMARY KEY," +
                    "GFXID INTEGER," +
                    "SCALE_X INTEGER," +
                    "SCALE_Y INTEGER," +
                    "SEX TINYINT(1)," +
                    "COLOR1 INTEGER," +
                    "COLOR2 INTEGER," +
                    "COLOR3 INTEGER," +
                    "ACCESSORIES VARCHAR(30)," +
                    "EXTRA_CLIP TINYINT(1)," +
                    "CUSTOM_ARTWORK INTEGER" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE NPC_TEMPLATE");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public NpcTemplate get(int id) {
        return utils.findOne(
            "SELECT * FROM NPC_TEMPLATE WHERE NPC_TEMPLATE_ID = ?",
            stmt -> stmt.setInt(1, id)
        );
    }

    @Override
    public NpcTemplate get(NpcTemplate entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public boolean has(NpcTemplate entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM NPC_TEMPLATE WHERE NPC_TEMPLATE_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<NpcTemplate> all() throws RepositoryException {
        return utils.findAll(
            "SELECT * FROM NPC_TEMPLATE",
            rs -> {}
        );
    }
}
