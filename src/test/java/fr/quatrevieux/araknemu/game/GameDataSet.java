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
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTrigger;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemSet;
import fr.quatrevieux.araknemu.data.world.entity.item.ItemTemplate;
import fr.quatrevieux.araknemu.data.world.transformer.ItemEffectsTransformer;
import fr.quatrevieux.araknemu.data.world.transformer.ItemSetBonusTransformer;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;
import fr.quatrevieux.araknemu.game.world.item.Type;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
            "INSERT INTO PLAYER_RACE (RACE_ID, RACE_NAME, RACE_STATS, START_DISCERNMENT, START_PODS, START_LIFE, PER_LEVEL_LIFE, MAP_ID, CELL_ID, STATS_BOOST) VALUES " +
                "(1,  'Feca',     '8:6;9:3;h:1;', 100, 1000, 50, 5, 10300, 320, '10:0@2,50@3,150@4,250@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5')," +
                "(2,  'Osamodas', '8:6;9:3;h:1;', 100, 1000, 50, 5, 10300, 320, '10:0@2,50@3,150@4,250@5;11:0@1;12:0@3;13:0@1,100@2,200@3,300@4,400@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5')," +
                "(3,  'Enutrof',  '8:6;9:3;h:1;', 120, 1000, 50, 5, 10300, 320, '10:0@1,50@2,150@3,250@4,350@5;11:0@1;12:0@3;13:0@1,100@2,150@3,230@4,330@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,20@2,60@3,100@4,150@5')," +
                "(4,  'Sram',     '8:6;9:3;h:1;', 100, 1000, 50, 5, 10300, 320, '10:0@1,100@2,200@3,300@4,400@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,100@2,200@3,300@4,400@5;15:0@2,50@3,150@4,250@5')," +
                "(5,  'Xelor',    '8:6;9:3;h:1;', 100, 1000, 50, 5, 10300, 320, '10:0@2,50@3,150@4,250@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5')," +
                "(6,  'Ecaflip',  '8:6;9:3;h:1;', 100, 1000, 50, 5, 10300, 320, '10:0@1,100@2,200@3,300@4,400@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,50@2,100@3,150@4,200@5;15:0@1,20@2,40@3,60@4,80@5')," +
                "(7,  'Eniripsa', '8:6;9:3;h:1;', 100, 1000, 50, 5, 10300, 320, '10:0@2,50@3,150@4,250@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5')," +
                "(8,  'Iop',      '8:6;9:3;h:1;', 100, 1000, 50, 5, 10300, 320, '10:0@1,100@2,200@3,300@4,400@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,20@2,40@3,60@4,80@5')," +
                "(9,  'Cra',      '8:6;9:3;h:1;', 100, 1000, 50, 5, 10300, 320, '10:0@1,50@2,150@3,250@4,350@5;11:0@1;12:0@3;13:0@1,20@2,40@3,60@4,80@5;14:0@1,50@2,100@3,150@4,200@5;15:0@1,50@2,150@3,250@4,350@5')," +
                "(10, 'Sadida',   '8:6;9:3;h:1;', 100, 1000, 50, 5, 10300, 320, '10:0@1,50@2,250@3,300@4,400@5;11:0@1;12:0@3;13:0@1,100@2,200@3,300@4,400@5;14:0@1,20@2,40@3,60@4,80@5;15:0@1,100@2,200@3,300@4,400@5')," +
                "(11, 'Sacrieur', '8:6;9:3;h:1;', 100, 1000, 50, 5, 10300, 320, '10:0@3,100@4,150@5;11:0@1@2;12:0@3;13:0@3,100@4,150@5;14:0@3,100@4,150@5;15:0@3,100@4,150@5')," +
                "(12, 'Pandawa',  '8:6;9:3;h:1;', 100, 1000, 50, 5, 10300, 320, '10:0@1,50@2,200@3;11:0@1;12:0@3;13:0@1,50@2,200@3;14:0@1,50@2,200@3;15:0@1,50@2,200@3')"
        );

        return this;
    }

    public GameDataSet pushMap(int id, String date, int width, int height, String key, String mapData) throws SQLException, ContainerException {
        Repository<MapTemplate> repository = repository(MapTemplate.class);

        if (repository.has(new MapTemplate(id, null, null, null, null))) {
            return this;
        }

        connection.prepare(
            "INSERT INTO maps (id, date, width, height, key, mapData) VALUES(?,?,?,?,?,?)",
            statement -> {
                statement.setInt(1, id);
                statement.setString(2, date);
                statement.setInt(3, width);
                statement.setInt(4, height);
                statement.setString(5, key);
                statement.setString(6, mapData);

                return statement.execute();
            }
        );

        return this;
    }

    public GameDataSet pushMaps() throws SQLException, ContainerException {
        use(MapTrigger.class);

        return
            pushMap(10300, "0802221747", 15, 17, "662838776047515721434a62545f5478543541592532356f25324257716f6e28657d2a5d4471206273545f5e4a614a442c2c73432c35515a553b386f6525324274262f3a747b6b675440557f5260754d6a52343d433b52755e6e6c3b437d417e22514524216771304a6e7e553c32794646287f3e57544c48526a284e2158376b4a414039752c2a2f303d6a5e746f2e323031385e6f483e785929262435777c3141463f363a43784e73345f5e406d2e235d423248427767582c5e4a6a4d234e3f61506d32595e7a2c4a2532355a7f5b5c474e6f732532426062775824253235", "HhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae7MaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaX8HhGae5QiaaGhaaeaaa7zHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaX7HhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeJgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeJgaaaHhaaeJgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae7MaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeHlaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaa5iHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeJga5iHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa7MHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaGhaae5ma7AHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeJga7HHhGaeaaaaaHhGae5UaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaX7HhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaa5jHhGaeaaaaaHhqaeaaa_4HhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaa7RHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa5jHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae5qiaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae5maaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaa")
            .pushMap(10540, "0707191128", 15, 17, "7e3a575438577150732c585135602e35682322772d402e585a3a4745732667286629217c38566a5c686b7779442426746b5454253242756a313e617a70205a644f5455217b3a2a4c70327169226e4e477f38522967367e7240424368233c6163433764215f2040572d735d25324276576a3f427e306c547c64604b746e582a5d61327e4f582d724c722a657e3b71572532354e656b7a333e4e422a7931244c5f", "HhaaeaaaaaHhaaeaEaaaHhaeeaaaaaHhaaeaaaaaHhcJeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa6bHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhIJem0aaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaa6cHhGaeaaa6aHhGaeaaa6cHhGaeaaa6aHhGaeaaa6cHhg-eaaaaaHhM-eaaaaaHhGeeaaaaaHhGaem0aaaHhGaem0aaaHhGaeaaaaaHhqaeqgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaa6aHhGaeaaa6cHhaaeaaaaaHhGaeaaaaaHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaa6cHhGaeaaa6aHhg-eaaa6aHhM-eaEqaaHhGeeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6cHhGae-Ba6bHhaaeaaa6bHhGaeaaaaaHhGeeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaaaaHhGae-AaaaGhaaeaaa9LHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaa6cHhGaeaaa6cHhGaeaaa6bHhaaeaaa6cHhM-eaaaaaHhGeeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6aHhaaeaaa6aHhGaeaIaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaEqaaHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaa95HhGaeaaaaaGhaaeaabbEGhaae-BaaaHhGaeaaa6aHhaaeaaaaaHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaae-CbbRHhGaeaaaaaHhaaeaaaaaHhGaeaEaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhg-eaaaaaHhGae-AaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaaa6aHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaabbIHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa9LHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaem0aaaHhM-eaEq6bHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaem0aaaHhg-eaaaaaGhaee-Da88HhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhaaem0aaaHhGaeaEa6bHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhqaeqgaaaHhaaeaaa6aHhGaeaIa6dHhGaeaaaaaHhGaeaaa6bGhaaeaaaaaHhGae-BaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhaaem0aaaHhw-eaaa6aHhGeeaaaaaHhGaeaaa6cGhaaeaaaaaGhaaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaaaaHhGae-BaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhg-eaaa6dHhGaeaIa6dHhGaeaaaaaHhGaeaaaaaGhaaeaabbPGhaaeaabbYHhGaeaaa6bHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaEq6aHhGeeaaa6dHhGaeaaaaaHhaaeaaaaaGhaaeaaaaaGhaaeaabbUHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhaaeaaaaaHhGeeaaa6dHhGaeaaaaaHhGaeaaaaaGhaae-DbbVHhGae-DaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaIa6bHhGae-Da6dHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhg-eaaa9LHhGeeaaa6dHhGae-DaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGafbpbboHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhg-eaaaaaHhGeeaaa6aHhGae-AaaaHhGaem0aaaHhGaem0aaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGae-DaaaHhGae-BaaaHhGaeaaaaaHhaaeaaaaaHhGaeaEaaaHhGae-DaaaHhGae-DbboHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaabb0GhaaeaabbZHhGaeaaaaaHhaaeaaaaaHhGae-AaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaa6cHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaaa6aHhGeeaEqaaHhGaeaEGaaHhGaem0a95HhGaem0aaaHhIJeaaaaaHhGaeaaaaaHhGaeaabbJHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaa6cHhGaeaaaaaHhaaeaaaaaHhGaeaEaaaHhGaeaIWaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaGhaaeaabbWHhGaeaaa6bHhGae-BaaaHhaaeaaaaaGeaaeaae9cHhGae-AaaaHhGae-Aa6dHhGaeaaaaaHhGaem0aaaHhsJeqgaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaae-DbbFHhGaeaaa6cHhGaeaaa6cGhaaeaabbYGfg-eaaa9LHhg-eaaa6aGhg-eaEq9kHhaeeaaa6dHhaaem0aaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaem0aaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa6aHhaaeaaaaaGhaaeaabb0")
        ;
    }

    /**
     * Create a simple player data
     */
    public Player createPlayer(int id) {
        return new Player(id, 10000 + id, 1, "PLAYER_" + id, Race.CRA, Sex.MALE, new Colors(-1, -1, -1), 1, new DefaultCharacteristics(), new Position(10540, 210), EnumSet.allOf(ChannelType.class), 0, 0, -1);
    }

    /**
     * Create an push a new player
     */
    public Player pushPlayer(String name, int accountId, int serverId) throws ContainerException {
        Player player = new Player(-1, accountId, serverId, name, Race.CRA, Sex.MALE, new Colors(-1, -1, -1), 1, new DefaultCharacteristics(), new Position(10540, 210), EnumSet.allOf(ChannelType.class), 0, 0, -1);

        return push(player);
    }

    public MapTrigger pushTrigger(MapTrigger trigger) throws ContainerException, SQLException {
        use(MapTrigger.class);

        connection.prepare(
            "INSERT INTO MAP_TRIGGER (TRIGGER_ID, MAP_ID, CELL_ID, ACTION, ARGUMENTS, CONDITIONS) VALUES (?, ?, ?, ?, ?, ?)",
            stmt -> {
                stmt.setInt(1, trigger.id());
                stmt.setInt(2, trigger.map());
                stmt.setInt(3, trigger.cell());
                stmt.setInt(4, trigger.action().ordinal());
                stmt.setString(5, trigger.arguments());
                stmt.setString(6, trigger.conditions());

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
        use(ItemTemplate.class);

        connection.prepare(
            "INSERT INTO ITEM_TEMPLATE (ITEM_TEMPLATE_ID, ITEM_TYPE, ITEM_NAME, ITEM_LEVEL, ITEM_EFFECTS, WEIGHT, ITEM_SET_ID, PRICE, CONDITIONS, WEAPON_INFO) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
            stmt -> {
                stmt.setInt(1, template.id());
                stmt.setInt(2, template.type().ordinal());
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
        pushItemTemplate(new ItemTemplate(39, Type.AMULETTE, "Petite Amulette du Hibou", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 2, 0, 0, "0d0+2")), 4, "", 0, "", 100));
        pushItemTemplate(new ItemTemplate(40, Type.EPEE, "Petite Epée de Boisaille", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_NEUTRAL, 1, 7, 0, "1d7+0")), 20, "CS>4", 0, "4;1;1;50;30;5;0", 200));
        pushItemTemplate(new ItemTemplate(284, Type.POUDRE, "Sel", 1, new ArrayList<>(), 1, "", 0, "", 10));
        pushItemTemplate(new ItemTemplate(694, Type.DOFUS, "Dofus Pourpre", 6, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_DAMAGE_PERCENT, 26, 50, 0, "1d25+25")), 1, "", 0, "", 10));

        pushItemTemplate(new ItemTemplate(2411, Type.COIFFE, "Coiffe du Bouftou", 10, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 40, 0, "1d40+0"), new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 40, 0, "1d40+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(2414, Type.CAPE, "Cape Bouffante", 10, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INITIATIVE, 1, 300, 0, "1d300+0"), new ItemTemplateEffectEntry(Effect.ADD_VITALITY, 1, 48, 0, "1d48+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(2416, Type.MARTEAU, "Marteau du Bouftou", 10, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_EARTH, 4, 8, 0, "1d5+3"), new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_FIRE, 4, 8, 0, "1d5+3"), new ItemTemplateEffectEntry(Effect.ADD_SUMMONS, 1, 0, 0, "0d0+1")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(2419, Type.ANNEAU, "Anneau de Bouze le Clerc", 10, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_VITALITY, 1, 28, 0, "1d28+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(2422, Type.BOTTES, "Boufbottes", 10, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_DAMAGE_PERCENT, 1, 15, 0, "1d15+0"), new ItemTemplateEffectEntry(Effect.ADD_VITALITY, 1, 33, 0, "1d33+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(2425, Type.AMULETTE, "Amulette du Bouftou", 3, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 10, 0, "1d10+0"), new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "1d10+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(2428, Type.CEINTURE, "Amulette du Bouftou", 20, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_PODS, 1, 500, 0, "1d500+0")), 10, "", 1, "", 550));

        pushItemTemplate(new ItemTemplate(2641, Type.COIFFE, "Toady", 30, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_AGILITY, 11, 60, 0, "1d50+10")), 10, "", 7, "", 2700));

        return this;
    }

    public GameDataSet pushHighLevelItems() throws SQLException, ContainerException {
        pushItemTemplate(new ItemTemplate(112411, Type.COIFFE, "Coiffe du Bouftou", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 40, 0, "1d40+0"), new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 40, 0, "1d40+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(112414, Type.CAPE, "Cape Bouffante", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INITIATIVE, 1, 300, 0, "1d300+0"), new ItemTemplateEffectEntry(Effect.ADD_VITALITY, 1, 48, 0, "1d48+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(112416, Type.MARTEAU, "Marteau du Bouftou", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_EARTH, 4, 8, 0, "1d5+3"), new ItemTemplateEffectEntry(Effect.INFLICT_DAMAGE_FIRE, 4, 8, 0, "1d5+3"), new ItemTemplateEffectEntry(Effect.ADD_SUMMONS, 1, 0, 0, "0d0+1")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(112419, Type.ANNEAU, "Anneau de Bouze le Clerc", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_VITALITY, 1, 28, 0, "1d28+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(112422, Type.BOTTES, "Boufbottes", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_DAMAGE_PERCENT, 1, 15, 0, "1d15+0"), new ItemTemplateEffectEntry(Effect.ADD_VITALITY, 1, 33, 0, "1d33+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(112425, Type.AMULETTE, "Amulette du Bouftou", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_INTELLIGENCE, 1, 10, 0, "1d10+0"), new ItemTemplateEffectEntry(Effect.ADD_STRENGTH, 1, 10, 0, "1d10+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(112428, Type.CEINTURE, "Amulette du Bouftou", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_PODS, 1, 500, 0, "1d500+0")), 10, "", 1, "", 550));
        pushItemTemplate(new ItemTemplate(111694, Type.DOFUS, "Dofus Pourpre", 200, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_DAMAGE_PERCENT, 26, 50, 0, "1d25+25")), 1, "", 0, "", 10));

        return this;
    }

    public GameDataSet pushUsableItems() throws SQLException, ContainerException {
        pushItemTemplate(new ItemTemplate(283, Type.POTION, "Fiole de Soin", 10, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_LIFE, 31, 60, 0, "1d30+30")), 1, "", 0, "", 10));
        pushItemTemplate(new ItemTemplate(468, Type.PAIN, "Pain d'Amakna", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_LIFE, 10, 0, 0, "0d0+10")), 1, "", 0, "", 1));
        pushItemTemplate(new ItemTemplate(800, Type.PARCHEMIN_CARAC, "Grand Parchemin d'Agilité ", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.ADD_CHARACT_AGILITY, 1, 0, 0, "")), 1, "", 0, "", 40000));
        pushItemTemplate(new ItemTemplate(2240, Type.FEE_ARTIFICE, "Petite Fée d'Artifice Rouge", 1, Arrays.asList(new ItemTemplateEffectEntry(Effect.FIREWORK, 1, 0, 2900, "")), 1, "", 0, "", 350));

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
                    new ItemTemplateEffectEntry(Effect.ADD_AGILITY, 50, 0, 0, ""),
                    new ItemTemplateEffectEntry(Effect.ADD_DAMAGE, 5, 0, 0, "")
                )
            )
        ));

        return this;
    }
}
