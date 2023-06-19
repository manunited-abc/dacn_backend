package nlu.dacn.dacn_backend.service.impl;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LaptopService implements ILaptopService {
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

}
