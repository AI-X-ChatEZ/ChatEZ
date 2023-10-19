package aix.project.chatez.myservice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MyServiceRepository extends JpaRepository<MyService, Long> {
    List<MyService> findByMember_MemberNo(Long memberNo);
}
