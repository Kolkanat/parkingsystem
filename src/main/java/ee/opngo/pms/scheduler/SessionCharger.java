package ee.opngo.pms.scheduler;

import ee.opngo.pms.conf.PmsProperties;
import ee.opngo.pms.constant.SessionState;
import ee.opngo.pms.model.Session;
import ee.opngo.pms.repository.SessionRepository;
import ee.opngo.pms.service.PmsChargeService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Component
public class SessionCharger {
    private static final Logger log = LogManager.getLogger(InvoiceSender.class);

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PmsProperties properties;

    @Autowired
    private PmsChargeService pmsChargeService;

    @Scheduled(fixedRate = 20000)
    @Transactional
    public void chargeSession() {
        LocalDateTime now = LocalDateTime.now();
        List<Session> sessions =
                sessionRepository.findByEndTimeIsNullAndStateNot(SessionState.INSUFFICIENT_FUNDS);
        sessions.stream()
                .filter(s -> s.getLastChargeTime().isBefore(now))
                .forEach(s -> {
                    try {
                        pmsChargeService.chargeCustomerById(s.getVehicle().getCustomer().getId(), properties.getPrice(), s);
                        log.info(String.format("trying to charge session:[%s]", s.toString()));
                    } catch (Exception e) {
                        log.error(String.format("[ERROR occurred during charging session = %s] Error:%s",
                                s.getId(), e.getMessage()));
                    }
                });
    }

}
