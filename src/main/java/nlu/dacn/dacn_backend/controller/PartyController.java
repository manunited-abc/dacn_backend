package nlu.dacn.dacn_backend.controller;

import lombok.RequiredArgsConstructor;
import nlu.dacn.dacn_backend.dto.response.ResponMessenger;
import nlu.dacn.dacn_backend.entity.Party;
import nlu.dacn.dacn_backend.service.IPartyService;
import nlu.dacn.dacn_backend.service.impl.PartyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RequestMapping("api/v1/party")
public class PartyController {
    @Autowired
    IPartyService partyService;
    @GetMapping("/get")
    public ResponseEntity<?> getByType(@RequestParam String type){
        List<Party> parties = partyService.getParties(type);
        return ResponseEntity.ok(parties);
    }
}
