package ee.opngo.pms.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class EmailService {
    private static final Logger logger = LogManager.getLogger(EmailService.class);

    //Fake email sending
    public void sendEmail(String from, String to, String msg) {
        logger.info(String.format("sending email from:%s to:%s message:%s", from, to, msg));
    }
}
