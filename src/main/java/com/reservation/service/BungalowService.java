package com.reservation.service;

import com.reservation.dto.BungalowRequest;
import com.reservation.dto.BungalowResponse;
import com.reservation.entity.Bungalow;
import com.reservation.repository.BungalowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BungalowService {

    private final BungalowRepository bungalowRepository;

    public BungalowResponse createBungalow(BungalowRequest request){
        Bungalow bungalow = new Bungalow();

        bungalow.setName(request.getName());
        bungalow.setDescription(request.getDescription());
        bungalow.setPricePerNight(request.getPricePerNight());

        bungalow.setCreatedAt(LocalDateTime.now());

        Bungalow savedBungalow = bungalowRepository.save(bungalow);

        return new BungalowResponse(savedBungalow);
    }

    public BungalowResponse updateBungalow(Integer bungalowId , BungalowRequest request){
        Bungalow bungalow = bungalowRepository.findById(bungalowId)
                        .orElseThrow(() -> new ResponseStatusException(
                                HttpStatus.BAD_REQUEST,
                                "Bungalow not found"
                        ));

        bungalow.setName(request.getName());
        bungalow.setDescription(request.getDescription());
        bungalow.setPricePerNight(request.getPricePerNight());

        bungalow.setUpdatedAt(LocalDateTime.now());

        Bungalow savedBungalow = bungalowRepository.save(bungalow);

        return new BungalowResponse(savedBungalow);
    }

    public List<BungalowResponse> getAllBungalows(){
        return bungalowRepository.findAll()
                .stream()
                .map(BungalowResponse::new)
                .toList();
    }

    public BungalowResponse getBungalowById(Integer bungalowId){
        Bungalow bungalow = bungalowRepository.findById(bungalowId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Bungalow not found"
                ));

        return new BungalowResponse(bungalow);
    }

    // Helper Methods
}
