package wooteco.subway.dto.response;

import java.util.List;

import wooteco.subway.domain.line.Line;

public class LineCreateResponse {
    private final Long id;
    private final String name;
    private final String color;
    private final List<StationResponse> stations;

    public LineCreateResponse(Line line) {
        this(line.getId(), line.getName(), line.getColor(), StationResponse.of(line.getStations()));
    }

    public LineCreateResponse(Long id, String name, String color, List<StationResponse> stations) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.stations = stations;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public List<StationResponse> getStations() {
        return stations;
    }
}
