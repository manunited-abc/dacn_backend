package nlu.dacn.dacn_backend.converter;

import nlu.dacn.dacn_backend.dto.request.LaptopDTO;
import nlu.dacn.dacn_backend.entity.Laptop;
import org.springframework.stereotype.Component;

@Component
public class LaptopConverter {
    public LaptopDTO toLaptopDTO(Laptop laptop) {
        LaptopDTO dto = LaptopDTO.builder().build();
        if (null != laptop.getId()) {
            dto.setId(laptop.getId());
        }
        dto.setProductName(laptop.getProductName());
        dto.setBrand(laptop.getBrand());
        dto.setPrice(laptop.getPrice());
        dto.setCpu(laptop.getCpu());
        dto.setChipCpu(laptop.getChipCpu());
        dto.setRam(laptop.getRam());
        dto.setStorage(laptop.getStorage());
        dto.setDisplay(laptop.getDisplay());
        dto.setGraphics(laptop.getGraphics());
        dto.setColor(laptop.getColor());
        dto.setBattery(laptop.getBattery());
        dto.setWeight(laptop.getWeight());
        dto.setCreatedDate(laptop.getCreatedDate());
        dto.setCreateBy(laptop.getCreateBy());
        dto.setModifiedBy(laptop.getModifiedBy());
        dto.setModifiedDate(laptop.getModifiedDate());
//        dto.setFacilityId(laptop.getFacility().getId());
        dto.setLaptopState(laptop.getLaptopState());
        dto.setQuantity(laptop.getQuantity());
        dto.setCreateBy(laptop.getCreateBy());
        dto.setCreatedDate(laptop.getCreatedDate());
        dto.setModifiedBy(laptop.getModifiedBy());
        dto.setModifiedDate(laptop.getModifiedDate());
        dto.setType(laptop.getType());
        dto.setLinkAvatar(laptop.getLinkAvatar());
        return dto;
    }

}
