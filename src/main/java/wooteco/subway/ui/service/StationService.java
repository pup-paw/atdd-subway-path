package wooteco.subway.ui.service;

import java.util.List;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import wooteco.subway.dao.StationDao;
import wooteco.subway.domain.Station;
import wooteco.subway.dto.request.StationRequest;
import wooteco.subway.dto.response.StationResponse;

@Service
public class StationService {
    private final StationDao stationDao;

    public StationService(StationDao stationDao) {
        this.stationDao = stationDao;
    }

    public StationResponse create(StationRequest stationRequest) {
        Station station = new Station(stationRequest.getName());
        try {
            Station newStation = stationDao.save(station);
            return StationResponse.from(newStation);
        } catch (DuplicateKeyException ignored) {
            throw new IllegalStateException("이미 존재하는 역 이름입니다.");
        }
    }

    public List<StationResponse> findAll() {
        List<Station> stations = stationDao.findAll();
        return StationResponse.of(stations);
    }

    public void delete(Long id) {
        stationDao.deleteById(id);
    }
}
