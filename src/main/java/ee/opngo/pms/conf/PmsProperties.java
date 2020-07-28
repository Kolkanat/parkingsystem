package ee.opngo.pms.conf;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;


@Data
@Component
@ConfigurationProperties(prefix = "pms")
public class PmsProperties {
    private BigDecimal minimalBalanceForCredit;
    private BigDecimal price;
    private Long chargePeriod;
    private String corporateEmail;
    private BigDecimal acceptableCreditAmount;
}
