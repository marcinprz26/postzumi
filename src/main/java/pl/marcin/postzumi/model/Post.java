package pl.marcin.postzumi.model;

import org.hibernate.annotations.GeneratorType;

import javax.persistence.*;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    private Long id;

    @Column
    private String title;

    @Column
    private String body;

    @Column
    private Long userId;

    public Post(){}

    public Post(Long id, String title, String body, Long userId) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.userId = userId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
