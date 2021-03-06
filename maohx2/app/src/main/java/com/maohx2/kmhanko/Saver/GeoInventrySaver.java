package com.maohx2.kmhanko.Saver;

import java.util.List;
import com.maohx2.kmhanko.database.MyDatabaseAdmin;
import com.maohx2.kmhanko.itemdata.GeoObjectData;
import com.maohx2.ina.Draw.Graphic;
import com.maohx2.kmhanko.itemdata.GeoObjectDataCreater;

import static com.maohx2.ina.Constants.Inventry.INVENTRY_DATA_MAX;


/**
 * Created by user on 2018/04/09.
 */

public class GeoInventrySaver extends InventrySaver {

    Graphic graphic;

    public GeoInventrySaver(MyDatabaseAdmin _databaseAdmin, String dbName, String dbAsset, int version, String _loadMode, Graphic _graphic) {
        super(_databaseAdmin, dbName, dbAsset, version, _loadMode);
        graphic = _graphic;
    }

    public void init(Graphic _graphic) {
        graphic = _graphic;
    }

    @Override
    public void dbinit() {
    }

    @Override
    public void save() {
        deleteAll(); //セーブデータをリセットして書き直す場合に呼び出す

        GeoObjectData geoObjectData = null;
        for (int i = 0; i < INVENTRY_DATA_MAX; i++) {
            if (inventry.getItemData(i) == null) {
                break;
            }

            geoObjectData = (GeoObjectData)inventry.getItemData(i);

            database.insertLineByArrayString(
                    "GeoInventry",
                    new String[] { "name", "image_name", "num", "hp", "attack", "defence", "luck", "hp_rate", "attack_rate", "defence_rate", "luck_rate", "slot_set_name", "slot_set_id" },
                    new String[] {
                            geoObjectData.getName(),
                            geoObjectData.getImageName(),
                            String.valueOf(inventry.getItemNum(i)),
                            String.valueOf(geoObjectData.getHp()),
                            String.valueOf(geoObjectData.getAttack()),
                            String.valueOf(geoObjectData.getDefence()),
                            String.valueOf(geoObjectData.getLuck()),
                            String.valueOf(geoObjectData.getHpRate()),
                            String.valueOf(geoObjectData.getAttackRate()),
                            String.valueOf(geoObjectData.getDefenceRate()),
                            String.valueOf(geoObjectData.getLuckRate()),
                            String.valueOf(geoObjectData.getSlotSetName()),
                            String.valueOf(geoObjectData.getSlotSetID())
                    }
            );

        }
/*
        database.insertRawByList("ExpendItemInventry", "name", itemNames);
        database.rewriteRawByList("ExpendItemInventry", "num", nums);
*/
    }

    @Override
    public void load() {
        System.out.println("takano "+database.getTables());

        List<String> itemNames = database.getString("GeoInventry", "name");
        List<String> imageNames = database.getString("GeoInventry", "image_name");
        List<Integer> nums = database.getInt("GeoInventry", "num");
        List<Integer> hps = database.getInt("GeoInventry", "hp");
        List<Integer> attacks = database.getInt("GeoInventry", "attack");
        List<Integer> defences = database.getInt("GeoInventry", "defence");
        List<Integer> lucks = database.getInt("GeoInventry", "luck");
        List<Float> hpRates = database.getFloat("GeoInventry", "hp_rate");
        List<Float> attackRates = database.getFloat("GeoInventry", "attack_rate");
        List<Float> defenceRates = database.getFloat("GeoInventry", "defence_rate");
        List<Float> luckRates = database.getFloat("GeoInventry", "luck_rate");
        List<String> slotSetNames = database.getString("GeoInventry", "slot_set_name");
        List<Integer> slotSetIDs = database.getInt("GeoInventry", "slot_set_id");

        System.out.println("takano :"+itemNames);

        for(int i = 0; i < itemNames.size(); i++) {
            for(int j = 0; j < nums.get(i); j++){
                inventry.addItemData(
                        new GeoObjectData(
                                itemNames.get(i),
                                imageNames.get(i),
                                graphic.searchBitmap(imageNames.get(i)),
                                hps.get(i),
                                attacks.get(i),
                                defences.get(i),
                                lucks.get(i),
                                hpRates.get(i),
                                attackRates.get(i),
                                defenceRates.get(i),
                                luckRates.get(i),
                                slotSetNames.get(i),
                                slotSetIDs.get(i)
                        )
                );
            }
        }
    }


}
