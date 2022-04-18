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

package fr.quatrevieux.araknemu.game;

import fr.arakne.utils.maps.constant.Direction;
import fr.arakne.utils.value.Colors;
import fr.arakne.utils.value.constant.Gender;
import fr.arakne.utils.value.constant.Race;
import fr.quatrevieux.araknemu.TestingDataSet;
import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.core.dbal.executor.ConnectionPoolExecutor;
import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.value.Geolocation;
import fr.quatrevieux.araknemu.data.world.entity.environment.area.Area;
import fr.quatrevieux.araknemu.data.world.entity.environment.area.SubArea;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerExperience;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.data.world.entity.environment.npc.*;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.data.world.entity.monster.*;
import fr.quatrevieux.araknemu.data.world.repository.monster.MonsterRewardItemRepository;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.data.world.transformer.ItemSetBonusTransformer;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import org.apache.commons.lang3.StringUtils;

import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

public class GameDataSet extends TestingDataSet {
    final private ConnectionPoolExecutor connection;

    public GameDataSet(Container repositories, ConnectionPoolExecutor connection) {
        super(repositories);

        this.connection = connection;
    }

    public GameDataSet pushRaces() throws SQLException, ContainerException {
        Repository<PlayerRace> repository = repository(PlayerRace.class);

        if (repository.has(new PlayerRace(Race.FECA))) {
            return this;
        }

        connection.query(
            "INSERT INTO PLAYER_RACE (RACE_ID, RACE_NAME, RACE_STATS, START_DISCERNMENT, START_PODS, START_LIFE, PER_LEVEL_LIFE, MAP_ID, CELL_ID, STATS_BOOST, RACE_SPELLS, ASTRUB_MAP_ID, ASTRUB_CELL_ID) VALUES " +
                "(1,  'Feca',     '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@2,50@3,150@4,250@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5',           '3|6|17', 10340, 250)," +
                "(2,  'Osamodas', '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@2,50@3,150@4,250@5;11:0@1;12:0@3;13:0@1,100@2,200@3,300@4,400@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5',       '3|6|17', 10340, 250)," +
                "(3,  'Enutrof',  '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 120, 1000, 50, 5, 10300, 320, '10:0@1,50@2,150@3,250@4,350@5;11:0@1;12:0@3;13:0@1,100@2,150@3,230@4,330@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,20@2,60@3,100@4,150@5',   '3|6|17', 10340, 250)," +
                "(4,  'Sram',     '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@1,100@2,200@3,300@4,400@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,100@2,200@3,300@4,400@5;15:0@2,50@3,150@4,250@5',       '3|6|17', 10340, 250)," +
                "(5,  'Xelor',    '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@2,50@3,150@4,250@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5',           '3|6|17', 10340, 250)," +
                "(6,  'Ecaflip',  '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@1,100@2,200@3,300@4,400@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,50@2,100@3,150@4,200@5;15:0@1,20@2,40@3,60@4,80@5',     '3|6|17', 10340, 250)," +
                "(7,  'Eniripsa', '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@2,50@3,150@4,250@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5',           '3|6|17', 10340, 250)," +
                "(8,  'Iop',      '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@1,100@2,200@3,300@4,400@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,20@2,40@3,60@4,80@5',        '3|6|17', 10340, 250)," +
                "(9,  'Cra',      '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@1,50@2,150@3,250@4,350@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,50@2,100@3,150@4,200@5;15:0@1,50@2,150@3,250@4,350@5',   '3|6|17', 10340, 250)," +
                "(10, 'Sadida',   '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@1,50@2,250@3,300@4,400@5;11:0@1;12:0@3;13:0@1,100@2,200@3,300@4,400@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5', '3|6|17', 10340, 250)," +
                "(11, 'Sacrieur', '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@3,100@4,150@5;11:0@1@2;12:0@3;13:0@3,100@4,150@5;14:0@3,100@4,150@5;15:0@3,100@4,150@5',                                          '3|6|17', 10340, 250)," +
                "(12, 'Pandawa',  '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@1,50@2,200@3;11:0@1;12:0@3;13:0@1,50@2,200@3;14:0@1,50@2,200@3;15:0@1,50@2,200@3',                                                '3|6|17', 10340, 250)"
        );

        return this;
    }

    public GameDataSet pushMap(int id, String date, int width, int height, String key, String mapData, String places, Geolocation geolocation, int subAreaId, boolean indoor) throws SQLException, ContainerException {
        Repository<MapTemplate> repository = repository(MapTemplate.class);

        if (repository.has(new MapTemplate(id, null, null, null, null, null, geolocation, subAreaId, false))) {
            return this;
        }

        connection.prepare(
            "INSERT INTO maps (id, date, width, height, key, mapData, places, MAP_X, MAP_Y, SUBAREA_ID, INDOOR) VALUES(?,?,?,?,?,?,?,?,?,?,?)",
            statement -> {
                statement.setInt(1, id);
                statement.setString(2, date);
                statement.setInt(3, width);
                statement.setInt(4, height);
                statement.setString(5, key);
                statement.setString(6, mapData);
                statement.setString(7, places);
                statement.setInt(8, geolocation.x());
                statement.setInt(9, geolocation.y());
                statement.setInt(10, subAreaId);
                statement.setBoolean(11, indoor);

                return statement.execute();
            }
        );

        return this;
    }

    public GameDataSet pushMaps() throws SQLException, ContainerException {
        use(NpcTemplate.class);
        use(Npc.class);
        use(MonsterGroupPosition.class);
        use(MonsterGroupData.class);
        use(MonsterTemplate.class);
        use(MapTrigger.class);

        return
            pushMap(10300, "0802221747", 15, 17, "662838776047515721434a62545f5478543541592532356f25324257716f6e28657d2a5d4471206273545f5e4a614a442c2c73432c35515a553b386f6525324274262f3a747b6b675440557f5260754d6a52343d433b52755e6e6c3b437d417e22514524216771304a6e7e553c32794646287f3e57544c48526a284e2158376b4a414039752c2a2f303d6a5e746f2e323031385e6f483e785929262435777c3141463f363a43784e73345f5e406d2e235d423248427767582c5e4a6a4d234e3f61506d32595e7a2c4a2532355a7f5b5c474e6f732532426062775824253235", "HhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae7MaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaX8HhGae5QiaaGhaaeaaa7zHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaX7HhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeJgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeJgaaaHhaaeJgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae7MaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeHlaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaa5iHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeJga5iHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa7MHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaGhaae5ma7AHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeJga7HHhGaeaaaaaHhGae5UaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaX7HhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaa5jHhGaeaaaaaHhqaeaaa_4HhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaa7RHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa5jHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae5qiaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae5maaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaa", "|", new Geolocation(-4, 3), 440, false)
            .pushMap(10540, "0707191128", 15, 17, "7e3a575438577150732c585135602e35682322772d402e585a3a4745732667286629217c38566a5c686b7779442426746b5454253242756a313e617a70205a644f5455217b3a2a4c70327169226e4e477f38522967367e7240424368233c6163433764215f2040572d735d25324276576a3f427e306c547c64604b746e582a5d61327e4f582d724c722a657e3b71572532354e656b7a333e4e422a7931244c5f", "HhaaeaaaaaHhaaeaEaaaHhaeeaaaaaHhaaeaaaaaHhcJeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa6bHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhIJem0aaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaa6cHhGaeaaa6aHhGaeaaa6cHhGaeaaa6aHhGaeaaa6cHhg-eaaaaaHhM-eaaaaaHhGeeaaaaaHhGaem0aaaHhGaem0aaaHhGaeaaaaaHhqaeqgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaa6aHhGaeaaa6cHhaaeaaaaaHhGaeaaaaaHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaa6cHhGaeaaa6aHhg-eaaa6aHhM-eaEqaaHhGeeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6cHhGae-Ba6bHhaaeaaa6bHhGaeaaaaaHhGeeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaaaaHhGae-AaaaGhaaeaaa9LHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaa6cHhGaeaaa6cHhGaeaaa6bHhaaeaaa6cHhM-eaaaaaHhGeeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6aHhaaeaaa6aHhGaeaIaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaEqaaHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaa95HhGaeaaaaaGhaaeaabbEGhaae-BaaaHhGaeaaa6aHhaaeaaaaaHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaae-CbbRHhGaeaaaaaHhaaeaaaaaHhGaeaEaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhg-eaaaaaHhGae-AaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaaa6aHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaabbIHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa9LHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaem0aaaHhM-eaEq6bHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaem0aaaHhg-eaaaaaGhaee-Da88HhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhaaem0aaaHhGaeaEa6bHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhqaeqgaaaHhaaeaaa6aHhGaeaIa6dHhGaeaaaaaHhGaeaaa6bGhaaeaaaaaHhGae-BaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhaaem0aaaHhw-eaaa6aHhGeeaaaaaHhGaeaaa6cGhaaeaaaaaGhaaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaaaaHhGae-BaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhg-eaaa6dHhGaeaIa6dHhGaeaaaaaHhGaeaaaaaGhaaeaabbPGhaaeaabbYHhGaeaaa6bHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaEq6aHhGeeaaa6dHhGaeaaaaaHhaaeaaaaaGhaaeaaaaaGhaaeaabbUHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhaaeaaaaaHhGeeaaa6dHhGaeaaaaaHhGaeaaaaaGhaae-DbbVHhGae-DaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaIa6bHhGae-Da6dHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhg-eaaa9LHhGeeaaa6dHhGae-DaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGafbpbboHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhg-eaaaaaHhGeeaaa6aHhGae-AaaaHhGaem0aaaHhGaem0aaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGae-DaaaHhGae-BaaaHhGaeaaaaaHhaaeaaaaaHhGaeaEaaaHhGae-DaaaHhGae-DbboHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaabb0GhaaeaabbZHhGaeaaaaaHhaaeaaaaaHhGae-AaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaa6cHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaaa6aHhGeeaEqaaHhGaeaEGaaHhGaem0a95HhGaem0aaaHhIJeaaaaaHhGaeaaaaaHhGaeaabbJHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaa6cHhGaeaaaaaHhaaeaaaaaHhGaeaEaaaHhGaeaIWaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaGhaaeaabbWHhGaeaaa6bHhGae-BaaaHhaaeaaaaaGeaaeaae9cHhGae-AaaaHhGae-Aa6dHhGaeaaaaaHhGaem0aaaHhsJeqgaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaae-DbbFHhGaeaaa6cHhGaeaaa6cGhaaeaabbYGfg-eaaa9LHhg-eaaa6aGhg-eaEq9kHhaeeaaa6dHhaaem0aaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaem0aaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa6aHhaaeaaaaaGhaaeaabb0", "|", new Geolocation(-51, 10), 454, false)
            .pushMap(10340, "0706131721", 15, 17, "682a5a717d49457e73274e3b3023452652224870524b735e6260457e377a4136216f7b5a7b332c55426c7b2776207136333f384333676577377828273860497a36214973525b606b6d3e7c4173716a713c6b232477664f3a6d2f79664f325f655b503e3a6f2c34202330272c4824635349657c2d554a31466a3f7e78667e485d527a203f37495d27664b5333207268452f2532426b74447e3a41215a386a6a5b70223f2d3078335a204543292d496c6366287637525723743f3e4c7155726e262f5f48703b294d4b537b544a4b3f4f7150512670323b6b43295a2e762129393254423944752e74636a6671693a235d34253235677a765841", "HhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa6GHhaaeaaaaaHhaaeaaaaaHhaae6HaaaHhaae60aaaHhaaeaaaaaHhaae6HaaaHhaaeaaaaaGhaaeaaa7oHhaae6HiaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaa6SHhgSe6HaaaHhaaeaaa6IHhGaeaaaaaHhGaeaaaaaHhqaeaaaqgHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7iHhGaeaaaaaHhGaeaaa6IHhMSeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhWaeaaaaaHhGaeJgaaaHhGaeaaaaaGhaaeaaa7hHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaa6THhGaeaaaaaHhGaeaaaaaHhMSe62aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhqaeaaaqgGhaaeaaa7AHhGaeaaaaaHhGaeaaaaaHhaae6Ha7eHhGaeaaaaaHhGaeaaaaaHhGaeaaa6IHhWaeaaaaaHhGaeaaaaaGhaaeaaa7gHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeJgaaaHhGaeaaaaaGhaaeaaa7jHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhWaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGae8uaaaGhaaeaaa7jHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGae8uaaaHhWae60aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhqaeaaaqgHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeJgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7iHhGaeJgaaaHhaaeaaaaaHhaaeJgaaaHhGae6HaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhWaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaa6IGhaaeaaa7hGhaaeaaa7iHhGaeaaaaaHhGaeaaaaaHhGaeJgaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7lGhaae8sa7gHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaGhaaeaaa7gGhaaeaaa7kHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhWae62aaaGhaaeaaa7kGhaaeaaa7hHhGaeaaaaaHhGaeaaaaaGhaaeaaa7lHhaaeaaaaaGhaaeaaa7nHhGaeaaaaaGhaaeaaa7lGhaaeaaa7jHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7hHhGaeaaaaaGhaaeaaa7mHhGaeaaaaaGhaaeJga7hHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhMTgJgaaaHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhGaeaaaaaHhGae8saaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhMSeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaae6HaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7jHhGaeaaa6IHhGaeaaaaaHhaaeaaaaaHhaaeaaa6IHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7gHhGaeaaaaaHhGaeaaaaaHhaaeaaa6GHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaGhaaeaaa7kHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa6GHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhgTeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaa7dHhaaeaaaaaHhaaeaaa6WHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaGhaaeaaa7yHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaa6XHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhMVgaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaa", "a3btbYb_cacQcRc3c5dg|aWa_blbAbCbQb5b6cjcw", new Geolocation(3, 6), 449, false)
            .pushMap(10342, "0706131721", 15, 17, "582f237b213957293c772a6d66455d2f515c365744695c49622f686a6a233038253242353c622532426f407a612e4d5c71394472357c3d7d21487b28202065497d5123702e77646d7c43695026352a434a7763307d232c3a4d626869354d73376271504072253235333e7b2a4073246e79562f625d31535d625e39486a2636324a435754454c792e2e7d3b5a224c7b297921603a407630213672686965447b6c69322a7c5a3872215a26507a39443f7033322c5759376f51335129306a6b3b542f51793446686d356b6d22492831", "HhaaeaaaaaHhaaeaaa6IHhaaeaaaaaHhaaeaaaaaHhaae6HaaaHhaaeaaaaaHhaaeaaaaaHhaae6HaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae6HaaaHhaae8uaaaGhaaeaaa6JHhaaeaaaaaHhaaeaaaaaHhaaeaaa6IHhaaeaaaaaHhaaeaaaaaHhGae8uaaaHhGaeaaa6IHhqaeaaaqgHhMSeaaaaaHhGaeaaaaaGhaaeaaa7CGhaaeaaa7IHhaaeaaa6SHhgSeaaaaaHhaaeaaaaaGhaae8sa7CHhaaeaaa6OHhaaeaaaaaHhaaeaaa6OGhaaeaaa7IHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaGhaaeaaa7EGhaaeaaa7IHhaaeJga6WHhaaeaaaaaGhaaeaaa7FGhaaeaaa7EGhaaeaaa7CGhaaeaaa7zHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhgSeaaa7FHhaaeaaaaaGhaae8sa7GHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGae8uaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaa6XGhaaeaaa7EGhaaeaaa7GHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGae8saaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGae8uaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaa6THhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeJgaaaHhGae8uaaaHhGaeaaaaaHhGaeaaaaaHhGaeJgaaaHhGae8uaaaGhaaeaaa7FHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhGaeaaaaaGhaaeaaa7IGhaaeaaa7EHhGaeaaaaaHhaaeaaaaaHhqaeaaaqgHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaGhaaeaaa7IHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6IHhaaeaaaaaHhaaeaaaaaHhaae6HaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaa6XHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaae8ue7UHhaaeaaaaaHhaaeJga7VHhaaeaaaaaGhaaeaaa7oHhGae8siaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7zHhaaeaaaaaHhaaeJgaaaHhGaeaaaaaHhaaeaaaaaHhgSeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGae8saaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaGhaaeaaa7nHhGaeaaaaaHhGaeaaaaaHhGaeJgaaaHhMSeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaa6IHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7pHhGaeJgaaaHhGaeaaaaaHhaaeaaa7eHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGae8saaaGhaaeaaa7rHhaaeaaa7UHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGae8saaaHhGaeaaaaaHhGaeJga6IGhaaeaaa7mHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeJgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhMTeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7yHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa6VHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaa", "bCbDbTclcPdhebeDe5|bAb2csdmd4ewe0e3fe", new Geolocation(5, 6), 449, false)
        ;
    }

    /**
     * Create a simple player data
     */
    public Player createPlayer(int id) {
        return new Player(id, 10000 + id, 2, "PLAYER_" + id, Race.CRA, Gender.MALE, new Colors(-1, -1, -1), 1, new DefaultCharacteristics(), new Position(10540, 210), EnumSet.allOf(ChannelType.class), 0, 0, Integer.MAX_VALUE, 0, new Position(10540, 210), 0);
    }

    /**
     * Create an push a new player
     */
    public Player pushPlayer(String name, int accountId, int serverId) throws ContainerException {
        Player player = new Player(-1, accountId, serverId, name, Race.CRA, Gender.MALE, new Colors(-1, -1, -1), 1, new DefaultCharacteristics(), new Position(10540, 210), EnumSet.allOf(ChannelType.class), 0, 0, Integer.MAX_VALUE, 0, new Position(10540, 210), 0);

        return push(player);
    }

    /**
     * Create a new player
     */
    public Player pushPlayer(Player player) throws SQLException {
        Player dbPlayer = push(player);

        if (player.id() == -1) {
            return dbPlayer;
        }

        // Change the DB id
        connection.prepare("UPDATE PLAYER SET PLAYER_ID = ? WHERE PLAYER_ID = ?", stmt -> {
            stmt.setInt(1, player.id());
            stmt.setInt(2, dbPlayer.id());
            return stmt.executeUpdate();
        });

        return player;
    }

    public MapTrigger pushTrigger(MapTrigger trigger) throws ContainerException, SQLException {
        use(MapTrigger.class);

        connection.prepare(
            "INSERT INTO MAP_TRIGGER (MAP_ID, CELL_ID, ACTION, ARGUMENTS, CONDITIONS) VALUES (?, ?, ?, ?, ?)",
            stmt -> {
                stmt.setInt(1, trigger.map());
                stmt.setInt(2, trigger.cell());
                stmt.setInt(3, trigger.action());
                stmt.setString(4, trigger.arguments());
                stmt.setString(5, trigger.conditions());

                return stmt.executeUpdate();
            }
        );

        return trigger;
    }

    public SubArea pushSubArea(SubArea area) throws ContainerException, SQLException {
        use(SubArea.class);

        connection.prepare(
            "INSERT INTO SUBAREA (SUBAREA_ID, AREA_ID, SUBAREA_NAME, CONQUESTABLE, ALIGNMENT) VALUES (?, ?, ?, ?, ?)",
            stmt -> {
                stmt.setInt(1, area.id());
                stmt.setInt(2, area.area());
                stmt.setString(3, area.name());
                stmt.setBoolean(4, area.conquestable());
                stmt.setInt(5, area.alignment().id());

                return stmt.executeUpdate();
            }
        );

        return area;
    }

    public GameDataSet pushSubAreas() throws SQLException, ContainerException {
        use(SubArea.class);

        if (repository(SubArea.class).has(new SubArea(1, 0, null, false, null))) {
            return this;
        }

        pushSubArea(new SubArea(1, 0, "Port de Madrestam", true, Alignment.NONE));
        pushSubArea(new SubArea(2, 0, "La montagne des Craqueleurs", true, Alignment.NEUTRAL));
        pushSubArea(new SubArea(3, 0, "Le champ des Ingalsses", true, Alignment.BONTARIAN));
        pushSubArea(new SubArea(4, 0, "La forêt d'Amakna", true, Alignment.BRAKMARIAN));

        pushSubArea(new SubArea(440, 45, "Pitons rocheux", false, Alignment.NONE));
        pushSubArea(new SubArea(449, 45, "Cimetière", false, Alignment.NONE));
        pushSubArea(new SubArea(454, 46, "Plaines herbeuses", true, Alignment.NONE));

        return this;
    }

    public Area pushArea(Area area) throws ContainerException, SQLException {
        use(Area.class);

        connection.prepare(
            "INSERT INTO AREA (AREA_ID, AREA_NAME, SUPERAREA_ID) VALUES (?, ?, ?)",
            stmt -> {
                stmt.setInt(1, area.id());
                stmt.setString(2, area.name());
                stmt.setInt(3, area.superarea());

                return stmt.executeUpdate();
            }
        );

        return area;
    }

    public GameDataSet pushAreas() throws SQLException, ContainerException {
        use(Area.class);

        if (repository(Area.class).has(new Area(12, null, 0))) {
            return this;
        }

        pushArea(new Area(0, "Amakna", 0));
        pushArea(new Area(12, "Lande de Sidimote", 0));
        pushArea(new Area(45, "Incarnam", 3));
        pushArea(new Area(46, "Ile d'Otomaï ", 0));

        return this;
    }

    public ItemTemplate pushItemTemplate(ItemTemplate template) throws SQLException, ContainerException {
        pushItemTypes();

        use(ItemTemplate.class);

        connection.prepare(
            "INSERT INTO ITEM_TEMPLATE (ITEM_TEMPLATE_ID, ITEM_TYPE, ITEM_NAME, ITEM_LEVEL, ITEM_EFFECTS, WEIGHT, ITEM_SET_ID, PRICE, CONDITIONS, WEAPON_INFO) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            stmt -> {
                stmt.setInt(1, template.id());
                stmt.setInt(2, template.type());
                stmt.setString(3, template.name());
                stmt.setInt(4, template.level());
                stmt.setString(5, new ItemEffectsTransformer().serialize(template.effects()));
                stmt.setInt(6, template.weight());
                stmt.setInt(7, template.itemSet());
                stmt.setInt(8, template.price());
                stmt.setString(9, template.condition());
                stmt.setString(10, template.weaponInfo());

                return stmt.executeUpdate();
            }
        );

        return template;
    }

    public GameDataSet pushItemTemplates() throws SQLException, ContainerException {
        pushItemTemplate(new ItemTemplate(39, 1, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100));
        pushItemTemplate(new ItemTemplate(40, 6, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200));
        pushItemTemplate(new ItemTemplate(284, 48, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10));
        pushItemTemplate(new ItemTemplate(694, 23, "Dofus Pourpre", 6, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_DAMAGE_PERCENT, 26, 50, 0, "1d25+25")), 1, "", 0, "", 10));

        pushItemTemplate(new ItemTemplate(2411, 16, "Coiffe du Bouftou", 10, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 40, 0, "1d40+0"), new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 40, 0, "1d40+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(2414, 17, "Cape Bouffante", 10, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INITIATIVE, 1, 300, 0, "1d300+0"), new ItemTemplateEffectEntry(Effect.ADD_VITALITY, 1, 48, 0, "1d48+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(2416, 7, "Marteau du Bouftou", 10, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_EARTH, 4, 8, 0, "1d5+3"), new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_FIRE, 4, 8, 0, "1d5+3"), new ItemTemplateEffectEntry(Effect.ADD_SUMMONS, 1, 0, 0, "0d0+1")), 10, "", 1, "5;1;1;40;40;5;0", 550));
        pushItemTemplate(new ItemTemplate(2419, 9, "Anneau de Bouze le Clerc", 10, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_VITALITY, 1, 28, 0, "1d28+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(2422, 11, "Boufbottes", 10, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_DAMAGE_PERCENT, 1, 15, 0, "1d15+0"), new ItemTemplateEffectEntry(Effect.ADD_VITALITY, 1, 33, 0, "1d33+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(2425, 1, "Amulette du Bouftou", 3, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 10, 0, "1d10+0"), new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "1d10+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(2428, 10, "Amulette du Bouftou", 20, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_PODS, 1, 500, 0, "1d500+0")), 10, "", 1, "", 550));

        pushItemTemplate(new ItemTemplate(2641, 16, "Toady", 30, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_AGILITY, 11, 60, 0, "1d50+10")), 10, "", 7, "", 2700));

        pushItemTemplate(new ItemTemplate(8213, 1, "Amulette du Piou Rouge", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 0, 0, "0d0+1")), 1, "", 60, "", 1));
        pushItemTemplate(new ItemTemplate(8219, 9, "Anneau du Piou Rouge", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 0, 0, "0d0+1")), 1, "", 60, "", 1));
        pushItemTemplate(new ItemTemplate(8225, 11, "Sandales du Piou Rouge", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 0, 0, "0d0+1")), 1, "", 60, "", 1));
        pushItemTemplate(new ItemTemplate(8231, 17, "Cape du Piou Rouge", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 0, 0, "0d0+1")), 10, "", 60, "", 100));
        pushItemTemplate(new ItemTemplate(8237, 10, "Ceinture du Piou Rouge", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 0, 0, "0d0+1")), 1, "", 60, "", 1));
        pushItemTemplate(new ItemTemplate(8243, 16, "Chapeau du Piou Rouge", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 0, 0, "0d0+1")), 1, "", 60, "", 1));

        return this;
    }

    public GameDataSet pushHighLevelItems() throws SQLException, ContainerException {
        pushItemTemplate(new ItemTemplate(112411, 16, "Coiffe du Bouftou", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 40, 0, "1d40+0"), new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 40, 0, "1d40+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(112414, 17, "Cape Bouffante", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INITIATIVE, 1, 300, 0, "1d300+0"), new ItemTemplateEffectEntry(Effect.ADD_VITALITY, 1, 48, 0, "1d48+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(112416, 7, "Marteau du Bouftou", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_EARTH, 4, 8, 0, "1d5+3"), new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_FIRE, 4, 8, 0, "1d5+3"), new ItemTemplateEffectEntry(Effect.ADD_SUMMONS, 1, 0, 0, "0d0+1")), 10, "", 1, "5;1;1;40;40;5;0", 550));
        pushItemTemplate(new ItemTemplate(112419, 9, "Anneau de Bouze le Clerc", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_VITALITY, 1, 28, 0, "1d28+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(112422, 11, "Boufbottes", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_DAMAGE_PERCENT, 1, 15, 0, "1d15+0"), new ItemTemplateEffectEntry(Effect.ADD_VITALITY, 1, 33, 0, "1d33+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(112425, 1, "Amulette du Bouftou", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 10, 0, "1d10+0"), new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "1d10+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(112428, 10, "Amulette du Bouftou", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_PODS, 1, 500, 0, "1d500+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(111694, 23, "Dofus Pourpre", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_DAMAGE_PERCENT, 26, 50, 0, "1d25+25")), 1, "", 0, "", 10));

        return this;
    }

    public GameDataSet pushUsableItems() throws SQLException, ContainerException {
        pushItemTemplate(new ItemTemplate(283, 12, "Fiole de Soin", 10, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_LIFE, 31, 60, 0, "1d30+30")), 1, "", 0, "", 10));
        pushItemTemplate(new ItemTemplate(468, 33, "Pain d'Amakna", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_LIFE, 10, 0, 0, "0d0+10")), 1, "", 0, "", 1));
        pushItemTemplate(new ItemTemplate(800, 76, "Grand Parchemin d'Agilité ", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_CHARACT_AGILITY, 1, 0, 0, "")), 1, "", 0, "", 40000));
        pushItemTemplate(new ItemTemplate(2240, 74, "Petite Fée d'Artifice Rouge", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.FIREWORK, 1, 0, 2900, "")), 1, "", 0, "", 350));

        return this;
    }

    public GameDataSet pushWeaponTemplates() throws SQLException, ContainerException {
        pushItemTemplate(new ItemTemplate(88, 2, "Petit Arc de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 4, 0, "1d4+0")), 10, "CS>1", 0, "4;2;6;30;50;5;0", 250));
        pushItemTemplate(new ItemTemplate(89, 2, "Arc de Boisaille", 2, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 2, 4, 0, "1d3+1")), 10, "CS>2", 0, "4;2;6;30;50;5;1", 300));

        return this;
    }

    public ItemSet pushItemSet(ItemSet itemSet) throws ContainerException, SQLException {
        use(ItemSet.class);

        connection.prepare(
            "INSERT INTO ITEM_SET (ITEM_SET_ID, ITEM_SET_NAME, ITEM_SET_BONUS) VALUES (?, ?, ?)",
            stmt -> {
                stmt.setInt(1, itemSet.id());
                stmt.setString(2, itemSet.name());
                stmt.setString(3, new ItemSetBonusTransformer().serialize(itemSet.bonus()));

                return stmt.executeUpdate();
            }
        );

        return itemSet;
    }

    public GameDataSet pushItemSets() throws SQLException, ContainerException {
        pushItemSet(new ItemSet(
            1, "Panoplie du Bouftou",
            Arrays.asList(
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 5, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 5, 0, 0, "")
                ),
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 10, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 10, 0, 0, "")
                ),
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 10, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 10, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_DAMAGE, 2, 0, 0, "")
                ),
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 30, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 30, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_DAMAGE, 4, 0, 0, "")
                ),
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 40, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 40, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_DAMAGE, 4, 0, 0, "")
                ),
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.ADD_VITALITY, 30, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_WISDOM, 20, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 50, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 50, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_ACTION_POINTS, 1, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_DAMAGE, 5, 0, 0, "")
                )
            )
        ));

        pushItemSet(new ItemSet(
            7, "Panoplie de Toady",
            Arrays.asList(
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_DAMAGE, 5, 0, 0, "")
                )
            )
        ));

        pushItemSet(new ItemSet(
            60, "Panoplie du Piou Rouge",
            Arrays.asList(
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 0, 0, "")
                ),
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "")
                ),
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 4, 0, 0, "")
                ),
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.ADD_INITIATIVE, 30, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 8, 0, 0, "")
                ),
                Arrays.asList(
                    new ItemTemplateEffectEntry(Effect.ADD_INITIATIVE, 60, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 16, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_HEAL_POINTS, 1, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_DAMAGE, 1, 0, 0, "")
                )
            )
        ));

        return this;
    }

    public GameDataSet pushSpells() throws SQLException, ContainerException {
        if (repository(SpellTemplate.class).has(new SpellTemplate(3, null, 0, null, null, null))) {
            return this;
        }

        connection.query(
            "INSERT OR IGNORE INTO `SPELL` (`SPELL_ID`, `SPELL_NAME`, `SPELL_SPRITE`, `SPELL_SPRITE_ARG`, `SPELL_LVL_1`, `SPELL_LVL_2`, `SPELL_LVL_3`, `SPELL_LVL_4`, `SPELL_LVL_5`, `SPELL_LVL_6`, `SPELL_TARGET`) VALUES " +
                "(2, 'Aveuglement', 102, '11,1,1', '101,1,2,,1,0,1d2+0;100,1,,,0,0,0d0+1|101,2,,,1,0,0d0+2;100,2,,,0,0,0d0+2|3|1|6|50|100|false|true|false|true|0|0|1|0|PaPaPaPa||18;19;3;1;41|6|false', '100,2,3,,1,0,1d2+1;101,1,2,,1,0,1d2+0|101,2,,,1,0,0d0+2;100,4,,,0,0,0d0+4|3|1|7|50|100|false|true|false|true|0|0|1|0|PaPaPaPa||18;19;3;1;41|6|false', '101,1,2,,1,0,1d2+0;100,2,4,,0,0,1d3+1|101,2,,,1,0,0d0+2;100,5,,,0,0,0d0+5|3|1|7|50|100|false|true|false|true|0|0|1|0|PaPaPaPa||18;19;3;1;41|6|false', '101,1,2,,1,0,1d2+0;100,2,5,,0,0,1d4+1|101,2,,,1,0,0d0+2;100,6,,,0,0,0d0+6|3|1|7|50|100|false|true|false|true|0|0|1|0|PaPaPaPa||18;19;3;1;41|6|false', '101,2,,,1,0,0d0+2;100,3,7,,0,0,1d5+2|101,2,3,,1,0,1d2+1;100,8,,,0,0,0d0+8|3|1|7|50|100|false|true|false|true|0|0|1|0|PaPaPaPa||18;19;3;1;41|6|false', '101,2,3,,1,0,1d2+1;100,6,15,,0,0,1d10+5|101,3,,,1,0,0d0+3;100,16,,,1,0,0d0+16|3|1|8|50|100|false|true|false|true|0|0|1|0|PaPaPaPa||18;19;3;1;41|106|false', '')," +
                "(3, 'Attaque Naturelle', 103, '30,1,1', '99,2,6,,0,0,1d5+1|99,8,,,0,0,0d0+8|5|1|6|50|100|false|true|false|true|0|0|0|0|PaPa||18;19;3;1;41|1|false', '99,3,7,,0,0,1d5+2|99,9,,,0,0,0d0+9|5|1|6|50|100|false|true|false|true|0|0|0|0|PaPa||18;19;3;1;41|1|false', '99,4,8,,0,0,1d5+3|99,10,,,0,0,0d0+10|4|1|6|50|100|false|true|false|true|0|0|0|0|PaPa||18;19;3;1;41|1|false', '99,5,9,,0,0,1d5+4|99,11,,,0,0,0d0+11|4|1|6|50|100|false|true|false|true|0|0|0|0|PaPa||18;19;3;1;41|1|false', '99,7,11,,0,0,1d5+6|99,13,,,0,0,0d0+13|4|1|7|50|100|false|true|false|true|0|0|0|0|PaPa||18;19;3;1;41|1|false', '99,9,13,,0,0,1d5+8|99,15,,,0,0,0d0+15|3|1|8|50|100|false|true|false|true|0|0|0|0|PaPa||18;19;3;1;41|101|false', '')," +
                "(6, 'Armure Terrestre', 110, '10,1,1', '265,9,,,4,0,0d0+9|265,10,,,4,0,0d0+10|2|0|0|50|100|false|false|false|false|0|0|0|5|CcCc||18;19;3;1;41|1|false', '265,10,,,4,0,0d0+10|265,11,,,4,0,0d0+11|2|0|0|50|100|false|false|false|false|0|0|0|5|CcCc||18;19;3;1;41|1|false', '265,11,,,4,0,0d0+11|265,12,,,4,0,0d0+12|2|0|0|50|100|false|false|false|false|0|0|0|5|CcCc||18;19;3;1;41|1|false', '265,12,,,4,0,0d0+12|265,13,,,4,0,0d0+13|2|0|0|50|100|false|false|false|false|0|0|0|5|CcCc||18;19;3;1;41|1|false', '265,13,,,4,0,0d0+13|265,14,,,4,0,0d0+14|2|0|0|50|100|false|false|false|false|0|0|0|5|CcCc||18;19;3;1;41|1|false', '265,16,,,4,0,0d0+16|265,17,,,4,0,0d0+17|2|0|0|50|100|false|false|false|false|0|0|0|5|CcCc||18;19;3;1;41|101|false', '4')," +
                "(17, 'Glyphe Agressif', 0, '0,1,1', '401,1503,1,2,3,0||3|1|3|0|100|false|false|true|true|0|0|0|2|Cc||18;19;3;1;41|1|false', '401,1503,2,2,3,0||3|1|4|0|100|false|false|true|true|0|0|0|2|Cc||18;19;3;1;41|1|false', '401,1503,3,2,3,0||3|1|5|0|100|false|false|true|true|0|0|0|2|Cc||18;19;3;1;41|1|false', '401,1503,4,2,3,0||3|1|6|0|100|false|false|true|true|0|0|0|2|Cc||18;19;3;1;41|1|false', '401,1503,5,2,3,0||3|1|7|0|100|false|false|true|true|0|0|0|2|Cc||18;19;3;1;41|1|false', '401,1503,6,2,4,0||3|1|8|0|100|false|false|true|true|0|0|0|2|Cc||18;19;3;1;41|101|false', '')," +
                "(202, 'Morsure du Bouftou', 0, '0,1,1', '100,4,9,,0,0,1d6+3|100,13,,,0,0,0d0+13|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '100,6,11,,0,0,1d6+5|100,17,,,0,0,0d0+17|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '100,8,13,,0,0,1d6+7|100,21,,,0,0,0d0+21|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '100,10,15,,0,0,1d6+9|100,25,,,0,0,0d0+25|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '100,12,17,,0,0,1d6+11|100,29,,,0,0,0d0+29|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '', '')"
        );

        return this;
    }

    public GameDataSet pushFunctionalSpells() throws SQLException, ContainerException {
        use(SpellTemplate.class);

        connection.query(
            "INSERT INTO `SPELL` (`SPELL_ID`, `SPELL_NAME`, `SPELL_SPRITE`, `SPELL_SPRITE_ARG`, `SPELL_LVL_1`, `SPELL_LVL_2`, `SPELL_LVL_3`, `SPELL_LVL_4`, `SPELL_LVL_5`, `SPELL_LVL_6`, `SPELL_TARGET`) VALUES " + StringUtils.join(new String[] {
                "(181, 'Tremblement', 1003, '11,1,1', '99,2,,,2,0,0d0+2|99,4,,,2,0,0d0+4|2|0|0|50|100|false|false|false|false|0|1|0|5|CfCf||18;19;3;1;41|26|false', '99,3,,,2,0,0d0+3|99,6,,,2,0,0d0+6|2|0|0|50|100|false|false|false|false|0|1|0|5|CgCg||18;19;3;1;41|26|false', '99,3,,,3,0,0d0+3|99,6,,,3,0,0d0+6|2|0|0|50|100|false|false|false|false|0|1|0|5|ChCh||18;19;3;1;41|26|false', '99,4,,,3,0,0d0+4|99,8,,,3,0,0d0+8|2|0|0|50|100|false|false|false|false|0|1|0|5|CiCi||18;19;3;1;41|26|false', '99,5,,,4,0,0d0+5|99,10,,,4,0,0d0+10|2|0|0|50|100|false|false|false|false|0|1|0|5|CjCj||18;19;3;1;41|26|false', '99,7,,,4,0,0d0+7|99,12,,,4,0,0d0+12|2|0|0|50|100|false|false|false|false|0|1|0|5|CkCk||18;19;3;1;41|126|false', '')",
                "(109, 'Bluff', 611, '31,1,1', '96,1,22,,0,50,1d22+0;98,1,22,,0,50,1d22+0|98,25,,,0,50,0d0+25;96,25,,,0,50,0d0+25|4|1|4|50|100|false|true|false|false|0|0|0|0|PaPaPaPa||18;19;3;1;41|3|false', '96,1,25,,0,50,1d25+0;98,1,25,,0,50,1d25+0|98,28,,,0,50,0d0+28;96,28,,,0,50,0d0+28|4|1|4|50|100|false|true|false|false|0|0|0|0|PaPaPaPa||18;19;3;1;41|3|false', '96,1,28,,0,50,1d28+0;98,1,28,,0,50,1d28+0|98,33,,,0,50,0d0+33;96,33,,,0,50,0d0+33|4|1|4|50|100|false|true|false|false|0|0|0|0|PaPaPaPa||18;19;3;1;41|3|false', '96,1,35,,0,50,1d35+0;98,1,35,,0,50,1d35+0|98,40,,,0,50,0d0+40;96,40,,,0,50,0d0+40|4|1|4|50|100|false|true|false|false|0|0|0|0|PaPaPaPa||18;19;3;1;41|3|false', '96,1,45,,0,50,1d45+0;98,1,45,,0,50,1d45+0|98,50,,,0,50,0d0+50;96,50,,,0,50,0d0+50|4|1|4|50|100|false|true|false|false|0|0|0|0|PaPaPaPa||18;19;3;1;41|3|false', '96,1,50,,0,50,1d50+0;98,1,50,,0,50,1d50+0|98,55,,,0,50,0d0+55;96,55,,,0,50,0d0+55|3|1|4|50|100|false|true|false|false|0|0|0|0|PaPaPaPa||18;19;3;1;41|103|false', '')",
                "(4, 'Renvoi de Sort', 101, '10,1,1', '106,,1,100,1,0||3|0|1|0|100|false|false|false|true|0|0|0|6|Pa||18;19;3;1;41|3|false', '106,,2,100,1,0||3|0|2|0|100|false|false|false|true|0|0|0|6|Pa||18;19;3;1;41|3|false', '106,,3,100,1,0||3|0|3|0|100|false|false|false|true|0|0|0|6|Pa||18;19;3;1;41|3|false', '106,,4,100,1,0||3|0|4|0|100|false|false|false|true|0|0|0|6|Pa||18;19;3;1;41|3|false', '106,,5,100,1,0||3|0|5|0|100|false|false|false|true|0|0|0|6|Pa||18;19;3;1;41|3|false', '106,,6,100,1,0||3|0|6|0|100|false|false|false|true|0|0|0|6|Pa||18;19;3;1;41|103|false', '')",
                "(115, 'Odorat', 2112, '10,1,1', '168,1,2,,3,0,1d2+0;111,1,2,,3,0,1d2+0;169,1,2,,3,0,1d2+0;128,1,2,,3,0,1d2+0|168,1,2,,6,0,1d2+0;111,1,2,,6,0,1d2+0;169,1,2,,6,0,1d2+0;128,1,2,,6,0,1d2+0|6|0|0|50|100|false|false|false|false|0|0|0|10|CfCfCfCfCfCfCfCf||18;19;3;1;41|54|false', '168,1,2,,3,0,1d2+0;111,2,3,,3,0,1d2+1;169,1,2,,3,0,1d2+0;128,2,3,,3,0,1d2+1|168,1,2,,6,0,1d2+0;111,2,3,,6,0,1d2+1;169,1,2,,6,0,1d2+0;128,2,3,,6,0,1d2+1|6|0|0|50|100|false|false|false|false|0|0|0|10|CfCfCfCfCfCfCfCf||18;19;3;1;41|54|false', '168,1,3,,3,0,1d3+0;111,2,4,,3,0,1d3+1;169,1,3,,3,0,1d3+0;128,2,4,,3,0,1d3+1|168,1,3,,6,0,1d3+0;111,2,4,,6,0,1d3+1;169,1,3,,6,0,1d3+0;128,2,4,,6,0,1d3+1|6|0|0|50|100|false|false|false|false|0|0|0|10|CfCfCfCfCfCfCfCf||18;19;3;1;41|54|false', '168,1,3,,3,0,1d3+0;111,2,4,,3,0,1d3+1;169,1,4,,3,0,1d4+0;128,2,5,,3,0,1d4+1|168,1,3,,6,0,1d3+0;111,2,4,,6,0,1d3+1;169,1,4,,6,0,1d4+0;128,2,5,,6,0,1d4+1|6|0|0|50|100|false|false|false|false|0|0|0|10|CfCfCfCfCfCfCfCf||18;19;3;1;41|54|false', '168,1,4,,3,0,1d4+0;111,2,5,,3,0,1d4+1;169,1,4,,3,0,1d4+0;128,2,5,,3,0,1d4+1|168,1,4,,6,0,1d4+0;111,2,5,,6,0,1d4+1;169,1,4,,6,0,1d4+0;128,2,5,,6,0,1d4+1|6|0|0|50|100|false|false|false|false|0|0|0|10|CfCfCfCfCfCfCfCf||18;19;3;1;41|54|false', '168,1,4,,4,0,1d4+0;111,2,5,,4,0,1d4+1;169,1,4,,4,0,1d4+0;128,2,5,,4,0,1d4+1|168,1,4,,8,0,1d4+0;111,2,5,,8,0,1d4+1;169,1,4,,8,0,1d4+0;128,2,5,,8,0,1d4+1|5|0|0|50|100|false|false|false|false|0|0|0|7|CgCgCgCgCgCgCgCg||18;19;3;1;41|154|false', '')",
                "(42, 'Chance', 301, '10,1,1', '123,11,20,,5,0,1d10+10|123,31,,,5,0,0d0+31|4|0|1|50|100|false|false|false|false|0|0|0|6|PaPa||18;19;3;1;41|6|false', '123,21,30,,5,0,1d10+20|123,51,,,5,0,0d0+51|4|0|1|50|100|false|false|false|false|0|0|0|6|PaPa||18;19;3;1;41|6|false', '123,31,40,,5,0,1d10+30|123,71,,,5,0,0d0+71|4|0|1|50|100|false|false|false|false|0|0|0|6|PaPa||18;19;3;1;41|6|false', '123,41,50,,5,0,1d10+40|123,91,,,5,0,0d0+91|4|0|1|50|100|false|false|false|false|0|0|0|6|PaPa||18;19;3;1;41|6|false', '123,51,60,,5,0,1d10+50|123,111,,,5,0,0d0+111|3|0|1|50|100|false|false|false|false|0|0|0|6|PaPa||18;19;3;1;41|6|false', '123,61,70,,5,0,1d10+60|123,120,,,5,0,0d0+120|2|0|1|50|100|false|false|false|false|0|0|0|6|PaPa||18;19;3;1;41|106|false', '')",
                "(468, 'Flêche d huile', 911, '51,0,1', '155,200,,,5,0,0d0+200||3|1|10|0|0|false|false|false|true|0|0|0|3|Pa||18;19;3;1;41|0|false', '155,250,,,5,0,0d0+250||3|1|10|0|0|false|false|false|true|0|0|0|3|Pa||18;19;3;1;41|0|false', '155,300,,,5,0,0d0+300||3|1|10|0|0|false|false|false|true|0|0|0|3|Pa||18;19;3;1;41|0|false', '155,350,,,5,0,0d0+350||3|1|10|0|0|false|false|false|true|0|0|0|3|Pa||18;19;3;1;41|0|false', '155,400,,,5,0,0d0+400||3|1|10|0|0|false|false|false|true|0|0|0|3|Pa||18;19;3;1;41|0|false', '', '')",
                "(1, 'Armure Incandescente', 108, '10,1,1', '265,7,,16,4,0,0d0+7|265,8,,16,4,0,0d0+8|2|0|0|50|100|false|false|false|false|0|0|0|5|CcCc||18;19;3;1;41|9|false', '265,8,,16,4,0,0d0+8|265,9,,16,4,0,0d0+9|2|0|0|50|100|false|false|false|false|0|0|0|5|CcCc||18;19;3;1;41|9|false', '265,9,,16,4,0,0d0+9|265,10,,16,4,0,0d0+10|2|0|0|50|100|false|false|false|false|0|0|0|5|CcCc||18;19;3;1;41|9|false', '265,10,,16,4,0,0d0+10|265,11,,16,4,0,0d0+11|2|0|0|50|100|false|false|false|false|0|0|0|5|CcCc||18;19;3;1;41|9|false', '265,11,,16,4,0,0d0+11|265,12,,16,4,0,0d0+12|2|0|0|50|100|false|false|false|false|0|0|0|5|CcCc||18;19;3;1;41|9|false', '265,14,,16,4,0,0d0+14|265,15,,16,4,0,0d0+15|2|0|0|50|100|false|false|false|false|0|0|0|5|CcCc||18;19;3;1;41|109|false', '4')",
                "(103, 'Chance d Ecaflip', 603, '10,1,1', '79,2,1,50,2,0|79,3,2,50,2,0|2|0|1|50|100|false|true|false|true|0|0|0|10|PaPa||18;19;3;1;41|1|false', '79,2,1,50,2,0|79,3,2,50,2,0|2|0|2|50|100|false|true|false|true|0|0|0|9|PaPa||18;19;3;1;41|1|false', '79,2,1,50,2,0|79,3,2,50,2,0|2|0|3|50|100|false|true|false|true|0|0|0|8|PaPa||18;19;3;1;41|1|false', '79,2,1,50,2,0|79,3,2,50,2,0|2|0|4|50|100|false|true|false|true|0|0|0|7|PaPa||18;19;3;1;41|1|false', '79,2,1,50,3,0|79,3,2,50,3,0|2|0|5|50|100|false|true|false|true|0|0|0|6|PaPa||18;19;3;1;41|1|false', '79,3,2,50,3,0|79,4,3,50,3,0|2|0|6|50|100|false|true|false|true|0|0|0|5|PaPa||18;19;3;1;41|101|false', '')",
                "(686, 'Picole', 0, '0,5,1', '950,,,1,5,0;169,1,,,5,0,0d0+1;212,5,,,5,0,0d0+5;211,5,,,5,0,0d0+5;213,5,,,5,0,0d0+5;214,5,,,5,0,0d0+5;210,5,,,5,0,0d0+5;149,1,,8010,5,0|950,,,1,5,0;212,10,,,5,0,0d0+10;211,10,,,5,0,0d0+10;213,10,,,5,0,0d0+10;214,10,,,5,0,0d0+10;210,10,,,5,0,0d0+10;149,1,,8010,5,0|5|0|0|40|100|false|true|false|false|0|0|0|0|PaPaPaPaPaPaPaPaPaPaPaPaPaPaPa||1;18;19;3;1;41|1|false', '950,,,1,6,0;169,1,,,6,0,0d0+1;212,10,,,6,0,0d0+10;211,10,,,6,0,0d0+10;213,10,,,6,0,0d0+10;214,10,,,6,0,0d0+10;210,10,,,6,0,0d0+10;149,1,,8010,6,0|950,,,1,6,0;212,15,,,6,0,0d0+15;211,15,,,6,0,0d0+15;213,15,,,6,0,0d0+15;214,15,,,6,0,0d0+15;210,15,,,6,0,0d0+15;149,1,,8010,6,0|4|0|0|40|100|false|true|false|false|0|0|0|0|PaPaPaPaPaPaPaPaPaPaPaPaPaPaPa||1;18;19;3;1;41|1|false', '950,,,1,7,0;169,1,,,7,0,0d0+1;212,12,,,7,0,0d0+12;211,12,,,7,0,0d0+12;213,12,,,7,0,0d0+12;214,12,,,7,0,0d0+12;210,12,,,7,0,0d0+12;149,1,,8010,7,0|950,,,1,7,0;212,17,,,7,0,0d0+17;211,17,,,7,0,0d0+17;213,17,,,7,0,0d0+17;214,17,,,7,0,0d0+17;210,17,,,7,0,0d0+17;149,1,,8010,7,0|4|0|0|40|100|false|true|false|false|0|0|0|0|PaPaPaPaPaPaPaPaPaPaPaPaPaPaPa||1;18;19;3;1;41|1|false', '950,,,1,8,0;169,1,,,8,0,0d0+1;212,15,,,8,0,0d0+15;211,15,,,8,0,0d0+15;213,15,,,8,0,0d0+15;214,15,,,8,0,0d0+15;210,15,,,8,0,0d0+15;149,1,,8010,8,0|950,,,1,8,0;212,20,,,8,0,0d0+20;211,20,,,8,0,0d0+20;213,20,,,8,0,0d0+20;214,20,,,8,0,0d0+20;210,20,,,8,0,0d0+20;149,1,,8010,8,0|3|0|0|40|100|false|true|false|false|0|0|0|0|PaPaPaPaPaPaPaPaPaPaPaPaPaPaPa||1;18;19;3;1;41|1|false', '950,,,1,9,0;169,1,,,9,0,0d0+1;212,20,,,9,0,0d0+20;211,20,,,9,0,0d0+20;213,20,,,9,0,0d0+20;214,20,,,9,0,0d0+20;210,20,,,9,0,0d0+20;149,1,,8010,9,0|950,,,1,9,0;212,25,,,9,0,0d0+25;211,25,,,9,0,0d0+25;213,25,,,9,0,0d0+25;214,25,,,9,0,0d0+25;210,25,,,9,0,0d0+25;149,1,,8010,9,0|2|0|0|40|100|false|true|false|false|0|0|0|0|PaPaPaPaPaPaPaPaPaPaPaPaPaPaPa||1;18;19;3;1;41|1|false', '950,,,1,10,0;169,1,,,10,0,0d0+1;212,25,,,10,0,0d0+25;211,25,,,10,0,0d0+25;213,25,,,10,0,0d0+25;214,25,,,10,0,0d0+25;210,25,,,10,0,0d0+25;149,1,,8010,10,0|950,,,1,10,0;212,30,,,10,0,0d0+30;211,30,,,10,0,0d0+30;213,30,,,10,0,0d0+30;214,30,,,10,0,0d0+30;210,30,,,10,0,0d0+30;149,1,,8010,10,0|1|0|0|40|100|false|true|false|false|0|0|0|0|PaPaPaPaPaPaPaPaPaPaPaPaPaPaPa||1;18;19;3;1;41|101|false', '')",
                "(699, 'Lait de Bambou', 1213, '10,1,1', '132,,,,0,0;149,,,,0,0;951,,,1,0,0||6|0|0|0|100|true|true|false|false|0|0|0|0|PaPaPa|1|18;19;3;41|48|false', '132,,,,0,0;149,,,,0,0;951,,,1,0,0||5|0|0|0|100|true|true|false|false|0|0|0|0|PaPaPa|1|18;19;3;41|48|false', '132,,,,0,0;149,,,,0,0;951,,,1,0,0||4|0|0|0|100|true|true|false|false|0|0|0|0|PaPaPa|1|18;19;3;41|48|false', '132,,,,0,0;149,,,,0,0;951,,,1,0,0||3|0|0|0|100|true|true|false|false|0|0|0|0|PaPaPa|1|18;19;3;41|48|false', '132,,,,0,0;149,,,,0,0;951,,,1,0,0||2|0|0|0|100|true|true|false|false|0|0|0|0|PaPaPa|1|18;19;3;41|48|false', '132,,,,0,0;149,,,,0,0;951,,,1,0,0||1|0|0|0|100|true|true|false|false|0|0|0|0|PaPaPa|1|18;19;3;41|148|false', '')",
                "(223, 'Météorite', 2008, '51,1,1', '97,11,30,,0,0,1d20+10|99,16,35,,0,0,1d20+15|5|1|6|50|50|false|false|false|true|0|0|0|0|CcCc||18;19;3;1;41|0|true', '97,11,32,,0,0,1d22+10|99,16,37,,0,0,1d22+15|5|1|7|50|50|false|false|false|true|0|0|0|0|CcCc||18;19;3;1;41|0|true', '97,11,35,,0,0,1d25+10|99,16,40,,0,0,1d25+15|5|1|7|50|50|false|false|false|true|0|0|0|0|CcCc||18;19;3;1;41|0|true', '97,11,38,,0,0,1d28+10|99,16,43,,0,0,1d28+15|5|1|8|50|50|false|false|false|true|0|0|0|0|CcCc||18;19;3;1;41|0|true', '97,11,40,,0,0,1d30+10|99,16,45,,0,0,1d30+15|5|1|9|50|50|false|false|false|true|0|0|0|0|CcCc||18;19;3;1;41|0|true', '', '')",
                "(584, 'Motivation Sylvestre', 0, '0,1,1', '950,,,9,5,0;115,4,5,,5,0,1d2+3||1|1|4|0|0|false|true|false|true|0|1|0|6|CeCe||18;19;3;1;41|0|true', '950,,,9,5,0;115,4,5,,5,0,1d2+3||1|1|4|0|0|false|true|false|true|0|1|0|6|CeCe||18;19;3;1;41|0|true', '950,,,9,5,0;115,4,5,,5,0,1d2+3||1|1|4|0|0|false|true|false|true|0|1|0|6|CeCe||18;19;3;1;41|0|true', '950,,,9,5,0;115,4,5,,5,0,1d2+3||1|1|4|0|0|false|true|false|true|0|1|0|6|CeCe||18;19;3;1;41|0|true', '950,,,9,5,0;115,4,5,,5,0,1d2+3||1|1|4|0|0|false|true|false|true|0|1|0|6|CeCe||18;19;3;1;41|0|true', '', '4;4')",
                "(142, 'Bond', 807, '51,1,1', '4,2,,0,0,0||6|1|2|0|100|false|false|true|false|0|0|0|0|Pa||7;18;19;3;1;41|1|false', '4,2,,0,0,0||5|1|2|0|100|false|false|true|false|0|0|0|0|Pa||7;18;19;3;1;41|1|false', '4,3,,0,0,0||5|1|3|0|100|false|false|true|false|0|0|0|0|Pa||7;18;19;3;1;41|1|false', '4,4,,0,0,0||5|1|4|0|100|false|false|true|false|0|0|0|0|Pa||7;18;19;3;1;41|1|false', '4,5,,0,0,0||5|1|5|0|100|false|false|true|false|0|0|0|0|Pa||7;18;19;3;1;41|1|false', '4,6,,0,0,0||5|1|6|0|100|false|false|true|false|0|0|0|0|Pa||7;18;19;3;1;41|101|false', '')",
                "(1630, 'Test skip turn', -1, '0,0,0', '140,,,,0,0||1|0|5|0|50|false|true|false|false|0|0|0|3|Pa||18;19;3;1;41|0|false', '140,,,,0,0||1|0|5|0|50|false|true|false|false|0|0|0|3|Pa||18;19;3;1;41|0|false', '140,,,,0,0||1|0|5|0|50|false|true|false|false|0|0|0|3|Pa||18;19;3;1;41|0|false', '140,,,,0,0||1|0|5|0|50|false|true|false|false|0|0|0|3|Pa||18;19;3;1;41|0|false', '140,,,,0,0||1|0|5|0|50|false|true|false|false|0|0|0|3|Pa||18;19;3;1;41|0|false', '', '')",
                "(49, 'Pelle Fantomatique', 309, '51,1,1', '99,6,10,,0,0,1d5+5|132,,,,0,0;99,6,10,,0,0,1d5+5|5|0|4|20|100|false|true|false|true|0|0|0|0|PaPaPa||18;19;3;1;41|3|false', '99,7,11,,0,0,1d5+6|132,,,,0,0;99,7,11,,0,0,1d5+6|5|0|5|18|100|false|true|false|true|0|0|0|0|PaPaPa||18;19;3;1;41|3|false', '99,8,12,,0,0,1d5+7|132,,,,0,0;99,8,12,,0,0,1d5+7|5|0|6|15|100|false|true|false|true|0|0|0|0|PaPaPa||18;19;3;1;41|3|false', '99,9,13,,0,0,1d5+8|132,,,,0,0;99,9,13,,0,0,1d5+8|5|0|7|12|100|false|true|false|true|0|0|0|0|PaPaPa||18;19;3;1;41|3|false', '99,10,14,,0,0,1d5+9|132,,,,0,0;99,10,14,,0,0,1d5+9|4|0|8|10|100|false|true|false|true|0|0|0|0|PaPaPa||18;19;3;1;41|3|false', '99,16,20,,0,0,1d5+15|132,,,,0,0;99,20,,,0,0,0d0+20|4|0|9|10|100|false|true|false|true|0|0|0|0|PaPaPa||18;19;3;1;41|103|false', '')",
                "(126, 'Mot Stimulant', 705, '11,1,1', '111,1,,,3,0,0d0+1;89,10,,,0,0,0d0+10|111,1,,,3,0,0d0+1|3|0|0|50|100|false|true|false|false|0|0|0|5|XcPaXc||18;19;3;1;41|9|false', '111,1,2,,3,0,1d2+0;89,10,,,0,0,0d0+10|111,2,,,3,0,0d0+2|2|0|0|50|100|false|true|false|false|0|0|0|5|XcPaXc||18;19;3;1;41|9|false', '111,1,2,,4,0,1d2+0;89,10,,,0,0,0d0+10|111,2,,,4,0,0d0+2|2|0|0|50|100|false|true|false|false|0|0|0|5|XcPaXc||18;19;3;1;41|9|false', '111,1,2,,4,0,1d2+0;89,10,,,0,0,0d0+10|111,2,,,4,0,0d0+2|2|0|0|50|100|false|true|false|false|0|0|0|5|XdPaXd||18;19;3;1;41|9|false', '111,1,2,,5,0,1d2+0;89,10,,,0,0,0d0+10|111,2,,,5,0,0d0+2|2|0|0|50|100|false|true|false|false|0|0|0|5|XePaXe||18;19;3;1;41|9|false', '111,2,,,5,0,0d0+2;89,10,,,0,0,0d0+10|111,2,3,,5,0,1d2+1|2|0|0|50|100|false|true|false|false|0|0|0|5|XfPaXf||18;19;3;1;41|109|false', '4;0')",
                "(27, 'Piqûre Motivante', 205, '51,1,1', '111,1,,,4,0,0d0+1;111,1,,,2,0,0d0+1|111,2,,,4,0,0d0+2;111,1,,,2,0,0d0+1|3|1|4|40|100|false|true|false|true|0|0|0|5|XbXbXbXb||18;19;3;1;41|26|false', '111,1,,,5,0,0d0+1;111,1,,,2,0,0d0+1|111,2,,,5,0,0d0+2;111,1,,,2,0,0d0+1|3|1|5|40|100|false|true|false|true|0|0|0|5|XbXbXbXb||18;19;3;1;41|26|false', '111,2,,,5,0,0d0+2;111,1,,,2,0,0d0+1|111,3,,,5,0,0d0+3;111,1,,,2,0,0d0+1|3|1|6|40|100|false|true|false|true|0|0|0|5|XbXbXbXb||18;19;3;1;41|26|false', '111,3,,,5,0,0d0+3;111,1,,,2,0,0d0+1|111,4,,,5,0,0d0+4;111,1,,,2,0,0d0+1|3|1|7|40|100|false|true|false|true|0|0|0|5|XbXbXbXb||18;19;3;1;41|26|false', '111,3,,,5,0,0d0+3;111,2,,,2,0,0d0+2|111,4,,,5,0,0d0+4;111,2,,,3,0,0d0+2|3|1|8|40|100|false|true|false|true|0|0|0|4|XbXbXbXb||18;19;3;1;41|26|false', '111,4,,,5,0,0d0+4;111,2,,,2,0,0d0+2|111,5,,,5,0,0d0+5;111,2,,,3,0,0d0+2|3|1|9|40|100|false|true|false|true|0|0|0|3|XbXbXbXb||18;19;3;1;41|126|false', '8;18')",
                "(148, 'Amplification', 801, '10,1,1', '112,1,2,,1,0,1d2+0|112,1,2,,2,0,1d2+0|1|0|1|45|100|false|false|false|false|0|0|1|0|PaPa||18;19;3;1;41|17|false', '112,2,3,,1,0,1d2+1|112,2,3,,2,0,1d2+1|1|0|1|45|100|false|false|false|false|0|0|1|0|PaPa||18;19;3;1;41|17|false', '112,3,4,,1,0,1d2+2|112,3,4,,2,0,1d2+2|1|0|1|45|100|false|false|false|false|0|0|1|0|PaPa||18;19;3;1;41|17|false', '112,4,5,,1,0,1d2+3|112,4,5,,2,0,1d2+3|1|0|1|45|100|false|false|false|false|0|0|1|0|PaPa||18;19;3;1;41|17|false', '112,7,8,,1,0,1d2+6|112,7,8,,2,0,1d2+6|1|0|1|45|100|false|false|false|false|0|0|1|0|PaPa||18;19;3;1;41|17|false', '112,11,12,,1,0,1d2+10|112,11,12,,2,0,1d2+10|1|0|1|45|100|false|false|false|false|0|0|1|0|PaPa||18;19;3;1;41|117|false', '')",
                "(145, 'Epée Divine', 806, '21,2,1', '98,5,14,,0,0,1d10+4;112,1,,,3,0,0d0+1|98,15,,,0,0,0d0+15|5|0|0|40|100|false|false|false|false|0|0|0|0|XbXbXb||18;19;3;1;41|6|false', '98,6,15,,0,0,1d10+5;112,1,,,3,0,0d0+1|98,16,,,0,0,0d0+16|5|0|0|40|100|false|false|false|false|0|0|0|0|XbXbXb||18;19;3;1;41|6|false', '98,7,16,,0,0,1d10+6;112,2,,,3,0,0d0+2|98,17,,,0,0,0d0+17|5|0|0|40|100|false|false|false|false|0|0|0|0|XbXbXb||18;19;3;1;41|6|false', '98,8,17,,0,0,1d10+7;112,2,,,3,0,0d0+2|98,18,,,0,0,0d0+18|5|0|0|40|100|false|false|false|false|0|0|0|0|XbXbXb||18;19;3;1;41|6|false', '98,9,18,,0,0,1d10+8;112,2,,,3,0,0d0+2|98,19,,,0,0,0d0+19|4|0|0|40|100|false|false|false|false|0|0|0|0|XbXbXb||18;19;3;1;41|6|false', '98,11,20,,0,0,1d10+10;112,3,,,3,0,0d0+3|98,21,,,0,0,0d0+21|3|0|0|40|100|false|false|false|false|0|0|0|0|XbXbXb||18;19;3;1;41|106|false', '2;32')",
                "(168, 'Oeil de Taupe', 1014, '51,1,1', '116,6,,,3,0,0d0+6;91,8,10,,0,0,1d3+7|116,6,,,3,0,0d0+6;91,11,13,,0,0,1d3+10|3|1|5|40|100|false|true|false|true|0|0|0|4|CcCcCcCc||18;19;3;1;41|17|false', '116,6,,,3,0,0d0+6;91,9,11,,0,0,1d3+8|116,6,,,3,0,0d0+6;91,12,14,,0,0,1d3+11|3|1|6|40|100|false|true|false|true|0|0|0|4|CcCcCcCc||18;19;3;1;41|17|false', '116,6,,,3,0,0d0+6;91,10,12,,0,0,1d3+9|116,6,,,3,0,0d0+6;91,13,15,,0,0,1d3+12|3|1|7|40|100|false|true|false|true|0|0|0|4|CcCcCcCc||18;19;3;1;41|17|false', '116,6,,,3,0,0d0+6;91,11,13,,0,0,1d3+10|116,6,,,3,0,0d0+6;91,14,16,,0,0,1d3+13|3|1|8|40|100|false|true|false|true|0|0|0|4|CcCcCcCc||18;19;3;1;41|17|false', '116,6,,,3,0,0d0+6;91,12,14,,0,0,1d3+11|116,6,,,3,0,0d0+6;91,15,17,,0,0,1d3+14|3|1|9|40|100|false|true|false|true|0|0|0|4|CcCcCcCc||18;19;3;1;41|17|false', '116,6,,,3,0,0d0+6;91,15,17,,0,0,1d3+14|116,6,,,3,0,0d0+6;91,19,21,,0,0,1d3+18|3|1|10|40|100|false|true|false|true|0|0|0|4|CdCdCdCd||18;19;3;1;41|117|false', '')",
                "(157, 'Epée Céleste', 808, '11,1,1', '98,11,20,,0,0,1d10+10|98,16,25,,0,0,1d10+15|6|0|3|50|100|true|true|false|false|0|0|0|0|CcCc||18;19;3;1;41|70|false', '98,16,25,,0,0,1d10+15|98,21,30,,0,0,1d10+20|6|0|3|50|100|true|true|false|false|0|0|0|0|CcCc||18;19;3;1;41|70|false', '98,21,30,,0,0,1d10+20|98,26,35,,0,0,1d10+25|6|0|3|50|100|true|true|false|false|0|0|0|0|CcCc||18;19;3;1;41|70|false', '98,26,35,,0,0,1d10+25|98,31,40,,0,0,1d10+30|5|0|3|50|100|true|true|false|false|0|0|0|0|CcCc||18;19;3;1;41|70|false', '98,26,40,,0,0,1d15+25|98,41,55,,0,0,1d15+40|4|0|3|50|100|true|true|false|false|0|0|0|0|CcCc||18;19;3;1;41|70|false', '98,26,40,,0,0,1d15+25|98,41,55,,0,0,1d15+40|4|0|4|45|100|true|true|false|false|0|0|0|0|CcCc||18;19;3;1;41|170|false', '')",
                "(183, 'Ronce', 1001, '11,1,1', '97,4,6,,0,0,1d3+3|97,8,,,0,0,0d0+8|5|1|6|50|100|false|true|false|true|0|0|2|0|PaPa||18;19;3;1;41|1|false', '97,6,8,,0,0,1d3+5|97,10,,,0,0,0d0+10|5|1|6|50|100|false|true|false|true|0|0|2|0|PaPa||18;19;3;1;41|1|false', '97,6,10,,0,0,1d5+5|97,12,,,0,0,0d0+12|5|1|7|50|100|false|true|false|true|0|0|2|0|PaPa||18;19;3;1;41|1|false', '97,9,13,,0,0,1d5+8|97,16,,,0,0,0d0+16|5|1|7|50|100|false|true|false|true|0|0|2|0|PaPa||18;19;3;1;41|1|false', '97,10,17,,0,0,1d8+9|97,20,,,0,0,0d0+20|4|1|8|45|100|false|true|false|true|0|0|2|0|PaPa||18;19;3;1;41|1|false', '97,11,18,,0,0,1d8+10|97,22,,,0,0,0d0+22|3|1|8|45|100|false|true|false|true|0|0|2|0|PaPa||18;19;3;1;41|101|false', '')",
                "(164, 'Flèche Empoisonnée', 901, '31,2,1', '100,2,3,,2,0,1d2+1|100,5,,,2,0,0d0+5|4|2|5|30|100|false|true|false|true|0|0|1|0|PaPa||18;19;3;1;41|1|false', '100,3,4,,2,0,1d2+2|100,6,,,2,0,0d0+6|4|2|6|30|100|false|true|false|true|0|0|1|0|PaPa||18;19;3;1;41|1|false', '100,4,5,,2,0,1d2+3|100,7,,,2,0,0d0+7|4|2|7|30|100|false|true|false|true|0|0|1|0|PaPa||18;19;3;1;41|1|false', '100,5,6,,2,0,1d2+4|100,8,,,2,0,0d0+8|4|2|8|30|100|false|true|false|true|0|0|1|0|PaPa||18;19;3;1;41|1|false', '100,6,7,,2,0,1d2+5|100,9,,,2,0,0d0+9|4|2|9|30|100|false|true|false|true|0|0|1|0|PaPa||18;19;3;1;41|1|false', '100,9,10,,2,0,1d2+8|100,12,,,2,0,0d0+12|4|2|10|30|100|false|true|false|true|0|0|1|0|PaPa||18;19;3;1;41|101|false', '')",
                "(121, 'Mot Curatif', 702, '11,1,1', '108,5,11,,0,0,1d7+4|108,16,,,0,0,0d0+16|6|0|0|50|100|false|false|false|false|0|1|0|0|PaPa||50;18;19;3;1;41|1|false', '108,7,13,,0,0,1d7+6|108,20,,,0,0,0d0+20|6|0|0|50|100|false|false|false|false|0|1|0|0|PaPa||50;18;19;3;1;41|1|false', '108,9,15,,0,0,1d7+8|108,24,,,0,0,0d0+24|5|0|0|50|100|false|false|false|false|0|1|0|0|PaPa||50;18;19;3;1;41|1|false', '108,11,17,,0,0,1d7+10|108,28,,,0,0,0d0+28|5|0|0|45|100|false|false|false|false|0|1|0|0|PaPa||50;18;19;3;1;41|1|false', '108,13,19,,0,0,1d7+12|108,32,,,0,0,0d0+32|4|0|0|40|100|false|false|false|false|0|1|0|0|PaPa||50;18;19;3;1;41|1|false', '108,17,25,,0,0,1d9+16|108,40,,,0,0,0d0+40|3|0|0|40|100|false|false|false|false|0|1|0|0|PaPa||50;18;19;3;1;41|101|false', '')",
                "(131, 'Mot de Régénération', 702, '11,1,1', '108,1,4,,2,0,1d4+0|108,5,,,2,0,0d0+5|3|0|0|50|100|true|true|false|false|0|0|1|0|PaPa||50;18;19;3;1;41|26|false', '108,1,4,,3,0,1d4+0|108,5,,,3,0,0d0+5|3|0|0|50|100|true|true|false|false|0|0|1|0|PaPa||50;18;19;3;1;41|26|false', '108,1,4,,3,0,1d4+0|108,5,,,3,0,0d0+5|3|0|1|50|100|true|true|false|false|0|0|1|0|PaPa||50;18;19;3;1;41|26|false', '108,1,4,,4,0,1d4+0|108,5,,,4,0,0d0+5|3|0|1|50|100|true|true|false|false|0|0|1|0|PaPa||50;18;19;3;1;41|26|false', '108,1,4,,4,0,1d4+0|108,5,,,4,0,0d0+5|3|0|2|50|100|true|true|false|false|0|0|1|0|PaPa||50;18;19;3;1;41|26|false', '108,3,6,,5,0,1d4+2|108,6,,,5,0,0d0+6|3|0|3|50|100|true|true|false|false|0|0|1|0|PaPa||50;18;19;3;1;41|126|false', '')",
                "(1556, 'Fourberie', -1, '0,0,0', '81,6,,,2,0,0d0+6|81,7,,,2,0,0d0+7|4|0|1|30|100|false|true|false|false|0|0|0|8|PaPa||18;19;3;1;41|30|false', '81,8,,,2,0,0d0+8|81,10,,,2,0,0d0+10|4|0|1|30|100|false|true|false|false|0|0|0|8|PaPa||18;19;3;1;41|30|false', '81,10,,,3,0,0d0+10|81,12,,,3,0,0d0+12|4|0|1|30|100|false|true|false|false|0|0|0|8|PaPa||18;19;3;1;41|30|false', '81,12,,,3,0,0d0+12|81,15,,,3,0,0d0+15|4|0|1|30|100|false|true|false|false|0|0|0|8|PaPa||18;19;3;1;41|30|false', '81,15,,,3,0,0d0+15|81,18,,,3,0,0d0+18|4|0|1|30|100|false|true|false|false|0|0|0|8|PaPa||18;19;3;1;41|30|false', '81,20,,,3,0,0d0+20|108,25,,,3,0,0d0+25|4|0|1|30|100|false|true|false|false|0|0|0|8|PaPa||18;19;3;1;41|30|false', '')",
                "(444, 'Dérobade', 0, '10,1,1', '9,100,1,,1,0|9,100,1,,2,0|3|0|1|50|100|false|false|false|true|0|0|0|10|PaPa||18;19;3;1;41|3|false', '9,100,1,,1,0|9,100,1,,2,0|3|0|2|50|100|false|false|false|true|0|0|0|9|PaPa||18;19;3;1;41|3|false', '9,100,1,,1,0|9,100,1,,2,0|3|0|3|50|100|false|false|false|true|0|0|0|8|PaPa||18;19;3;1;41|3|false', '9,100,1,,1,0|9,100,1,,2,0|3|0|4|50|100|false|false|false|true|0|0|0|7|PaPa||18;19;3;1;41|3|false', '9,100,1,,1,0|9,100,1,,2,0|3|0|5|50|100|false|false|false|true|0|0|0|6|PaPa||18;19;3;1;41|3|false', '9,100,1,,1,0|9,100,1,,2,0|3|0|6|50|100|false|false|false|true|0|0|0|5|PaPa||18;19;3;1;41|103|false', '4')",
                "(128, 'Mot de Frayeur', 707, '11,1,1', '5,1,,,0,0||3|1|1|0|100|true|true|false|false|0|0|3|0|Pa||18;19;3;1;41|1|false', '5,1,,,0,0||3|1|2|0|100|true|true|false|false|0|0|3|0|Pa||18;19;3;1;41|1|false', '5,1,,,0,0||2|1|2|0|100|true|true|false|false|0|0|3|0|Pa||18;19;3;1;41|1|false', '5,1,,,0,0||2|1|3|0|100|true|true|false|false|0|0|3|0|Pa||18;19;3;1;41|1|false', '5,1,,,0,0||2|1|4|0|100|true|true|false|false|0|0|3|0|Pa||18;19;3;1;41|1|false', '5,1,,,0,0||1|1|5|0|100|true|true|false|false|0|0|3|0|Pa||18;19;3;1;41|101|false', '')",
                "(67, 'Peur', 403, '21,2,1', '783,,,,0,0||2|2|2|0|100|true|false|true|false|0|0|0|0|Pa||18;19;3;1;41|60|false', '783,,,,0,0||2|2|3|0|100|true|false|true|false|0|0|0|0|Pa||18;19;3;1;41|60|false', '783,,,,0,0||2|2|4|0|100|true|false|true|false|0|0|0|0|Pa||18;19;3;1;41|60|false', '783,,,,0,0||2|2|5|0|100|true|false|true|false|0|0|0|0|Pa||18;19;3;1;41|60|false', '783,,,,0,0||2|2|6|0|100|true|false|true|false|0|0|0|0|Pa||18;19;3;1;41|60|false', '783,,,,0,0||2|2|7|0|100|true|false|true|false|0|0|0|0|Pa||18;19;3;1;41|160|false', '')",
                "(434, 'Attirance', 1052, '51,1,1', '6,2,,,0,0||3|2|9|0|100|true|true|false|true|0|0|1|0|Pa||18;19;3;1;41|1|false', '6,3,,,0,0||3|2|10|0|100|true|true|false|true|0|0|1|0|Pa||18;19;3;1;41|1|false', '6,4,,,0,0||3|2|11|0|100|true|true|false|true|0|0|1|0|Pa||18;19;3;1;41|1|false', '6,5,,,0,0||3|2|12|0|100|true|true|false|true|0|0|1|0|Pa||18;19;3;1;41|1|false', '6,6,,,0,0||3|2|13|0|100|true|true|false|true|0|0|1|0|Pa||18;19;3;1;41|1|false', '6,7,,,0,0||3|2|14|0|100|true|true|false|true|0|0|1|0|Pa||18;19;3;1;41|101|false', '')",
                "(445, 'Coopération', 1055, '51,1,1', '8,,,,0,0||4|1|10|0|100|false|false|false|true|0|0|0|8|Pa||7;18;19;3;1;41|48|false', '8,,,,0,0||4|1|10|0|100|false|false|false|true|0|0|0|7|Pa||7;18;19;3;1;41|48|false', '8,,,,0,0||4|1|10|0|100|false|false|false|true|0|0|0|6|Pa||7;18;19;3;1;41|48|false', '8,,,,0,0||4|1|10|0|100|false|false|false|true|0|0|0|5|Pa||7;18;19;3;1;41|48|false', '8,,,,0,0||4|1|10|0|100|false|false|false|true|0|0|0|4|Pa||7;18;19;3;1;41|48|false', '8,,,,0,0||4|1|10|0|100|false|false|false|true|0|0|0|3|Pa||7;18;19;3;1;41|148|false', '1')",
                "(440, 'Sacrifice', 1055, '50,1,1', '765,,,,5,0||4|1|1|0|100|false|false|false|true|0|0|0|6|Pa||18;19;3;1;41|31|false', '765,,,,5,0||4|1|2|0|100|false|false|false|true|0|0|0|6|Pa||18;19;3;1;41|31|false', '765,,,,5,0||4|1|3|0|100|false|false|false|true|0|0|0|6|Pa||18;19;3;1;41|31|false', '765,,,,5,0||4|1|4|0|100|false|false|false|true|0|0|0|6|Pa||18;19;3;1;41|31|false', '765,,,,5,0||4|1|4|0|100|false|false|false|true|0|0|0|6|Cc||18;19;3;1;41|31|false', '765,,,,5,0||3|1|5|0|100|false|false|false|true|0|0|0|6|Cc||18;19;3;1;41|131|false', '4')",
                "(577, 'Bambou Musical', -1, '0,0,0', '8,,,,0,0;91,71,100,,0,0,1d30+70;127,1,4,,2,50,1d4+0;84,1,4,,0,50,1d4+0|122,15,,,5,33,0d0+15;8,,,,0,0;91,110,,,0,0,0d0+110;127,1,4,,-1,33,1d4+0;84,8,,,0,33,0d0+8|5|1|5|40|60|false|true|false|true|0|0|0|0|PaPaPaPaPaPaPaPaPa||7;18;19;3;1;41|0|false', '8,,,,0,0;91,71,100,,0,0,1d30+70;127,1,4,,2,50,1d4+0;84,1,4,,0,50,1d4+0|122,15,,,5,33,0d0+15;8,,,,0,0;91,110,,,0,0,0d0+110;127,1,4,,-1,33,1d4+0;84,8,,,0,33,0d0+8|5|1|5|40|60|false|true|false|true|0|0|0|0|PaPaPaPaPaPaPaPaPa||7;18;19;3;1;41|0|false', '8,,,,0,0;91,71,100,,0,0,1d30+70;127,1,4,,2,50,1d4+0;84,1,4,,0,50,1d4+0|122,15,,,5,33,0d0+15;8,,,,0,0;91,110,,,0,0,0d0+110;127,1,4,,-1,33,1d4+0;84,8,,,0,33,0d0+8|5|1|5|40|60|false|true|false|true|0|0|0|0|PaPaPaPaPaPaPaPaPa||7;18;19;3;1;41|0|false', '8,,,,0,0;91,71,100,,0,0,1d30+70;127,1,4,,2,50,1d4+0;84,1,4,,0,50,1d4+0|122,15,,,5,33,0d0+15;8,,,,0,0;91,110,,,0,0,0d0+110;127,1,4,,-1,33,1d4+0;84,8,,,0,33,0d0+8|5|1|5|40|60|false|true|false|true|0|0|0|0|PaPaPaPaPaPaPaPaPa||7;18;19;3;1;41|0|false', '8,,,,0,0;91,71,100,,0,0,1d30+70;127,1,4,,2,50,1d4+0;84,1,4,,0,50,1d4+0|122,15,,,5,33,0d0+15;8,,,,0,0;91,110,,,0,0,0d0+110;127,1,4,,-1,33,1d4+0;84,8,,,0,33,0d0+8|5|1|5|40|60|false|true|false|true|0|0|0|0|PaPaPaPaPaPaPaPaPa||7;18;19;3;1;41|0|false', '', '')",
                "(82, 'Contre', 504, '10,1,1', '107,3,,,3,0,0d0+3;666,,,,0,90;120,1,,,0,10,0d0+1|107,4,,,3,0,0d0+4;666,,,,0,90;120,1,,,0,10,0d0+1|2|0|1|50|100|false|true|false|false|0|1|0|6|PaPaPaPaPaPa||18;19;3;1;41|1|false', '107,4,,,3,0,0d0+4;666,,,,0,90;120,1,,,0,10,0d0+1|107,5,,,3,0,0d0+5;666,,,,0,90;120,1,,,0,10,0d0+1|2|0|1|50|100|false|true|false|false|0|1|0|6|PaPaPaPaPaPa||18;19;3;1;41|1|false', '107,5,,,3,0,0d0+5;666,,,,0,90;120,1,,,0,10,0d0+1|107,6,,,3,0,0d0+6;666,,,,0,90;120,1,,,0,10,0d0+1|2|0|1|50|100|false|true|false|false|0|1|0|6|PaPaPaPaPaPa||18;19;3;1;41|1|false', '107,6,,,3,0,0d0+6;666,,,,0,90;120,1,,,0,10,0d0+1|107,7,,,3,0,0d0+7;666,,,,0,90;120,1,,,0,10,0d0+1|2|0|1|50|100|false|true|false|false|0|1|0|6|PaPaPaPaPaPa||18;19;3;1;41|1|false', '107,7,,,3,0,0d0+7;666,,,,0,90;120,1,,,0,10,0d0+1|107,8,,,3,0,0d0+8;666,,,,0,90;120,1,,,0,10,0d0+1|2|0|1|50|100|false|true|false|false|0|1|0|6|PaPaPaPaPaPa||18;19;3;1;41|1|false', '107,8,,,3,0,0d0+8;666,,,,0,90;120,1,,,0,10,0d0+1|107,9,,,3,0,0d0+9;666,,,,0,90;120,1,,,0,10,0d0+1|2|0|1|50|100|false|true|false|false|0|1|0|6|PaPaPaPaPaPa||18;19;3;1;41|101|false', '')",
                "(81, 'Ralentissement', 503, '11,1,1', '101,1,,,1,0,0d0+1;666,,,,0,90;120,1,,,0,10,0d0+1||1|3|4|0|100|false|true|false|true|0|4|1|0|PaPaPa||18;19;3;1;41|1|false', '101,1,2,,1,0,1d2+0;666,,,,0,90;120,1,,,0,10,0d0+1||1|3|5|0|100|false|true|false|true|0|4|1|0|PaPaPa||18;19;3;1;41|1|false', '101,1,2,,1,0,1d2+0;666,,,,0,90;120,1,,,0,10,0d0+1||1|3|6|0|100|false|true|false|true|0|4|1|0|PaPaPa||18;19;3;1;41|1|false', '101,1,2,,1,0,1d2+0;666,,,,0,90;120,1,,,0,10,0d0+1||1|3|7|0|100|false|true|false|true|0|4|1|0|PaPaPa||18;19;3;1;41|1|false', '101,2,,,1,0,0d0+2;666,,,,0,90;120,1,,,0,10,0d0+1||1|3|8|0|100|false|true|false|true|0|4|1|0|PaPaPa||18;19;3;1;41|1|false', '101,3,,,1,0,0d0+3;666,,,,0,90;120,1,,,0,10,0d0+1||1|3|9|0|100|false|true|false|true|0|4|1|0|PaPaPa||18;19;3;1;41|101|false', '')",
                "(50, 'Maladresse', 303, '11,1,1', '127,1,,,1,0,0d0+1|127,2,,,1,0,0d0+2|1|1|4|50|100|false|true|false|true|0|0|1|0|PaPa||18;19;3;1;41|42|false', '127,1,2,,1,0,1d2+0|127,2,3,,1,0,1d2+1|1|1|5|50|100|false|true|false|true|0|0|1|0|PaPa||18;19;3;1;41|42|false', '127,1,2,,1,0,1d2+0|127,2,3,,1,0,1d2+1|1|1|8|50|100|false|true|false|true|0|0|1|0|PaPa||18;19;3;1;41|42|false', '127,1,2,,1,0,1d2+0|127,2,3,,1,0,1d2+1|1|1|11|50|100|false|true|false|true|0|0|1|0|PaPa||18;19;3;1;41|42|false', '127,2,,,1,0,0d0+2|127,3,,,1,0,0d0+3|1|1|12|50|100|false|true|false|true|0|0|1|0|PaPa||18;19;3;1;41|42|false', '127,3,,,1,0,0d0+3|127,4,,,1,0,0d0+4|1|1|13|50|100|false|true|false|true|0|0|1|0|PaPa||18;19;3;1;41|142|false', '')",
                "(98, 'Vol du Temps', 507, '11,1,1', '84,2,,,1,0,0d0+2;666,,,,0,90;120,1,,,0,10,0d0+1||4|3|3|0|20|false|true|false|false|0|0|2|0|PaPaPa||18;19;3;1;41|31|false', '84,2,,,1,0,0d0+2;666,,,,0,90;120,1,,,0,10,0d0+1||4|3|3|0|30|false|true|false|false|0|0|2|0|PaPaPa||18;19;3;1;41|31|false', '84,2,,,1,0,0d0+2;666,,,,0,90;120,1,,,0,10,0d0+1||4|3|4|0|40|false|true|false|false|0|0|2|0|PaPaPa||18;19;3;1;41|31|false', '84,2,,,1,0,0d0+2;666,,,,0,90;120,1,,,0,10,0d0+1||4|3|4|0|50|false|true|false|false|0|0|2|0|PaPaPa||18;19;3;1;41|31|false', '84,2,,,1,0,0d0+2;666,,,,0,90;120,1,,,0,10,0d0+1||4|3|5|0|60|false|true|false|false|0|0|2|0|PaPaPa||18;19;3;1;41|31|false', '84,2,,,1,0,0d0+2;666,,,,0,90;120,1,,,0,10,0d0+1||4|3|6|0|100|false|true|false|false|0|0|2|0|PaPaPa||18;19;3;1;41|131|false', '')",
                "(170, 'Flèche d Immobilisation', 901, '31,2,1', '96,3,4,,0,0,1d2+2;77,1,,,0,0,0d0+1|96,4,5,,0,0,1d2+3;77,1,,,0,0,0d0+1|2|1|5|40|100|false|true|false|true|0|0|2|0|PaPaPaPa||18;19;3;1;41|26|false', '96,4,5,,0,0,1d2+3;77,1,,,0,0,0d0+1|96,5,6,,0,0,1d2+4;77,1,,,0,0,0d0+1|2|1|6|40|100|false|true|false|true|0|0|2|0|PaPaPaPa||18;19;3;1;41|26|false', '96,5,6,,0,0,1d2+4;77,1,,,0,0,0d0+1|96,6,7,,0,0,1d2+5;77,1,,,0,0,0d0+1|2|1|7|40|100|false|true|false|true|0|0|2|0|PaPaPaPa||18;19;3;1;41|26|false', '96,6,7,,0,0,1d2+5;77,1,,,0,0,0d0+1|96,9,10,,0,0,1d2+8;77,1,,,0,0,0d0+1|2|1|8|40|100|false|true|false|true|0|0|2|0|PaPaPaPa||18;19;3;1;41|26|false', '96,7,8,,0,0,1d2+6;77,1,,,0,0,0d0+1|96,10,11,,0,0,1d2+9;77,1,,,0,0,0d0+1|2|1|9|40|100|false|true|false|true|0|0|2|0|PaPaPaPa||18;19;3;1;41|26|false', '96,9,10,,0,0,1d2+8;77,1,,,0,0,0d0+1|96,12,13,,0,0,1d2+11;77,1,,,0,0,0d0+1|2|1|10|40|100|false|true|false|true|0|0|2|0|PaPaPaPa||18;19;3;1;41|126|false', '')",
                "(135, 'Mot de Sacrifice', 709, '51,1,1', '109,11,20,,0,0,1d10+10;108,11,20,,0,0,1d10+10|108,11,20,,0,0,1d10+10|6|1|3|50|100|false|true|false|false|0|0|2|0|PaPaPa||50;18;19;3;1;41|48|false', '109,11,20,,0,0,1d10+10;108,11,20,,0,0,1d10+10|108,11,20,,0,0,1d10+10|5|1|4|50|100|false|true|false|false|0|0|2|0|PaPaPa||50;18;19;3;1;41|48|false', '109,16,25,,0,0,1d10+15;108,16,25,,0,0,1d10+15|108,16,25,,0,0,1d10+15|5|1|4|50|100|false|true|false|false|0|0|2|0|PaPaPa||50;18;19;3;1;41|48|false', '109,21,30,,0,0,1d10+20;108,21,30,,0,0,1d10+20|108,21,30,,0,0,1d10+20|5|1|4|50|100|false|true|false|false|0|0|2|0|PaPaPa||50;18;19;3;1;41|48|false', '109,31,40,,0,0,1d10+30;108,31,40,,0,0,1d10+30|108,31,40,,0,0,1d10+30|5|1|5|50|100|false|true|false|false|0|0|2|0|PaPaPa||50;18;19;3;1;41|48|false', '109,36,45,,0,0,1d10+35;108,36,45,,0,0,1d10+35|108,36,45,,0,0,1d10+35|4|1|6|50|100|false|true|false|false|0|0|2|0|PaPaPa||50;18;19;3;1;41|148|false', '')",
                "(536, 'Banzai', -1, '0,0,0', '111,2,,,2,0,0d0+2;144,5,,,0,0,0d0+5|111,3,,,2,0,0d0+3;144,5,,,0,0,0d0+5|1|0|0|20|35|true|true|false|true|0|0|0|6|PaPaPaPa||18;19;3;1;41|0|true', '111,2,,,2,0,0d0+2;144,5,,,0,0,0d0+5|111,3,,,2,0,0d0+3;144,5,,,0,0,0d0+5|1|0|0|20|35|true|true|false|true|0|0|0|6|PaPaPaPa||18;19;3;1;41|0|true', '111,2,,,2,0,0d0+2;144,5,,,0,0,0d0+5|111,3,,,2,0,0d0+3;144,5,,,0,0,0d0+5|1|0|0|20|35|true|true|false|true|0|0|0|6|PaPaPaPa||18;19;3;1;41|0|true', '111,2,,,2,0,0d0+2;144,5,,,0,0,0d0+5|111,3,,,2,0,0d0+3;144,5,,,0,0,0d0+5|1|0|0|20|35|true|true|false|true|0|0|0|6|PaPaPaPa||18;19;3;1;41|0|true', '111,2,,,2,0,0d0+2;144,5,,,0,0,0d0+5|111,3,,,2,0,0d0+3;144,5,,,0,0,0d0+5|1|0|0|20|35|true|true|false|true|0|0|0|6|PaPaPaPa||18;19;3;1;41|0|true', '', '')",
                "(450, 'Folie sanguinaire', 0, '0,1,1', '82,300,,,0,0,0d0+300|82,300,,,0,0,0d0+300;143,100,,,0,0,0d0+100|2|1|3|50|100|false|false|false|true|0|1|1|0|PaPaPa||18;19;3;1;41|100|false', '82,300,,,0,0,0d0+300|82,300,,,0,0,0d0+300;143,100,,,0,0,0d0+100|2|1|4|50|100|false|false|false|true|0|2|2|0|PaPaPa||18;19;3;1;41|100|false', '82,300,,,0,0,0d0+300|82,300,,,0,0,0d0+300;143,100,,,0,0,0d0+100|2|1|4|50|100|false|false|false|true|0|3|3|0|PaPaPa||18;19;3;1;41|100|false', '82,300,,,0,0,0d0+300|82,300,,,0,0,0d0+300;143,100,,,0,0,0d0+100|2|1|5|50|100|false|false|false|true|0|4|4|0|PaPaPa||18;19;3;1;41|100|false', '82,300,,,0,0,0d0+300|82,300,,,0,0,0d0+300;143,100,,,0,0,0d0+100|2|1|6|50|100|false|false|false|true|0|5|5|0|PaPaPa||18;19;3;1;41|100|false', '82,200,,,0,0,0d0+200|82,200,,,0,0,0d0+200;143,100,,,0,0,0d0+100|1|1|7|50|100|false|false|false|true|0|6|6|0|PaPaPa||18;19;3;1;41|200|false', '4;2')",
                "(951, 'Rocaille', -1, '0,0,0', '86,15,,,0,0,0d0+15|86,30,,,0,0,0d0+30|4|1|1|50|50|false|true|false|false|0|0|0|0|PaPa||18;19;3;1;41|0|true', '86,15,,,0,0,0d0+15|86,30,,,0,0,0d0+30|4|1|1|50|50|false|true|false|false|0|0|0|0|PaPa||18;19;3;1;41|0|true', '86,15,,,0,0,0d0+15|86,30,,,0,0,0d0+30|4|1|1|50|50|false|true|false|false|0|0|0|0|PaPa||18;19;3;1;41|0|true', '86,15,,,0,0,0d0+15|86,30,,,0,0,0d0+30|4|1|1|50|50|false|true|false|false|0|0|0|0|PaPa||18;19;3;1;41|0|true', '86,15,,,0,0,0d0+15|86,30,,,0,0,0d0+30|4|1|1|50|50|false|true|false|false|0|0|0|0|PaPa||18;19;3;1;41|0|true', '', '')",
                "(1708, 'Correction Bwork', 0, '0,1,1', '279,30,,,0,0,0d0+30|279,35,,,0,0,0d0+35|5|1|1|50|100|false|true|false|false|0|0|0|2|PaPa||18;19;3;1;41|0|false', '279,30,,,0,0,0d0+30|279,35,,,0,0,0d0+35|5|1|1|50|100|false|true|false|false|0|0|0|2|PaPa||18;19;3;1;41|0|false', '279,30,,,0,0,0d0+30|279,35,,,0,0,0d0+35|5|1|1|50|100|false|true|false|false|0|0|0|2|PaPa||18;19;3;1;41|0|false', '279,30,,,0,0,0d0+30|279,35,,,0,0,0d0+35|5|1|1|50|100|false|true|false|false|0|0|0|2|PaPa||18;19;3;1;41|0|false', '279,30,,,0,0,0d0+30|279,35,,,0,0,0d0+35|5|1|1|50|100|false|true|false|false|0|0|0|2|PaPa||18;19;3;1;41|0|false', '', '')",
                "(446, 'Punition', 0, '0,6,1', '672,30,,,0,0,0d0+30|672,35,,,0,0,0d0+35|5|1|1|50|100|false|true|false|false|0|0|0|6|PaPa||18;19;3;1;41|60|false', '672,30,,,0,0,0d0+30|672,35,,,0,0,0d0+35|5|1|1|50|100|false|true|false|false|0|0|0|5|PaPa||18;19;3;1;41|60|false', '672,30,,,0,0,0d0+30|672,35,,,0,0,0d0+35|5|1|1|50|100|false|true|false|false|0|0|0|4|PaPa||18;19;3;1;41|60|false', '672,30,,,0,0,0d0+30|672,35,,,0,0,0d0+35|5|1|1|50|100|false|true|false|false|0|0|0|3|PaPa||18;19;3;1;41|60|false', '672,30,,,0,0,0d0+30|672,35,,,0,0,0d0+35|5|1|1|50|100|false|true|false|false|0|0|0|2|PaPa||18;19;3;1;41|60|false', '672,30,,,0,0,0d0+30|672,35,,,0,0,0d0+35|4|1|1|50|100|false|true|false|false|0|0|0|2|PaPa||18;19;3;1;41|160|false', '')",
                "(427, 'Mot Lotof', 0, '11,1,1', '787,1679,1,1,0,0;149,,,7032,1,0||6|0|2|0|100|false|false|false|true|4|1|1|10|PaPa||18;19;3;1;41|1|false', '787,1679,2,1,0,0;149,,,7032,1,0||6|0|3|0|100|false|false|false|true|4|1|1|9|PaPa||18;19;3;1;41|1|false', '787,1679,3,1,0,0;149,,,7032,1,0||6|0|4|0|100|false|false|false|true|4|1|1|8|PaPa||18;19;3;1;41|1|false', '787,1679,4,1,0,0;149,,,7032,1,0||6|0|5|0|100|false|false|false|true|4|1|1|7|PaPa||18;19;3;1;41|1|false', '787,1679,5,1,0,0;149,,,7032,1,0||6|0|6|0|100|false|false|false|true|4|1|1|6|PaPa||18;19;3;1;41|1|false', '787,1679,6,1,0,0;149,,,7032,1,0||6|0|7|0|100|false|false|false|true|4|1|1|5|PaPa||18;19;3;1;41|100|false', '4;4;5;5')",
                "(1679, 'Combustion spontanée.', 2009, '10,0,0', '89,33,,,0,0,0d0+33;671,33,,,0,0,0d0+33;149,,,-7032,0,0||1|0|0|0|0|false|true|false|true|0|0|0|0|ObPaPa||18;19;3;1;41|1|false', '89,33,,,0,0,0d0+33;671,33,,,0,0,0d0+33;149,,,-7032,0,0||1|0|0|0|0|false|true|false|true|0|0|0|0|ObPaPa||18;19;3;1;41|1|false', '89,33,,,0,0,0d0+33;671,33,,,0,0,0d0+33;149,,,-7032,0,0||1|0|0|0|0|false|true|false|true|0|0|0|0|ObPaPa||18;19;3;1;41|1|false', '89,33,,,0,0,0d0+33;671,33,,,0,0,0d0+33;149,,,-7032,0,0||1|0|0|0|0|false|true|false|true|0|0|0|0|ObPaPa||18;19;3;1;41|1|false', '89,33,,,0,0,0d0+33;671,33,,,0,0,0d0+33;149,,,-7032,0,0||1|0|0|0|0|false|true|false|true|0|0|0|0|ObPaPa||18;19;3;1;41|1|false', '89,33,,,0,0,0d0+33;671,33,,,0,0,0d0+33;149,,,-7032,0,0||1|0|0|0|0|false|true|false|true|0|0|0|0|ObPaPa||18;19;3;1;41|1|false', '')",
                "(435, 'Transfert de Vie', 1050, '51,1,1', '90,10,,,0,0,0d0+10|90,10,,,0,0,0d0+10;108,10,,,0,0,0d0+10|2|0|0|50|100|false|false|false|false|0|1|0|0|CbCbPa||18;19;3;1;41|90|false', '90,10,,,0,0,0d0+10|90,10,,,0,0,0d0+10;108,10,,,0,0,0d0+10|2|0|0|50|100|false|false|false|false|0|2|0|0|CbCbPa||18;19;3;1;41|90|false', '90,10,,,0,0,0d0+10|90,10,,,0,0,0d0+10;108,10,,,0,0,0d0+10|2|0|0|50|100|false|false|false|false|0|3|0|0|CbCbPa||18;19;3;1;41|90|false', '90,10,,,0,0,0d0+10|90,10,,,0,0,0d0+10;108,10,,,0,0,0d0+10|2|0|0|50|100|false|false|false|false|0|4|0|0|CcCcPa||18;19;3;1;41|90|false', '90,10,,,0,0,0d0+10|90,10,,,0,0,0d0+10;108,10,,,0,0,0d0+10|2|0|0|50|100|false|false|false|false|0|5|0|0|CdCdPa||18;19;3;1;41|90|false', '90,10,,,0,0,0d0+10|90,10,,,0,0,0d0+10;108,10,,,0,0,0d0+10|2|0|0|50|100|false|false|false|false|0|6|0|0|CeCePa||18;19;3;1;41|190|false', '6')",
                "(416, 'Poisse', 0, '0,1,1', '781,,,,2,0||3|1|1|0|100|false|true|false|false|4|0|0|10|Pa||18;19;3;1;41|0|false', '781,,,,2,0||3|1|1|0|100|false|true|false|false|4|0|0|9|Pa||18;19;3;1;41|0|false', '781,,,,2,0||3|1|1|0|100|false|true|false|false|4|0|0|8|Pa||18;19;3;1;41|0|false', '781,,,,2,0||3|1|1|0|100|false|true|false|false|4|0|0|7|Pa||18;19;3;1;41|0|false', '781,,,,2,0||3|1|1|0|100|false|true|false|false|4|0|0|6|Pa||18;19;3;1;41|0|false', '781,,,,2,0||3|1|1|0|100|false|true|false|false|4|0|0|5|Pa||18;19;3;1;41|100|false', '')",
                "(410, 'Brokle', 810, '51,1,1', '782,,,,2,0|782,,,,3,0|3|0|1|40|100|false|false|false|false|4|0|0|10|PaPa||18;19;3;1;41|0|false', '782,,,,2,0|782,,,,3,0|3|0|1|40|100|false|false|false|false|4|0|0|9|PaPa||18;19;3;1;41|0|false', '782,,,,2,0|782,,,,3,0|3|0|1|40|100|false|false|false|false|4|0|0|8|PaPa||18;19;3;1;41|0|false', '782,,,,2,0|782,,,,3,0|3|0|1|40|100|false|false|false|false|4|0|0|7|PaPa||18;19;3;1;41|0|false', '782,,,,2,0|782,,,,3,0|3|0|1|40|100|false|false|false|false|4|0|0|6|PaPa||18;19;3;1;41|0|false', '782,,,,2,0|782,,,,3,0|3|0|1|40|100|false|false|false|false|4|0|0|5|PaPa||18;19;3;1;41|100|false', '')",
                "(2115, 'Tir Puissant du Dopeul', -1, '0,0,0', '114,2,,,2,0|114,3,,,2,0|6|0|1|40|5|false|false|false|false|0|0|0|10|PaPa||18;19;3;1;41|36|false', '114,2,,,2,0|114,3,,,2,0|5|0|1|40|10|false|false|false|false|0|0|0|10|PaPa||18;19;3;1;41|36|false', '114,2,,,2,0|114,3,,,2,0|5|0|1|40|15|false|false|false|false|0|0|0|10|PaPa||18;19;3;1;41|36|false', '114,2,,,2,0|114,3,,,2,0|5|0|1|40|20|false|false|false|false|0|0|0|10|PaPa||18;19;3;1;41|36|false', '114,2,,,3,0|114,3,,,3,0|4|0|1|40|30|false|false|false|false|0|0|0|10|PaPa||18;19;3;1;41|36|false', '114,2,,,4,0|114,3,,,4,0|4|0|1|40|100|false|false|false|false|0|0|0|10|PaPa||18;19;3;1;41|136|false', '2')",
            }, ",") + ";"
        );

        return this;
    }

    public GameDataSet pushHighLevelSpells() throws SQLException, ContainerException {
        if (repository(SpellTemplate.class).has(new SpellTemplate(1908, null, 0, null, null, null))) {
            return this;
        }

        connection.query(
            "INSERT INTO `SPELL` (`SPELL_ID`, `SPELL_NAME`, `SPELL_SPRITE`, `SPELL_SPRITE_ARG`, `SPELL_LVL_1`, `SPELL_LVL_2`, `SPELL_LVL_3`, `SPELL_LVL_4`, `SPELL_LVL_5`, `SPELL_LVL_6`, `SPELL_TARGET`) VALUES " +
            "(1908, 'Invocation de Dopeul Iop', -1, '0,0,0', '181,962,1,,0,0||8|1|1|0|100|false|true|true|false|0|0|0|10|Pa||18;19;3;1;41|200|false', '181,962,2,,0,0||8|1|1|0|100|false|true|true|false|0|0|0|10|Pa||18;19;3;1;41|200|false', '181,962,3,,0,0||8|1|1|0|100|false|true|true|false|0|0|0|10|Pa||18;19;3;1;41|200|false', '181,962,4,,0,0||8|1|1|0|100|false|true|true|false|0|0|0|10|Pa||18;19;3;1;41|200|false', '181,962,5,,0,0||8|1|1|0|100|false|true|true|false|0|0|0|10|Pa||18;19;3;1;41|200|false', '181,962,6,,0,0||8|1|1|0|100|false|true|true|false|0|0|0|10|Pa||18;19;3;1;41|200|false', '')"
        );

        return this;
    }

    public GameDataSet pushMonsterSpells() throws SQLException, ContainerException {
        if (repository(SpellTemplate.class).has(new SpellTemplate(1709, null, 0, null, null, null))) {
            return this;
        }

        connection.query(
            "INSERT OR IGNORE INTO `SPELL` (`SPELL_ID`, `SPELL_NAME`, `SPELL_SPRITE`, `SPELL_SPRITE_ARG`, `SPELL_LVL_1`, `SPELL_LVL_2`, `SPELL_LVL_3`, `SPELL_LVL_4`, `SPELL_LVL_5`, `SPELL_LVL_6`, `SPELL_TARGET`) VALUES\n" +
                "(1709, 'Contusion', 0, '0,1,1', '100,5,7,,0,0,1d3+4;950,,,7,1,0|100,10,,,0,0,0d0+10;950,,,7,1,0|4|1|1|50|100|false|true|false|false|0|0|0|3|PaPaPaPa||18;19;3;1;41|0|false', '100,7,9,,0,0,1d3+6;950,,,7,1,0|100,12,,,0,0,0d0+12;950,,,7,1,0|4|1|1|50|100|false|true|false|false|0|0|0|3|PaPaPaPa||18;19;3;1;41|0|false', '100,9,11,,0,0,1d3+8;950,,,7,1,0|100,14,,,0,0,0d0+14;950,,,7,1,0|4|1|1|50|100|false|true|false|false|0|0|0|3|PaPaPaPa||18;19;3;1;41|0|false', '100,11,13,,0,0,1d3+10;950,,,7,1,0|100,16,,,0,0,0d0+16;950,,,7,1,0|4|1|1|50|100|false|true|false|false|0|0|0|3|PaPaPaPa||18;19;3;1;41|0|false', '100,12,14,,0,0,1d3+11;950,,,7,1,0|100,17,,,0,0,0d0+17;950,,,7,1,0|4|1|1|50|100|false|true|false|false|0|0|0|3|PaPaPaPa||18;19;3;1;41|0|false', '100,13,15,,0,0,1d3+12;950,,,7,1,0|100,18,,,0,0,0d0+18;950,,,7,1,0|4|1|1|50|100|false|true|false|false|0|0|0|3|PaPaPaPa||18;19;3;1;41|0|false', '')," +
                "(213, 'Frappe', 0, '0,0,1', '97,2,4,,0,0,1d3+1|97,6,,,0,0,0d0+6|3|1|1|50|50|false|true|false|false|0|0|0|0|PaPa||18;19;3;1;41|0|false', '97,3,5,,0,0,1d3+2|97,8,,,0,0,0d0+8|3|1|1|50|50|false|true|false|false|0|0|0|0|PaPa||18;19;3;1;41|0|false', '97,4,6,,0,0,1d3+3|97,10,,,0,0,0d0+10|3|1|1|50|50|false|true|false|false|0|0|0|0|PaPa||18;19;3;1;41|0|false', '97,5,7,,0,0,1d3+4|97,12,,,0,0,0d0+12|3|1|1|50|50|false|true|false|false|0|0|0|0|PaPa||18;19;3;1;41|0|false', '97,6,8,,0,0,1d3+5|97,14,,,0,0,0d0+14|3|1|1|50|50|false|true|false|false|0|0|0|0|PaPa||18;19;3;1;41|0|false', '97,7,9,,0,0,1d3+6|97,16,,,0,0,0d0+16|3|1|1|50|50|false|true|false|false|0|0|0|0|PaPa||18;19;3;1;41|0|false', '')," +
                "(212, 'Larvement', 2011, '30,1,1', '101,1,,,1,0,0d0+1;96,1,2,,0,0,1d2+0|101,2,,,1,0,0d0+2;96,1,3,,0,0,1d3+0|3|1|5|30|50|false|true|false|false|0|0|0|0|PaPaPaPa||18;19;3;1;41|0|false', '101,1,,,1,0,0d0+1;96,1,2,,0,0,1d2+0|101,2,,,1,0,0d0+2;96,1,3,,0,0,1d3+0|3|1|6|30|50|false|true|false|false|0|0|0|0|PaPaPaPa||18;19;3;1;41|0|false', '101,1,,,1,0,0d0+1;96,1,2,,0,0,1d2+0|101,2,,,1,0,0d0+2;96,1,3,,0,0,1d3+0|3|1|7|30|50|false|true|false|false|0|0|0|0|PaPaPaPa||18;19;3;1;41|0|false', '101,1,,,1,0,0d0+1;96,1,2,,0,0,1d2+0|101,2,,,1,0,0d0+2;96,1,3,,0,0,1d3+0|3|1|8|30|50|false|true|false|false|0|0|0|0|PaPaPaPa||18;19;3;1;41|0|false', '101,1,,,1,0,0d0+1;96,1,2,,0,0,1d2+0|101,2,,,1,0,0d0+2;96,1,3,,0,0,1d3+0|3|1|9|20|50|false|true|false|false|0|0|0|0|PaPaPaPa||18;19;3;1;41|0|false', '', '')," +
                "(202, 'Morsure du Bouftou', 0, '0,1,1', '100,4,9,,0,0,1d6+3|100,13,,,0,0,0d0+13|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '100,6,11,,0,0,1d6+5|100,17,,,0,0,0d0+17|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '100,8,13,,0,0,1d6+7|100,21,,,0,0,0d0+21|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '100,10,15,,0,0,1d6+9|100,25,,,0,0,0d0+25|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '100,12,17,,0,0,1d6+11|100,29,,,0,0,0d0+29|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '', '')," +
                "(2000, 'Morsure du Bouftou', 0, '0,1,1', '100,4,7,,0,0,1d4+3|100,10,,,0,0,0d0+10|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '100,6,9,,0,0,1d4+5|100,12,,,0,0,0d0+12|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '100,8,11,,0,0,1d4+7|100,14,,,0,0,0d0+14|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '100,10,13,,0,0,1d4+9|100,16,,,0,0,0d0+16|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '100,11,14,,0,0,1d4+10|100,17,,,0,0,0d0+17|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '100,12,15,,0,0,1d4+11|100,18,,,0,0,0d0+18|4|1|1|50|100|false|true|false|false|0|3|0|0|PaPa||18;19;3;1;41|0|false', '')," +
                "(215, 'Retour de flamme', 110, '10,1,1', '106,,3,70,-1,0|106,,3,75,-1,0|4|0|0|50|50|false|false|false|false|0|0|0|63|PaPa||18;19;3;1;41|0|false', '106,,3,70,-1,0|106,,3,75,-1,0|4|0|0|50|50|false|false|false|false|0|0|0|63|PaPa||18;19;3;1;41|0|false', '106,,3,70,-1,0|106,,3,75,-1,0|4|0|0|50|50|false|false|false|false|0|0|0|63|PaPa||18;19;3;1;41|0|false', '106,,3,75,-1,0|106,,3,80,-1,0|4|0|0|50|50|false|false|false|false|0|0|0|63|PaPa||18;19;3;1;41|0|false', '106,,3,80,-1,0|106,,3,85,-1,0|4|0|0|50|50|false|false|false|false|0|0|0|63|PaPa||18;19;3;1;41|0|false', '', '')"
        );

        return this;
    }

    public GameDataSet pushExperience() throws SQLException, ContainerException {
        use(PlayerExperience.class);

        if (repository(PlayerExperience.class).has(new PlayerExperience(1, 0))) {
            return this;
        }

        connection.query(
            "INSERT INTO `PLAYER_XP` (`PLAYER_LEVEL`, `EXPERIENCE`) VALUES\n" +
                "(1, 0), (2, 110), (3, 650), (4, 1500), (5, 2800), (6, 4800), (7, 7300), (8, 10500), (9, 14500), (10, 19200), (11, 25200), (12, 32600), (13, 41000), (14, 50500), (15, 61000), (16, 75000), (17, 91000), (18, 115000), (19, 142000), (20, 171000), (21, 202000), (22, 235000), (23, 270000), (24, 310000), (25, 353000), (26, 398500), (27, 448000), (28, 503000), (29, 561000), (30, 621600), (31, 687000), (32, 755000), (33, 829000), (34, 910000), (35, 1000000), (36, 1100000), (37, 1240000), (38, 1400000), (39, 1580000), (40, 1780000), (41, 2000000), (42, 2250000), (43, 2530000), (44, 2850000), (45, 3200000), (46, 3570000), (47, 3960000), (48, 4400000), (49, 4860000), (50, 5350000), (51, 5860000)," +
                "(52, 6390000), (53, 6950000), (54, 7530000), (55, 8130000), (56, 8765100), (57, 9420000), (58, 10150000), (59, 10894000), (60, 11650000), (61, 12450000), (62, 13280000), (63, 14130000), (64, 15170000), (65, 16251000), (66, 17377000), (67, 18553000), (68, 19778000), (69, 21055000), (70, 22385000), (71, 23529000), (72, 25209000), (73, 26707000), (74, 28264000), (75, 29882000), (76, 31563000), (77, 33307000), (78, 35118000), (79, 36997000), (80, 38945000), (81, 40965000), (82, 43059000), (83, 45229000), (84, 47476000), (85, 49803000), (86, 52211000), (87, 54704000), (88, 57284000), (89, 59952000), (90, 62712000), (91, 65565000), (92, 68514000), (93, 71561000), (94, 74710000), (95, 77963000), (96, 81323000), (97, 84792000), (98, 88374000), (99, 92071000), (100, 95886000), (101, 99823000)," +
                "(102, 103885000), (103, 108075000), (104, 112396000), (105, 116853000), (106, 121447000), (107, 126184000), (108, 131066000), (109, 136098000), (110, 141283000), (111, 146626000), (112, 152130000), (113, 157800000), (114, 163640000), (115, 169655000), (116, 175848000), (117, 182225000), (118, 188791000), (119, 195550000), (120, 202507000), (121, 209667000), (122, 217037000), (123, 224620000), (124, 232424000), (125, 240452000), (126, 248712000), (127, 257209000), (128, 265949000), (129, 274939000), (130, 284186000), (131, 293694000), (132, 303473000), (133, 313527000), (134, 323866000), (135, 334495000), (136, 345423000), (137, 356657000), (138, 368206000), (139, 380076000), (140, 392278000), (141, 404818000), (142, 417706000), (143, 430952000), (144, 444564000), (145, 458551000), (146, 472924000), (147, 487693000), (148, 502867000), (149, 518458000), (150, 534476000), (151, 551000000)," +
                "(152, 567839000), (153, 585206000), (154, 603047000), (155, 621374000), (156, 640199000), (157, 659536000), (158, 679398000), (159, 699798000), (160, 720751000), (161, 742772000), (162, 764374000), (163, 787074000), (164, 810387000), (165, 834329000), (166, 858917000), (167, 884167000), (168, 910098000), (169, 936727000), (170, 964073000), (171, 992154000), (172, 1020991000), (173, 1050603000), (174, 1081010000), (175, 1112235000), (176, 1144298000), (177, 1177222000), (178, 1211030000), (179, 1245745000), (180, 1281393000), (181, 1317997000), (182, 1355584000), (183, 1404179000), (184, 1463811000), (185, 1534506000), (186, 1616294000), (187, 1709205000), (188, 1813267000), (189, 1928513000), (190, 2054975000), (191, 2192686000), (192, 2341679000), (193, 2501990000), (194, 2673655000), (195, 2856710000), (196, 3051194000), (197, 3257146000), (198, 3474606000), (199, 3703616000), (200, 7407232000)"
        );

        return this;
    }

    public GameDataSet pushItemTypes() throws ContainerException, SQLException {
        use(ItemType.class);

        if (repository(ItemType.class).has(new ItemType(1, null, null, null))) {
            return this;
        }

        connection.query(
            "INSERT INTO ITEM_TYPE (TYPE_ID, TYPE_NAME, SUPER_TYPE, EFFECT_AREA) VALUES (1, \"Amulette\", 1, NULL),\n" +
                "(2, \"Arc\", 2, \"Pa\"),\n" +
                "(3, \"Baguette\", 2, \"Pa\"),\n" +
                "(4, \"Bâton\", 2, \"Tb\"),\n" +
                "(5, \"Dague\", 2, \"Pa\"),\n" +
                "(6, \"Épée\", 2, \"Pa\"),\n" +
                "(7, \"Marteau\", 2, \"Xb\"),\n" +
                "(8, \"Pelle\", 2, \"Pa\"),\n" +
                "(9, \"Anneau\", 3, NULL),\n" +
                "(10, \"Ceinture\", 4, NULL),\n" +
                "(11, \"Botte\", 5, NULL),\n" +
                "(12, \"Potion\", 6, NULL),\n" +
                "(13, \"Parchemin d'expérience\", 6, NULL),\n" +
                "(14, \"Objet de dons\", 6, NULL),\n" +
                "(15, \"Ressource\", 9, NULL),\n" +
                "(16, \"Chapeau\", 10, NULL),\n" +
                "(17, \"Cape\", 11, NULL),\n" +
                "(18, \"Familier\", 12, NULL),\n" +
                "(19, \"Hache\", 2, \"Pa\"),\n" +
                "(20, \"Outil\", 2, \"Pa\"),\n" +
                "(21, \"Pioche\", 2, \"Pa\"),\n" +
                "(22, \"Faux\", 2, \"Pa\"),\n" +
                "(23, \"Dofus\", 13, NULL),\n" +
                "(24, \"Objet de Quête\", 14, NULL),\n" +
                "(25, \"Document\", 6, NULL),\n" +
                "(26, \"Potion de forgemagie\", 9, NULL),\n" +
                "(27, \"Objet de Mutation\", 15, NULL),\n" +
                "(28, \"Nourriture boost\", 16, NULL),\n" +
                "(29, \"Bénédiction\", 17, NULL),\n" +
                "(30, \"Malédiction\", 18, NULL),\n" +
                "(31, \"Roleplay Buffs\", 19, NULL),\n" +
                "(32, \"Personnage suiveur\", 20, NULL),\n" +
                "(33, \"Pain\", 6, NULL),\n" +
                "(34, \"Céréale\", 9, NULL),\n" +
                "(35, \"Fleur\", 9, NULL),\n" +
                "(36, \"Plante\", 9, NULL),\n" +
                "(37, \"Bière\", 6, NULL),\n" +
                "(38, \"Bois\", 9, NULL),\n" +
                "(39, \"Minerai\", 9, NULL),\n" +
                "(40, \"Alliage\", 9, NULL),\n" +
                "(41, \"Poisson\", 9, NULL),\n" +
                "(42, \"Friandise\", 6, NULL),\n" +
                "(43, \"Potion d'oubli de sort\", 6, NULL),\n" +
                "(44, \"Potion d'oubli de métier\", 6, NULL),\n" +
                "(45, \"Potion d'oubli de maîtrise\", 6, NULL),\n" +
                "(46, \"Fruit\", 9, NULL),\n" +
                "(47, \"Os\", 9, NULL),\n" +
                "(48, \"Poudre\", 9, NULL),\n" +
                "(49, \"Poisson comestible\", 6, NULL),\n" +
                "(50, \"Pierre précieuse\", 9, NULL),\n" +
                "(51, \"Pierre brute\", 9, NULL),\n" +
                "(52, \"Farine\", 9, NULL),\n" +
                "(53, \"Plume\", 9, NULL),\n" +
                "(54, \"Poil\", 9, NULL),\n" +
                "(55, \"Etoffe\", 9, NULL),\n" +
                "(56, \"Cuir\", 9, NULL),\n" +
                "(57, \"Laine\", 9, NULL),\n" +
                "(58, \"Graine\", 9, NULL),\n" +
                "(59, \"Peau\", 9, NULL),\n" +
                "(60, \"Huile\", 9, NULL),\n" +
                "(61, \"Peluche\", 9, NULL),\n" +
                "(62, \"Poisson vidé\", 9, NULL),\n" +
                "(63, \"Viande\", 9, NULL),\n" +
                "(64, \"Viande conservée\", 9, NULL),\n" +
                "(65, \"Queue\", 9, NULL),\n" +
                "(66, \"Metaria\", 9, NULL),\n" +
                "(68, \"Légume\", 9, NULL),\n" +
                "(69, \"Viande comestible\", 6, NULL),\n" +
                "(70, \"Teinture\", 6, NULL),\n" +
                "(71, \"Matériel d'alchimie\", 9, NULL),\n" +
                "(72, \"Oeuf de familier\", 6, NULL),\n" +
                "(73, \"Maîtrise\", 6, NULL),\n" +
                "(74, \"Fée d'artifice\", 6, NULL),\n" +
                "(75, \"Parchemin de sort\", 6, NULL),\n" +
                "(76, \"Parchemin de caractéristique\", 6, NULL),\n" +
                "(77, \"Certificat de mise en chanil\", 6, NULL),\n" +
                "(78, \"Rune de forgemagie\", 9, NULL),\n" +
                "(79, \"Boisson\", 6, NULL),\n" +
                "(80, \"Objet de mission\", 6, NULL),\n" +
                "(81, \"Sac à dos\", 11, NULL),\n" +
                "(82, \"Bouclier\", 7, NULL),\n" +
                "(83, \"Pierre d'âme\", 8, NULL),\n" +
                "(84, \"Clefs\", 9, NULL),\n" +
                "(85, \"Pierre d'âme pleine\", 6, NULL),\n" +
                "(86, \"Potion d'oubli percepteur\", 6, NULL),\n" +
                "(87, \"Parchemin de recherche\", 6, NULL),\n" +
                "(88, \"Pierre magique\", 6, NULL),\n" +
                "(89, \"Cadeaux\", 6, NULL),\n" +
                "(90, \"Fantôme de Familier\", 9, NULL),\n" +
                "(91, \"Dragodinde\", 21, NULL),\n" +
                "(92, \"Bouftou\", 21, NULL),\n" +
                "(93, \"Objet d'élevage\", 6, NULL),\n" +
                "(94, \"Objet utilisable\", 6, NULL),\n" +
                "(95, \"Planche\", 9, NULL),\n" +
                "(96, \"Ecorce\", 9, NULL),\n" +
                "(97, \"Certificat de monture\", 6, NULL),\n" +
                "(98, \"Racine\", 9, NULL),\n" +
                "(99, \"Filet de capture\", 8, NULL),\n" +
                "(100, \"Sac de ressources\", 6, NULL),\n" +
                "(102, \"Arbalète\", 2, \"Lc\"),\n" +
                "(103, \"Patte\", 9, NULL),\n" +
                "(104, \"Aile\", 9, NULL),\n" +
                "(105, \"Oeuf\", 9, NULL),\n" +
                "(106, \"Oreille\", 9, NULL),\n" +
                "(107, \"Carapace\", 9, NULL),\n" +
                "(108, \"Bourgeon\", 9, NULL),\n" +
                "(109, \"Oeil\", 9, NULL),\n" +
                "(110, \"Gelée\", 9, NULL),\n" +
                "(111, \"Coquille\", 9, NULL),\n" +
                "(112, \"Prisme\", 6, NULL),\n" +
                "(113, \"Objet vivant\", 22, NULL),\n" +
                "(114, \"Arme magique\", 2, \"Pa\"),\n" +
                "(115, \"Fragment d'âme de Shushu\", 6, NULL),\n" +
                "(116, \"Potion de familier\", 6, NULL)"
        );

        return this;
    }

    public NpcTemplate pushNpcTemplate(NpcTemplate template) throws SQLException, ContainerException {
        use(NpcTemplate.class);
        use(NpcExchange.class);

        connection.prepare(
            "INSERT INTO NPC_TEMPLATE (NPC_TEMPLATE_ID, GFXID, SCALE_X, SCALE_Y, SEX, COLOR1, COLOR2, COLOR3, ACCESSORIES, EXTRA_CLIP, CUSTOM_ARTWORK, STORE_ITEMS) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            stmt -> {
                int i = 1;

                stmt.setInt(i++, template.id());
                stmt.setInt(i++, template.gfxId());
                stmt.setInt(i++, template.scaleX());
                stmt.setInt(i++, template.scaleY());
                stmt.setInt(i++, template.gender().ordinal());
                stmt.setInt(i++, template.colors().color1());
                stmt.setInt(i++, template.colors().color2());
                stmt.setInt(i++, template.colors().color3());
                stmt.setString(i++, template.accessories());
                stmt.setInt(i++, template.extraClip());
                stmt.setInt(i++, template.customArtwork());

                if (template.storeItems().isPresent()) {
                    stmt.setString(i, StringUtils.join(template.storeItems().get(), ','));
                } else {
                    stmt.setNull(i, Types.VARCHAR);
                }

                return stmt.executeUpdate();
            }
        );

        return template;
    }

    public Npc pushNpc(Npc npc) throws SQLException, ContainerException {
        use(Npc.class);

        connection.prepare(
            "INSERT INTO NPC (NPC_ID, NPC_TEMPLATE_ID, MAP_ID, CELL_ID, ORIENTATION, QUESTIONS) VALUES (?, ?, ?, ?, ?, ?)",
            stmt -> {
                int i = 1;

                stmt.setInt(i++, npc.id());
                stmt.setInt(i++, npc.templateId());
                stmt.setInt(i++, npc.position().map());
                stmt.setInt(i++, npc.position().cell());
                stmt.setInt(i++, npc.orientation().ordinal());
                stmt.setString(i++, StringUtils.join(npc.questions(), ';'));

                return stmt.executeUpdate();
            }
        );

        return npc;
    }

    public GameDataSet pushNpcTemplates() throws SQLException, ContainerException {
        if (repository(NpcTemplate.class).has(new NpcTemplate(848, 0, 0, 0, null, null, null, 0, 0, null))) {
            return this;
        }

        pushNpcTemplate(new NpcTemplate(848, 30, 100, 100, Gender.MALE, new Colors(394758, 16121664, 13070517), "0,1be7,0,0,0", -1, 9096, null));
        pushNpcTemplate(new NpcTemplate(849, 9037, 100, 100, Gender.MALE, new Colors(-1, -1, -1), "0,0,0,0,0", -1, 0, null));
        pushNpcTemplate(new NpcTemplate(878, 40, 100, 100, Gender.MALE, new Colors(8158389, 13677665, 3683117), "0,20f9,2a5,1d5e,1b9e", 4, 9092, null));


        return this;
    }

    public GameDataSet pushNpcs() throws SQLException, ContainerException {
        pushNpcTemplates();
        pushQuestions();
        pushResponseActions();

        pushNpc(new Npc(457, 848, new Position(10302, 220), Direction.SOUTH_EAST, new int[] {3593, 3588}));
        pushNpc(new Npc(458, 849, new Position(10302, 293), Direction.SOUTH_EAST, new int[] {3596}));
        pushNpc(new Npc(472, 878, new Position(10340, 82), Direction.SOUTH_EAST, new int[] {3786}));

        return this;
    }

    /**
     * Add an npc with a store (id: 10001)
     */
    public GameDataSet pushNpcWithStore() throws SQLException {
        pushItemTemplates();
        pushItemSets();

        pushNpcTemplate(new NpcTemplate(10001, 9037, 100, 100, Gender.MALE, new Colors(-1, -1, -1), "0,0,0,0,0", -1, 0, new int[] {39, 2425}));
        pushNpc(new Npc(10001, 10001, new Position(10340, 125), Direction.SOUTH_EAST, new int[] {}));

        return this;
    }

    public Question pushQuestion(Question question) throws SQLException, ContainerException {
        use(Question.class);

        connection.prepare(
            "INSERT INTO NPC_QUESTION (QUESTION_ID, RESPONSE_IDS, PARAMETERS, CONDITIONS) VALUES (?, ?, ?, ?)",
            stmt -> {
                int i = 1;

                stmt.setInt(i++, question.id());
                stmt.setString(i++, StringUtils.join(question.responseIds(), ';'));
                stmt.setString(i++, StringUtils.join(question.parameters(), ';'));
                stmt.setString(i++, question.condition());

                return stmt.executeUpdate();
            }
        );

        return question;
    }

    public GameDataSet pushQuestions() throws SQLException, ContainerException {
        if (repository(Question.class).has(new Question(3596, null, null, null))) {
            return this;
        }

        pushQuestion(new Question(3596, new int[] {3182}, new String[] {}, ""));
        pushQuestion(new Question(3786, new int[] {3323, 3324}, new String[] {}, ""));
        pushQuestion(new Question(3593, new int[] {}, new String[] {}, "PO!60024"));
        pushQuestion(new Question(3588, new int[] {}, new String[] {}, ""));
        pushQuestion(new Question(3597, new int[] {}, new String[] {}, ""));
        pushQuestion(new Question(3787, new int[] {}, new String[] {}, ""));

        return this;
    }

    public ResponseAction pushResponseAction(ResponseAction action) throws SQLException, ContainerException {
        use(ResponseAction.class);

        connection.prepare(
            "INSERT INTO NPC_RESPONSE_ACTION (RESPONSE_ID, ACTION, ARGUMENTS) VALUES (?, ?, ?)",
            stmt -> {
                int i = 1;

                stmt.setInt(i++, action.responseId());
                stmt.setString(i++, action.action());
                stmt.setString(i++, action.arguments());

                return stmt.executeUpdate();
            }
        );

        return action;
    }

    public GameDataSet pushResponseActions() throws SQLException, ContainerException {
        if (repository(ResponseAction.class).has(new ResponseAction(3182, "NEXT", "3597"))) {
            return this;
        }

        pushResponseAction(new ResponseAction(3182, "NEXT", "3597"));
        pushResponseAction(new ResponseAction(3323, "NEXT", "3787"));
        pushResponseAction(new ResponseAction(3324, "LEAVE", ""));

        return this;
    }

    public NpcExchange pushNpcExchange(int id, int npcTemplateId, long requiredKamas, String requiredItems, long exchangedKamas, String exchangedItems) throws SQLException {
        Repository<NpcExchange> repository = repository(NpcExchange.class);

        connection.prepare(
            "INSERT INTO NPC_EXCHANGE (NPC_EXCHANGE_ID, NPC_TEMPLATE_ID, REQUIRED_KAMAS, REQUIRED_ITEMS, EXCHANGED_KAMAS, EXCHANGED_ITEMS) VALUES(?, ?, ?, ?, ?, ?)",
            statement -> {
                statement.setInt(1, id);
                statement.setInt(2, npcTemplateId);
                statement.setLong(3, requiredKamas);
                statement.setString(4, requiredItems);
                statement.setLong(5, exchangedKamas);
                statement.setString(6, exchangedItems);

                return statement.executeUpdate();
            }
        );

        return repository.get(new NpcExchange(id, 0, 0, null, 0, null));
    }

    public GameDataSet pushMonsterTemplates() throws SQLException, ContainerException {
        if (repository(MonsterTemplate.class).has(new MonsterTemplate(31, null, 0, null, null, null))) {
            return this;
        }

        connection.query(
            "INSERT INTO `MONSTER_TEMPLATE` (`MONSTER_ID`, `MONSTER_NAME`, `GFXID`, `COLORS`, `AI`, `CHARACTERISTICS`, `LIFE_POINTS`, `INITIATIVES`, `SPELLS`) VALUES " +
                "(31, 'Larve Bleue', 1563, '-1,-1,-1', 'AGGRESSIVE', '2@v:1;13:5;1f:5;17:-9;1b:-9;s:5;t:3;a:2g;f:2g;d:2g;8:4;9:2;|3@v:2;13:6;1f:6;17:-8;1b:-8;s:6;t:4;a:2l;f:2l;d:2l;8:4;9:2;|4@v:3;13:7;1f:7;17:-7;1b:-7;s:7;t:5;a:2q;f:2q;d:2q;8:4;9:2;|5@v:4;13:8;1f:8;17:-6;1b:-6;s:8;t:6;a:2v;f:2v;d:2v;8:4;9:2;|6@v:5;13:9;1f:9;17:-5;1b:-5;s:9;t:7;a:34;f:34;d:34;8:4;9:2;', '10|15|20|25|30', '20|25|35|40|50', '213@1;212@1|213@2;212@2|213@3;212@3|213@4;212@4|213@5;212@5')," +
                "(34, 'Larve Verte', 1568, '-1,-1,-1', 'AGGRESSIVE', '6@v:6;13:5;1f:-a;17:6;1b:a;s:5;t:4;a:2g;f:2g;d:2g;8:5;9:3;|7@v:7;13:6;1f:-9;17:7;1b:b;s:6;t:5;a:2l;f:2l;d:2l;8:5;9:3;|8@v:8;13:7;1f:-8;17:8;1b:c;s:7;t:6;a:2q;f:2q;d:2q;8:5;9:3;|9@v:9;13:8;1f:-7;17:9;1b:d;s:8;t:7;a:2v;f:2v;d:2v;8:5;9:3;|10@v:a;13:9;1f:-6;17:a;1b:e;s:9;t:8;a:34;f:34;d:34;8:5;9:3;', '20|35|50|65|80', '20|25|35|40|50', '215@1;213@1;212@1|215@2;213@2;212@2|215@3;213@3;212@3|215@4;213@4;212@4|215@5;213@5;212@5')," +
                "(36, 'Bouftou', 1566, '-1,-1,-1', 'AGGRESSIVE', '1@v:p;1f:-c;17:6;1b:-1i;s:f;t:f;a:2g;c:1s;f:2g;d:2g;e:26;8:5;9:3;|2@v:u;1f:-a;17:7;1b:-1d;s:g;t:g;a:2l;c:21;f:2l;d:2l;e:2b;8:5;9:3;|3@v:13;1f:-9;17:8;1b:-18;s:h;t:h;a:2q;c:26;f:2q;d:2q;e:2b;8:5;9:3;|4@v:18;1f:-8;17:9;1b:-13;s:i;t:i;a:2v;c:2l;f:2v;d:2v;e:2b;8:5;9:3;|5@v:1d;1f:-7;17:a;1b:-u;s:k;t:k;a:34;c:2q;f:34;d:34;e:2g;8:5;9:4;|6@v:1i;1f:-6;17:c;1b:-p;s:p;t:p;a:4m;c:4m;f:4m;d:7q;e:7q;8:6;9:4;', '30|40|50|60|70|140', '12|15|20|21|23|25', '2000@1;202@1;1709@1|2000@2;202@2;1709@2|2000@3;202@3;1709@3|2000@4;202@4;1709@4|2000@5;202@5;1709@5|2000@6;1709@6')"
        );

        use(MonsterRewardData.class);

        connection.query(
            "INSERT INTO `MONSTER_REWARD` (`MONSTER_ID`, `MIN_KAMAS`, `MAX_KAMAS`, `EXPERIENCES`) VALUES " +
                "(31, 50, 70, '3|7|12|18|26')," +
                "(34, 50, 70, '18|35|58|84|115')," +
                "(36, 65, 95, '5|7|9|12|14|16')"
        );

        pushRewardItems();

        return this;
    }

    public GameDataSet pushMonsterTemplateWithoutRewards() throws SQLException, ContainerException {
        if (repository(MonsterTemplate.class).has(new MonsterTemplate(400, null, 0, null, null, null))) {
            return this;
        }

        connection.query(
            "INSERT INTO `MONSTER_TEMPLATE` (`MONSTER_ID`, `MONSTER_NAME`, `GFXID`, `COLORS`, `AI`, `CHARACTERISTICS`, `LIFE_POINTS`, `INITIATIVES`, `SPELLS`) VALUES " +
                "(400, 'Larve Bleue', 1563, '-1,-1,-1', '1', '2@v:1;13:5;1f:5;17:-9;1b:-9;s:5;t:3;a:2g;f:2g;d:2g;8:4;9:2;|3@v:2;13:6;1f:6;17:-8;1b:-8;s:6;t:4;a:2l;f:2l;d:2l;8:4;9:2;|4@v:3;13:7;1f:7;17:-7;1b:-7;s:7;t:5;a:2q;f:2q;d:2q;8:4;9:2;|5@v:4;13:8;1f:8;17:-6;1b:-6;s:8;t:6;a:2v;f:2v;d:2v;8:4;9:2;|6@v:5;13:9;1f:9;17:-5;1b:-5;s:9;t:7;a:34;f:34;d:34;8:4;9:2;', '10|15|20|25|30', '20|25|35|40|50', '213@1;212@1|213@2;212@2|213@3;212@3|213@4;212@4|213@5;212@5')"        );

        return this;
    }

    public MonsterRewardItem pushRewardItem(MonsterRewardItem item) throws SQLException, ContainerException {
        use(MonsterRewardItem.class);

        connection.prepare(
            "INSERT INTO MONSTER_REWARD_ITEM (MONSTER_ID, ITEM_TEMPLATE_ID, QUANTITY, DISCERNMENT, RATE) VALUES (?, ?, ?, ?, ?)",
            stmt -> {
                int i = 1;

                stmt.setInt(i++, item.monsterId());
                stmt.setInt(i++, item.itemTemplateId());
                stmt.setInt(i++, item.quantity());
                stmt.setInt(i++, item.discernment());
                stmt.setDouble(i++, item.rate());

                return stmt.executeUpdate();
            }
        );

        return item;
    }

    public GameDataSet pushRewardItems() throws SQLException, ContainerException {
        if (!((MonsterRewardItemRepository) repository(MonsterRewardItem.class)).byMonster(36).isEmpty()) {
            return this;
        }

        pushItemTemplates();

        pushRewardItem(new MonsterRewardItem(31, 39, 1, 200, 15));
        pushRewardItem(new MonsterRewardItem(34, 40, 1, 200, 5));

        pushRewardItem(new MonsterRewardItem(36, 2416, 2, 100, 1));
        pushRewardItem(new MonsterRewardItem(36, 2422, 1, 300, 1));
        pushRewardItem(new MonsterRewardItem(36, 2428, 1, 400, 1));

        return this;
    }

    public GameDataSet pushMonsterGroups() throws SQLException, ContainerException {
        if (repository(MonsterGroupData.class).has(new MonsterGroupData(1, null, 0, 0, null, null, null, false))) {
            return this;
        }

        connection.query(
            "INSERT INTO `MONSTER_GROUP` (`MONSTER_GROUP_ID`, `MONSTERS`, `MAX_SIZE`, `MAX_COUNT`, `RESPAWN_TIME`, `COMMENT`, `WIN_FIGHT_TELEPORT_MAP_ID`, `WIN_FIGHT_TELEPORT_CELL_ID`, `FIXED_TEAM_NUMBER`) VALUES" +
                "(1, '31|34,10', 4, 2, 30000, 'larves', 0, 0, 0)," +
                "(2, '36,3,6', 6, 3, 75000, 'bouftous', 0, 0, 1)," +
                "(3, '36', 1, 1, 100, 'reswpan', 10340, 125, 0);"
        );

        return this;
    }

    public MonsterGroupPosition pushMonsterGroupPosition(MonsterGroupPosition entity) throws SQLException {
        use(MonsterGroupPosition.class);

        connection.prepare(
            "INSERT INTO MONSTER_GROUP_POSITION (MAP_ID, CELL_ID, MONSTER_GROUP_ID) VALUES (?, ?, ?)",
            stmt -> {
                int i = 1;

                stmt.setInt(i++, entity.map());
                stmt.setInt(i++, entity.cell());
                stmt.setInt(i++, entity.groupId());

                return stmt.executeUpdate();
            }
        );

        return entity;
    }
}
