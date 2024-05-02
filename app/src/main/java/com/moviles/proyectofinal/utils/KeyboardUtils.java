package com.moviles.proyectofinal.utils;

import android.graphics.Rect;
import android.view.View;
import android.widget.ScrollView;

public class KeyboardUtils {

    public static void addKeyboardVisibilityListener(View rootLayout, ScrollView scrollView, OnKeyboardVisibilityListener listener) {
        rootLayout.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            Rect r = new Rect();
            rootLayout.getWindowVisibleDisplayFrame(r);
            int screenHeight = rootLayout.getRootView().getHeight();
            int keypadHeight = screenHeight - r.bottom;

            boolean isVisible = keypadHeight > screenHeight * 0.15;
            listener.onVisibilityChanged(isVisible);

            if (isVisible) {
                scrollView.setPadding(0, 0, 0, keypadHeight + 100);
            }
        });
    }

    public interface OnKeyboardVisibilityListener {
        void onVisibilityChanged(boolean isVisible);
    }
}

