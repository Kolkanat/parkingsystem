package ee.opngo.pms.repository;

import ee.opngo.pms.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByEndTimeIsNotNullAndInvoiceSentIsFalseAndState(String state);
    @Query(value = "select s from Session s where s.endTime is null and (s.state is null or s.state <> ?1)")
    List<Session> findByEndTimeIsNullAndStateNot(String state);
    Optional<Session> findByLicenseAndEndTimeIsNull(String license);
    List<Session> findByVehicle_Customer_IdAndEndTimeIsNotNull(Long customerId);
}
