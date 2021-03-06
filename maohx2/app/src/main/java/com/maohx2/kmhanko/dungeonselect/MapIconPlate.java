package com.maohx2.kmhanko.dungeonselect;

import com.maohx2.ina.Constants;
import com.maohx2.ina.Draw.Graphic;
import com.maohx2.ina.Draw.ImageContext;
import com.maohx2.ina.Text.CircleImagePlate;
import com.maohx2.ina.UI.UserInterface;

import java.util.List;

/**
 * Created by user on 2018/02/04.
 */

public class MapIconPlate extends CircleImagePlate {

    String mapIconName;
    String event;

    public MapIconPlate(Graphic _graphic, UserInterface _user_interface, Constants.Touch.TouchWay _judge_way, Constants.Touch.TouchWay _feedback_way, int[] _position, ImageContext _default_image_context, ImageContext _feedback_image_context, String _mapIconName, String _event) {
        super(_graphic, _user_interface, _judge_way, _feedback_way, _position, _default_image_context, _feedback_image_context);
        mapIconName = _mapIconName;
        event = _event;
    }

    public String getEvent() {
        return event;
    }
    public String getMapIconName() {
        return mapIconName;
    }

}
