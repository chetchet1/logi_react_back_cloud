package kr.co.seoulit.logistics.prodcsvc.quality.mapStruct;


import kr.co.seoulit.logistics.prodcsvc.quality.Entity.WorkOrderEntity;
import kr.co.seoulit.logistics.prodcsvc.quality.to.WorkOrderInfoTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface WorkOrderMapper {
    WorkOrderMapper INSTANCE = Mappers.getMapper(WorkOrderMapper.class);
    @Mapping(target = "mpsNo", ignore = true)
    @Mapping(target = "operaioncompleted", ignore = true)
    WorkOrderInfoTO entitiyToDTO(WorkOrderEntity workOrderEntity);
}
