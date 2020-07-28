package ee.opngo.pms.repository;

import ee.opngo.pms.model.WalletCharges;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletChargesRepository extends JpaRepository<WalletCharges, Long> {
}
