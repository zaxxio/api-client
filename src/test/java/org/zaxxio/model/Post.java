package org.zaxxio.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Post {
    private Integer userId;
    private Integer id;
    private String title;
    private String body;
}
