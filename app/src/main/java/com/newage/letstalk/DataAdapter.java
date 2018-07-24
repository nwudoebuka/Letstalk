package com.newage.letstalk;

/**
 * Created by Newage_android on 5/11/2018.
 */

public class DataAdapter
{
    public String ImageURL;
    public String ImageTitle;
    public String ImageSender;
    public String imgsession;

    public String getImageUrl() {

        return ImageURL;
    }

    public void setImageUrl(String imageServerUrl) {

        this.ImageURL = imageServerUrl;
    }

    public String getImageTitle() {

        return ImageTitle;
    }
    public String getImageSender() {

        return ImageSender;
    }

    public String getImagesession() {

        return imgsession;
    }

    public void setImageTitle(String Imagetitlename) {

        this.ImageTitle = Imagetitlename;
    }

    public void setImageSender(String Imagesendername) {

        this.ImageSender = Imagesendername;
    }

    public void setsession(String Imagsession) {

        this.imgsession = Imagsession;
    }

}