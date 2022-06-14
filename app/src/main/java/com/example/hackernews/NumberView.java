package com.example.hackernews;

public class NumberView {
    private int ivNumbersImageId;

    // TextView 1
    private String mNumberInDigit;


    // create constructor to set the values for all the parameters of the each single view
    public NumberView(int NumbersImageId, String NumbersInDigit) {
        ivNumbersImageId = NumbersImageId;
        mNumberInDigit = NumbersInDigit;

    }

    // getter method for returning the ID of the imageview
    public int getNumbersImageId() {
        return ivNumbersImageId;
    }

    // getter method for returning the ID of the TextView 1
    public String getNumberInDigit() {
        return mNumberInDigit;
    }

    // getter method for returning the ID of the TextView 2

    }

