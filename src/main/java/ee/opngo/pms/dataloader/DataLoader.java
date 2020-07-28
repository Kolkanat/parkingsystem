package ee.opngo.pms.dataloader;

import ee.opngo.pms.conf.PmsProperties;
import ee.opngo.pms.constant.SessionState;
import ee.opngo.pms.model.*;
import ee.opngo.pms.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
public class DataLoader implements ApplicationRunner {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private WalletRepository walletRepository;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private PmsProperties properties;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        Customer c1 = Customer.builder().email("piopi.88@gmail.com").password("pwd1").build();
        customerRepository.save(c1);

        Customer c2 = Customer.builder().email("kolkanat.bashpayev@gmail.com").password("pwd2").build();
        customerRepository.save(c2);

        Vehicle v1 = Vehicle.builder().name("bmw").customer(c1).license("12ABC").build();
        vehicleRepository.save(v1);

        Vehicle v2 = Vehicle.builder().name("mercedes").customer(c1).license("22CCD").build();
        vehicleRepository.save(v2);

        Vehicle v3 = Vehicle.builder().name("toyota").customer(c2).license("543CCH").build();
        vehicleRepository.save(v3);

        Wallet w1 = Wallet.builder().customer(c1).balance(new BigDecimal("500")).build();
        walletRepository.save(w1);

        Wallet w2 = Wallet.builder().customer(c2).balance(new BigDecimal("200")).build();
        walletRepository.save(w2);

        Asset a1 = Asset.builder().address("Some street 144b").isActive(true).build();
        assetRepository.save(a1);

        Asset a2 = Asset.builder().address("Another street 133s").isActive(true).build();
        assetRepository.save(a2);

        LocalDateTime start = LocalDateTime.of(2018, 10, 1, 14, 0, 0);
        Session s = Session
                .builder()
                .invoiceSent(false)
                .startTime(start)
                .endTime(start.plusSeconds(properties.getChargePeriod()))
                .lastChargeTime(start.plusSeconds(properties.getChargePeriod()))
                .state(SessionState.CHARGED)
                .price(properties.getPrice())
                .license(v1.getLicense())
                .vehicle(v1)
                .build();
        sessionRepository.save(s);
    }
}
