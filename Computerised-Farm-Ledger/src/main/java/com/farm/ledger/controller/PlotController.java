package com.farm.ledger.controller;

import com.farm.ledger.constant.FarmLedgerExceptionConstants;
import com.farm.ledger.dto.PlotRequestDTO;
import com.farm.ledger.dto.PlotResponseDTO;
import com.farm.ledger.dto.PlotStatisticsDTO;
import com.farm.ledger.service.PlotService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Validated
public class PlotController {

    private static final Logger logger = LoggerFactory.getLogger(PlotController.class);

    @Autowired
    private final PlotService plotService;

    // 1. Create Plot
    @PostMapping(value = "/farmers/{farmerId}/plots",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<PlotResponseDTO> createPlot(@PathVariable String farmerId,
                                                      @Valid @RequestBody final PlotRequestDTO request) {
        logger.trace("Method start: createPlot for farmerId={}", farmerId);
        logger.info("Received request to create plot. Payload: {}", request);

        PlotResponseDTO response = plotService.createPlot(farmerId, request);

        logger.info("Plot created successfully for farmerId={}. Response: {}", farmerId, response);
        logger.trace("Method exit: createPlot for farmerId={}", farmerId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 2. Get all plots (with filters)
    @GetMapping("/plots")
    public ResponseEntity<Page<PlotResponseDTO>> getAllPlots(
            @RequestParam(required = false) String soilType,
            @RequestParam(required = false) String ownershipType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        logger.trace("Method start: getAllPlots with soilType={} ownershipType={} page={} size={}", soilType, ownershipType, page, size);
        Page<PlotResponseDTO> result = plotService.getAllPlots(soilType, ownershipType, PageRequest.of(page, size));
        logger.info("Returning {} plots (page {}) for getAllPlots", result.getNumberOfElements(), page);
        logger.trace("Method exit: getAllPlots");
        return ResponseEntity.ok(result);
    }

    // 3. Get all plots for a farmer
	@Operation(summary = "Get all plots of the Farmer based on FarmerID", description = "Fetches all plots of the Farmer")
	@ApiResponse(responseCode = "200", description = "successfully")
	@ApiResponse(responseCode = "404", description = "Farmer not found")
    @GetMapping("/farmers/{farmerId}/plots")
    public ResponseEntity<List<PlotResponseDTO>> getPlotsByFarmer(@PathVariable String farmerId) {
        logger.trace("Method start: getPlotsByFarmer for farmerId={}", farmerId);
        List<PlotResponseDTO> result = plotService.getPlotsByFarmer(farmerId);
        logger.info("Found {} plots for farmerId={}", result == null ? 0 : result.size(), farmerId);
        logger.trace("Method exit: getPlotsByFarmer for farmerId={}", farmerId);
        return ResponseEntity.ok(result);
    }

    // 4. Get plot by ID
    @GetMapping("/plots/{plotId}")
    public ResponseEntity<PlotResponseDTO> getPlotById(@PathVariable @NotNull(message = FarmLedgerExceptionConstants.PLOT_ID_NOT_BLANK) @Positive Long plotId) {
        logger.trace("Method start: getPlotById for plotId={}", plotId);
        PlotResponseDTO dto = plotService.getPlotById(plotId);
        logger.info("Returning plotId={} with name={}", dto != null ? dto.getPlotId() : null, dto != null ? dto.getPlotName() : null);
        logger.trace("Method exit: getPlotById for plotId={}", plotId);
        return ResponseEntity.ok(dto);
    }

    // 5. Update plot
    @PutMapping("/plots/{plotId}")
    public ResponseEntity<PlotResponseDTO> updatePlot(@PathVariable Long plotId, @Valid @RequestBody PlotRequestDTO dto) {
        logger.trace("Method start: updatePlot for plotId={}", plotId);
        logger.info("Received update for plotId={} Payload: {}", plotId, dto);
        PlotResponseDTO updated = plotService.updatePlot(plotId, dto);
        logger.info("Plot updated successfully for plotId={}. Response: {}", plotId, updated);
        logger.trace("Method exit: updatePlot for plotId={}", plotId);
        return ResponseEntity.ok(updated);
    }

    // 6. Partial Update (Patch)
    @PatchMapping("/plots/{plotId}")
    public ResponseEntity<PlotResponseDTO> partialUpdate(@PathVariable Long plotId, @RequestBody PlotRequestDTO dto) {
        logger.trace("Method start: partialUpdate for plotId={}", plotId);
        logger.info("Received partial update for plotId={} Payload: {}", plotId, dto);
        PlotResponseDTO updated = plotService.partialUpdatePlot(plotId, dto);
        logger.info("Plot partially updated for plotId={}. Response: {}", plotId, updated);
        logger.trace("Method exit: partialUpdate for plotId={}", plotId);
        return ResponseEntity.ok(updated);
    }

    // 7. Delete Plot
    @DeleteMapping("/plots/{plotId}")
    public ResponseEntity<Void> deletePlot(@PathVariable Long plotId) {
        logger.trace("Method start: deletePlot for plotId={}", plotId);
        plotService.deletePlot(plotId);
        logger.info("Deleted plotId={}", plotId);
        logger.trace("Method exit: deletePlot for plotId={}", plotId);
        return ResponseEntity.noContent().build();
    }

    // 8. Statistics API
    @GetMapping("/plots/statistics")
    public ResponseEntity<PlotStatisticsDTO> getStatistics() {
        logger.trace("Method start: getStatistics");
        PlotStatisticsDTO stats = plotService.getStatistics();
        logger.info("Returning plot statistics: totalPlots={} totalArea={}", stats.getTotalPlots(), stats.getTotalArea());
        logger.trace("Method exit: getStatistics");
        return ResponseEntity.ok(stats);
    }
}
