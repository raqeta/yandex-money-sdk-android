package ru.yandex.money.android.utils;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * @author vyasevich
 */
public class Views {

    public static void setText(View container, int viewId, String text) {
        TextView textView = (TextView) container.findViewById(viewId);
        if (textView != null) {
            textView.setText(text);
        }
    }

    public static void setImageResource(View container, int viewId, int resId) {
        ImageView imageView = (ImageView) container.findViewById(viewId);
        if (imageView != null) {
            imageView.setImageResource(resId);
        }
    }
}
