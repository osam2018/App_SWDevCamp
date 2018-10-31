package com.swdevcamp.jun.swdevcamp;

import android.graphics.drawable.Drawable;

public class DataCardItem {
    int image;
    String title;

    DataCardItem(int image, String title) {
        this.image=image;
        this.title = title;
    }

    void setImage(int image) {
        this.image = image;
    }

    void setTitle(String title) {
        this.title = title;
    }

    int getImage() {
        return this.image;
    }

    String getTitle() {
        return this.title;
    }
}
