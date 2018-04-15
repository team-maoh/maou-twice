package com.maohx2.kmhanko.geonode;

import com.maohx2.fuusya.TextBox.TextBoxAdmin;
import com.maohx2.ina.Arrange.Inventry;
import com.maohx2.ina.Arrange.InventryData;
import com.maohx2.ina.Constants;
import com.maohx2.ina.Draw.BitmapData;
import com.maohx2.ina.Draw.ImageContext;
import com.maohx2.ina.Text.BoxTextPlate;
import com.maohx2.ina.UI.UserInterface;

// Added by kmhanko
import com.maohx2.ina.WorldModeAdmin;
import com.maohx2.kmhanko.PlayerStatus.PlayerStatus;
import com.maohx2.kmhanko.database.MyDatabaseAdmin;
import com.maohx2.kmhanko.database.MyDatabase;
import com.maohx2.ina.Draw.Graphic;
import com.maohx2.ina.Text.PlateGroup;
import com.maohx2.kmhanko.itemdata.GeoObjectData;
import com.maohx2.kmhanko.plate.BackPlate;

// *** Graphic関係 ***
import android.graphics.Color;
import android.graphics.Paint;

// *** List関係 ***
import java.util.ArrayList;
import java.util.List;


import com.maohx2.fuusya.TextBox.TextBoxAdmin;
import com.maohx2.ina.Text.ListBox;

/**
 * Created by ina on 2017/10/08.
 */

    /* メモ
    tree_code 2120021010

    2 - 1 - 2 - 0
              - 0
      - 2 - 1 - 0
            1 - 0

     */

    //imageMagic

public class GeoSlotAdmin {

    //** Created by kmhanko **//

    static MyDatabase geoSlotMapDB;
    static MyDatabase geoSlotEventDB;

    public final int GEO_SLOT_MAX = 64;
    public final int TOUCH_R = 90;

    String t_name; //このGeoSlotAdmin = ジオマップの名称 = Table名

    List<Integer> tree_code = new ArrayList<Integer>(); //GeoSlotのツリー上構造を表す数値列
    List<GeoSlot> geo_slots = new ArrayList<GeoSlot>(GEO_SLOT_MAX);
    GeoSlot grand_geo_slot; //ツリーの中心であるGeoSlot
    GeoCalcSaverAdmin geo_calc_saver_admin; //GeoSlotの計算を行い、計算結果を格納する

    //TODO:ここでのGeoObjectDataは、書き換えを行うという点で少しおかしいので、変えた方がいいかもしれない
    GeoObjectData holdGeoObject; //現在選択中のジオオブジェクトを格納する。

    Graphic graphic;
    TextBoxAdmin textBoxAdmin;
    UserInterface userInterface;
    WorldModeAdmin worldModeAdmin;
    GeoSlotAdminManager geoSlotAdminManager;

    PlateGroup<GeoSlot> geoSlotGroup;
    PlateGroup<BoxTextPlate> releasePlateGroup;//解放する/やめる　の選択
    PlateGroup<BackPlate> backPlateGroup;

    GeoSlot focusGeoSlot; //今操作している(条件の解放のため選択している)GeoSlot

    int releaseTextBoxID; //スロット条件解放の説明文を表示するためのTextBoxID
    boolean isReleasePlateActive = false;
    //ListBox releaseList;//解放する/やめる　の選択

    PlayerStatus playerStatus;

    //Rewrite by kmhanko
    public GeoSlotAdmin(Graphic _graphic, UserInterface _user_interface, WorldModeAdmin _worldModeAdmin, TextBoxAdmin _textBoxAdmin, GeoSlotAdminManager _geoSlotAdminManager) {
        graphic = _graphic;
        userInterface = _user_interface;
        textBoxAdmin = _textBoxAdmin;
        worldModeAdmin = _worldModeAdmin;
        geoSlotAdminManager = _geoSlotAdminManager;

        //TextBoxなどの初期化
        releaseTextBoxID = textBoxAdmin.createTextBox(650,600,1450,800,2);
        textBoxAdmin.setTextBoxUpdateTextByTouching(releaseTextBoxID, false);
        textBoxAdmin.setTextBoxExists(releaseTextBoxID, false);
        //textBoxAdmin.hideTextBox(releaseTextBoxID);

        loadBackPlate();
    }

    //ジオスロットの並びを表すツリーコードを用いて、GeoSlotのインスタンス化を行う。
    public void loadDatabase(String _t_name) {
        t_name = _t_name;

        //DBからツリーコードを取得する関数
        tree_code = this.getTreeCode();

        //各GeoSlotのパラメータをDBから取得
        List<Integer> xs = geoSlotMapDB.getInt(t_name, "x");
        List<Integer> ys = geoSlotMapDB.getInt(t_name, "y");
        List<String> release_events = geoSlotMapDB.getString(t_name, "release_event");
        List<String> restrictions = geoSlotMapDB.getString(t_name, "restriction");

        //根スロットのインスタンス化
        grand_geo_slot = new GeoSlot(this, graphic, userInterface,
                Constants.Touch.TouchWay.UP_MOMENT,
                Constants.Touch.TouchWay.MOVE,
                new int[] { 0, 0, 100 },
                graphic.makeImageContext(graphic.searchBitmap("neco"), 0, 0, 5.0f, 5.0f, 0.0f, 128, false),
                graphic.makeImageContext(graphic.searchBitmap("neco"), 0, 0, 6.0f, 6.0f, 0.0f, 128, false)
        );
        //TODO 配列の位置はタッチ座標用、COntextの位置は表示用
        //TODO 拡大縮小を動的に行う場合は毎回このmakeImageContextを呼ぶと言うことになるわけだが。
        //TODO そもそも画像の表示位置はContextに入っているから毎回呼ぶと言うことになるわけだが。
        //TODO Contextをベースとして、bookingの時の値をオフセットにするとか？
        //TODO 0はとりあえずの値。

        //このメソッドを呼ぶと、全てのGeoSlotのインスタンス化が完了する。実体は各GeoSlotが子GeoSlotとして持つ。
        grand_geo_slot.makeGeoSlotInstance(tree_code, null);


        //GeoSlotの管理のため、GeoSlotのインスタンスをコピーしてくるメソッド。
        geo_slots = grand_geo_slot.getGeoSlots();

        GeoSlot.staticInit(textBoxAdmin, geoSlotEventDB);

        for(int i = 0; i < geo_slots.size(); i++) {
            geo_slots.get(i).setParam(xs.get(i), ys.get(i), TOUCH_R);
            //TouchIDセット
            //geo_slots.get(i).setTouchID(userInterface.setCircleTouchUI(xs.get(i), ys.get(i), 100));
            geo_slots.get(i).setReleaseEvent(release_events.get(i));
            geo_slots.get(i).setRestriction(restrictions.get(i));
        }

        //plateGroupインスタンス化
        geoSlotGroup = new PlateGroup<GeoSlot>((GeoSlot[])grand_geo_slot.getGeoSlots().toArray(new GeoSlot[0]));

    }


    private void loadBackPlate() {
        /*
        Paint textPaint = new Paint();
        textPaint.setTextSize(80f);
        textPaint.setARGB(255,255,255,255);

        backPlateGroup = new PlateGroup<BoxTextPlate>(
                new BoxTextPlate[] {
                        new BoxTextPlate(
                                graphic, userInterface, new Paint(),
                                Constants.Touch.TouchWay.UP_MOMENT,
                                Constants.Touch.TouchWay.MOVE,
                                Constants.BUTTON.BACK_BUTTON_POS,
                                "戻る",
                                textPaint
                        )
                }
        );
        */
        backPlateGroup = new PlateGroup<BackPlate>(
                new BackPlate[] {
                        new BackPlate(
                                graphic, userInterface, worldModeAdmin
                        ) {
                            @Override
                            public void callBackEvent() {
                                //戻るボタンが押された時の処理
                                geoSlotAdminManager.calcPlayerStatus();
                                geoSlotAdminManager.saveGeoInventry();

                                worldModeAdmin.setGeoSlotMap(Constants.Mode.ACTIVATE.STOP);
                                worldModeAdmin.setWorldMap(Constants.Mode.ACTIVATE.ACTIVE);
                            }
                        }
                }
        );
    }

    //***** GeoObjectステータス計算関係 *****

    //GeoSlotによるステータスへの加算量を計算する
    public boolean calcGeoSlot() {
        geo_calc_saver_admin = grand_geo_slot.calcPass();
        if (geo_calc_saver_admin != null) {
            geo_calc_saver_admin.finalCalc();
            return true;
        }
        return false;
    }

    public GeoCalcSaverAdmin getGeoCalcSaverAdmin() {
        return geo_calc_saver_admin;
    }

    //とりあえず計算後のパラメーターを表示するだけのメソッド。いつか消える。
    /*
    public void drawParam(Canvas canvas) {
        if (geo_calc_saver_admin == null) {
            return;
        }
        Paint paint = new Paint();
        paint.setColor(Color.argb(128, 0, 0, 0));
        paint.setTextSize(80);
        canvas.drawText(geo_calc_saver_admin.getParam("HP"), 1550, 200, paint);
        canvas.drawText(geo_calc_saver_admin.getParam("Attack"), 1550, 300, paint);
        canvas.drawText(geo_calc_saver_admin.getParam("Defence"), 1550, 400, paint);
        canvas.drawText(geo_calc_saver_admin.getParam("Luck"), 1550, 500, paint);
    }
    */

    // ***** GeoObject計算関係ここまで　*****

    //GeoSlotを解放しますか？的なもの
    public void geoSlotReleaseChoice() {
        isReleasePlateActive = false;
        textBoxAdmin.setTextBoxExists(releaseTextBoxID, false);

        if (!focusGeoSlot.isEventClear()) {
            //TextBox表示「ここを解放するためには　？？？　が必要」

            textBoxAdmin.setTextBoxExists(releaseTextBoxID, true);

            textBoxAdmin.bookingDrawText(releaseTextBoxID, "このスロットを解放するには");
            textBoxAdmin.bookingDrawText(releaseTextBoxID, "\n");
            textBoxAdmin.bookingDrawText(releaseTextBoxID, focusGeoSlot.getReleaseEvent());//TODO:イベント名そのままになっているが、これは仮
            textBoxAdmin.bookingDrawText(releaseTextBoxID, "が必要です。");
            textBoxAdmin.bookingDrawText(releaseTextBoxID, "MOP");

            textBoxAdmin.updateText(releaseTextBoxID);

            Paint textPaint = new Paint();
            textPaint.setTextSize(80f);
            textPaint.setARGB(255,255,255,255);

            //「解放する」「解放しない」ボタン表示　→　ListBox<Button>の完成待ち
            releasePlateGroup = new PlateGroup<BoxTextPlate>(
                    new BoxTextPlate[]{
                            new BoxTextPlate(
                                    graphic, userInterface, new Paint(),
                                    Constants.Touch.TouchWay.UP_MOMENT,
                                    Constants.Touch.TouchWay.MOVE,
                                    new int[]{1100, 50, 1550, 200},
                                    "解放する",
                                    textPaint
                            ),
                            new BoxTextPlate(
                                    graphic, userInterface, new Paint(),
                                    Constants.Touch.TouchWay.UP_MOMENT,
                                    Constants.Touch.TouchWay.MOVE,
                                    new int[]{1100, 250, 1550, 400},
                                    "やめる",
                                    textPaint
                            )
                    }
            );
            isReleasePlateActive = true;

            //TODO: PlateGroupのアクティブ切り替えと表示切り替え　いな

            /*
            releaseList = new ListBox();
            releaseList.init(userInterface, graphic, Constants.Touch.TouchWay.DOWN_MOMENT, 2 , 1200, 50, 1500, 50 + 100 * 2);
            releaseList.setContent(0, "解放する");
            releaseList.setContent(1, "やめる");
            isReleaseListActive = true;
            */
        }
    }


    public void update(){
        //GeoSlot
        /*
        for(int i = 0; i < geo_slots.size(); i++) {
            if (geo_slots.get(i) != null) {
                geo_slots.get(i).update();
            }
        }
        */
        geoSlotGroup.update();

        if (isReleasePlateActive) {
            releasePlateGroup.update();
            int content = releasePlateGroup.getTouchContentNum();
            switch (content) {
                case (0)://解放する
                    //解放するための色々な処理
                    focusGeoSlot.geoSlotRelease();
                    isReleasePlateActive = false;
                    textBoxAdmin.setTextBoxExists(releaseTextBoxID, false);
                    break;
                case (1)://やめる
                    isReleasePlateActive = false;
                    textBoxAdmin.setTextBoxExists(releaseTextBoxID, false);
                    break;
            }
        }

        checkInventrySelect();

        backPlateGroup.update();
    }

    public void draw() {
        //GeoSlot
        //線とスロットの描画は2つのfor文に分けなければならない(描画順の問題)
        for(int i = 0; i < geo_slots.size(); i++) {
            geo_slots.get(i).drawLine();
        }
        /*
        for(int i = 0; i < geo_slots.size(); i++) {
            /oolean f = geo_slots.get(i).equals(focusGeoSlot);
            geo_slots.get(i).draw(f);
        }
        */
        geoSlotGroup.draw();

        //Holdの表示
        if (isHoldGeoObject()) {
            graphic.bookingDrawBitmapData(
                    graphic.makeImageContext(holdGeoObject.getItemImage(), 100, 100, 5.0f, 5.0f, 0, 255, false)
            );
        }

        //ListBox
        if (isReleasePlateActive) {
            if (releasePlateGroup != null) {
                releasePlateGroup.draw();
            }
        }
        backPlateGroup.draw();
    }

    //Inventryから何か選択されているならそれを格納
    public void checkInventrySelect() {
        InventryData inventryData = userInterface.getInventryData();
        if (inventryData != null) {
            if (inventryData.getItemNum() > 0) {
                setHoldGeoObject((GeoObjectData) inventryData.getItemData());
                userInterface.setInventryData(null);
            }
        }
    }

    //InventryにGeoを加える
    public void addToInventry(GeoObjectData geoObjectData) {
        geoSlotAdminManager.addToInventry(geoObjectData);
    }

    //InventryからGeoを消す
    public void deleteFromInventry(GeoObjectData geoObjectData) {
        geoSlotAdminManager.deleteFromInventry(geoObjectData);
    }

    // ***** Getter *****
    private List<Integer> getTreeCode() {
        return geoSlotMapDB.getInt(t_name, "children_num");
    }
    public boolean isHoldGeoObject() {
        return holdGeoObject != null;
    }
    public String getName() { return t_name; }
    public GeoObjectData getHoldGeoObject() {
        return holdGeoObject;
    }

    // ***** Settet *****
    public void setHoldGeoObject(GeoObjectData geoObjectData) {
        holdGeoObject = geoObjectData;
    }
    public void setFocusGeoSlot(GeoSlot _focusGeoSlot) {
        focusGeoSlot = _focusGeoSlot;
    }
    public static void setGeoSlotMapDB(MyDatabase _geoSlotMapDB) { geoSlotMapDB = _geoSlotMapDB; }
    public static void setGeoSlotEventDB(MyDatabase _geoSlotEventDB) { geoSlotEventDB = _geoSlotEventDB; }

    //** Created by ina **

    //rewrire by kmhanko
    //GeoSlot geo_slots[] = new GeoSlot[10];
    //UserInterface user_interface;

    /* rewrite by kmhanko
    public void init(UserInterface _user_interface) {
        user_interface = _user_interface;

        for(int i = 0; i < 10; i++) {
            geo_slots.get(i) = new GeoSlot();
            geo_slots.get(i).init();
        }

        for(int i = 0; i < 10; i++) {
            geo_slots.get(i).setParam(30+50*i, 30+50*i, 20);
            geo_slots.get(i).setTouchID(user_interface.setCircleTouchUI(30+50*i,30+50*i,30));
        }
    }
    */

    /*rewrite by kmhanko
    public void update(){

        for(int i = 0; i < 10; i++) {
            if(user_interface.checkUI(geo_slots.get(i).getTouchID(), Constants.Touch.TouchWay.UP_MOMENT) == true){
                System.out.println(user_interface.getItemID());
                geo_slots.get(i).setItemID(user_interface.getItemID());
            }
        }
    }
    */


    /*rewrite by kmhanko
    public void draw(Canvas canvas) {

        for(int i = 0; i < 10; i++) {
            geo_slots.get(i).draw(canvas);
        }
    }
    */
}