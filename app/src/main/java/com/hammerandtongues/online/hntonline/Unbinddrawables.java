package com.hammerandtongues.online.hntonline;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

/**
 * Created by NgonidzaIshe on 08 Mar,2017.
 */
public class Unbinddrawables {


    public   void unbindDrawables(View view)
    {
        if (view.getBackground() != null)
        {
            view.getBackground().setCallback(null);
        }
        if (view instanceof ViewGroup && !(view instanceof AdapterView))
        {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++)
            {
                unbindDrawables(((ViewGroup) view).getChildAt(i));
            }
            ((ViewGroup) view).removeAllViews();
        }
    }
}
