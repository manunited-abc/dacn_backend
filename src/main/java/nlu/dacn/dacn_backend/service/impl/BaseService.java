package nlu.dacn.dacn_backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BaseService {
    @Autowired
    public  ModelMapper mapper;
}
