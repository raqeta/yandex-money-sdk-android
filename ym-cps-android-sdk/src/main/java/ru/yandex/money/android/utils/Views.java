package ru.yandex.money.android.utils;

import android.text.Editable;
import android.view.View;
import android.widget.EditText;
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

    public static String getTextSafely(EditText editText) {
        Editable text = editText.getText();
        return text == null ? null : text.toString();
    }

    public static void setImageResource(View container, int viewId, int resId) {
        ImageView imageView = (ImageView) container.findViewById(viewId);
        if (imageView != null) {
            imageView.setImageResource(resId);
        }
    }

    public static void setVisibility(View container, int viewId, int visibility) {
        View view = container.findViewById(viewId);
        if (view != null) {
            view.setVisibility(visibility);
        }
    }
}
