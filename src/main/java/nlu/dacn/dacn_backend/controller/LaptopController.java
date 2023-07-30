package nlu.dacn.dacn_backend.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.dto.request.LaptopDTO;
import nlu.dacn.dacn_backend.dto.response.ImageModel;
import nlu.dacn.dacn_backend.entity.Laptop;
import nlu.dacn.dacn_backend.model.request.LaptopFilter;
import nlu.dacn.dacn_backend.model.response.LaptopOutput;
import nlu.dacn.dacn_backend.service.impl.LaptopService;
import nlu.dacn.dacn_backend.utils.JsonUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    @GetMapping("/laptop/seo/{urlSeo}")
    public LaptopDTO getLaptopBySeo(@PathVariable("urlSeo") String urlSeo) {
        return laptopService.findFirstByUrlSeo(urlSeo);
    }
    @GetMapping("/laptop/images/{id}")
    public List<ImageModel> getImageLinks(@PathVariable("id") Long id) {
        return laptopService.getImageLinks(id);
    }
    @GetMapping("/laptop/images/seo/{urlSeo}")
    public List<ImageModel> getImageLinksBySeo(@PathVariable("urlSeo") String urlSeo) {
        return laptopService.getImageLinksBySeo(urlSeo);
    }

    @GetMapping("/laptop/brand")
    public List<String> getAllBrand() {
        return laptopService.getAllBrand();
    }

    @GetMapping("/laptop/type")
    public List<String> getAllType() {
        return laptopService.getAllType();
    }

    @GetMapping("/laptop/chipCpu")
    public List<String> getAllChipCpu() {
        return laptopService.getAllChipCpu();
    }
    @GetMapping("/laptop/product_name")
    public List<LaptopDTO> searchLaptopByProductName(@RequestParam("productName") String productName) {
        return laptopService.search(productName);
    }
    @PostMapping(value = "/laptop", consumes = "multipart/form-data")
    public LaptopDTO addNewLaptop(@RequestParam("laptopDTO") String laptopDTOJson,
                                  @RequestParam("avatarFile") MultipartFile avatarFile,
                                  @RequestParam("imageFiles") MultipartFile[] imageFiles) throws JsonProcessingException {
        LaptopDTO laptopDTO = JsonUtils.jsonToLaptopDTO(laptopDTOJson);
        return laptopService.addLaptop(laptopDTO, avatarFile, imageFiles);
    }
    @PutMapping("/laptop/update/{id}")
    public LaptopDTO updateLaptop(@RequestBody LaptopDTO laptopDTO, @PathVariable("id") long id) {
        laptopDTO.setId(id);
        return laptopService.updateLaptop(laptopDTO);
    }
    @PutMapping("/laptop/update-quantity/{id}/{newQuantity}")
    public ResponseEntity<?> updateLaptop(@PathVariable("id") long id, @PathVariable("newQuantity") int newQuantity) {
        laptopService.updateQuantity(id,newQuantity);
        return ResponseEntity.ok("Cập nhật thành công");
    }
}
