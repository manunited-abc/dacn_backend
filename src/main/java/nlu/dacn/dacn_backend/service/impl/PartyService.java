package nlu.dacn.dacn_backend.service.impl;

import nlu.dacn.dacn_backend.dto.request.PartyRequest;
import nlu.dacn.dacn_backend.entity.Party;
import nlu.dacn.dacn_backend.repository.PartyRepository;
import nlu.dacn.dacn_backend.service.IPartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Component
@ComponentScan({"vn.longvan"})
public class PartyService extends BaseService implements IPartyService {
    @Autowired
    PartyRepository partyRepository;
    @Override
    public List<Party> getParties(String type) {
        return partyRepository.findByType(type);
    }

    @Override
    public Party create(PartyRequest party) {
        Party partyCreate = mapper.map(party, Party.class);
        return partyRepository.save(partyCreate);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMulti(List<PartyRequest> partyRequests) {
        List<Party> parties = new ArrayList<>();
        for(PartyRequest partyRequest : partyRequests){
            parties.add(mapper.map(partyRequest,Party.class));
        }
        partyRepository.saveAll(parties);
    }

    @Override
    public void update(Party party) {

    }
}
