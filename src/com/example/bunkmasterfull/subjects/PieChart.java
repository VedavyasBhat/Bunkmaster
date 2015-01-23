package com.example.bunkmasterfull.subjects;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

public class PieChart extends View 
{
	Paint paint;
    Paint bgpaint;
    RectF rect;
    float percentage = 0;
    Context cont;
    
    public PieChart (Context context) 
    {
        super(context);
        cont = context;
        init();
    }
    public PieChart (Context context, AttributeSet attrs) 
    {
        super(context, attrs);
        cont = context;
        init();
    }
    public PieChart (Context context, AttributeSet attrs, int defStyle) 
    {
        super(context, attrs, defStyle);
        cont = context;
        init();
    }
    private void init() 
    {
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        bgpaint = new Paint();
        bgpaint.setColor(Color.RED);
        bgpaint.setAntiAlias(true);
        bgpaint.setStyle(Paint.Style.FILL);
        rect = new RectF();
    }
    
    @Override
    protected void onDraw(Canvas canvas) 
    {
        super.onDraw(canvas);
        //draw background circle anyway
        int left = 0;
        int width = getWidth();
        int top = 0;
        rect.set(left, top, left+width, top + width); 
        canvas.drawArc(rect, -90, 360, true, bgpaint);
        if(percentage!=0) 
        {
            canvas.drawArc(rect, 0, (360*percentage), true, paint);
        }
    }
    
    public void setPercentage(float percentage) 
    {
        this.percentage = percentage / 100;
        invalidate();
    }
    
    public ImageView getImageView()
    {
    	ImageView iv = new ImageView(cont);
    	return iv;
    }
}