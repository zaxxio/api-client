package org.zaxxio.service;

import org.zaxxio.http.*;
import org.zaxxio.model.Post;

public interface ApiService {
    @GET("/posts/{id}")
    @Headers({"Accept: application/json", "User-Agent: Application"})
    Post getPost(@Header("Accept") String accept, @Path("id") Integer id, @Query("name") Integer name);

    @POST("/posts")
    @Headers({"Content-type: application/json; charset=UTF-8"})
    Post savePost(@Body Post post);

}
