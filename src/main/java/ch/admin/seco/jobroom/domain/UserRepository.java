package ch.admin.seco.jobroom.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

@Deprecated
public interface UserRepository extends JpaRepository<User, UUID> {

    String USERS_BY_LOGIN_CACHE = "usersByLogin";

    Optional<User> findOneByActivationKey(String activationKey);

    List<User> findAllByActivatedIsFalseAndCreatedDateBefore(Instant dateTime);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByEmailIgnoreCase(String email);

    Optional<User> findOneByLogin(String login);

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesById(UUID id);

    @EntityGraph(attributePaths = "authorities")
    default Optional<User> findOneWithAuthoritiesByLoginFromCache(String login) {
        return findOneWithAuthoritiesByLogin(login);
    }

    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByLogin(String login);

    Page<User> findAllByLoginNot(Pageable pageable, String login);

    @EntityGraph(attributePaths = "authorities")
    @Query("select u from User u")
    Stream<User> streamAll();
}
