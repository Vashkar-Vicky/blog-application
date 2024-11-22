package io.mountblue.dao;

import io.mountblue.model.Post;
import io.mountblue.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
    @Query("SELECT p FROM Post p JOIN p.tags t WHERE p.user = :user AND t.name IN :tags " +
            "AND (:startDate IS NULL OR p.publishedAt >= :startDate) " +
            "AND (:endDate IS NULL OR p.publishedAt <= :endDate)")
    List<Post> findFilteredPosts(@Param("user") User user,
                                 @Param("tags") List<String> tags,
                                 @Param("startDate") LocalDateTime startDate,
                                 @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Post p WHERE p.publishedAt BETWEEN :startDate AND :endDate")
    List<Post> filterByDate(@Param("startDate") LocalDateTime startDate,
                            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT p FROM Post p ORDER BY p.publishedAt DESC")
    List<Post> findAllByOrderByPublishedAtDesc();

    @Query("SELECT p FROM Post p ORDER BY p.publishedAt ASC")
    List<Post> findAllByOrderByPublishedAtAsc();

    @Query(value = "SELECT p.* FROM post p " +
            "WHERE to_tsvector('english', p.title || ' ' || p.content) " +
            " @@ to_tsquery('english', replace(:query, ' ', ' & ')) " +
            "OR EXISTS (SELECT 1 FROM post_tags pt " +
            "JOIN tags t ON pt.tag_id = t.id " +
            "WHERE pt.post_id = p.id " +
            "AND to_tsvector('english', t.name) @@ to_tsquery('english', replace(:query, ' ', ' & ')))",
            nativeQuery = true)
    List<Post> searchPosts(@Param("query") String query);

@Query("SELECT p FROM Post p WHERE p.user.name = :name")
List<Post> findFilteredPostsByUser(@Param("name") String name);

@Query("SELECT p FROM Post p JOIN p.tags t WHERE t.name = :name")
List<Post> findFilteredPostsByTags(@Param("name") String name);

@Query("SELECT p FROM Post p JOIN p.tags t WHERE p.user.name = :author AND t.name = :tag AND p.publishedAt BETWEEN :startDate AND :endDate")
List<Post> findByUserAndTagsAndDate(
        @Param("author") String author,
        @Param("tag") String tag,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate);



    @Query("SELECT p FROM Post p WHERE p.user.name = :author AND p.publishedAt BETWEEN :startDate AND :endDate")
    List<Post> findFilteredPostsByUserAndDate(@Param("author") String author, @Param("startDate") LocalDateTime startDate,
                                                  @Param("endDate") LocalDateTime endDate);


    List<Post> findByUser(User user);

}