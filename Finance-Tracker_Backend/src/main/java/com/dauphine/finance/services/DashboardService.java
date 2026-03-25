package com.dauphine.finance.services;

import com.dauphine.finance.DTO.DashboardResponse;
import java.util.UUID;

public interface DashboardService {
    DashboardResponse getDashboard(UUID userId, Integer month, Integer year);
}