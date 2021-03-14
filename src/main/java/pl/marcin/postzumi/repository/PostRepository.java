package pl.marcin.postzumi.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import pl.marcin.postzumi.model.Post;

@Repository
public interface PostRepository extends CrudRepository<Post, Long> {

}
