package nlu.dacn.dacn_backend.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.converter.LaptopConverter;
import nlu.dacn.dacn_backend.dto.request.LaptopDTO;
import nlu.dacn.dacn_backend.dto.response.ImageModel;
import nlu.dacn.dacn_backend.entity.ImageLaptop;
import nlu.dacn.dacn_backend.entity.Laptop;
import nlu.dacn.dacn_backend.exception.ServiceException;
import nlu.dacn.dacn_backend.model.request.LaptopFilter;
import nlu.dacn.dacn_backend.repository.LaptopFilterRepository;
import nlu.dacn.dacn_backend.repository.LaptopRepository;
import nlu.dacn.dacn_backend.service.ILaptopService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LaptopService extends BaseService implements ILaptopService {
    private final LaptopFilterRepository laptopFilterRepository;
    private final LaptopRepository laptopRepository;
    private final LaptopConverter laptopConverter;

    @Override
    public Page<Laptop> getAllLaptop(LaptopFilter filter, int start, int limit) {
        Page<Laptop> laptopPage = laptopFilterRepository.findLaptopByFilter(filter.getTypes(), filter.getBrands(), filter.getChipCpus(), start - 1, limit);
        return laptopPage;
    }

    @Override
    public LaptopDTO findLaptopById(Long id) {
        Optional<Laptop> laptopOptional = laptopRepository.findById(id);
        if (laptopOptional.isPresent()) {
            return laptopConverter.toLaptopDTO(laptopOptional.get());
        } else {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Không tìm thấy laptop hoặc đã bị xóa");
        }
    }

    @Override
    public List<ImageModel> getImageLinks(Long id) {
        Optional<Laptop> laptopOptional = laptopRepository.findById(id);
        if (laptopOptional.isPresent()) {
            List<ImageModel> imageModels = new ArrayList<>();
            List<ImageLaptop> imageLaptops = laptopOptional.get().getImages();

            ImageModel imageModel;
            for (ImageLaptop il : imageLaptops) {
                imageModel = new ImageModel();
                imageModel.setUid(il.getId());
                imageModel.setName(il.getImageName());
                imageModel.setUrl(il.getLinkImage());
                imageModels.add(imageModel);
            }
            return imageModels;
        } else {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Không tìm thấy laptop");
        }
    }

    @Override
    public List<ImageModel> getImageLinksBySeo(String urlSeo) {
        Laptop laptopOptional = laptopRepository.findFirstByUrlSeo(urlSeo);
        if (!org.springframework.util.ObjectUtils.isEmpty(laptopOptional)) {
            List<ImageModel> imageModels = new ArrayList<>();
            List<ImageLaptop> imageLaptops = laptopOptional.getImages();
            ImageModel imageModel;
            for (ImageLaptop il : imageLaptops) {
                imageModel = new ImageModel();
                imageModel.setUid(il.getId());
                imageModel.setName(il.getImageName());
                imageModel.setUrl(il.getLinkImage());
                imageModels.add(imageModel);
            }
            return imageModels;
        } else {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }
    }

    @Override
    public List<String> getAllBrand() {
        return laptopRepository.findAllBrand();
    }

    @Override
    public List<String> getAllType() {
        return laptopRepository.findAllType();
    }

    @Override
    public List<String> getAllChipCpu() {
        return laptopRepository.findAllChipCpu();
    }

    @Override
    public List<LaptopDTO> getLaptopByProductName(String key) {
        List<LaptopDTO> result = new ArrayList<>();
        List<Laptop> laptops = laptopRepository.findByProductNameContainingIgnoreCase(key);
        LaptopDTO laptopDTO;
        for (Laptop laptop : laptops) {
            laptopDTO = laptopConverter.toLaptopDTO(laptop);
            result.add(laptopDTO);
        }
        return result;
    }

    @Override
    public LaptopDTO addLaptop(LaptopDTO laptopDTO, MultipartFile linkAvatar, MultipartFile[] imageFiles) {
        try {
            List<Laptop> laptops = laptopRepository.findByProductNameContainingIgnoreCase(laptopDTO.getProductName());
            if (!CollectionUtils.isEmpty(laptops)) {
                throw new RuntimeException("Tên sản phẩm đã tồn tại");
            }

            // Lưu ảnh avatar laptop vào cloud
            String linkAva = getLink(linkAvatar);
            laptopDTO.setLinkAvatar(linkAva);
            Laptop laptop = laptopConverter.toLaptop(laptopDTO, new Laptop());
            ImageLaptop imageLaptop;
            String fileName;
            for (MultipartFile file : imageFiles) {
                imageLaptop = new ImageLaptop();
                fileName = file.getOriginalFilename();
                imageLaptop.setImageName(fileName);
                imageLaptop.setLinkImage(getLink(file));
                imageLaptop.setLaptop(laptop);
                laptop.getImages().add(imageLaptop);
            }
//            Generate url seo
            String path = generateUrlSeo(laptop.getProductName());
            laptop.setUrlSeo(path);
            laptop = laptopRepository.save(laptop);
            return laptopConverter.toLaptopDTO(laptop);

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi thêm hình ảnh, vui lòng thử lại");
        }
    }

    private String getLink(MultipartFile file) throws IOException {
        Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", "diyrrlmqk",
                "api_key", "137284888978213",
                "api_secret", "Rxu7XVXAxkeUXoEcwgt1s4dSpAs"));

        String fileName = file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf("."));
        Map params = ObjectUtils.asMap(
                "public_id", fileName
        );
        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
        String linkImage = (String) uploadResult.get("url");
        return linkImage;
    }

    public String generateUrlSeo(String path) {
        // Loại bỏ dấu trong chuỗi
        String normalizedText = Normalizer.normalize(path, Normalizer.Form.NFD).toLowerCase(Locale.ROOT);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        normalizedText = pattern.matcher(normalizedText).replaceAll("");

        // Thay thế khoảng trắng bằng gạch ngang
        String[] words = normalizedText.split("\\W+");
        String formattedText = String.join("-", words);
        return formattedText;
    }

    public LaptopDTO findFirstByUrlSeo(String urlSeo) {
        Laptop laptop = laptopRepository.findFirstByUrlSeo(urlSeo);
        if (org.springframework.util.ObjectUtils.isEmpty(laptop)) {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }
        return laptopConverter.toLaptopDTO(laptop);
    }

    @Override
    public LaptopDTO updateLaptop(LaptopDTO laptopDTO) {
        Long id = laptopDTO.getId();
        if (id != null) {
            Optional<Laptop> laptopOptional = laptopRepository.findById(id);
            //Kiem tra ten da ton tai ?

            if (!laptopOptional.get().getProductName().equals(laptopDTO.getProductName())) {
                List<Laptop> laptops = laptopRepository.findByProductNameContainingIgnoreCase(laptopDTO.getProductName());
                if (!CollectionUtils.isEmpty(laptops)) {
                    throw new RuntimeException("Tên sản phẩm đã tồn tại");
                }
            }
            if (laptopOptional.isPresent()) {
                Laptop oldLaptop = laptopOptional.get();
                String linkAvatar = oldLaptop.getLinkAvatar();
                Laptop laptop = laptopConverter.toLaptop(laptopDTO, oldLaptop);
                laptop.setLinkAvatar(linkAvatar);
                laptop.setUrlSeo(generateUrlSeo(laptop.getProductName()));
                laptop = laptopRepository.save(laptop);
                return laptopConverter.toLaptopDTO(laptop);
            } else {
                throw new RuntimeException("Không tìm thấy sản phầm");
            }
        } else {
            throw new RuntimeException("Không tìm thấy sản phầm");
        }
    }

    @Override
    public void deleteLaptop(Long... ids) {
        if (ids != null && ids.length > 0) {
            for (Long id : ids) {
                Optional<Laptop> laptopOptional = laptopRepository.findById(id);
                if (laptopOptional.isPresent()) {
                    laptopRepository.delete(laptopOptional.get());
                }
            }
        } else {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR, "Chưa có id laptop");
        }
    }

    public List<LaptopDTO> search(String keyword) {
        List<Laptop> searchResult1 = laptopRepository.findByName(keyword);
        List<Laptop> searchResult2 = laptopRepository.findByType(keyword);
        List<Laptop> searchResult3 = laptopRepository.findByBrand(keyword);
        List<Laptop> searchResult4 = laptopRepository.findByChipCpu(keyword);
        List<Laptop> searchResult5 = laptopRepository.findByCpu(keyword);
        List<Laptop> searchResult6 = laptopRepository.findByRam(keyword);
        List<Laptop> searchResult7 = laptopRepository.findByStorage(keyword);
        List<Laptop> searchResult8 = laptopRepository.findByGraphics(keyword);
        List<Laptop> searchResult9 = laptopRepository.findByBattery(keyword);
        List<Laptop> searchResult10 = laptopRepository.findByColor(keyword);
        List<Long> listFilterId = retainAll(searchResult1, searchResult2, searchResult3, searchResult4, searchResult5
                , searchResult6, searchResult7, searchResult8, searchResult9, searchResult10);
        List<Laptop> listSearchById = laptopRepository.findAllById(listFilterId);
        List<LaptopDTO> results = new ArrayList<>();

        for (Laptop laptop2 : listSearchById) {
            results.add(laptopConverter.toLaptopDTO(laptop2));
        }
        return results;
    }

    public List<Long> retainAll(List<Laptop>... lists) {
        //List<Laptop> result = new ArrayList<>();
        List<Long> result = new ArrayList<>();
        for (int i = 0; i < lists.length ; i++) {
            List<Long> list = lists[i].stream().map(Laptop::getId).collect(Collectors.toList());
            if (CollectionUtils.isEmpty(result)) {
                if (!CollectionUtils.isEmpty(list)) {
                    result = list;
                }
            } else {
                if (!CollectionUtils.isEmpty(list)){
                    List<Long> temp=  result;
                    List<Long> temp2=  list;
                    boolean flag = false;
                    for(Long t: temp){
                        if(temp2.contains(t)){
                            flag = true;
                            break;
                        }
                    }

                    if(!flag) result.addAll(list);
                    else result.retainAll(list);
                }

            }
        }
        return result;
    }


}
