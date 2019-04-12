package com.gaston.ciudaduniversitariainteligente;


/**
 * Description:
 * Clase desarrollada simplemente para insertarlo dentro de un spinner item.
 * -------------------------------------
 * Created by ERic Bastida <eribastida@gmail.com>
 * on 05-Jan-19.
 * -------------------------------------
 */

public class ItemData {

    String text;
    Integer imageId;

    public ItemData(String text, Integer imageId){
        this.text=text;
        this.imageId=imageId;
    }

    public String getText(){
        return text;
    }

    public Integer getImageId(){
        return imageId;
    }
}