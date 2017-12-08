package com.maohx2.kmhanko.itemshop;

import com.maohx2.kmhanko.database.MyDatabase;
import com.maohx2.kmhanko.database.MyDatabaseAdmin;
import com.maohx2.kmhanko.database.NamedDataAdmin;
import com.maohx2.kmhanko.itemdata.ItemData;
import com.maohx2.kmhanko.itemdata.ItemDataAdmin;

import java.util.List;

/**
 * Created by user on 2017/11/05.
 */

public abstract class ItemShopData<T extends ItemData> extends NamedDataAdmin<T> {
    String dbName;
    String dbAsset;

    public void setDatabase(MyDatabaseAdmin databaseAdmin) {
        databaseAdmin.addMyDatabase(dbName, dbAsset, 1, "r");
        database = databaseAdmin.getMyDatabase(dbName);
    }

    public List<T> getItemDatas() {
        return datas;
    }

    public T getItemData(int i) {
        try {
            return datas.get(i);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new Error("タカノ:ItemShopData#getItemData : Cannot get ItemData[" + i + "]" + e);
        }
    }

    public int getItemDataSize() {
        return datas.size();
    }

    public abstract void loadShopData(String table_name);

}