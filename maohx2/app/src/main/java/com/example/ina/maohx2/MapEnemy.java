package com.example.ina.maohx2;

import android.graphics.Bitmap;
import android.view.SurfaceHolder;

import java.util.Random;

/**
 * Created by Fuusya on 2017/09/11.
 */

public class MapEnemy extends MapUnit {

    MapUnit player;
    int STEP;
    double dst_x = 100, dst_y = 100;

    @Override
    public void init(SurfaceHolder holder, Bitmap draw_object, MapObjectAdmin map_object_admin) {
        super.init(holder, draw_object, map_object_admin);
        STEP = 10;
        Random random = new Random();
        x = random.nextDouble() * 1000;
        y = random.nextDouble() * 600;
    }

    @Override
    public void update(double touch_x, double touch_y, int touch_state) {

        player = map_object_admin.getUnit(0);

        dst_x = player.getMapX();//getMapX(),getMapY()はMapObject内部に記述されている
        dst_y = player.getMapY();

        dst_steps = (int) (Math.pow(Math.pow(dst_x - x, 2.0) + Math.pow(dst_y - y, 2.0), 0.5) / (double) STEP);
        dst_steps++;//dst_steps = 0 のときゼロ除算が発生するので

        dx = (int) ((dst_x - x) / dst_steps);
        dy = (int) ((dst_y - y) / dst_steps);
        x += dx;
        y += dy;
    }
}