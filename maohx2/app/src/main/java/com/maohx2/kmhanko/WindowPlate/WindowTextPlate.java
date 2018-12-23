package com.maohx2.kmhanko.WindowPlate;

import android.graphics.Paint;
import com.maohx2.ina.Draw.Graphic;

public class WindowTextPlate extends WindowPlate {

    protected boolean exist[] ;
    protected String text;
    protected Paint textPaint;
    protected float textWidth;
    protected float textHeight;
    protected int textX, textY;

    public enum TextPosition {
        CENTER,
        RIGHT,
        LEFT,
        UP,
        DOWN,
        UPRIGHT,
        UPLEFT,
        DOWNLEFT,
        DOWNRIGHT,
    }

    TextPosition textPosition;

    public WindowTextPlate(Graphic _graphic, int[] _position) {
        super(_graphic, _position);
    }

    public WindowTextPlate(Graphic _graphic, int[] _position, String _text, Paint _textPaint, TextPosition _textPosition) {
        super(_graphic, _position);
        setText(_text, _textPaint, _textPosition);
    }

    public WindowTextPlate(Graphic _graphic, int[] _position, String _windowImageName) {
        super(_graphic, _position, _windowImageName);
    }

    public WindowTextPlate(Graphic _graphic, int[] _position, String _text, Paint _textPaint, TextPosition _textPosition, String _windowImageName) {
        super(_graphic, _position, _windowImageName);
        setText(_text, _textPaint, _textPosition);
    }

    @Override
    public void draw() {
        if (!drawFlag) {
            return;
        }
        super.drawWindow();
        this.drawText();
    }

    public void setText(String _text, Paint _textPaint, TextPosition _textPosition) {
        setText(0, _text, _textPaint, _textPosition);
    }

    public void setText(int id, String _text, Paint _textPaint, TextPosition _textPosition) {
        text = _text;
        textPaint = _textPaint;
        textPosition = _textPosition;
        textHeight= textPaint.getTextSize();
        textWidth = textPaint.measureText(text);
        updateTextPosition(id);
    }

    protected void updateTextPosition() {
        updateTextPosition(0);
    }

    protected void updateTextPosition(int id) {
        if (textPosition == TextPosition.UP || textPosition == TextPosition.UPLEFT || textPosition == TextPosition.UPRIGHT) {
            textY = position[1];
        }
        if (textPosition == TextPosition.DOWN || textPosition == TextPosition.DOWNLEFT || textPosition == TextPosition.DOWNRIGHT) {
            textY = (int) (position[1] + height - textHeight);
        }
        if (textPosition == TextPosition.RIGHT || textPosition == TextPosition.UPRIGHT || textPosition == TextPosition.DOWNRIGHT) {
            textX = position[0];
        }
        if (textPosition == TextPosition.LEFT || textPosition == TextPosition.DOWNLEFT || textPosition == TextPosition.UPLEFT) {
            textX = (int) (position[0] + width - textWidth);
        }
        if (textPosition == TextPosition.CENTER) {
            textX = (int)(position[0] + (width - textWidth)/2.0f - 4);
            textY = (int)(position[1] + (height + textHeight)/2.0f - 4);
        }

    }
    protected  void drawText() {
        graphic.bookingDrawText(text, textX, textY, textPaint);
    }

    @Override
    public void release() {
        super.release();
    }

}
