package ee.opngo.pms.service;

import ee.opngo.pms.conf.PmsProperties;
import ee.opngo.pms.constant.SessionState;
import ee.opngo.pms.model.Session;
import ee.opngo.pms.model.Wallet;
import ee.opngo.pms.model.WalletCharges;
import ee.opngo.pms.repository.SessionRepository;
import ee.opngo.pms.repository.WalletChargesRepository;
import ee.opngo.pms.repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PmsChargeService {

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private WalletChargesRepository walletChargesRepository;

    @Autowired
    private PmsProperties properties;

    @Autowired
    private SessionRepository sessionRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
    public void chargeCustomerById(Long customerId, BigDecimal amount, Session session) {
        Optional<Wallet> wallet = walletRepository.findByCustomerId(customerId);

        if (wallet.isPresent() && isEligibleForCharging(customerId, amount, wallet.get())) {
            wallet.get().setBalance(wallet.get().getBalance().subtract(amount));
            walletChargesRepository.save(
                    WalletCharges
                            .builder()
                            .session(session)
                            .wallet(wallet.get())
                            .chargeTime(LocalDateTime.now())
                            .amount(amount)
                            .build());
            walletRepository.save(wallet.get());
            session.setLastChargeTime(session.getLastChargeTime().plusSeconds(properties.getChargePeriod()));
            BigDecimal price = Objects.isNull(session.getPrice()) ?
                    new BigDecimal(0) : session.getPrice();
            session.setPrice(price.add(amount));
            session.setState(SessionState.CHARGED);
        } else {
            session.setState(SessionState.INSUFFICIENT_FUNDS);
        }
        sessionRepository.save(session);
    }

    public Boolean isEligibleForCharging(Long customerId, BigDecimal amount, Wallet wallet) {
        //checking for insufficient amount in wallet
        if (wallet.getBalance().compareTo(amount) < 0) {
            //check if we can credit
            List<Session> sessions = sessionRepository.findByVehicle_Customer_IdAndEndTimeIsNotNull(customerId);
            if (!sessions.isEmpty()) {
                //checking for enough balance for credit
                if (properties.getMinimalBalanceForCredit().compareTo(wallet.getBalance()) <= 0) {
                    //checking for acceptable amount for credit
                    if (properties.getAcceptableCreditAmount().compareTo(amount) >= 0) {
                        return true;
                    }
                }
            }
        } else {
            return true;
        }
        return false;
    }
}
