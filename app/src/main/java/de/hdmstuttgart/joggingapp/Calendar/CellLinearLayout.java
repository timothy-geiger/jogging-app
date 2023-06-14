package de.hdmstuttgart.joggingapp.Calendar;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class CellLinearLayout extends LinearLayout {

    /**
     * Eigenes LinearLayout um die einzelnen Dates im Kalender quadratisch darzustellen
     * @param context
     */
    public CellLinearLayout(Context context) {
        super(context);
    }

    public CellLinearLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CellLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        // width = height -> Quadratisch
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
