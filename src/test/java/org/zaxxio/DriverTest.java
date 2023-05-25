package org.zaxxio;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.zaxxio.client.ApiClient;
import org.zaxxio.model.Post;
import org.zaxxio.service.PostService;

import java.util.List;

@RunWith(JUnit4.class)
public class DriverTest {


    @Test
    public void test(){
        ApiClient apiClient = new ApiClient.Builder()
                .basePath("https://jsonplaceholder.typicode.com")
                .build();

        PostService service = apiClient.create(PostService.class);
        Post post = service.getPost("Mozilla/Firefox", 1, 1);

        Post p = new Post();
        p.setTitle("Partha Blog");
        p.setBody("Hello From Bangladesh");

        Post post1 = service.savePost(p);
        System.out.println(post1);

        List<Post> allPost = service.getAllPost();
        System.out.println(allPost.size());

    }

}
