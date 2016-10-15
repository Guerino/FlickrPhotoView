package model;

import android.graphics.Bitmap;

/**
 * Created by Guerino on 22/09/2016.
 */

public class Directory {
    private String id;
    private String primaryId;
    private String farmId;
    private String serverId;
    private String photoSecret;
    private Bitmap bitmap;
    private String pathImage;
    private String name;
    private String photos;
    private String views;
    private String comments;

    public Directory() {

    }

    public Directory(String id, String primaryId, String farmId, String serverId,
                     String photoSecret, Bitmap bitmap, String pathImage , String name, String photos,
                     String views, String comments) {
        this.id = id;
        this.primaryId = primaryId;
        this.farmId = farmId;
        this.serverId = serverId;
        this.photoSecret = photoSecret;
        this.bitmap = bitmap;
        this.pathImage = pathImage;
        this.name = name;
        this.photos = photos;
        this.views = views;
        this.comments = comments;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrimaryId() {
        return primaryId;
    }

    public void setPrimaryId(String primaryId) {
        this.primaryId = primaryId;
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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getPathBitmap() {
        return pathImage;
    }

    public void setPathBitmap(String path) {
        this.pathImage = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhotos() {
        return photos;
    }

    public void setPhotos(String photos) {
        this.photos = photos;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getUrlToSmallPhoto() {
        //Imagen cuadrada de 75x75px = 100x100sp
        //https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_s.jpg
        //https://farm6.staticflickr.com/5346/72157673063872941_30ec46b976_s.jpg
        String url = "https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + primaryId + "_" + photoSecret + "_s.jpg";
        return url;
    }

    @Override
    public String toString() {
        return "Directory{" +
                "id='" + id + '\'' +
                ", primaryId='" + primaryId + '\'' +
                ", farmId='" + farmId + '\'' +
                ", serverId='" + serverId + '\'' +
                ", photoSecret='" + photoSecret + '\'' +
                ", bitmap=" + bitmap +
                ", name='" + name + '\'' +
                ", photos='" + photos + '\'' +
                ", views='" + views + '\'' +
                ", comments='" + comments + '\'' +
                '}';
    }
}
