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

package fr.quatrevieux.araknemu.data.living.repository.implementation.sql;

import fr.quatrevieux.araknemu.core.dbal.repository.EntityNotFoundException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryException;
import fr.quatrevieux.araknemu.core.dbal.repository.RepositoryUtils;
import fr.quatrevieux.araknemu.core.dbal.executor.QueryExecutor;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.living.entity.player.PlayerItem;
import fr.quatrevieux.araknemu.data.living.repository.player.PlayerItemRepository;
import fr.quatrevieux.araknemu.data.transformer.Transformer;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import org.apache.commons.lang3.StringUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * SQL implementation for {@link PlayerItem} repository
 */
final class SqlPlayerItemRepository implements PlayerItemRepository {
    private class Loader implements RepositoryUtils.Loader<PlayerItem> {
        @Override
        public PlayerItem create(ResultSet rs) throws SQLException {
            return new PlayerItem(
                rs.getInt("PLAYER_ID"),
                rs.getInt("ITEM_ENTRY_ID"),
                rs.getInt("ITEM_TEMPLATE_ID"),
                effectsTransformer.unserialize(rs.getString("ITEM_EFFECTS")),
                rs.getInt("QUANTITY"),
                rs.getInt("POSITION")
            );
        }

        @Override
        public PlayerItem fillKeys(PlayerItem entity, ResultSet keys) throws SQLException {
            throw new UnsupportedOperationException();
        }
    }

    final private QueryExecutor executor;
    final private RepositoryUtils<PlayerItem> utils;
    final private Transformer<List<ItemTemplateEffectEntry>> effectsTransformer;

    public SqlPlayerItemRepository(QueryExecutor executor, Transformer<List<ItemTemplateEffectEntry>> effectsTransformer) {
        this.executor = executor;
        this.effectsTransformer = effectsTransformer;
        this.utils = new RepositoryUtils<>(this.executor, new Loader());
    }

    @Override
    public void initialize() throws RepositoryException {
        try {
            executor.query(
                "CREATE TABLE PLAYER_ITEM (" +
                    "PLAYER_ID INTEGER," +
                    "ITEM_ENTRY_ID INTEGER," +
                    "ITEM_TEMPLATE_ID INTEGER," +
                    "ITEM_EFFECTS TEXT," +
                    "QUANTITY SMALLINT," +
                    "POSITION TINYINT," +
                    "PRIMARY KEY (PLAYER_ID, ITEM_ENTRY_ID)" +
                ")"
            );
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public void destroy() throws RepositoryException {
        try {
            executor.query("DROP TABLE PLAYER_ITEM");
        } catch (SQLException e) {
            throw new RepositoryException(e);
        }
    }

    @Override
    public Collection<PlayerItem> byPlayer(Player player) {
        return utils.findAll(
            "SELECT * FROM PLAYER_ITEM WHERE PLAYER_ID = ?",
            stmt -> stmt.setInt(1, player.id())
        );
    }

    @Override
    public void update(PlayerItem item) {
        int count = utils.update(
            "UPDATE PLAYER_ITEM SET QUANTITY = ?, POSITION = ? WHERE PLAYER_ID = ? AND ITEM_ENTRY_ID = ?",
            stmt -> {
                stmt.setInt(1, item.quantity());
                stmt.setInt(2, item.position());
                stmt.setInt(3, item.playerId());
                stmt.setInt(4, item.entryId());
            }
        );

        if (count != 1) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public void delete(PlayerItem item) {
        int count = utils.update(
            "DELETE FROM PLAYER_ITEM WHERE PLAYER_ID = ? AND ITEM_ENTRY_ID = ?",
            stmt -> {
                stmt.setInt(1, item.playerId());
                stmt.setInt(2, item.entryId());
            }
        );

        if (count != 1) {
            throw new EntityNotFoundException();
        }
    }

    @Override
    public PlayerItem add(PlayerItem entity) throws RepositoryException {
        utils.update(
            "INSERT INTO PLAYER_ITEM (PLAYER_ID, ITEM_ENTRY_ID, ITEM_TEMPLATE_ID, ITEM_EFFECTS, QUANTITY, POSITION) VALUES (?, ?, ?, ?, ?, ?)",
            stmt -> {
                stmt.setInt(1,    entity.playerId());
                stmt.setInt(2,    entity.entryId());
                stmt.setInt(3,    entity.itemTemplateId());
                stmt.setString(4, effectsTransformer.serialize(entity.effects()));
                stmt.setInt(5,    entity.quantity());
                stmt.setInt(6,    entity.position());
            }
        );

        return entity;
    }

    @Override
    public PlayerItem get(PlayerItem entity) throws RepositoryException {
        return utils.findOne(
            "SELECT * FROM PLAYER_ITEM WHERE PLAYER_ID = ? AND ITEM_ENTRY_ID = ?",
            stmt -> {
                stmt.setInt(1, entity.playerId());
                stmt.setInt(2, entity.entryId());
            }
        );
    }

    @Override
    public boolean has(PlayerItem entity) throws RepositoryException {
        return utils.aggregate(
            "SELECT COUNT(*) FROM PLAYER_ITEM WHERE PLAYER_ID = ? AND ITEM_ENTRY_ID = ?",
            stmt -> {
                stmt.setInt(1, entity.playerId());
                stmt.setInt(2, entity.entryId());
            }
        ) > 0;
    }

    @Override
    public Map<Integer, List<PlayerItem>> forCharacterList(int serverId, int accountId, int[] positions) {
        return utils
            .findAll(
                "SELECT I.* " +
                "FROM PLAYER_ITEM I " +
                "NATURAL JOIN PLAYER " +
                "WHERE SERVER_ID = ? AND ACCOUNT_ID = ? AND POSITION IN(" + StringUtils.repeat("?, ", positions.length - 1) + "?)",
                stmt -> {
                    int i = 0;
                    stmt.setInt(++i, serverId);
                    stmt.setInt(++i, accountId);

                    for (int position : positions) {
                        stmt.setInt(++i, position);
                    }
                }
            )
            .stream()
            .collect(Collectors.groupingBy(PlayerItem::playerId))
        ;
    }
}
