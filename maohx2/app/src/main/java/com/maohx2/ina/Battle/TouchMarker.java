package com.maohx2.ina.Battle;

import android.graphics.Paint;

import com.maohx2.ina.Draw.Graphic;

/**
 * Created by ina on 2018/03/09.
 */

public class TouchMarker {

    boolean exist;
    int position_x;
    int position_y;
    int radius;
    double decay_rate;
    int damage;

    Graphic graphic;
    Paint paint;

    TouchMarker(Graphic _graphic){
        graphic = _graphic;

        exist = false;
        position_x = 0;
        position_y = 0;
        radius = 0;
        decay_rate = 0;

        paint = new Paint();
        paint.setARGB(255,0,0,0);
        paint.setTextSize(35);


    }

    void generate(int _position_x, int _position_y, int _radius, int _damage, double _decay_rate){
        exist = true;

        position_x = _position_x;
        position_y = _position_y;
        radius = _radius;
        damage = _damage;
        decay_rate = _decay_rate;
    }

    void update(){

        //by kmhanko マーカーが消滅しないバグ
        if(radius*decay_rate < 1.0f){
            clear();
        } else {
            radius = (int) (radius * decay_rate);
        }
    }


    void draw(){

        if(exist == true){
            graphic.bookingDrawCircle(position_x,position_y,radius);
            //graphic.bookingDrawCircle(position_x,position_y,radius,paint);
        }
    }

    void clear(){
        exist = false;
        position_x = 0;
        position_y = 0;
        radius = 0;
        decay_rate = 0;
    }

    boolean isExist(){
        return exist;
    }

    public int getPositionX() {
        return position_x;
    }
    public int getPositionY() {
        return position_y;
    }
    public int getRadius() {
        return radius;
    }
    public int getDamage(){return damage;}

    //TODO 配備
    public void release() {
        System.out.println("takanoRelease : TouchMarker");
        paint = null;
    }

}
