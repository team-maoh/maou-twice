package com.maohx2.ina.UI;

import android.graphics.PointF;

import com.maohx2.ina.GlobalConstants;

import static com.maohx2.ina.Constants.Touch.*;

/**
 * Created by ina on 2017/10/01.
 */

public class UserInterface {


    double touch_x;
    double touch_y;
    TouchState touch_state;
    int circle_touch_index_num;
    int box_touch_index_num;
    double circle_center_list_x[] = new double[100];
    double circle_center_list_y[] = new double[100];
    double circle_radius_list[] = new double[100];
    double box_left_list[] =  new double[100];
    double box_top_list[] =  new double[100];
    double box_right_list[] =  new double[100];
    double box_down_list[] =  new double[100];

    int item_id;

    final int DISP_X;
    final int DISP_Y;
    final PointF NORMARIZED_DISP_RATE;
    final PointF DISP_NORMARIZED_RATE;


    public UserInterface(GlobalConstants global_constants){
        DISP_X = global_constants.DISP_X;
        DISP_Y = global_constants.DISP_Y;
        NORMARIZED_DISP_RATE = global_constants.NORMARIZED_DISP_RATE;
        DISP_NORMARIZED_RATE = global_constants.DISP_NORMARIZED_RATE;
    }


    public void init() {

        touch_x = 0;
        touch_y = 0;
        touch_state = TouchState.AWAY;

    }


    public void updateTouchState(double _touch_x, double _touch_y, TouchState _touch_state) {

        touch_x = _touch_x;
        touch_y = _touch_y;

        //DOWN,UP判定を1フレームのみにするためのもの
        if (_touch_state == TouchState.DOWN) {
            if (touch_state == TouchState.AWAY) {
                touch_state = TouchState.DOWN;
            } else if (touch_state == TouchState.DOWN) {
                touch_state = TouchState.DOWN_MOVE;
            }


        } else if (_touch_state == TouchState.MOVE) {
            if (touch_state == TouchState.DOWN_MOVE) {
                touch_state = TouchState.MOVE;
            }


        } else if (_touch_state == TouchState.UP) {
            if ((touch_state == TouchState.DOWN) || (touch_state == TouchState.DOWN_MOVE) || (touch_state == TouchState.MOVE)) {
                touch_state = TouchState.UP;
            } else if (touch_state == TouchState.UP) {
                touch_state = TouchState.AWAY;
            }
        }

    }

    public double getTouchX() {
        return DISP_NORMARIZED_RATE.x*touch_x;
    }

    public double getTouchY() { return DISP_NORMARIZED_RATE.y*touch_y; }

    public TouchState getTouchState() {
        return touch_state;
    }

    public int setCircleTouchUI(double center_x, double center_y, double ciclre_radius) {
        circle_center_list_x[circle_touch_index_num] = NORMARIZED_DISP_RATE.x*center_x;
        circle_center_list_y[circle_touch_index_num] = NORMARIZED_DISP_RATE.y*center_y;
        circle_radius_list[circle_touch_index_num] = ciclre_radius;//todo:ここヤバイ(倍率設定をのちにしなければならない)
        int id = 314000 + circle_touch_index_num;
        circle_touch_index_num++;
        return id;
    }

    public int setBoxTouchUI(double left, double top, double right, double down) {
        box_left_list[box_touch_index_num] = NORMARIZED_DISP_RATE.x*left;
        box_top_list[box_touch_index_num] = NORMARIZED_DISP_RATE.y*top;
        box_right_list[box_touch_index_num] = NORMARIZED_DISP_RATE.x*right;
        box_down_list[box_touch_index_num] = NORMARIZED_DISP_RATE.y*down;

        int id = 8010000 + box_touch_index_num;
        box_touch_index_num++;
        return id;
    }

    public boolean checkUI(int id, TouchWay touch_way) {
        int index_num = id % 1000;

        if ((id - index_num) / 1000 == 314) {
            switch (touch_way) {
                case DOWN_MOMENT:
                    if ((touch_state == TouchState.DOWN) && (Math.pow(circle_center_list_x[index_num] - touch_x, 2) + Math.pow(circle_center_list_y[index_num] - touch_y, 2) <= Math.pow(circle_radius_list[index_num], 2))) {
                        return true;
                    }
                    break;
                case MOVE:
                    if((touch_state == TouchState.DOWN || touch_state == TouchState.DOWN_MOVE || touch_state == TouchState.MOVE) && (Math.pow(circle_center_list_x[index_num] - touch_x, 2) + Math.pow(circle_center_list_y[index_num] - touch_y, 2) <= Math.pow(circle_radius_list[index_num], 2))){
                        return true;
                    }
                    break;
                case UP_MOMENT:
                    if ((touch_state == TouchState.UP) && (Math.pow(circle_center_list_x[index_num] - touch_x, 2) + Math.pow(circle_center_list_y[index_num] - touch_y, 2) <= Math.pow(circle_radius_list[index_num], 2))) {
                        return true;
                    }
                    break;
            }
        }

        else if ((id - index_num) / 1000 == 8010) {
            switch (touch_way) {
                case DOWN_MOMENT:
                    if((touch_state == TouchState.DOWN) && (touch_x >= box_left_list[index_num] && touch_x <= box_right_list[index_num]) && (touch_y >= box_top_list[index_num] && touch_y < box_down_list[index_num])) {
                        return true;
                    }
                    break;
                case MOVE:
                    if((touch_state == TouchState.DOWN || touch_state == TouchState.DOWN_MOVE || touch_state == TouchState.MOVE) && (touch_x >= box_left_list[index_num] && touch_x <= box_right_list[index_num]) && (touch_y >= box_top_list[index_num] && touch_y < box_down_list[index_num])) {
                    return true;
                }
                    break;
                case UP_MOMENT:
                    if((touch_state == TouchState.UP) && (touch_x >= box_left_list[index_num] && touch_x <= box_right_list[index_num]) && (touch_y >= box_top_list[index_num] && touch_y < box_down_list[index_num])) {
                    return true;
                }
                    break;
            }
        }

        /*
        if(touch_way == TouchWay.DOWN_MOMENT) {
            if (touch_state == TouchState.UP && (id - index_num) / 1000 == 314) {
                if (Math.pow(circle_center_list_x[index_num] - touch_x, 2) + Math.pow(circle_center_list_y[index_num] - touch_y, 2) <= Math.pow(circle_radius_list[index_num], 2)) {
                    return true;
                }
            }
        }


        else if ((id - index_num) / 1000 == 314) {
            if (Math.pow(circle_center_list_x[index_num] - touch_x, 2) + Math.pow(circle_center_list_y[index_num] - touch_y, 2) <= Math.pow(circle_radius_list[index_num], 2)) {
                return true;
            }
        }


        if ((id - index_num) / 1000 == 8010) {
            if((touch_state == TouchState.DOWN || touch_state == TouchState.DOWN_MOVE || touch_state == TouchState.MOVE) && (touch_x >= box_left_list[index_num] && touch_x <= box_right_list[index_num]) && (touch_y >= box_top_list[index_num] && touch_y < box_down_list[index_num])) {
                return true;
            }
        }
        */

        return false;
    }


    public boolean checkCircleTouchUI(double center_x, double center_y, double circle_radius) {

        if (Math.pow(center_x - touch_x, 2) + Math.pow(center_y - touch_y, 2) <= Math.pow(circle_radius, 2)) {
            return true;
        }

        return false;
    }

    public void resetCircleUI() {

        for (int i = 0; i < 100; i++) {
            circle_center_list_x[i] = 0;
            circle_center_list_y[i] = 0;
            circle_radius_list[i] = 0;
        }

        circle_touch_index_num = 0;
    }

    public void resetBoxUI() {

        for (int i = 0; i < 100; i++) {
            box_left_list[i] = 0;
            box_top_list[i] =  0;
            box_right_list[i] = 0;
            box_down_list[i] = 0;
        }

        circle_touch_index_num = 0;
    }

    public void resetUI() {

        resetCircleUI();
        resetBoxUI();
    }


    public int getItemID(){

        return item_id;
    }

    public void setItemID(int _item_id) {
        item_id = _item_id;
    }
}
