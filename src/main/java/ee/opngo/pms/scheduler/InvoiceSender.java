package ee.opngo.pms.scheduler;

import ee.opngo.pms.conf.PmsProperties;
import ee.opngo.pms.constant.SessionState;
import ee.opngo.pms.model.Session;
import ee.opngo.pms.repository.SessionRepository;
import ee.opngo.pms.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class InvoiceSender {
    private static final String HTML_INVOICE_TEMPLATE =
            "<html><body>" +
                    "<table><tbody>" +
                    "<tr><td>session start time</td><td>%s</td></tr>" +
                    "<tr><td>session stop time</td><td>%s</td></tr>" +
                    "<tr><td>vehicle license number</td><td>%s</td></tr>" +
                    "<tr><td>price</td><td>%s</td></tr>" +
                    "</tbody></table>" +
                    "</body></html>";
    @Autowired
    private SessionRepository sessionRepository;
    @Autowired
    private EmailService emailService;
    @Autowired
    private PmsProperties properties;

    @Scheduled(fixedRate = 50000)
    @Transactional
    public void sendInvoice() {
        List<Session> sessions = sessionRepository
                .findByEndTimeIsNotNullAndInvoiceSentIsFalseAndState(SessionState.CHARGED);

        for (Session s : sessions) {
            emailService.sendEmail(properties.getCorporateEmail(),
                    s.getVehicle().getCustomer().getEmail(), generateInvoice(s));
            s.setInvoiceSent(true);
        }
    }

    private String generateInvoice(Session s) {
        return String.format(HTML_INVOICE_TEMPLATE, s.getStartTime(), s.getEndTime(),
                s.getLicense(), s.getPrice());
    }

}
