package com.maohx2.ina;


import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import com.maohx2.fuusya.TextBox.TextBoxAdmin;
import com.maohx2.ina.Arrange.Inventry;
import com.maohx2.ina.Arrange.InventryItem;
import com.maohx2.ina.Battle.BattleUnitAdmin;
import com.maohx2.ina.Draw.Graphic;
import com.maohx2.ina.ItemData.EquipmentItemDataAdmin;
import com.maohx2.ina.ItemData.ItemData;
import com.maohx2.ina.Text.ListBoxAdmin;
import com.maohx2.ina.UI.BattleUserInterface;
import com.maohx2.ina.UI.UserInterface;
import com.maohx2.kmhanko.database.MyDatabaseAdmin;


/**
 * Created by ina on 2017/10/15.
 */




public class StartGameSystem {

    BattleUnitAdmin battle_unit_admin;
    SurfaceHolder holder;
    UserInterface start_user_interface;
    Graphic graphic;
    Inventry inventry;
    EquipmentItemDataAdmin equipment_item_data_admin;

    public void init(SurfaceHolder _holder, Graphic _graphic, UserInterface _start_user_interface, Activity start_activity, MyDatabaseAdmin my_database_admin) {

        holder = _holder;
        graphic = _graphic;
        start_user_interface = _start_user_interface;

        equipment_item_data_admin = new EquipmentItemDataAdmin(graphic, my_database_admin);

        inventry = new Inventry(start_user_interface, graphic);
        inventry.test_add_item(0,(ItemData)(equipment_item_data_admin.getOneDataByName("剣")));

    }


    public void updata() {
        inventry.updata();
    }


    public void draw() {
        inventry.draw();
        graphic.draw();
    }
}