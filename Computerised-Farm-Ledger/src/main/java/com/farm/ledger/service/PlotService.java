package com.farm.ledger.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.farm.ledger.dto.PlotRequestDTO;
import com.farm.ledger.dto.PlotResponseDTO;
import com.farm.ledger.dto.PlotStatisticsDTO;

import java.util.List;

public interface PlotService {
	PlotResponseDTO createPlot(String farmerId, PlotRequestDTO dto);

	Page<PlotResponseDTO> getAllPlots(String soilType, String ownershipType, Pageable pageable);

	PlotResponseDTO getPlotById(Long plotId);

	List<PlotResponseDTO> getPlotsByFarmer(String farmerId);

	PlotResponseDTO updatePlot(Long plotId, PlotRequestDTO dto);

	PlotResponseDTO partialUpdatePlot(Long plotId, PlotRequestDTO dto);

	void deletePlot(Long plotId);

	PlotStatisticsDTO getStatistics();
}
