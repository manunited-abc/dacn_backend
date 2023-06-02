package nlu.dacn.dacn_backend.controller;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.model.response.LaptopOutput;
import nlu.dacn.dacn_backend.service.impl.LaptopService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@RestController
@CrossOrigin(origins = "*")
public class LaptopController {
    private final LaptopService laptopService;

    @GetMapping("/laptop")
    public LaptopOutput getLaptop(@RequestParam("start") int start,
                                  @RequestParam("limit") int limit,
                                  @RequestParam(value = "types", required = false) String typeJson,
                                  @RequestParam(value = "brands", required = false) String brandJson,
                                  @RequestParam(value = "chipCpus", required = false) String chipCpuJson) {

        return null;
    }
}
