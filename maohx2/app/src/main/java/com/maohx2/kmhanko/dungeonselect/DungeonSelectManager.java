package com.maohx2.kmhanko.dungeonselect;

import com.maohx2.fuusya.TextBox.TextBoxAdmin;
import com.maohx2.horie.map.MapStatus;
import com.maohx2.horie.map.MapStatusSaver;
import com.maohx2.ina.Activity.DemoManager;
import com.maohx2.ina.Activity.UnitedActivity;
//import com.maohx2.ina.ActivityChange;
import com.maohx2.ina.Constants;
import com.maohx2.ina.Constants.SELECT_WINDOW;
import com.maohx2.ina.Constants.SELECT_WINDOW_PLATE;
import com.maohx2.ina.Constants.LOOP_WINDOW;
import com.maohx2.ina.Constants.GAMESYSTEN_MODE.WORLD_MODE;
import com.maohx2.ina.Constants.DungeonKind.DUNGEON_KIND;
import com.maohx2.ina.GlobalData;
import com.maohx2.ina.UI.UserInterface;
import com.maohx2.ina.GameSystem.WorldModeAdmin;
import com.maohx2.kmhanko.Arrange.InventryS;
import com.maohx2.kmhanko.PlayerStatus.PlayerStatus;
import com.maohx2.kmhanko.WindowPlate.WindowTextPlate;
import com.maohx2.kmhanko.database.MyDatabase;
import com.maohx2.kmhanko.database.MyDatabaseAdmin;
import com.maohx2.kmhanko.geonode.GeoSlotAdmin;
import com.maohx2.kmhanko.plate.BoxImageTextPlate;

import com.maohx2.ina.Constants.POPUP_WINDOW;

import com.maohx2.ina.Text.CircleImagePlate;
import com.maohx2.ina.Text.BoxTextPlate;
import com.maohx2.ina.Text.PlateGroup;

import com.maohx2.ina.Constants.WorldMap.SELECT_MODE;

import android.graphics.Color;
import android.graphics.Paint;

import com.maohx2.kmhanko.sound.SoundAdmin;
/**
 * Created by user on 2017/11/24.
 */

import com.maohx2.ina.Draw.Graphic;
import com.maohx2.ina.UI.UserInterface;
import com.maohx2.ina.Text.ListBox;
import com.maohx2.kmhanko.geonode.GeoSlotAdminManager;

import android.content.Intent;

import java.util.ArrayList;
import java.util.List;
import com.maohx2.kmhanko.Talking.TalkAdmin;

//TODO 指を離すと元に戻ってしまうので、どこを選択したのかわかりにくい。説明プレートを出せばわかるけど
public class DungeonSelectManager {
    //DungeonSelectButtonAdmin dungeonSelectButtonAdmin;
    //LoopSelectButtonAdmin loopSelectButtonAdmin;


    static final float DUNGEON_SELECT_BUTTON_RATE_DEFAULT = 4.0f;
    static final float DUNGEON_SELECT_BUTTON_RATE_FEEDBACK = 5.0f;
    static final int DUNGEON_SELECT_BUTTON_RATE_TOURH_R = 110;

    static final String DUNGEON_SELECT_BUTTON_TABLE_NAME = "dungeon_select_button";
    //static final String MENU_BUTTON_TABLE_NAME = "menu_button";
    
    boolean initUIsFlag = false;

    private boolean isDungeonSelectActive = false;

    int enterTextBoxID;
    Paint enterTextPaint;

    Paint dungeonEnterTextPaint;

    //int loopCountTextBoxID;
    Paint loopCountTextPaint;

    Paint dungeonNotEnterPaint;

    Graphic graphic;
    UserInterface userInterface;
    MyDatabaseAdmin databaseAdmin;
    GeoSlotAdminManager geoSlotAdminManager;
    WorldModeAdmin worldModeAdmin;
    TextBoxAdmin textBoxAdmin;

    MyDatabase database;

    SoundAdmin soundAdmin;

    //TODO いなの関数待ち
    List<String> dungeonName;
    List<String> dungeonNameExpress;
    List<String> event;
    List<Integer> dungeonNumber;

    PlateGroup<MapIconPlate> mapIconPlateGroup;

    //PlateGroup<BoxTextPlate> dungeonInformationPlate;
    PlateGroup<BoxImageTextPlate> dungeonEnterSelectButtonGroup;
    PlateGroup<BoxImageTextPlate> maohEnterSelectButtonGroup;


    WindowTextPlate dungeonEnterNamePlate;
    WindowTextPlate loopCountWindowPlate;
    WindowTextPlate dungeonNotEnterPlate;

    WindowTextPlate dungeonIconName[];
    WindowTextPlate dungeonIconNumber[];

    //PlateGroup<CircleImagePlate> menuButtonGroup;

    static final String DB_NAME = "dungeonselectDB";
    static final String DB_ASSET = "dungeonselectDB.db";


    int focusDungeonButtonID;

    PlayerStatus playerStatus;

   //WorldActivity worldActivity;
    //ActivityChange activityChange;
    UnitedActivity unitedActivity;

    //いなの実装までの仮置き
    //boolean enterSelectFlag = false;

    InventryS geoInventry;
    InventryS expendItemInventry;
    InventryS equipmentInventry;

    MapStatus mapStatus;
    MapStatusSaver mapStatusSaver;

    TalkAdmin talkAdmin;

    List<Integer> x;
    List<Integer> y;

    public DungeonSelectManager(Graphic _graphic, UserInterface _userInterface, TextBoxAdmin _textBoxAdmin, WorldModeAdmin _worldModeAdmin, MyDatabaseAdmin _databaseAdmin, GeoSlotAdminManager _geoSlotAdminManager, PlayerStatus _playerStatus, SoundAdmin _soundAdmin, UnitedActivity _unitedActivity, MapStatus _mapStatus, MapStatusSaver _mapStatusSaver, TalkAdmin _talkAdmin) {
        graphic = _graphic;
        userInterface = _userInterface;
        textBoxAdmin = _textBoxAdmin;
        databaseAdmin = _databaseAdmin;
        geoSlotAdminManager = _geoSlotAdminManager;
        worldModeAdmin = _worldModeAdmin;
        unitedActivity = _unitedActivity;

        //playerStatus = _playerStatus;
        soundAdmin = _soundAdmin;
        mapStatus = _mapStatus;
        mapStatusSaver = _mapStatusSaver;
        talkAdmin = _talkAdmin;


        GlobalData globalData = (GlobalData) unitedActivity.getApplication();
        playerStatus = globalData.getPlayerStatus();

        geoInventry = globalData.getGeoInventry();
        expendItemInventry = globalData.getExpendItemInventry();
        equipmentInventry = globalData.getEquipmentInventry();

        setDatabase(databaseAdmin);
        initMapIconPlate();
        initDungeonEnterSelectButton();
        initMaohEnterSelectButton();
        //initModeSelectButton();
        initTextBox();
        initOkButton();
        initLoopCountSelectButton();
        initWindow();
        initTutorialButton();

        initUIs();//一番最後

        //TODO : Loopselect
    }

    public void start() {


        if(playerStatus.getMaohWinCount() == 10 && playerStatus.getEndingFlag() == 0){
            playerStatus.setEndingFlag(1);
            playerStatus.save();
            worldModeAdmin.setMode(WORLD_MODE.ENDING);
            return;
        }


        //各ダンジョン初クリア時のイベント発生。
        //if (playerStatus.getClearCount() == 0) {
            for (int i = Constants.STAGE_NUM - 1; i >= 0; i--) {
                if (mapStatus.getMapClearStatus(i) == 1) {
                    switch(i) {
                        case 0:
                            talkAdmin.start("ClearForest", false);
                            break;
                        case 1:
                            talkAdmin.start("ClearLava", false);
                            break;
                        case 2:
                            talkAdmin.start("ClearSea", false);
                            break;
                        case 3:
                            talkAdmin.start("ClearChess", false);
                            break;
                        case 4:
                            talkAdmin.start("ClearSwamp", false);
                            break;
                        case 5:
                            talkAdmin.start("ClearHaunted", false);
                            break;
                        case 6:
                            talkAdmin.start("ClearDragon", false);
                            break;
                        default:
                            break;
                    }
                }
            }
        //}
        //各種魔王討伐時のイベント発生。
        switch(playerStatus.getMaohWinCount()) {
            case 1:
                talkAdmin.start("AfterMaoh001", false);
                break;
            case 2:
                if (DemoManager.getDemoMode()) {
                    talkAdmin.start("AfterMaoh002forDEMO", false);
                }
                break;
            case 3:
                talkAdmin.start("AfterMaoh003", false);
                break;
            case 6:
                talkAdmin.start("AfterMaoh006", false);
                break;
            case 8:
                talkAdmin.start("AfterMaoh008", false);
                break;
            case 9:
                talkAdmin.start("AfterMaoh009", false);
                break;
            case 10:
                talkAdmin.start("AfterMaoh010", false);
                break;
        }


        //前ダンジョンクリアかつ魔王討伐回数＝Clear+1なら、Clearを+1
        boolean flag = true;
        for (int i = 0; i < Constants.STAGE_NUM; i++) {
            if (mapStatus.getMapClearStatus(i) == 0) {
                flag = false;
            }
        }

        if (flag) {
            playerStatus.addClearCount();
            playerStatus.setNowClearCount(playerStatus.getClearCount());

            /*
            enterTextBoxUpdateCountUp();
            OkButtonGroup.setUpdateFlag(true);
            OkButtonGroup.setDrawFlag(true);
            */

            for (int i = 0; i < Constants.STAGE_NUM; i++) {
                mapStatus.setMapClearStatus(0,i);
                mapStatusSaver.save();
            }
            playerStatus.save();
        }

        mapIconPlateListUpdate();

    }

    private void setDatabase(MyDatabaseAdmin databaseAdmin) {
        databaseAdmin.addMyDatabase(DB_NAME, DB_ASSET, 1, "r");
        database = databaseAdmin.getMyDatabase(DB_NAME);
    }

    PlateGroup<BoxImageTextPlate> tutorialButtonGroup;
    private void initTutorialButton() {
        Paint textPaint1 = new Paint();
        textPaint1.setTextSize(Constants.TUTRIAL_BUTTON.TEXT_SIZE_TU);
        textPaint1.setARGB(255, 255, 255, 255);
        Paint textPaint2 = new Paint();
        textPaint2.setTextSize(Constants.TUTRIAL_BUTTON.TEXT_SIZE_NAME);
        textPaint2.setARGB(255, 255, 255, 255);

        tutorialButtonGroup = new PlateGroup<>(
                new BoxImageTextPlate[]{
                        new BoxImageTextPlate(
                                graphic, userInterface, Constants.Touch.TouchWay.UP_MOMENT, Constants.Touch.TouchWay.MOVE,
                                new int[]{Constants.TUTRIAL_BUTTON.UNDER_LEFT,Constants.TUTRIAL_BUTTON.UNDER_UP,Constants.TUTRIAL_BUTTON.UNDER_RIGHT,Constants.TUTRIAL_BUTTON.UNDER_BOTTOM},
                                new String[] { "チュートリアル", "- ジオ -"},
                                new Paint[] { textPaint1, textPaint2},
                                new WindowTextPlate.TextPosition[] { WindowTextPlate.TextPosition.UP, WindowTextPlate.TextPosition.DOWN }
                        ) {
                            @Override
                            public void callBackEvent() {
                                //OKが押された時の処理
                                soundAdmin.play("enter00");
                                //チュートリアル表示
                                worldModeAdmin.setMode(Constants.GAMESYSTEN_MODE.WORLD_MODE.TU_GEO);
                            }
                        }
                });
        tutorialButtonGroup.setUpdateFlag(false);
        tutorialButtonGroup.setDrawFlag(false);
    }

    //***** GeoMapとDungeonSelectMapの切り替え *****

    public void switchSelectMode() {
        if (worldModeAdmin.getMode() == WORLD_MODE.GEO_MAP_SELECT) {
            worldModeAdmin.setMode(WORLD_MODE.DUNGEON_SELECT_INIT);
            tutorialButtonGroup.setDrawFlag(false);
            tutorialButtonGroup.setUpdateFlag(false);
        } else {
            worldModeAdmin.setMode(WORLD_MODE.GEO_MAP_SELECT_INIT);
            tutorialButtonGroup.setDrawFlag(true);
            tutorialButtonGroup.setUpdateFlag(true);
        }
    }

    //***** Buttonのinit関係 *****
    private void initMapIconPlate(){
        int size = database.getSize(DUNGEON_SELECT_BUTTON_TABLE_NAME);

        dungeonName = database.getString(DUNGEON_SELECT_BUTTON_TABLE_NAME, "name");
        dungeonNameExpress = database.getString(DUNGEON_SELECT_BUTTON_TABLE_NAME, "dungeonName");
        List<String> imageName = database.getString(DUNGEON_SELECT_BUTTON_TABLE_NAME, "image_name");
        List<Integer> x = database.getInt(DUNGEON_SELECT_BUTTON_TABLE_NAME, "x");
        List<Integer> y = database.getInt(DUNGEON_SELECT_BUTTON_TABLE_NAME, "y");
        List<Integer> scale = database.getInt(DUNGEON_SELECT_BUTTON_TABLE_NAME, "scale");
        List<Integer> scale_feed = database.getInt(DUNGEON_SELECT_BUTTON_TABLE_NAME, "scale_feed");
        dungeonNumber = database.getInt(DUNGEON_SELECT_BUTTON_TABLE_NAME, "number");

        event = database.getString(DUNGEON_SELECT_BUTTON_TABLE_NAME, "event");

        List<MapIconPlate> mapIconPlateList = new ArrayList<MapIconPlate>();

        //インスタンス化
        for (int i = 0; i < size; i++) {
            mapIconPlateList.add(new MapIconPlate(
                    graphic, userInterface,
                    Constants.Touch.TouchWay.UP_MOMENT,
                    Constants.Touch.TouchWay.MOVE,
                    new int[] { x.get(i), y.get(i), DUNGEON_SELECT_BUTTON_RATE_TOURH_R },
                    graphic.makeImageContext(graphic.searchBitmap(imageName.get(i)),x.get(i), y.get(i), scale.get(i), scale.get(i), 0.0f, 255, false),
                    graphic.makeImageContext(graphic.searchBitmap(imageName.get(i)),x.get(i), y.get(i), scale_feed.get(i), scale_feed.get(i), 0.0f, 255, false),
                    dungeonName.get(i),
                    dungeonNameExpress.get(i),
                    event.get(i)

            ));
        }


        MapIconPlate[] mapIconPlates = new MapIconPlate[mapIconPlateList.size()];
        mapIconPlateGroup = new PlateGroup<MapIconPlate>(mapIconPlateList.toArray(mapIconPlates));
        mapIconPlateListUpdate();

        //ここからアイコンの名前プレート
        Paint dungeonIconNamePaint = new Paint();
        dungeonIconNamePaint.setTextSize(30);
        dungeonIconNamePaint.setColor(Color.WHITE);

        int count;

        count = 0;
        dungeonIconName = new WindowTextPlate[size];
        for (int i = 0; i < size; i++) {
            if (dungeonNameExpress.get(i) != null) {
                int centerX = x.get(i) + 0;
                int centerY;

                if (dungeonName.get(i).equals("Maoh")) {
                    centerY = y.get(i) + 130;
                } else {
                    centerY = y.get(i) + 105;
                }
                int width = 230;
                int height = 40;
                dungeonIconName[count] = new WindowTextPlate(graphic, new int[]{ centerX-width/2, centerY-height/2, centerX+width/2, centerY+height/2 },dungeonNameExpress.get(i),dungeonIconNamePaint, WindowTextPlate.TextPosition.CENTER, "dungeonIconPlate00");
                dungeonIconName[count].setDrawFlag(true);
                dungeonIconName[count].setExtendOffset(1.01f);
                count++;
            }
        }

        //ここからアイコンの番号
        Paint dungeonIconNumberPaint = new Paint();
        dungeonIconNumberPaint.setTextSize(30);
        dungeonIconNumberPaint.setColor(Color.WHITE);
        dungeonIconNumberPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        dungeonIconNumberPaint.setStrokeWidth(1.5f);

        count = 0;
        dungeonIconNumber = new WindowTextPlate[size];
        for (int i = 0; i < dungeonIconNumber.length; i++) {
            if (dungeonNumber.get(i) != null) {
                if (dungeonNumber.get(i) != 0) {
                    int centerX = x.get(i) - 115;
                    int centerY = y.get(i) + 105 - 10;

                    int width = 40;
                    int height = 40;
                    dungeonIconNumber[count] = new WindowTextPlate(graphic, new int[]{centerX - width / 2, centerY - height / 2, centerX + width / 2, centerY + height / 2}, String.valueOf(dungeonNumber.get(i)), dungeonIconNumberPaint, WindowTextPlate.TextPosition.CENTER, "dungeonIconPlate00");
                    dungeonIconNumber[count].setDrawFlag(true);
                    dungeonIconNumber[count].setTextOffset(4, -1);
                    dungeonIconNumber[count].setExtendOffset(1.01f);
                    count++;
                }
            }
        }



    }

    public void mapIconPlateListUpdate() {
        int size = database.getSize(DUNGEON_SELECT_BUTTON_TABLE_NAME);
        dungeonName = database.getString(DUNGEON_SELECT_BUTTON_TABLE_NAME, "name");
        List<String> imageName = database.getString(DUNGEON_SELECT_BUTTON_TABLE_NAME, "image_name");
        x = database.getInt(DUNGEON_SELECT_BUTTON_TABLE_NAME, "x");
        y = database.getInt(DUNGEON_SELECT_BUTTON_TABLE_NAME, "y");
        List<Integer> scale = database.getInt(DUNGEON_SELECT_BUTTON_TABLE_NAME, "scale");
        List<Integer> scale_feed = database.getInt(DUNGEON_SELECT_BUTTON_TABLE_NAME, "scale_feed");
        event = database.getString(DUNGEON_SELECT_BUTTON_TABLE_NAME, "event");


        MapIconPlate mapIconPlates[] = mapIconPlateGroup.getPlates();
        boolean alphaFlag, geoEnterFlag;
        int clear = 0;
        for (int i = 0; i < mapIconPlates.length; i++) {
            switch (mapIconPlates[i].getMapIconName()) {
                case "Forest":
                    clear = 1;//mapStatus.getMapClearStatus(DUNGEON_KIND.FOREST.ordinal());
                    geoEnterFlag = (mapStatus.getMapClearStatus(DUNGEON_KIND.FOREST.ordinal()) == 1 || (playerStatus.getClearCount() > 0));
                    break;
                case "Lava":
                    clear = mapStatus.getMapClearStatus(DUNGEON_KIND.FOREST.ordinal());
                    geoEnterFlag = (mapStatus.getMapClearStatus(DUNGEON_KIND.LAVA.ordinal()) == 1 || (playerStatus.getClearCount() > 0));
                    break;
                case "Sea":
                    clear = mapStatus.getMapClearStatus(DUNGEON_KIND.LAVA.ordinal());
                    geoEnterFlag = (mapStatus.getMapClearStatus(DUNGEON_KIND.SEA.ordinal()) == 1 || (playerStatus.getClearCount() > 0));
                    break;
                case "Chess":
                    clear = mapStatus.getMapClearStatus(DUNGEON_KIND.SEA.ordinal());
                    geoEnterFlag = (mapStatus.getMapClearStatus(DUNGEON_KIND.SEA.ordinal()) == 1 || (playerStatus.getClearCount() > 0));
                    break;
                case "Swamp":
                    clear = mapStatus.getMapClearStatus(DUNGEON_KIND.CHESS.ordinal());
                    geoEnterFlag = (mapStatus.getMapClearStatus(DUNGEON_KIND.CHESS.ordinal()) == 1 || (playerStatus.getClearCount() > 0));
                    break;
                case "Haunted":
                    clear = mapStatus.getMapClearStatus(DUNGEON_KIND.SWAMP.ordinal());
                    geoEnterFlag = (mapStatus.getMapClearStatus(DUNGEON_KIND.SWAMP.ordinal()) == 1 || (playerStatus.getClearCount() > 0));
                    break;
                case "Dragon":
                    clear = mapStatus.getMapClearStatus(DUNGEON_KIND.HAUNTED.ordinal());
                    geoEnterFlag = (mapStatus.getMapClearStatus(DUNGEON_KIND.HAUNTED.ordinal()) == 1 || (playerStatus.getClearCount() > 0));
                    break;
                case "Maoh":
                    clear = 1;
                    geoEnterFlag = true;
                    break;
                default:
                    clear = 1;
                    geoEnterFlag = true;
                    break;
            }
            alphaFlag = (clear != 1) && (playerStatus.getNowClearCount() == playerStatus.getClearCount());

            mapIconPlates[i].setImageContext(
                    imageName.get(i),x.get(i), y.get(i), scale.get(i), scale.get(i), scale_feed.get(i), scale_feed.get(i), alphaFlag
            );
            mapIconPlates[i].setEnterFlag(!alphaFlag);
            mapIconPlates[i].setGeoEnterFlag(geoEnterFlag);
        }
    }

    private void initDungeonEnterSelectButton(){
        Paint textPaint = new Paint();
        textPaint.setTextSize(SELECT_WINDOW_PLATE.BUTTON_TEXT_SIZE);
        textPaint.setARGB(255,255,255,255);

        dungeonEnterSelectButtonGroup = new PlateGroup<BoxImageTextPlate>(
                new BoxImageTextPlate[]{
                        new BoxImageTextPlate(
                                graphic, userInterface,
                                Constants.Touch.TouchWay.UP_MOMENT,
                                Constants.Touch.TouchWay.MOVE,
                                new int[]{SELECT_WINDOW.YES_LEFT, SELECT_WINDOW.YES_UP, SELECT_WINDOW.YES_RIGHT, SELECT_WINDOW.YES_BOTTOM},
                                "侵入する",
                                textPaint
                        ),
                        new BoxImageTextPlate(
                                graphic, userInterface,
                                Constants.Touch.TouchWay.UP_MOMENT,
                                Constants.Touch.TouchWay.MOVE,
                                new int[]{SELECT_WINDOW.NO_LEFT, SELECT_WINDOW.NO_UP, SELECT_WINDOW.NO_RIGHT, SELECT_WINDOW.NO_BOTTOM},
                                "やめる",
                                textPaint
                        )
                }
        );

        dungeonEnterSelectButtonGroup.setUpdateFlag(false);
        dungeonEnterSelectButtonGroup.setDrawFlag(false);
    }

    private void initMaohEnterSelectButton(){
        Paint textPaint = new Paint();
        textPaint.setTextSize(SELECT_WINDOW_PLATE.BUTTON_TEXT_SIZE);
        textPaint.setARGB(255,255,255,255);

        maohEnterSelectButtonGroup = new PlateGroup<BoxImageTextPlate>(
                new BoxImageTextPlate[]{
                        new BoxImageTextPlate(
                                graphic, userInterface,
                                Constants.Touch.TouchWay.UP_MOMENT,
                                Constants.Touch.TouchWay.MOVE,
                                new int[]{SELECT_WINDOW.YES_LEFT, SELECT_WINDOW.YES_UP, SELECT_WINDOW.YES_RIGHT, SELECT_WINDOW.YES_BOTTOM},
                                "魔王と戦う",
                                textPaint
                        ),
                        new BoxImageTextPlate(
                                graphic, userInterface,
                                Constants.Touch.TouchWay.UP_MOMENT,
                                Constants.Touch.TouchWay.MOVE,
                                new int[]{SELECT_WINDOW.NO_LEFT, SELECT_WINDOW.NO_UP, SELECT_WINDOW.NO_RIGHT, SELECT_WINDOW.NO_BOTTOM},
                                "やめる",
                                textPaint
                        )
                }
        );
        maohEnterSelectButtonGroup.setUpdateFlag(false);
        maohEnterSelectButtonGroup.setDrawFlag(false);
    }


    PlateGroup<CircleImagePlate> loopCountSelectButtonGroup;
    private void initLoopCountSelectButton(){
        float tempScale = 2.0f;

        loopCountSelectButtonGroup = new PlateGroup<CircleImagePlate>(
                new CircleImagePlate[]{
                        new CircleImagePlate(
                                graphic, userInterface,
                                Constants.Touch.TouchWay.UP_MOMENT,
                                Constants.Touch.TouchWay.MOVE,
                                new int[]{ LOOP_WINDOW.MENOS_X, LOOP_WINDOW.MENOS_Y, LOOP_WINDOW.MENOS_R },
                                graphic.makeImageContext(graphic.searchBitmap("矢印左"), LOOP_WINDOW.MENOS_X, LOOP_WINDOW.MENOS_Y, tempScale, tempScale, 0.0f, 255, false),
                                graphic.makeImageContext(graphic.searchBitmap("矢印左"), LOOP_WINDOW.MENOS_X - 30, LOOP_WINDOW.MENOS_Y, tempScale, tempScale, 0.0f, 255, false)
                        ) {
                            @Override
                            public void callBackEvent() {
                                //-ボタンが押された時の処理
                                soundAdmin.play("enter00");
                                playerStatus.subNowClearCountLoop();
                                loopCountWindowPlateUpdate();
                                mapIconPlateListUpdate();
                            }
                        },
                        new CircleImagePlate(
                                graphic, userInterface,
                                Constants.Touch.TouchWay.UP_MOMENT,
                                Constants.Touch.TouchWay.MOVE,
                                new int[]{ LOOP_WINDOW.PLUS_X, LOOP_WINDOW.PLUS_Y, LOOP_WINDOW.PLUS_R },
                                graphic.makeImageContext(graphic.searchBitmap("矢印右"), LOOP_WINDOW.PLUS_X, LOOP_WINDOW.PLUS_Y, tempScale, tempScale, 0.0f, 245, false),
                                graphic.makeImageContext(graphic.searchBitmap("矢印右"), LOOP_WINDOW.PLUS_X + 30, LOOP_WINDOW.PLUS_Y, tempScale, tempScale, 0.0f, 255, false)

                        ) {
                            @Override
                            public void callBackEvent() {
                                //-ボタンが押された時の処理
                                soundAdmin.play("enter00");
                                playerStatus.addNowClearCountLoop();
                                loopCountWindowPlateUpdate();
                                mapIconPlateListUpdate();
                            }
                        }
                }
        );
        loopCountSelectButtonGroup.setUpdateFlag(false);
        loopCountSelectButtonGroup.setDrawFlag(false);
    }

    /*
    PlateGroup<BoxImageTextPlate> loopCountSelectButtonGroup;
    private void initLoopCountSelectButton(){
        Paint textPaint = new Paint();
        textPaint.setTextSize(LOOP_WINDOW.TEXT_SIZE);
        textPaint.setARGB(255,255,255,255);

        loopCountSelectButtonGroup = new PlateGroup<BoxImageTextPlate>(
                new BoxImageTextPlate[]{
                        new BoxImageTextPlate(
                                graphic, userInterface,
                                Constants.Touch.TouchWay.UP_MOMENT,
                                Constants.Touch.TouchWay.MOVE,
                                new int[]{ LOOP_WINDOW.MENOS_LEFT, LOOP_WINDOW.MENOS_UP, LOOP_WINDOW.MENOS_RIGHT, LOOP_WINDOW.MENOS_BOTTOM },
                                "-",
                                textPaint
                        ) {
                            @Override
                            public void callBackEvent() {
                                //-ボタンが押された時の処理
                                soundAdmin.play("enter00");
                                playerStatus.subNowClearCountLoop();
                                loopCountTextBoxUpdate();
                                mapIconPlateListUpdate();
                            }
                        },
                        new BoxImageTextPlate(
                                graphic, userInterface,
                                Constants.Touch.TouchWay.UP_MOMENT,
                                Constants.Touch.TouchWay.MOVE,
                                new int[]{ LOOP_WINDOW.PLUS_LEFT, LOOP_WINDOW.PLUS_UP, LOOP_WINDOW.PLUS_RIGHT, LOOP_WINDOW.PLUS_BOTTOM },
                                "+",
                                textPaint
                        ) {
                            @Override
                            public void callBackEvent() {
                                //-ボタンが押された時の処理
                                soundAdmin.play("enter00");
                                playerStatus.addNowClearCountLoop();
                                loopCountTextBoxUpdate();
                                mapIconPlateListUpdate();
                            }
                        }
                }
        );
        loopCountSelectButtonGroup.setUpdateFlag(false);
        loopCountSelectButtonGroup.setDrawFlag(false);
    }
    */

    private void initTextBox() {
        enterTextBoxID = textBoxAdmin.createTextBox(SELECT_WINDOW.MESS_LEFT, SELECT_WINDOW.MESS_UP, SELECT_WINDOW.MESS_RIGHT, SELECT_WINDOW.MESS_BOTTOM, SELECT_WINDOW.MESS_ROW);
        textBoxAdmin.setTextBoxUpdateTextByTouching(enterTextBoxID, false);
        textBoxAdmin.setTextBoxExists(enterTextBoxID, false);
        enterTextPaint = new Paint();
        enterTextPaint.setTextSize(SELECT_WINDOW.TEXT_SIZE);
        enterTextPaint.setColor(Color.WHITE);


        //loopCountTextBoxID = textBoxAdmin.createTextBox(LOOP_WINDOW.COUNT_LEFT, LOOP_WINDOW.COUNT_UP, LOOP_WINDOW.COUNT_RIGHT, LOOP_WINDOW.COUNT_BOTTOM, LOOP_WINDOW.MESS_ROW);
        //textBoxAdmin.setTextBoxUpdateTextByTouching(loopCountTextBoxID, false);
        //textBoxAdmin.setTextBoxExists(loopCountTextBoxID, false);
        //loopCountTextPaint = new Paint();
        //loopCountTextPaint.setTextSize(LOOP_WINDOW.TEXT_SIZE);
        //loopCountTextPaint.setColor(Color.WHITE);
    }

    //***** draw関係 *****
    public void draw() {
        // ** Buttonの表示
        mapIconPlateGroup.draw();

        for (int i = 0; i < dungeonIconName.length; i++) {
            if (dungeonIconName[i] != null) {
                dungeonIconName[i].draw();
            }
        }
        for (int i = 0; i < dungeonIconNumber.length; i++) {
            if (dungeonIconNumber[i] != null) {
                dungeonIconNumber[i].draw();
            }
        }


        dungeonEnterSelectButtonGroup.draw();
        //menuButtonGroup.draw();

        maohEnterSelectButtonGroup.draw();
        loopCountSelectButtonGroup.draw();

        dungeonEnterNamePlate.draw();
        loopCountWindowPlate.draw();
        dungeonNotEnterPlate.draw();

        tutorialButtonGroup.draw();

        OkButtonGroup.draw();
    }

    public void initWindow() {
        dungeonEnterNamePlate = new WindowTextPlate(graphic, new int[]{SELECT_WINDOW_PLATE.MESS_LEFT, SELECT_WINDOW_PLATE.MESS_UP, SELECT_WINDOW_PLATE.MESS_RIGHT, SELECT_WINDOW_PLATE.MESS_BOTTOM});
        dungeonEnterTextPaint = new Paint();
        dungeonEnterTextPaint.setTextSize(100);
        dungeonEnterTextPaint.setStrokeWidth(20);
        dungeonEnterTextPaint.setColor(Color.WHITE);

        loopCountWindowPlate = new WindowTextPlate(graphic, new int[]{LOOP_WINDOW.COUNT_LEFT, LOOP_WINDOW.COUNT_UP, LOOP_WINDOW.COUNT_RIGHT, LOOP_WINDOW.COUNT_BOTTOM});
        loopCountTextPaint = new Paint();
        loopCountTextPaint.setTextSize(LOOP_WINDOW.TEXT_SIZE);
        loopCountTextPaint.setColor(Color.WHITE);

        dungeonNotEnterPlate = new WindowTextPlate(graphic, new int[]{SELECT_WINDOW_PLATE.MESS_LEFT - 50, SELECT_WINDOW_PLATE.MESS_UP- 25, SELECT_WINDOW_PLATE.MESS_LEFT + 250, SELECT_WINDOW_PLATE.MESS_UP+ 75 }, "baseButton01");
        dungeonNotEnterPaint = new Paint();
        dungeonNotEnterPaint.setTextSize(60);
        dungeonNotEnterPaint.setColor(Color.WHITE);
    }


    //***** update関係 *****
    public void update() {
        /*
        if (selectMode == SELECT_MODE.GEOMAP_SELECT) {
            mapIconPlateCheckGeo();
        }
        if (selectMode == SELECT_MODE.DUNGEON_SELECT) {
            mapIconPlateCheck();
            if (enterSelectFlag) {
                dungeonEnterSelectButtonCheck();
            }
        }
        */


        //TODO いな依頼:同一フレームの同時タッチ問題→PlatePlateにおけるフラグ判定を予約タイプに変更する(set_falseした場合に実際にfalseになるのはupdate()の最後にする。)ことで解決できる。
        dungeonEnterSelectButtonCheck();
        dungeonEnterSelectButtonGroup.update();

        maohEnterSelectButtonCheck();
        maohEnterSelectButtonGroup.update();

        mapIconPlateCheck();
        mapIconPlateGroup.update();

        loopCountSelectButtonGroup.update();
        OkButtonGroup.update();

        tutorialButtonGroup.update();

        //modeSelectButtonCheck();
        //menuButtonGroup.update();

        if (initUIsFlag) {
            initUIs();
            initUIsFlag = false;
        }

    }


    //注 : 紛らわしいが、DungeonSelectButtonはGeoMapSelectとDungeonSelectとで共通になっている
    public void mapIconPlateCheck() {
        int buttonID = mapIconPlateGroup.getTouchContentNum();
        if (buttonID != -1 ) {
            //この間、マップアイコンなどの操作を受け付けない
            //menuButtonGroup.setUpdateFlag(false);
            mapIconPlateGroup.setUpdateFlag(false);

            focusDungeonButtonID = buttonID;
            //TODO plateGroupの内部のT型配列を返す関数が欲しい。eventの確認のため
            //if mapIconPlateGroup.
            //ボタンに登録されているイベント名を参照して、それそれの場合の結果を返す
            if (event.get(focusDungeonButtonID).equals("dungeon")) {
                if (worldModeAdmin.getMode() == WORLD_MODE.DUNGEON_SELECT) {
                    soundAdmin.play("enter00");
                    if (dungeonEnterCheck()) {
                        //enterTextBoxUpdateDungeon();
                        dungeonEnterNamePlateUpdate();
                        dungeonEnterNamePlate.setDrawFlag(true);//ダンジョン名表示
                        dungeonEnterSelectButtonGroup.setUpdateFlag(true);
                        dungeonEnterSelectButtonGroup.setDrawFlag(true);
                    };
                }
                if (worldModeAdmin.getMode() == WORLD_MODE.GEO_MAP_SELECT) {
                    if (mapIconPlateGroup.getPlates(focusDungeonButtonID).getGeoEnterFlag()) {
                        soundAdmin.play("enter00");
                        geoSlotAdminManager.setActiveGeoSlotAdmin(dungeonName.get(buttonID));
                        worldModeAdmin.setMode(WORLD_MODE.GEO_MAP_INIT);
                        geoSlotAdminManager.setMode(GeoSlotAdminManager.MODE.WORLD_NORMAL);
                        initUIsFlag = true;
                    } else {
                        soundAdmin.play("enter00");
                        geoSlotAdminManager.setActiveGeoSlotAdmin(dungeonName.get(buttonID));
                        worldModeAdmin.setMode(WORLD_MODE.GEO_MAP_INIT);
                        geoSlotAdminManager.setMode(GeoSlotAdminManager.MODE.WORLD_SEE_ONLY);
                        initUIsFlag = true;
                    }
                }
            }

            if (event.get(focusDungeonButtonID).equals("shop")) {
                soundAdmin.play("enter00");
                worldModeAdmin.setMode(Constants.GAMESYSTEN_MODE.WORLD_MODE.SHOP_INIT);
                initUIsFlag = true;

            }
            if (event.get(focusDungeonButtonID).equals("present")) {
                soundAdmin.play("enter00");
                worldModeAdmin.setMode(Constants.GAMESYSTEN_MODE.WORLD_MODE.PRESENT_INIT);
                initUIsFlag = true;
            }
            if (event.get(focusDungeonButtonID).equals("maoh")) {
                soundAdmin.play("enter00");
                if (worldModeAdmin.getMode() == WORLD_MODE.DUNGEON_SELECT) {
                    if (dungeonEnterCheck()) {
                        geoSlotAdminManager.calcMaohMenosStatus();
                        //enterTextBoxUpdateMaoh();
                        dungeonEnterNamePlateUpdateMaoh();
                        dungeonEnterNamePlate.setDrawFlag(true);//ダンジョン名表示
                        maohEnterSelectButtonGroup.setUpdateFlag(true);
                        maohEnterSelectButtonGroup.setDrawFlag(true);
                    }
                }
                if (worldModeAdmin.getMode() == WORLD_MODE.GEO_MAP_SELECT) {
                    if (mapIconPlateGroup.getPlates(focusDungeonButtonID).getGeoEnterFlag()) {
                        soundAdmin.play("enter00");
                        geoSlotAdminManager.setActiveGeoSlotAdmin(dungeonName.get(buttonID));
                        worldModeAdmin.setMode(WORLD_MODE.GEO_MAP_INIT);
                        geoSlotAdminManager.setMode(GeoSlotAdminManager.MODE.WORLD_NORMAL);
                        initUIsFlag = true;
                    } else {
                        //soundAdmin.play("cannot_exit_room");
                        soundAdmin.play("enter00");
                        geoSlotAdminManager.setActiveGeoSlotAdmin(dungeonName.get(buttonID));
                        worldModeAdmin.setMode(WORLD_MODE.GEO_MAP_INIT);
                        geoSlotAdminManager.setMode(GeoSlotAdminManager.MODE.WORLD_SEE_ONLY);
                        initUIsFlag = true;
                    }
                }
            }
            if (event.get(focusDungeonButtonID).equals("map")) {
                soundAdmin.play("enter00");
                switchSelectMode();
                initUIsFlag = true;
            }
            if (event.get(focusDungeonButtonID).equals("equip")) {
                soundAdmin.play("enter00");
                initUIsFlag = true;
                worldModeAdmin.setMode(Constants.GAMESYSTEN_MODE.WORLD_MODE.EQUIP_INIT);
            }
            if (event.get(focusDungeonButtonID).equals("option")) {
                soundAdmin.play("enter00");
                initUIsFlag = true;
            }
            if (event.get(focusDungeonButtonID).equals("credit")) {
                soundAdmin.play("enter00");
                initUIsFlag = true;
                worldModeAdmin.setMode(Constants.GAMESYSTEN_MODE.WORLD_MODE.CREDIT);//TODO : 0612先輩、遷移先のMODEを指定
            }
            if (event.get(focusDungeonButtonID).equals("loop")) {
                soundAdmin.play("enter00");
                //ループ回数セッティング
                loopCountWindowPlateUpdate();
                loopCountWindowPlate.setDrawFlag(true);
                loopCountSelectButtonGroup.setDrawFlag(true);
                loopCountSelectButtonGroup.setUpdateFlag(true);
                OkButtonGroup.setDrawFlag(true);
                OkButtonGroup.setUpdateFlag(true);
                //initUIsFlag = true;
            }
        }
    }

    /*
    public void modeSelectButtonCheck() {
        int buttonID = menuButtonGroup.getTouchContentNum();
        if (buttonID == 0 ) { //Map
            switchSelectMode();
        }
        if (buttonID == 1 ) { //Equip
            initUIs();
            worldModeAdmin.setMode(Constants.GAMESYSTEN_MODE.WORLD_MODE.EQUIP);
        }
    }
    */

    private boolean dungeonEnterCheck() {
        System.out.println("takano geo : " + geoInventry.getInventryNum());
        if (geoInventry.getInventryNum() > playerStatus.getGeoInventryMaxNum()) {
            enterTextBoxUpdateInventryMax(Constants.Item.ITEM_KIND.GEO);
            OkButtonGroup.setUpdateFlag(true);
            OkButtonGroup.setDrawFlag(true);
            return false;
        }
        System.out.println("takano expend : " + expendItemInventry.getInventryNum());
        if (expendItemInventry.getInventryNum() > playerStatus.getExpendInvetryMaxNum()) {
            enterTextBoxUpdateInventryMax(Constants.Item.ITEM_KIND.EXPEND);
            OkButtonGroup.setUpdateFlag(true);
            OkButtonGroup.setDrawFlag(true);
            return false;
        }
        System.out.println("takano equipment : " + equipmentInventry.getInventryNum());
        if (equipmentInventry.getInventryNum() > playerStatus.getEquipmentInventryMaxNum()) {
            enterTextBoxUpdateInventryMax(Constants.Item.ITEM_KIND.EQUIPMENT);
            OkButtonGroup.setUpdateFlag(true);
            OkButtonGroup.setDrawFlag(true);
            return false;
        }

        if (!mapIconPlateGroup.getPlates(focusDungeonButtonID).getEnterFlag()) {
            //enterTextBoxUpdateNotAccept();
            dungeonEnterNamePlateUpdate();
            dungeonEnterNamePlate.setDrawFlag(true);//ダンジョン名表示
            dungeonEnterNamePlateUpdateNotAccept();
            dungeonNotEnterPlate.setDrawFlag(true);
            OkButtonGroup.setUpdateFlag(true);
            OkButtonGroup.setDrawFlag(true);
            return false;
        }
        return true;
    }



    public void dungeonEnterSelectButtonCheck() {
        if (!(dungeonEnterSelectButtonGroup.getUpdateFlag() && worldModeAdmin.getMode() == WORLD_MODE.DUNGEON_SELECT)) {
            return;
        }

        int buttonID = dungeonEnterSelectButtonGroup.getTouchContentNum();
        if (buttonID == 0 ) { //侵入する
            soundAdmin.play("enter00");
            playerStatus.setNowHPMax();
            initUIsFlag = true;
            MapIconPlate tmp = (MapIconPlate)mapIconPlateGroup.getPlate(focusDungeonButtonID);
            String dungeonName = tmp.getMapIconName();
            Constants.DungeonKind.DUNGEON_KIND dungeonKind;

            switch(dungeonName) {
                case "Chess": dungeonKind = Constants.DungeonKind.DUNGEON_KIND.CHESS; break;
                case "Dragon": dungeonKind = Constants.DungeonKind.DUNGEON_KIND.DRAGON; break;
                case "Haunted": dungeonKind = Constants.DungeonKind.DUNGEON_KIND.HAUNTED; break;
                case "Forest": dungeonKind = Constants.DungeonKind.DUNGEON_KIND.FOREST; break;
                case "Lava": dungeonKind = Constants.DungeonKind.DUNGEON_KIND.LAVA; break;
                case "Swamp": dungeonKind = Constants.DungeonKind.DUNGEON_KIND.SWAMP; break;
                case "Sea": dungeonKind = Constants.DungeonKind.DUNGEON_KIND.SEA; break;
                default: dungeonKind = Constants.DungeonKind.DUNGEON_KIND.GOKI; break;
            }

            //activityChange.toDungeonActivity(dungeonKind);
            unitedActivity.getUnitedSurfaceView().toDungeonGameMode(dungeonKind);
        }
        if (buttonID == 1 ) { //やめる
            soundAdmin.play("cancel00");
            initUIsFlag = true;
        }
    }

    public void maohEnterSelectButtonCheck() {
        if (!maohEnterSelectButtonGroup.getUpdateFlag() || !(worldModeAdmin.getMode() == WORLD_MODE.DUNGEON_SELECT)) {
            return;
        }

        int buttonID = maohEnterSelectButtonGroup.getTouchContentNum();
        if (buttonID == 0 ) { //挑戦する
            soundAdmin.play("enter00");
            initUIsFlag = true;

            //activityChange.toDungeonActivity(Constants.DungeonKind.DUNGEON_KIND.MAOH);
            unitedActivity.getUnitedSurfaceView().toDungeonGameMode(Constants.DungeonKind.DUNGEON_KIND.MAOH);

            //dungeon_mode_manage.setMode(Constants.GAMESYSTEN_MODE.DUNGEON_MODE.BUTTLE_INIT);
        }
        if (buttonID == 1 ) { //やめる
            soundAdmin.play("cancel00");
            initUIsFlag = true;
        }
    }

    //TExtBox
    public void enterTextBoxUpdateMaoh() {
        textBoxAdmin.setTextBoxExists(enterTextBoxID, true);
        textBoxAdmin.resetTextBox(enterTextBoxID);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "魔王に挑戦しますか？", enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "\n", enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "現在の討伐回数 : ", enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, String.valueOf(playerStatus.getMaohWinCount()), enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "MOP", enterTextPaint);

        textBoxAdmin.updateText(enterTextBoxID);
    }

    public void enterTextBoxUpdateCountUp() {
        textBoxAdmin.setTextBoxExists(enterTextBoxID, true);
        textBoxAdmin.resetTextBox(enterTextBoxID);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "全ダンジョンを制覇した！", enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "\n", enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "しかし、", enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "戦いはまだ終わらない…　", enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "\n", enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "ループ回数：", enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, String.valueOf(playerStatus.getClearCount()), enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "MOP", enterTextPaint);

        textBoxAdmin.updateText(enterTextBoxID);
    }
    /*
    public void enterTextBoxUpdateDungeon() {
        MapIconPlate tmp = (MapIconPlate)mapIconPlateGroup.getPlate(focusDungeonButtonID);

        textBoxAdmin.setTextBoxExists(enterTextBoxID, true);
        textBoxAdmin.resetTextBox(enterTextBoxID);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "", enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, tmp.getDungeonName(), enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "\n", enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "このダンジョンに入りますか？", enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "MOP", enterTextPaint);

        textBoxAdmin.updateText(enterTextBoxID);
    }
    */

    public void dungeonEnterNamePlateUpdate() {
        MapIconPlate tmp = (MapIconPlate)mapIconPlateGroup.getPlate(focusDungeonButtonID);
        dungeonEnterNamePlate.setText(tmp.getDungeonName(), dungeonEnterTextPaint, WindowTextPlate.TextPosition.CENTER);
    }
    public void dungeonEnterNamePlateUpdateNotAccept() {
        dungeonNotEnterPlate.setText("未開放", dungeonNotEnterPaint, WindowTextPlate.TextPosition.CENTER);
    }

    public void dungeonEnterNamePlateUpdateMaoh() {
        dungeonEnterNamePlate.setText("魔王に挑戦", dungeonEnterTextPaint, WindowTextPlate.TextPosition.CENTER);
    }

/*
    public void enterTextBoxUpdateNotAccept() {
        textBoxAdmin.setTextBoxExists(enterTextBoxID, true);
        textBoxAdmin.resetTextBox(enterTextBoxID);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "このダンジョンにはまだ侵入できません", enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "MOP", enterTextPaint);

        textBoxAdmin.updateText(enterTextBoxID);
    }
*/

    public void enterTextBoxUpdateInventryMax(Constants.Item.ITEM_KIND inventryKind) {
        textBoxAdmin.setTextBoxExists(enterTextBoxID, true);

        textBoxAdmin.bookingDrawText(enterTextBoxID, "インベントリの容量がオーバーしているため侵入できません", enterTextPaint);
        textBoxAdmin.bookingDrawText(enterTextBoxID, "\n", enterTextPaint);
        switch(inventryKind) {
            case GEO:
                textBoxAdmin.bookingDrawText(enterTextBoxID, "ジオオブジェクト : " + geoInventry.getInventryNum() + " / " + playerStatus.getGeoInventryMaxNum(), enterTextPaint);
                break;
            case EQUIPMENT:
                textBoxAdmin.bookingDrawText(enterTextBoxID,"装備品 : " +  equipmentInventry.getInventryNum() + " / " + playerStatus.getEquipmentInventryMaxNum(), enterTextPaint);
                break;
            case EXPEND:
                textBoxAdmin.bookingDrawText(enterTextBoxID,"消費アイテム : " +  expendItemInventry.getInventryNum() + " / " + playerStatus.getExpendInvetryMaxNum(), enterTextPaint);
                break;
        }
        textBoxAdmin.bookingDrawText(enterTextBoxID, "MOP", loopCountTextPaint);


        textBoxAdmin.updateText(enterTextBoxID);
    }

    public void loopCountWindowPlateUpdate() {
        loopCountWindowPlate.setText("" + playerStatus.getNowClearCount() + " / " + playerStatus.getClearCount(), loopCountTextPaint, WindowTextPlate.TextPosition.CENTER);
    }

    /*
    public void loopCountTextBoxUpdate() {
        textBoxAdmin.setTextBoxExists(loopCountTextBoxID, true);

        textBoxAdmin.bookingDrawText(loopCountTextBoxID, "\n", loopCountTextPaint);
        textBoxAdmin.bookingDrawText(loopCountTextBoxID, "" + playerStatus.getNowClearCount() + " / " + playerStatus.getClearCount(), loopCountTextPaint);
        textBoxAdmin.bookingDrawText(loopCountTextBoxID, "MOP", loopCountTextPaint);

        textBoxAdmin.updateText(loopCountTextBoxID);
    }
    */

    private void initUIs() {
        dungeonEnterSelectButtonGroup.setUpdateFlag(false);
        dungeonEnterSelectButtonGroup.setDrawFlag(false);
        maohEnterSelectButtonGroup.setUpdateFlag(false);
        maohEnterSelectButtonGroup.setDrawFlag(false);
        loopCountSelectButtonGroup.setUpdateFlag(false);
        loopCountSelectButtonGroup.setDrawFlag(false);
        OkButtonGroup.setUpdateFlag(false);
        OkButtonGroup.setDrawFlag(false);
        textBoxAdmin.setTextBoxExists(enterTextBoxID, false);
        //textBoxAdmin.setTextBoxExists(loopCountTextBoxID, false);
        dungeonEnterNamePlate.setDrawFlag(false);
        loopCountWindowPlate.setDrawFlag(false);
        dungeonNotEnterPlate.setDrawFlag(false);

        //menuButtonGroup.setUpdateFlag(true);
        mapIconPlateGroup.setUpdateFlag(true);

    }

    PlateGroup<BoxImageTextPlate> OkButtonGroup;
    private void initOkButton() {
        Paint textPaint = new Paint();
        textPaint.setTextSize(POPUP_WINDOW.BUTTON_TEXT_SIZE);
        textPaint.setARGB(255, 255, 255, 255);

        OkButtonGroup = new PlateGroup<BoxImageTextPlate>(
                new BoxImageTextPlate[]{
                        new BoxImageTextPlate(
                                graphic, userInterface, Constants.Touch.TouchWay.UP_MOMENT, Constants.Touch.TouchWay.MOVE, new int[]{POPUP_WINDOW.OK_LEFT, POPUP_WINDOW.OK_UP, POPUP_WINDOW.OK_RIGHT, POPUP_WINDOW.OK_BOTTOM}, "OK", textPaint
                        ) {
                            @Override
                            public void callBackEvent() {
                                //OKが押された時の処理
                                soundAdmin.play("enter00");
                                initUIs();
                            }
                        }
                });
        OkButtonGroup.setUpdateFlag(false);
        OkButtonGroup.setDrawFlag(false);
    }

    public void release() {
        System.out.println("takanoRelease : DungeonSelectManager");
        loopCountTextPaint = null;
        enterTextPaint = null;
        if (dungeonName != null) {
            dungeonName.clear();
            dungeonName = null;
        }
        if (dungeonNameExpress != null) {
            dungeonNameExpress.clear();
            dungeonNameExpress = null;
        }
        if (event != null) {
            event.clear();
            event = null;
        }
        if (mapIconPlateGroup != null) {
            mapIconPlateGroup.release();
            mapIconPlateGroup = null;
        }
        if (dungeonEnterSelectButtonGroup !=null ) {
            dungeonEnterSelectButtonGroup.release();
            dungeonEnterSelectButtonGroup = null;
        }
        if (maohEnterSelectButtonGroup != null) {
            maohEnterSelectButtonGroup.release();
            maohEnterSelectButtonGroup = null;
        }
        if (loopCountSelectButtonGroup != null) {
            loopCountSelectButtonGroup.release();
            loopCountSelectButtonGroup = null;
        }
        if (OkButtonGroup != null) {
            OkButtonGroup.release();
            OkButtonGroup = null;
        }

        if (loopCountWindowPlate != null) {
            loopCountWindowPlate.release();
            loopCountWindowPlate = null;
        }
        if (dungeonEnterNamePlate != null) {
            dungeonEnterNamePlate.release();
            dungeonEnterNamePlate = null;
        }
        if (tutorialButtonGroup != null) {
            tutorialButtonGroup.release();
            tutorialButtonGroup = null;
        }

    }


}

        /*
        int size = 2;

        List<BoxTextPlate> dungeonEnterSelectButtonList = new ArrayList<BoxTextPlate>();
        List<Integer> x1 = new ArrayList<Integer>();
        List<Integer> y1 = new ArrayList<Integer>();
        List<Integer> x2 = new ArrayList<Integer>();
        List<Integer> y2 = new ArrayList<Integer>();
        List<String> text = new ArrayList<String>();

        text.add("侵入する");
        text.add("やめる");

        x1.add(500);
        y1.add(600);
        x2.add(700);
        y2.add(700);

        x1.add(800);
        y1.add(600);
        x2.add(1000);
        y2.add(700);

        Paint textPaint = new Paint();
        textPaint.setTextSize(50);
        textPaint.setARGB(255,255,0,0);

        //インスタンス化
        for (int i = 0; i < size; i++) {

            dungeonEnterSelectButtonList.add(new BoxTextPlate(
                    graphic, userInterface,
                    new Paint(),
                    Constants.Touch.TouchWay.UP_MOMENT,
                    Constants.Touch.TouchWay.MOVE,
                    new int[] { x1.get(i), y1.get(i), x2.get(i), y2.get(i) },
                    text.get(i),
                    textPaint
            ));
        }
        BoxTextPlate[] dungeonEnterSelectButton = new BoxTextPlate[dungeonEnterSelectButtonList.size()];
        dungeonEnterSelectButtonGroup = new PlateGroup<BoxTextPlate>(dungeonEnterSelectButtonList.toArray(dungeonEnterSelectButton));
        */
