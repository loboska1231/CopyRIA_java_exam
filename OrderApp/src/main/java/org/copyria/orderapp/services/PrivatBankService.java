package org.copyria.orderapp.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.copyria.orderapp.dto.ChangeDto;
import org.copyria.orderapp.entity.Change;
import org.copyria.orderapp.mapper.OrderMapper;
import org.copyria.orderapp.repository.ChangesRepository;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class PrivatBankService {
    private final RestClient restClient;
    private final OrderMapper orderMapper;
    private final ChangesRepository changesRepository;
    @Scheduled(fixedRate = 1, timeUnit = TimeUnit.DAYS)
    public void getChangeCourse(){
        ResponseEntity<ChangeDto[]> json = restClient.get().retrieve().toEntity(ChangeDto[].class);
//        changesRepository.deleteAll();
//        changesRepository.save(json);
        List<Change> changes = Arrays.stream(json.getBody())
                        .map(orderMapper::toChangeEntity)
                        .toList();
        changesRepository.deleteAll();
        changesRepository.saveAll(changes);
        log.info("Change course has been saved: {}", json);
    }
}
