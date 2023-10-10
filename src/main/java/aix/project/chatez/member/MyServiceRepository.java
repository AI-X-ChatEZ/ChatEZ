package aix.project.chatez.member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MyServiceRepository extends JpaRepository<MyService, Long> {
    List<MyService> findByMemberMemberNo(Long memberNo);
}
