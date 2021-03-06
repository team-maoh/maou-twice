package com.maohx2.ina;

import android.app.Activity;
import android.content.Intent;

/**
 * Created by ina on 2018/04/01.
 */

public class ActivityChange {

    Activity currentActivity;
    BaseSurfaceView currentSurfaceView;

    ActivityChange(BaseSurfaceView _currentSurfaceView, Activity _currentActivity){

        currentSurfaceView = _currentSurfaceView;
        currentActivity = _currentActivity;
    }


    public void toStartActivity() {
        currentSurfaceView.stopThread();
        Intent intent = new Intent(currentActivity, StartActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        currentActivity.finish();
        currentActivity.startActivity(intent);
    }

    public void toWorldActivity() {
        currentSurfaceView.stopThread();
        Intent intent = new Intent(currentActivity, WorldActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        currentActivity.finish();
        currentActivity.startActivity(intent);
    }

    public void toDungeonActivity(Constants.DungeonKind.DUNGEON_KIND dungeon_kind) {
        //thread.interrupt();
        currentSurfaceView.stopThread();
        Intent intent = new Intent(currentActivity, DungeonActivity.class);
        intent.putExtra("DungeonKind", dungeon_kind);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        currentActivity.finish();
        currentActivity.startActivity(intent);
    }
}

//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
/*
ガーベッジコレクションでは、不必要と思われるメモリを解放する。
解放する判断は参照がnullとなって居るか。よってnullにしる。
上のやつを読んでdestroyでその処理をするとか。

そもそもアクティビティチェンジすると、
A→B→C→B
時ても、新しくBが生成(少なくともメンバのクラスのインスタンスは別のメモリ領域に保存)されて居るように見える
(参照が異なって居るため)
これではメモリを食うばかり。FLAG_ACTIVITY_CLEAR_TOPで思い切って現在のアクティビティ以外を削除して、
削除する時そのメンバを全てnullにしてやる(この場合毎回activityを作るので毎回newしたりDBから呼んだりはする)必要があるか

 */