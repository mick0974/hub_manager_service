package com.sch.hub_manager_service.hub.dto.response;

import com.sch.hub_manager_service.domain.model.state.PlugType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
public class HubStructureDTO {
    private List<ChargerStructureDTO> chargerStructureDTOs;

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @ToString
    public static class ChargerStructureDTO {
        private String chargerId;
        private PlugType plugType;
    }
}
