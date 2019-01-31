package fr.quatrevieux.araknemu.data.world.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.ConnectionPool;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Npc;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.Question;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Question repository implementation for SQL database
 *
 * @see Question
 */
final class QuestionRepository implements fr.quatrevieux.araknemu.data.world.repository.environment.npc.QuestionRepository {
    private class Loader implements RepositoryUtils.Loader<Question> {
        @Override
        public Question create(ResultSet rs) throws SQLException {
            return new Question(
                rs.getInt("QUESTION_ID"),
                Arrays.stream(StringUtils.split(rs.getString("RESPONSE_IDS"), ';'))
                    .mapToInt(Integer::parseInt)
                    .toArray(),
                StringUtils.split(rs.getString("PARAMETERS"), ';'),
                rs.getString("CONDITIONS")
            );
        }

        @Override
        public Question fillKeys(Question entity, ResultSet keys) {
            throw new RepositoryException("Read-only entity");
        }
    }

    final private ConnectionPoolUtils pool;
    final private RepositoryUtils<Question> utils;

    public QuestionRepository(ConnectionPool pool) {
        this.pool = new ConnectionPoolUtils(pool);

        utils = new RepositoryUtils<>(this.pool, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            pool.query(
                "CREATE TABLE NPC_QUESTION(" +
                    "QUESTION_ID INTEGER PRIMARY KEY," +
                    "RESPONSE_IDS VARCHAR(100)," +
                    "PARAMETERS VARCHAR(100)," +
                    "CONDITIONS VARCHAR(256)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            pool.query("DROP TABLE NPC_QUESTION");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Question get(int id) {
        return utils.findOne(
            "SELECT * FROM NPC_QUESTION WHERE QUESTION_ID = ?",
            stmt -> stmt.setInt(1, id)
        );
    }

    @Override
    public Question get(Question entity) throws RepositoryException {
        return get(entity.id());
    }

    @Override
    public boolean has(Question entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM NPC_QUESTION WHERE QUESTION_ID = ?",
            stmt -> stmt.setInt(1, entity.id())
        ) > 0;
    }

    @Override
    public Collection<Question> all() throws RepositoryException {
        return utils.findAll("SELECT * FROM NPC_QUESTION");
    }

    @Override
    public Collection<Question> byNpc(Npc npc) {
        return byIds(npc.questions());
    }

    @Override
    public Collection<Question> byIds(int[] ids) {
        switch (ids.length) {
            case 0:
                return Collections.emptyList();

            case 1:
                return utils.findAll("SELECT * FROM NPC_QUESTION WHERE QUESTION_ID = ?", stmt -> stmt.setInt(1, ids[0]));

            default:
                return sortByIds(utils.findAll(
                    "SELECT * FROM NPC_QUESTION WHERE QUESTION_ID IN(" + StringUtils.repeat("?, ", ids.length - 1) + "?)",
                    rs -> {
                        for (int i = 0; i < ids.length; ++i) {
                            rs.setInt(i + 1, ids[i]);
                        }
                    }
                ), ids);
        }
    }

    /**
     * Sort the database data by the request ids order
     *
     * @param data The database data
     * @param ids The sorted ids
     */
    private Collection<Question> sortByIds(Collection<Question> data, int[] ids) {
        Collection<Question> sorted = new ArrayList<>(data.size());

        Map<Integer, Question> questions = new HashMap<>();

        data.forEach(question -> questions.put(question.id(), question));

        for (int id : ids) {
            Question question = questions.get(id);

            if (question != null) {
                sorted.add(question);
            }
        }

        return sorted;
    }
}
