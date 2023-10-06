package aix.project.chatez.member;

import org.springframework.data.jpa.repository.JpaRepository;

public interface MyServiceRepository extends JpaRepository<MyService, Long> {
}
