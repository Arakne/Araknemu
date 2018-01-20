package fr.quatrevieux.araknemu.game;

import fr.quatrevieux.araknemu.TestingDataSet;
import fr.quatrevieux.araknemu.core.dbal.repository.Repository;
import fr.quatrevieux.araknemu.core.dbal.util.ConnectionPoolUtils;
import fr.quatrevieux.araknemu.core.di.Container;
import fr.quatrevieux.araknemu.core.di.ContainerException;
import fr.quatrevieux.araknemu.data.constant.Race;
import fr.quatrevieux.araknemu.data.constant.Sex;
import fr.quatrevieux.araknemu.data.living.entity.player.Player;
import fr.quatrevieux.araknemu.data.value.Colors;
import fr.quatrevieux.araknemu.data.value.Position;
import fr.quatrevieux.araknemu.data.world.entity.character.PlayerRace;
import fr.quatrevieux.araknemu.data.world.entity.environment.MapTemplate;
import fr.quatrevieux.araknemu.game.chat.ChannelType;
import fr.quatrevieux.araknemu.game.world.creature.characteristics.DefaultCharacteristics;

import java.sql.SQLException;
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
            "INSERT INTO PLAYER_RACE (RACE_ID, RACE_NAME, RACE_STATS, MAP_ID, CELL_ID) VALUES " +
                "(1,  'Feca',     '8:1;9:34;a:6;b:3;j:1;', 10300, 320)," +
                "(2,  'Osamodas', '8:1;9:34;a:6;b:3;j:1;', 10300, 320)," +
                "(3,  'Enutrof',  '8:1;9:3o;a:6;b:3;j:1;', 10300, 320)," +
                "(4,  'Sram',     '8:1;9:34;a:6;b:3;j:1;', 10300, 320)," +
                "(5,  'Xelor',    '8:1;9:34;a:6;b:3;j:1;', 10300, 320)," +
                "(6,  'Ecaflip',  '8:1;9:34;a:6;b:3;j:1;', 10300, 320)," +
                "(7,  'Eniripsa', '8:1;9:34;a:6;b:3;j:1;', 10300, 320)," +
                "(8,  'Iop',      '8:1;9:34;a:6;b:3;j:1;', 10300, 320)," +
                "(9,  'Cra',      '8:1;9:34;a:6;b:3;j:1;', 10300, 320)," +
                "(10, 'Sadida',   '8:1;9:34;a:6;b:3;j:1;', 10300, 320)," +
                "(11, 'Sacrieur', '8:1;9:34;a:6;b:3;j:1;', 10300, 320)," +
                "(12, 'Pandawa',  '8:1;9:34;a:6;b:3;j:1;', 10300, 320)"
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
        return
            pushMap(10300, "0802221747", 15, 17, "662838776047515721434a62545f5478543541592532356f25324257716f6e28657d2a5d4471206273545f5e4a614a442c2c73432c35515a553b386f6525324274262f3a747b6b675440557f5260754d6a52343d433b52755e6e6c3b437d417e22514524216771304a6e7e553c32794646287f3e57544c48526a284e2158376b4a414039752c2a2f303d6a5e746f2e323031385e6f483e785929262435777c3141463f363a43784e73345f5e406d2e235d423248427767582c5e4a6a4d234e3f61506d32595e7a2c4a2532355a7f5b5c474e6f732532426062775824253235", "HhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae7MaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaX8HhGae5QiaaGhaaeaaa7zHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeaaaX7HhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhaaeJgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeJgaaaHhaaeJgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae7MaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeHlaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaa5iHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeJga5iHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa7MHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaGhaae5ma7AHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeJga7HHhGaeaaaaaHhGae5UaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaX7HhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaa5jHhGaeaaaaaHhqaeaaa_4HhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaa7RHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa5jHhGaeaaaaaHhGaeaaa6IHhGaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae5qiaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaae5maaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaa")
            .pushMap(10540, "0707191128", 15, 17, "7e3a575438577150732c585135602e35682322772d402e585a3a4745732667286629217c38566a5c686b7779442426746b5454253242756a313e617a70205a644f5455217b3a2a4c70327169226e4e477f38522967367e7240424368233c6163433764215f2040572d735d25324276576a3f427e306c547c64604b746e582a5d61327e4f582d724c722a657e3b71572532354e656b7a333e4e422a7931244c5f", "HhaaeaaaaaHhaaeaEaaaHhaeeaaaaaHhaaeaaaaaHhcJeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa6bHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhGaeaaaaaHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhIJem0aaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaa6cHhGaeaaa6aHhGaeaaa6cHhGaeaaa6aHhGaeaaa6cHhg-eaaaaaHhM-eaaaaaHhGeeaaaaaHhGaem0aaaHhGaem0aaaHhGaeaaaaaHhqaeqgaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaa6aHhGaeaaa6cHhaaeaaaaaHhGaeaaaaaHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaa6cHhGaeaaa6aHhg-eaaa6aHhM-eaEqaaHhGeeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6cHhGae-Ba6bHhaaeaaa6bHhGaeaaaaaHhGeeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaaaaHhGae-AaaaGhaaeaaa9LHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaa6cHhGaeaaa6cHhGaeaaa6bHhaaeaaa6cHhM-eaaaaaHhGeeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhIJeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6aHhaaeaaa6aHhGaeaIaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaEqaaHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaa95HhGaeaaaaaGhaaeaabbEGhaae-BaaaHhGaeaaa6aHhaaeaaaaaHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaae-CbbRHhGaeaaaaaHhaaeaaaaaHhGaeaEaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhg-eaaaaaHhGae-AaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaaa6aHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaabbIHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaaa9LHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaem0aaaHhM-eaEq6bHhGeeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaem0aaaHhg-eaaaaaGhaee-Da88HhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhaaem0aaaHhGaeaEa6bHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhqaeqgaaaHhaaeaaa6aHhGaeaIa6dHhGaeaaaaaHhGaeaaa6bGhaaeaaaaaHhGae-BaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaa6cHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhaaem0aaaHhw-eaaa6aHhGeeaaaaaHhGaeaaa6cGhaaeaaaaaGhaaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaaaaHhGaeaaaaaHhGae-BaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhg-eaaa6dHhGaeaIa6dHhGaeaaaaaHhGaeaaaaaGhaaeaabbPGhaaeaabbYHhGaeaaa6bHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaEq6aHhGeeaaa6dHhGaeaaaaaHhaaeaaaaaGhaaeaaaaaGhaaeaabbUHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhaaeaaaaaHhGeeaaa6dHhGaeaaaaaHhGaeaaaaaGhaae-DbbVHhGae-DaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaIa6bHhGae-Da6dHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhg-eaaa9LHhGeeaaa6dHhGae-DaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGafbpbboHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhGaeaIaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhg-eaaaaaHhGeeaaa6aHhGae-AaaaHhGaem0aaaHhGaem0aaaHhGaeaaaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGae-DaaaHhGae-BaaaHhGaeaaaaaHhaaeaaaaaHhGaeaEaaaHhGae-DaaaHhGae-DbboHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaaeaabb0GhaaeaabbZHhGaeaaaaaHhaaeaaaaaHhGae-AaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6cHhGaeaaa6cHhGaeaaaaaHhGaeaaaaaHhaaeaaaaaHhM-eaaa6aHhGeeaEqaaHhGaeaEGaaHhGaem0a95HhGaem0aaaHhIJeaaaaaHhGaeaaaaaHhGaeaabbJHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaa6bHhGaeaaa6cHhGaeaaaaaHhaaeaaaaaHhGaeaEaaaHhGaeaIWaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaem0aaaHhGaeaaaaaHhGaeaaaaaGhaaeaabbWHhGaeaaa6bHhGae-BaaaHhaaeaaaaaGeaaeaae9cHhGae-AaaaHhGae-Aa6dHhGaeaaaaaHhGaem0aaaHhsJeqgaaaHhIJeaaaaaHhGaeaaaaaHhGaeaaaaaHhGaeaaaaaGhaae-DbbFHhGaeaaa6cHhGaeaaa6cGhaaeaabbYGfg-eaaa9LHhg-eaaa6aGhg-eaEq9kHhaeeaaa6dHhaaem0aaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaem0aaaHhaaeaaaaaHhaaeaaaaaHhaaeaaaaaHhaaeaaa6aHhaaeaaaaaGhaaeaabb0")
        ;
    }

    /**
     * Create a simple player data
     */
    public Player createPlayer(int id) {
        return new Player(id, 10000 + id, 1, "PLAYER_" + id, Race.CRA, Sex.MALE, new Colors(-1, -1, -1), 1, new DefaultCharacteristics(), new Position(10540, 210), EnumSet.allOf(ChannelType.class));
    }

    /**
     * Create an push a new player
     */
    public Player pushPlayer(String name, int accountId, int serverId) throws ContainerException {
        Player player = new Player(-1, accountId, serverId, name, Race.CRA, Sex.MALE, new Colors(-1, -1, -1), 1, new DefaultCharacteristics(), new Position(10540, 210), EnumSet.allOf(ChannelType.class));

        return push(player);
    }
}
