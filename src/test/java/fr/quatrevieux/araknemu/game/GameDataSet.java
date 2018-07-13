package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.TestingDataSet;
import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Alignment;
import fr.quatrevieux.araknemu.data.constant.Effect;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.environment.SubArea;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.ItemTemplateEffectEntry;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.SpellTemplate;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerExperience;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemType;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.data.world.transformer.ItemSetBonusTransformer;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;

public class GameDataSet extends TestingDataSet {
    final private ConnectionPoolUtils connection;

    public GameDataSet(Container repositories, ConnectionPoolUtils connection) {
        super(repositories);

        this.connection = connection;
    }

    public GameDataSet pushRaces() throws SQLException, ContainerException {
        Repository<PlayerRace> repository = repository(PlayerRace.class);

        if (repository.has(new PlayerRace(Race.FECA))) {
            return this;
        }

        connection.query(
            "INSERT INTO PLAYER_RACE (RACE_ID, RACE_NAME, RACE_STATS, START_DISCERNMENT, START_PODS, START_LIFE, PER_LEVEL_LIFE, MAP_ID, CELL_ID, STATS_BOOST, RACE_SPELLS) VALUES " +
                "(1,  'Feca',     '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@2,50@3,150@4,250@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5',           '3|6|17')," +
                "(2,  'Osamodas', '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@2,50@3,150@4,250@5;11:0@1;12:0@3;13:0@1,100@2,200@3,300@4,400@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5',       '3|6|17')," +
                "(3,  'Enutrof',  '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 120, 1000, 50, 5, 10300, 320, '10:0@1,50@2,150@3,250@4,350@5;11:0@1;12:0@3;13:0@1,100@2,150@3,230@4,330@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,20@2,60@3,100@4,150@5',   '3|6|17')," +
                "(4,  'Sram',     '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@1,100@2,200@3,300@4,400@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,100@2,200@3,300@4,400@5;15:0@2,50@3,150@4,250@5',       '3|6|17')," +
                "(5,  'Xelor',    '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@2,50@3,150@4,250@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5',           '3|6|17')," +
                "(6,  'Ecaflip',  '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@1,100@2,200@3,300@4,400@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,50@2,100@3,150@4,200@5;15:0@1,20@2,40@3,60@4,80@5',     '3|6|17')," +
                "(7,  'Eniripsa', '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@2,50@3,150@4,250@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5',           '3|6|17')," +
                "(8,  'Iop',      '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@1,100@2,200@3,300@4,400@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,20@2,40@3,60@4,80@5',        '3|6|17')," +
                "(9,  'Cra',      '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@1,50@2,150@3,250@4,350@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,50@2,100@3,150@4,200@5;15:0@1,50@2,150@3,250@4,350@5',   '3|6|17')," +
                "(10, 'Sadida',   '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@1,50@2,250@3,300@4,400@5;11:0@1;12:0@3;13:0@1,100@2,200@3,300@4,400@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5', '3|6|17')," +
                "(11, 'Sacrieur', '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@3,100@4,150@5;11:0@1@2;12:0@3;13:0@3,100@4,150@5;14:0@3,100@4,150@5;15:0@3,100@4,150@5',                                          '3|6|17')," +
                "(12, 'Pandawa',  '8:6;9:3;h:1;@1|8:7;9:3;h:1;@100', 100, 1000, 50, 5, 10300, 320, '10:0@1,50@2,200@3;11:0@1;12:0@3;13:0@1,50@2,200@3;14:0@1,50@2,200@3;15:0@1,50@2,200@3',                                                '3|6|17')"
        );

        return this;
    }

    public GameDataSet pushMap(int id, String date, int width, int height, String key, String mapData, String places) throws SQLException, ContainerException {
        Repository<MapTemplate> repository = repository(MapTemplate.class);

        if (repository.has(new MapTemplate(id, null, null, null, null, null))) {
            return this;
        }

        connection.prepare(
            "INSERT INTO maps (id, date, width, height, key, mapData, places) VALUES(?,?,?,?,?,?,?)",
            statement -> {
                statement.setInt(1, id);
                statement.setString(2, date);
                statement.setInt(3, width);
                statement.setInt(4, height);
                statement.setString(5, key);
                statement.setString(6, mapData);
                statement.setString(7, places);

                return statement.execute();
            }
        );

        return this;
    }

    public GameDataSet pushMaps() throws SQLException, ContainerException {
        use(MapTrigger.class);

        return
            pushMap(10300, "0802221747", 15, 17, "662838776047515721434a62545f5478543541592532356f25324257716f6e28657d2a5d4471206273545f5e4a614a442c2c73432c35515a553b386f6525324274262f3a747b6b675440557f5260754d6a52343d433b52755e6e6c3b437d417e22514524216771304a6e7e553c32794646287f3e57544c48526a284e2158376b4a414039752c2a2f303d6a5e746f2e323031385e6f483e785929262435777c3141463f363a43784e73345f5e406d2e235d423248427767582c5e4a6a4d234e3f61506d32595e7a2c4a2532355a7f5b5c474e6f732532426062775824253235", "HhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae7MaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaX8HhGae5QiaaGhaaeaaa7zHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaX7HhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeJgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeJgaaaHhaaeJgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae7MaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeHlaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaa5iHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeJga5iHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa7MHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaGhaae5ma7AHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeJga7HHhGaeaaaaaHhGae5UaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaX7HhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaa5jHhGaeaaaaaHhqaeaaa_4HhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaa7RHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa5jHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae5qiaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae5maaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaa", "|")
            .pushMap(10540, "0707191128", 15, 17, "7e3a575438577150732c585135602e35682322772d402e585a3a4745732667286629217c38566a5c686b7779442426746b5454253242756a313e617a70205a644f5455217b3a2a4c70327169226e4e477f38522967367e7240424368233c6163433764215f2040572d735d25324276576a3f427e306c547c64604b746e582a5d61327e4f582d724c722a657e3b71572532354e656b7a333e4e422a7931244c5f", "HhaaeaaaaaHhaaeaEaaaHhaeeaaaaaHhaaeaaaaaHhcJeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa6bHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhIJem0aaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaa6cHhGaeaaa6aHhGaeaaa6cHhGaeaaa6aHhGaeaaa6cHhg-eaaaaaHhM-eaaaaaHhGeeaaaaaHhGaem0aaaHhGaem0aaaHhGaeaaaaaHhqaeqgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaa6aHhGaeaaa6cHhaaeaaaaaHhGaeaaaaaHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaa6cHhGaeaaa6aHhg-eaaa6aHhM-eaEqaaHhGeeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6cHhGae-Ba6bHhaaeaaa6bHhGaeaaaaaHhGeeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaaaaHhGae-AaaaGhaaeaaa9LHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaa6cHhGaeaaa6cHhGaeaaa6bHhaaeaaa6cHhM-eaaaaaHhGeeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6aHhaaeaaa6aHhGaeaIaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaEqaaHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaa95HhGaeaaaaaGhaaeaabbEGhaae-BaaaHhGaeaaa6aHhaaeaaaaaHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaae-CbbRHhGaeaaaaaHhaaeaaaaaHhGaeaEaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhg-eaaaaaHhGae-AaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaaa6aHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaabbIHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa9LHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaem0aaaHhM-eaEq6bHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaem0aaaHhg-eaaaaaGhaee-Da88HhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhaaem0aaaHhGaeaEa6bHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhqaeqgaaaHhaaeaaa6aHhGaeaIa6dHhGaeaaaaaHhGaeaaa6bGhaaeaaaaaHhGae-BaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhaaem0aaaHhw-eaaa6aHhGeeaaaaaHhGaeaaa6cGhaaeaaaaaGhaaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaaaaHhGae-BaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhg-eaaa6dHhGaeaIa6dHhGaeaaaaaHhGaeaaaaaGhaaeaabbPGhaaeaabbYHhGaeaaa6bHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaEq6aHhGeeaaa6dHhGaeaaaaaHhaaeaaaaaGhaaeaaaaaGhaaeaabbUHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhaaeaaaaaHhGeeaaa6dHhGaeaaaaaHhGaeaaaaaGhaae-DbbVHhGae-DaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaIa6bHhGae-Da6dHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhg-eaaa9LHhGeeaaa6dHhGae-DaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGafbpbboHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhg-eaaaaaHhGeeaaa6aHhGae-AaaaHhGaem0aaaHhGaem0aaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGae-DaaaHhGae-BaaaHhGaeaaaaaHhaaeaaaaaHhGaeaEaaaHhGae-DaaaHhGae-DbboHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaabb0GhaaeaabbZHhGaeaaaaaHhaaeaaaaaHhGae-AaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaa6cHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaaa6aHhGeeaEqaaHhGaeaEGaaHhGaem0a95HhGaem0aaaHhIJeaaaaaHhGaeaaaaaHhGaeaabbJHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaa6cHhGaeaaaaaHhaaeaaaaaHhGaeaEaaaHhGaeaIWaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaGhaaeaabbWHhGaeaaa6bHhGae-BaaaHhaaeaaaaaGeaaeaae9cHhGae-AaaaHhGae-Aa6dHhGaeaaaaaHhGaem0aaaHhsJeqgaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaae-DbbFHhGaeaaa6cHhGaeaaa6cGhaaeaabbYGfg-eaaa9LHhg-eaaa6aGhg-eaEq9kHhaeeaaa6dHhaaem0aaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaem0aaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa6aHhaaeaaaaaGhaaeaabb0", "|")
            .pushMap(10340, "0706131721", 15, 17, "682a5a717d49457e73274e3b3023452652224870524b735e6260457e377a4136216f7b5a7b332c55426c7b2776207136333f384333676577377828273860497a36214973525b606b6d3e7c4173716a713c6b232477664f3a6d2f79664f325f655b503e3a6f2c34202330272c4824635349657c2d554a31466a3f7e78667e485d527a203f37495d27664b5333207268452f2532426b74447e3a41215a386a6a5b70223f2d3078335a204543292d496c6366287637525723743f3e4c7155726e262f5f48703b294d4b537b544a4b3f4f7150512670323b6b43295a2e762129393254423944752e74636a6671693a235d34253235677a765841", "HhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa6GHhaaeaaaaaHhaaeaaaaaHhaae6HaaaHhaae60aaaHhaaeaaaaaHhaae6HaaaHhaaeaaaaaGhaaeaaa7oHhaae6HiaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaa6SHhgSe6HaaaHhaaeaaa6IHhGaeaaaaaHhGaeaaaaaHhqaeaaaqgHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7iHhGaeaaaaaHhGaeaaa6IHhMSeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhWaeaaaaaHhGaeJgaaaHhGaeaaaaaGhaaeaaa7hHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaa6THhGaeaaaaaHhGaeaaaaaHhMSe62aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhqaeaaaqgGhaaeaaa7AHhGaeaaaaaHhGaeaaaaaHhaae6Ha7eHhGaeaaaaaHhGaeaaaaaHhGaeaaa6IHhWaeaaaaaHhGaeaaaaaGhaaeaaa7gHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeJgaaaHhGaeaaaaaGhaaeaaa7jHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhWaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGae8uaaaGhaaeaaa7jHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGae8uaaaHhWae60aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhqaeaaaqgHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeJgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7iHhGaeJgaaaHhaaeaaaaaHhaaeJgaaaHhGae6HaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhWaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaa6IGhaaeaaa7hGhaaeaaa7iHhGaeaaaaaHhGaeaaaaaHhGaeJgaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7lGhaae8sa7gHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaGhaaeaaa7gGhaaeaaa7kHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhWae62aaaGhaaeaaa7kGhaaeaaa7hHhGaeaaaaaHhGaeaaaaaGhaaeaaa7lHhaaeaaaaaGhaaeaaa7nHhGaeaaaaaGhaaeaaa7lGhaaeaaa7jHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7hHhGaeaaaaaGhaaeaaa7mHhGaeaaaaaGhaaeJga7hHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhMTgJgaaaHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhGaeaaaaaHhGae8saaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhMSeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaae6HaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7jHhGaeaaa6IHhGaeaaaaaHhaaeaaaaaHhaaeaaa6IHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa7gHhGaeaaaaaHhGaeaaaaaHhaaeaaa6GHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaGhaaeaaa7kHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa6GHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhgTeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaa7dHhaaeaaaaaHhaaeaaa6WHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaGhaaeaaa7yHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaa6XHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhMVgaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaa", "a3btbYb_cacQcRc3c5dg|aWa_blbAbCbQb5b6cjcw")
        ;
    }

    /**
     * Create a simple player data
     */
    public Player createPlayer(int id) {
        return new Player(id, 10000 + id, 1, "PLAYER_" + id, Race.CRA, Sex.MALE, new Colors(-1, -1, -1), 1, new DefaultCharacteristics(), new Position(10540, 210), EnumSet.allOf(ChannelType.class), 0, 0, -1, 0);
    }

    /**
     * Create an push a new player
     */
    public Player pushPlayer(String name, int accountId, int serverId) throws ContainerException {
        Player player = new Player(-1, accountId, serverId, name, Race.CRA, Sex.MALE, new Colors(-1, -1, -1), 1, new DefaultCharacteristics(), new Position(10540, 210), EnumSet.allOf(ChannelType.class), 0, 0, -1, 0);

        return push(player);
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
        pushSubArea(new SubArea(1, 0, "Port de Madrestam", true, Alignment.NONE));
        pushSubArea(new SubArea(2, 0, "La montagne des Craqueleurs", true, Alignment.NEUTRAL));
        pushSubArea(new SubArea(3, 0, "Le champ des Ingalsses", true, Alignment.BONTARIAN));
        pushSubArea(new SubArea(4, 0, "La forêt d'Amakna", true, Alignment.BRAKMARIAN));

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
            "INSERT INTO `SPELL` (`SPELL_ID`, `SPELL_NAME`, `SPELL_SPRITE`, `SPELL_SPRITE_ARG`, `SPELL_LVL_1`, `SPELL_LVL_2`, `SPELL_LVL_3`, `SPELL_LVL_4`, `SPELL_LVL_5`, `SPELL_LVL_6`, `SPELL_TARGET`) VALUES " +
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
            "INSERT INTO `SPELL` (`SPELL_ID`, `SPELL_NAME`, `SPELL_SPRITE`, `SPELL_SPRITE_ARG`, `SPELL_LVL_1`, `SPELL_LVL_2`, `SPELL_LVL_3`, `SPELL_LVL_4`, `SPELL_LVL_5`, `SPELL_LVL_6`, `SPELL_TARGET`) VALUES " +
                "(181, 'Tremblement', 1003, '11,1,1', '99,2,,,2,0,0d0+2|99,4,,,2,0,0d0+4|2|0|0|50|100|false|false|false|false|0|1|0|5|CfCf||18;19;3;1;41|26|false', '99,3,,,2,0,0d0+3|99,6,,,2,0,0d0+6|2|0|0|50|100|false|false|false|false|0|1|0|5|CgCg||18;19;3;1;41|26|false', '99,3,,,3,0,0d0+3|99,6,,,3,0,0d0+6|2|0|0|50|100|false|false|false|false|0|1|0|5|ChCh||18;19;3;1;41|26|false', '99,4,,,3,0,0d0+4|99,8,,,3,0,0d0+8|2|0|0|50|100|false|false|false|false|0|1|0|5|CiCi||18;19;3;1;41|26|false', '99,5,,,4,0,0d0+5|99,10,,,4,0,0d0+10|2|0|0|50|100|false|false|false|false|0|1|0|5|CjCj||18;19;3;1;41|26|false', '99,7,,,4,0,0d0+7|99,12,,,4,0,0d0+12|2|0|0|50|100|false|false|false|false|0|1|0|5|CkCk||18;19;3;1;41|126|false', '')," +
                "(1630, 'Test skip turn', -1, '0,0,0', '140,,,,0,0||1|1|5|0|50|false|true|false|false|0|0|0|3|Pa||18;19;3;1;41|0|false', '140,,,,0,0||1|1|5|0|50|false|true|false|false|0|0|0|3|Pa||18;19;3;1;41|0|false', '140,,,,0,0||1|1|5|0|50|false|true|false|false|0|0|0|3|Pa||18;19;3;1;41|0|false', '140,,,,0,0||1|1|5|0|50|false|true|false|false|0|0|0|3|Pa||18;19;3;1;41|0|false', '140,,,,0,0||1|1|5|0|50|false|true|false|false|0|0|0|3|Pa||18;19;3;1;41|0|false', '', '');"
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
}
