package model;

import android.graphics.Bitmap;

/**
 * Created by Guerino on 22/09/2016.
 */

public class Photo {
    private String id;
    private Bitmap bitmapImage;
    private String title;
    private String views;
    private String dateTaken;
    private String description;
    private String farmId;
    private String serverId;
    private String photoSecret;
    private String albumId; //El album al que pertenece esta foto

    public Photo() {

    }

    public Photo(String id, Bitmap bitmapImage, String title, String views, String dateTaken, String description, String farmId, String serverId, String photoSecret, String albumId) {
        this.id = id;
        this.bitmapImage = bitmapImage;
        this.title = title;
        this.views = views;
        this.dateTaken = dateTaken;
        this.description = description;
        this.farmId = farmId;
        this.serverId = serverId;
        this.photoSecret = photoSecret;
        this.albumId = albumId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Bitmap getBitmap() {
        return bitmapImage;
    }

    public void setBitmap(Bitmap bitmapImage) {
        this.bitmapImage = bitmapImage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getDateTaken() {
        return dateTaken;
    }

    public void setDateTaken(String dateTaken) {
        this.dateTaken = dateTaken;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFarmId() {
        return farmId;
    }

    public void setFarmId(String farmId) {
        this.farmId = farmId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getPhotoSecret() {
        return photoSecret;
    }

    public void setPhotoSecret(String photoSecret) {
        this.photoSecret = photoSecret;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getUrlToSmallPhoto() {
        //Imagen cuadrada de 75x75px = 100x100sp
        //https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_s.jpg
        String url = "https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + id + "_" + photoSecret + "_s.jpg";
        return url;
    }

    public String getUrlToMediumPhoto() {
        //Imagen cuadrada de 640x640px = 100x100sp
        //https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_s.jpg
        String url = "https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + id + "_" + photoSecret + "_z.jpg";
        return url;
    }

    public String getUrlToPhoto() {
        //Imagen cuadrada de 75x75px = 100x100sp
        //https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_s.jpg
        String url = "https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + id + "_" + photoSecret + ".jpg";
        return url;
    }

}
