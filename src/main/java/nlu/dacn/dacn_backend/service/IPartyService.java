package nlu.dacn.dacn_backend.service;

import nlu.dacn.dacn_backend.dto.request.PartyRequest;
import nlu.dacn.dacn_backend.entity.Party;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IPartyService {
    public List<Party> getParties(String type);
    public Party create(PartyRequest party);
    public void createMulti(List<PartyRequest> partyRequests);
    public void update(Party party);
}
