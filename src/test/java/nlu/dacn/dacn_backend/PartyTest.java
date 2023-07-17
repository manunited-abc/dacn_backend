package nlu.dacn.dacn_backend;

import nlu.dacn.dacn_backend.dto.request.PartyRequest;
import nlu.dacn.dacn_backend.entity.Party;
import nlu.dacn.dacn_backend.enumv1.PartyConst;
import nlu.dacn.dacn_backend.service.impl.PartyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import java.util.List;

@SpringBootTest(classes = PartyTest.class)
@ComponentScan({"nlu.dacn.dacn_backend"})
public class PartyTest {
    @Autowired
    PartyService partyService;
    @Test
    public void create(){
//        PartyRequest partyRequest1 = PartyRequest.builder().name("8GB")
//                .type(PartyConst.PartyType.RAM.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest2 = PartyRequest.builder().name("16GB")
//                .type(PartyConst.PartyType.RAM.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest3 = PartyRequest.builder().name("4GB")
//                .type(PartyConst.PartyType.RAM.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest4 = PartyRequest.builder().name("SSD: 256GB (M.2 NVMe)")
//                .type(PartyConst.PartyType.STORAGE.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest5 = PartyRequest.builder().name("SSD: 512GB (M.2 NVMe)")
//                .type(PartyConst.PartyType.STORAGE.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest6 = PartyRequest.builder().name("SSD: 128GB (M.2 NVMe)")
//                .type(PartyConst.PartyType.STORAGE.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest7 = PartyRequest.builder().name("SSD: 1024GB (M.2 NVMe)")
//                .type(PartyConst.PartyType.STORAGE.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest8 = PartyRequest.builder().name("Học tập")
//                .type(PartyConst.PartyType.TYPE.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest9 = PartyRequest.builder().name("Văn phòng")
//                .type(PartyConst.PartyType.TYPE.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest10 = PartyRequest.builder().name("Gamming")
//                .type(PartyConst.PartyType.TYPE.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest11 = PartyRequest.builder().name("Học tập")
//                .type(PartyConst.PartyType.TYPE.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest12 = PartyRequest.builder().name("Văn phòng")
//                .type(PartyConst.PartyType.TYPE.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest13 = PartyRequest.builder().name("Dell")
//                .type(PartyConst.PartyType.BRAND.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest14 = PartyRequest.builder().name("Lenovo")
//                .type(PartyConst.PartyType.BRAND.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest15 = PartyRequest.builder().name("Apple")
//                .type(PartyConst.PartyType.BRAND.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest16 = PartyRequest.builder().name("LG")
//                .type(PartyConst.PartyType.BRAND.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest17 = PartyRequest.builder().name("Microsoft")
//                .type(PartyConst.PartyType.BRAND.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest18 = PartyRequest.builder().name("HP")
//                .type(PartyConst.PartyType.BRAND.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest19= PartyRequest.builder().name("Asus")
//                .type(PartyConst.PartyType.BRAND.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest20= PartyRequest.builder().name("Intel Core i5")
//                .type(PartyConst.PartyType.CPU.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest21= PartyRequest.builder().name("Intel Core i7")
//                .type(PartyConst.PartyType.CPU.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest22= PartyRequest.builder().name("AMD")
//                .type(PartyConst.PartyType.CPU.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//        PartyRequest partyRequest23= PartyRequest.builder().name("Apple M1")
//                .type(PartyConst.PartyType.CPU.name())
//                .status(PartyConst.PartyStatus.ACTIVE.name())
//                .build();
//
//
//
//
//        List<PartyRequest> partyRequests = List.of(partyRequest1,partyRequest2,partyRequest3,
//                partyRequest4,partyRequest5,partyRequest6,
//                partyRequest7,    partyRequest8,partyRequest9,partyRequest10,
//                partyRequest11,partyRequest12,partyRequest13,
//                partyRequest14,partyRequest15,partyRequest16,
//                partyRequest17,partyRequest18,partyRequest19,    partyRequest20,partyRequest21,partyRequest22,
//                partyRequest23);
//        partyService.createMulti(partyRequests);
//        System.out.println("Created OK");

    }
}
