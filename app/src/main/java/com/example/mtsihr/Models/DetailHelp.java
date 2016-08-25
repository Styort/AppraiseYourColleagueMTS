package com.example.mtsihr.Models;

/**
 * Created by Виктор on 24.08.2016.
 */
public class DetailHelp {
    public String title, description;
    public int image, num;

    public DetailHelp(int image, String title, String description, int num) {
        this.description = description;
        this.image = image;
        this.num = num;
        this.title = title;
    }
}
