package model;

/**
 * Created by Guerino on 23/09/2016.
 */

public class FlickrAPI {
    private String apiKey;
    private String secret;
    private String userId;
    private String albumId;
    private String farmId;
    private String serverId;
    private String primaryId;
    private String photoSecret;

    public FlickrAPI() {
        apiKey = "4d1712132822061a2ebc9a2964206f47";
        secret = "b8ba35e82f0d508a";
        userId = "144947682%40N05";
    }

    public FlickrAPI(String apiKey, String secret, String userId) {
        this.apiKey = apiKey;
        this.secret = secret;
        this.userId = userId;
    }

    public FlickrAPI(String albumId, String farmId, String serverId, String primaryId, String photoSecret) {
        //Parametros para armar la url a las fotos
        this.albumId = albumId;
        this.farmId = farmId;
        this.serverId = serverId;
        this.photoSecret = photoSecret;
        this.primaryId = primaryId;
    }

    public String getPhotosetsList() {
        String result = "https://api.flickr.com/services/rest/?method=flickr.photosets.getList";
        result += "&api_key=" + apiKey + "&user_id=" + userId + "&format=json&nojsoncallback=1";
        return result;
    }

    public String getPhotosetsList(String userID) {
        String result = "https://api.flickr.com/services/rest/?method=flickr.photosets.getList";
        result += "&api_key=" + apiKey + "&user_id=" + userID + "&format=json&nojsoncallback=1";
        return result;
    }

    public String getPhotoList(String photosetId) {
        String result = "https://api.flickr.com/services/rest/?method=flickr.photosets.getPhotos";
        result += "&api_key=" + apiKey + "&photoset_id=" + photosetId + "&user_id=" + userId + "&format=json&nojsoncallback=1";
        return result;
    }

    public String getAlbumComments(String photosetId) {
        //Obtener los comentarios de un album
        String result = "https://api.flickr.com/services/rest/?method=flickr.photos.comments.getList";
        result += "&api_key=" + apiKey + "&photoset_id=" + photosetId + "&format=json&nojsoncallback=1";
        return result;
    }

    public String getPhotoComments(String photoId) {
        // Obtener los comentarios de una foto
        String result = "https://api.flickr.com/services/rest/?method=flickr.photos.comments.getList";
        result += "&api_key=" + apiKey + "&photo_id=" + photoId + "&format=json&nojsoncallback=1";
        return result;
    }


    public String getAlbumId() {
        return albumId;
    }

    public String getPrimaryId() {
        return primaryId;
    }

    public String getUrlToPhotoSmall() {
        //Imagen cuadrada de 75x75px = 100x100sp
        //https://farm{farm-id}.staticflickr.com/{server-id}/{id}_{secret}_s.jpg
        String result = "https://farm" + farmId + ".staticflickr.com/" + serverId + "/" + primaryId + "_" + photoSecret + "_s.jpg";
        return result;
    }


}
