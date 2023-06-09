package org.zaxxio.service;

import org.zaxxio.http.*;
import org.zaxxio.model.Post;

import java.util.List;

public interface PostService {
    @GET("/posts/{id}")
    @Headers({"Accept: application/json", "User-Agent: Application"})
    Post getPost(@Header("Accept") String accept, @Path("id") Integer id, @Query("limit") Integer limit);

    @POST("/posts")
    @Headers({"Content-type: application/json; charset=UTF-8"})
    Post savePost(@Body Post post);

    @GET("/posts")
    List<Post> getAllPost();

    @DELETE("/posts/{id}")
    @Headers({"Accept: application/json", "User-Agent: Application"})
    void deletePost(@Header("Accept") String accept, @Path("id") Integer id);
    @PATCH("/posts/{id}")
    @Headers({"Accept: application/json", "User-Agent: Application"})
    Post updatePost(@Body Post post, @Path("id") Integer id);
}
