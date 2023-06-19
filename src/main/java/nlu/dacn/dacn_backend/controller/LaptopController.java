package nlu.dacn.dacn_backend.controller;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.dto.request.LaptopDTO;
import nlu.dacn.dacn_backend.entity.Laptop;
import nlu.dacn.dacn_backend.model.request.LaptopFilter;
import nlu.dacn.dacn_backend.model.response.LaptopOutput;
import nlu.dacn.dacn_backend.service.impl.LaptopService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*")
public class LaptopController {
    private final LaptopService laptopService;

    // start must be natural number. (1,2,3...)
    @GetMapping("/laptop")
    public LaptopOutput getLaptop(@RequestParam("start") int start,
                                  @RequestParam("limit") int limit,
                                  @RequestParam(value = "types", required = false) String typeJson,
                                  @RequestParam(value = "brands", required = false) String brandJson,
                                  @RequestParam(value = "chipCpus", required = false) String chipCpuJson) {
        List<String> types = typeJson != null && typeJson.length() > 0 ? List.of(  typeJson.split(",")) : new ArrayList<>() ;
        List<String> brands =  brandJson != null && brandJson.length() > 0 ? List.of(  brandJson.split(",")) : new ArrayList<>();
        List<String> chipCpus =  chipCpuJson != null && chipCpuJson.length() > 0 ? List.of(  chipCpuJson.split(",")) : new ArrayList<>();

        LaptopFilter filter = new LaptopFilter(types, brands, chipCpus);

        Page<Laptop> laptopPage = laptopService.getAllLaptop(filter, start, limit);
        LaptopOutput output = new LaptopOutput();
        output.setTotalPage(laptopPage.getTotalPages());
        output.setPage(laptopPage.getNumber() + 1);
        output.setLaptopList(laptopPage.getContent());
        return output;
    }

    @GetMapping("/laptop/{id}")
    public LaptopDTO getLaptop(@PathVariable("id") Long id) {
        return laptopService.findLaptopById(id);
    }
}
