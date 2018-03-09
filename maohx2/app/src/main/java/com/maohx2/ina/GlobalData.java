package com.maohx2.ina;

/**
 * Created by ina on 2017/10/22.
 */

import android.app.Application;

import com.maohx2.ina.Arrange.Inventry;
import com.maohx2.ina.Draw.BitmapDataAdmin;
import com.maohx2.kmhanko.database.MyDatabaseAdmin;

//by kmhanko
import com.maohx2.kmhanko.PlayerStatus.PlayerStatus;

//アイテムのアイコン画像、プレイヤーのステータス、アイテムのデータ、をグローバルで持つ予定
public class GlobalData extends Application {

    BitmapDataAdmin g_bitmap_data_admin;
    MyDatabaseAdmin g_my_database_admin;
    GlobalConstants g_constants;

    //by kmhanko
    PlayerStatus playerStatus;

    public void init(int disp_x, int disp_y) {
        g_my_database_admin = new MyDatabaseAdmin(this);
        g_my_database_admin.addMyDatabase("globalImageDB", "globalImage.db", 1, "r");
        g_bitmap_data_admin = new BitmapDataAdmin();
        g_bitmap_data_admin.init(this);
        g_bitmap_data_admin.loadGlobalImages(g_my_database_admin.getMyDatabase("globalImageDB"));
        g_constants = new GlobalConstants(disp_x, disp_y);

        //by kmhanko
        playerStatus = new PlayerStatus(g_my_database_admin);
    }

    //ゲッターとか
    public BitmapDataAdmin getGlobalBitmapDataAdmin() { return g_bitmap_data_admin;}
    public GlobalConstants getGlobalConstants() { return g_constants;}
    // by kmhanko
    public PlayerStatus getPlayerStatus() { return playerStatus; }

}