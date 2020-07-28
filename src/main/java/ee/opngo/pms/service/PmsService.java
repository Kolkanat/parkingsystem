package ee.opngo.pms.service;

import ee.opngo.pms.conf.PmsProperties;
import ee.opngo.pms.constant.SessionState;
import ee.opngo.pms.exception.PmsException;
import ee.opngo.pms.model.Asset;
import ee.opngo.pms.model.Session;
import ee.opngo.pms.model.Vehicle;
import ee.opngo.pms.repository.AssetRepository;
import ee.opngo.pms.repository.SessionRepository;
import ee.opngo.pms.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class PmsService {

    @Autowired
    private PmsProperties properties;

    @Autowired
    private AssetRepository assetRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private SessionRepository sessionRepository;

    public void getAccessToParking(@NotNull Long assetId, @NotNull String licensePlateNumber) throws PmsException {

        Vehicle vehicle = checkEligibility(assetId, licensePlateNumber);

        if (sessionRepository.findByLicenseAndEndTimeIsNull(vehicle.getLicense()).isPresent())
            throw new PmsException(
                    String.format("Car with license number = %s is already in parking!", vehicle.getLicense()),
                    PmsException.PmsExceptionCode.ALREADY_PARKED);

        LocalDateTime now = LocalDateTime.now();
        sessionRepository.save(
                Session
                        .builder()
                        .startTime(now)
                        .lastChargeTime(now)
                        .vehicle(vehicle)
                        .license(licensePlateNumber)
                        .invoiceSent(false)
                        .build()
        );
    }

    public Asset createAsset(@NotNull Asset asset) {
        return assetRepository.save(asset);
    }

    public void stopSession(@NotNull Long assetId, @NotNull String license, String status) throws PmsException {
        if (!"stopped".equals(status)) throw new PmsException("Wrong status", PmsException.PmsExceptionCode.BAD_REQUEST);

        LocalDateTime now = LocalDateTime.now();
        checkEligibility(assetId, license);
        Session session = sessionRepository.findByLicenseAndEndTimeIsNull(license)
                .orElseThrow(() ->
                        new PmsException(String.format("Car with license number = %s is not in parking!", license),
                                PmsException.PmsExceptionCode.VEHICLE_NOT_FOUND));
        if (!session.getState().equals(SessionState.CHARGED)) {
            throw new PmsException(String.format("Session has status: %s", session.getState()),
                    PmsException.PmsExceptionCode.INCORRECT_SESSION_STATUS);
        }
        session.setEndTime(now);
        sessionRepository.save(session);
    }

    private Vehicle checkEligibility(@NotNull Long assetId, @NotNull String licensePlateNumber) throws PmsException {
        Optional<Asset> asset = assetRepository.findById(assetId);
        if (asset.map(a -> a.getIsActive()).orElse(false)) {
            Optional<Vehicle> vehicle = vehicleRepository.findByLicense(licensePlateNumber);
            return vehicle.orElseThrow(
                    () -> new PmsException(
                            String.format("There is no vehicle with license plate number = %s", licensePlateNumber),
                            PmsException.PmsExceptionCode.VEHICLE_NOT_FOUND));
        } else {
            throw new PmsException(String.format("Asset is not active or there is no asset with id = %s", assetId),
                    PmsException.PmsExceptionCode.ASSET_NOT_FOUND);
        }
    }
}
