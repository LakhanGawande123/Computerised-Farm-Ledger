package com.farm.ledger.util;

import com.farm.ledger.dto.PlotRequestDTO;
import com.farm.ledger.dto.PlotResponseDTO;
import com.farm.ledger.entity.Plot;

public class MapperUtil {

    public static Plot toEntity(PlotRequestDTO dto) {
        Plot p = new Plot();
        updateEntityFromDto(dto, p);
        return p;
    }

    public static void updateEntityFromDto(PlotRequestDTO dto, Plot p) {
        p.setPlotName(dto.getPlotName());
        p.setSurveyNumber(dto.getSurveyNumber());
        p.setGatNumber(dto.getGatNumber());
        p.setArea(dto.getArea());
        p.setSoilType(dto.getSoilType());
        p.setWaterSource(dto.getWaterSource());
        p.setIrrigationType(dto.getIrrigationType());
        p.setLocation(dto.getLocation());
        if (dto.getOwnershipType() != null) {
            p.setOwnershipType(Plot.OwnershipType.valueOf(dto.getOwnershipType().toUpperCase()));
        }
        p.setLeaseStart(dto.getLeaseStart());
        p.setLeaseEnd(dto.getLeaseEnd());
    }

    public static void patchEntityFromDto(PlotRequestDTO dto, Plot p) {
        if (dto.getPlotName() != null) p.setPlotName(dto.getPlotName());
        if (dto.getSurveyNumber() != null) p.setSurveyNumber(dto.getSurveyNumber());
        if (dto.getGatNumber() != null) p.setGatNumber(dto.getGatNumber());
        if (dto.getArea() != null) p.setArea(dto.getArea());
        if (dto.getSoilType() != null) p.setSoilType(dto.getSoilType());
        if (dto.getWaterSource() != null) p.setWaterSource(dto.getWaterSource());
        if (dto.getIrrigationType() != null) p.setIrrigationType(dto.getIrrigationType());
        if (dto.getLocation() != null) p.setLocation(dto.getLocation());
        if (dto.getOwnershipType() != null) p.setOwnershipType(Plot.OwnershipType.valueOf(dto.getOwnershipType().toUpperCase()));
        if (dto.getLeaseStart() != null) p.setLeaseStart(dto.getLeaseStart());
        if (dto.getLeaseEnd() != null) p.setLeaseEnd(dto.getLeaseEnd());
    }

    public static PlotResponseDTO toDto(Plot p) {
        PlotResponseDTO dto = new PlotResponseDTO();
        dto.setPlotId(p.getPlotId());
        dto.setFarmerId(p.getFarmer() != null ? p.getFarmer().getFarmerId() : null);
        dto.setPlotName(p.getPlotName());
        dto.setSurveyNumber(p.getSurveyNumber());
        dto.setGatNumber(p.getGatNumber());
        dto.setArea(p.getArea());
        dto.setSoilType(p.getSoilType());
        dto.setWaterSource(p.getWaterSource());
        dto.setIrrigationType(p.getIrrigationType());
        dto.setLocation(p.getLocation());
        dto.setOwnershipType(p.getOwnershipType() != null ? p.getOwnershipType().name() : null);
        dto.setLeaseStart(p.getLeaseStart());
        dto.setLeaseEnd(p.getLeaseEnd());
        return dto;
    }
}
