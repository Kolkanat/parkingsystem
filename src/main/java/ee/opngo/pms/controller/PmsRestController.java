package ee.opngo.pms.controller;


import ee.opngo.pms.exception.PmsException;
import ee.opngo.pms.model.Asset;
import ee.opngo.pms.request.body.AccessRequestBody;
import ee.opngo.pms.request.body.SessionStopBody;
import ee.opngo.pms.service.PmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(value = "/pms/v1")
public class PmsRestController {

    @Autowired
    PmsService pmsService;

    @PostMapping("/assets/{asset}/sessions")
    public void getAccessToParking(@PathVariable("asset") Long assetId,
                                   @RequestBody AccessRequestBody body) throws PmsException {
        pmsService.getAccessToParking(assetId, body.getLicensePlateNumber());
    }

    @PostMapping("/assets/{asset}/vehicle/{license}/session")
    public void stopSession(@PathVariable("asset") @NotNull Long assetId,
                            @PathVariable("license") @NotNull String license,
                            @RequestBody @NotNull SessionStopBody body) throws PmsException {
        pmsService.stopSession(assetId, license, body.getStatus());
    }

    @PostMapping("/asset")
    public Asset createAsset(@RequestBody @NotNull Asset asset) {
        return pmsService.createAsset(asset);
    }
}
