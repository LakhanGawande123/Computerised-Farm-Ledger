package com.farm.ledger.service.impl;

import com.farm.ledger.constant.FarmLedgerExceptionConstants;
import com.farm.ledger.dto.PlotRequestDTO;
import com.farm.ledger.dto.PlotResponseDTO;
import com.farm.ledger.dto.PlotStatisticsDTO;
import com.farm.ledger.entity.Farmer;
import com.farm.ledger.entity.Plot;
import com.farm.ledger.exception.BadRequestException;
import com.farm.ledger.exception.FarmLedgerServiceException;
import com.farm.ledger.exception.ResourceNotFoundException;
import com.farm.ledger.repository.FarmerRepository;
import com.farm.ledger.repository.PlotRepository;
import com.farm.ledger.service.PlotService;
import com.farm.ledger.specs.PlotSpecifications;
import com.farm.ledger.util.MapperUtil;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PlotServiceImpl implements PlotService {

    private final PlotRepository plotRepository;
    private final FarmerRepository farmerRepository;

    @Override
    public PlotResponseDTO createPlot(String farmerId, PlotRequestDTO dto) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new FarmLedgerServiceException(FarmLedgerExceptionConstants.FARMER_ID_NOT_FOUND));

        validatePlotDto(dto);

        Plot plot = MapperUtil.toEntity(dto);
        plot.setFarmer(farmer);
        Plot saved = plotRepository.save(plot);
        return MapperUtil.toDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PlotResponseDTO> getAllPlots(String soilType, String ownershipType, Pageable pageable) {
        Specification<Plot> spec = Specification.where(PlotSpecifications.hasSoilType(soilType))
                .and(PlotSpecifications.hasOwnershipType(ownershipType));
        return plotRepository.findAll(spec, pageable).map(MapperUtil::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public PlotResponseDTO getPlotById(Long plotId) {
        Plot plot = plotRepository.findById(plotId)
                .orElseThrow(() -> new ResourceNotFoundException("Plot not found with id " + plotId));
        return MapperUtil.toDto(plot);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlotResponseDTO> getPlotsByFarmer(String farmerId) {
        Farmer farmer = farmerRepository.findById(farmerId)
                .orElseThrow(() -> new ResourceNotFoundException("Farmer not found with id " + farmerId));
        return farmer.getPlots().stream().map(MapperUtil::toDto).collect(Collectors.toList());
    }

    @Override
    public PlotResponseDTO updatePlot(Long plotId, PlotRequestDTO dto) {
        Plot plot = plotRepository.findById(plotId)
                .orElseThrow(() -> new ResourceNotFoundException("Plot not found with id " + plotId));

        validatePlotDto(dto);

        // replace fields
        MapperUtil.updateEntityFromDto(dto, plot);

        Plot saved = plotRepository.save(plot);
        return MapperUtil.toDto(saved);
    }

    @Override
    public PlotResponseDTO partialUpdatePlot(Long plotId, PlotRequestDTO dto) {
        Plot plot = plotRepository.findById(plotId)
                .orElseThrow(() -> new ResourceNotFoundException("Plot not found with id " + plotId));

        // apply non-null fields only
        MapperUtil.patchEntityFromDto(dto, plot);
        validatePartial(plot);
        Plot saved = plotRepository.save(plot);
        return MapperUtil.toDto(saved);
    }

    @Override
    public void deletePlot(Long plotId) {
        Plot plot = plotRepository.findById(plotId)
                .orElseThrow(() -> new ResourceNotFoundException("Plot not found with id " + plotId));
        plotRepository.delete(plot);
    }

    @Override
    @Transactional(readOnly = true)
    public PlotStatisticsDTO getStatistics() {
        List<Plot> plots = plotRepository.findAll();
        PlotStatisticsDTO stats = new PlotStatisticsDTO();
        stats.setTotalPlots(plots.size());
        stats.setTotalArea(plots.stream().mapToDouble(p -> p.getArea() == null ? 0d : p.getArea()).sum());
        stats.setOwnedPlots(plots.stream().filter(p -> p.getOwnershipType() == Plot.OwnershipType.OWNED).count());
        stats.setLeasedPlots(plots.stream().filter(p -> p.getOwnershipType() == Plot.OwnershipType.LEASED).count());
        Map<String, Long> irrigationCount = plots.stream()
                .collect(Collectors.groupingBy(p -> p.getIrrigationType() == null ? "UNKNOWN" : p.getIrrigationType(), Collectors.counting()));
        stats.setIrrigationTypesCount(irrigationCount);
        return stats;
    }

    private void validatePlotDto(PlotRequestDTO dto) {
        if (dto.getArea() != null && dto.getArea() < 0)
            throw new BadRequestException("area must be non-negative");
        if (dto.getOwnershipType() != null) {
            try {
                Plot.OwnershipType.valueOf(dto.getOwnershipType().toUpperCase());
            } catch (Exception ex) {
                throw new BadRequestException("ownershipType must be one of OWNED or LEASED");
            }
        }
    }

    private void validatePartial(Plot plot) {
        if (plot.getArea() != null && plot.getArea() < 0)
            throw new BadRequestException("area must be non-negative");
    }
}
