package wooteco.subway.domain;

import java.util.LinkedList;
import java.util.List;

public class Line {

    private final Long id;
    private final String name;
    private final String color;
    private final int extraFare;
    private final Sections sections;

    public Line(Long id, Line line) {
        this(id, line.getName(), line.getColor(), line.extraFare, line.getSections());
    }

    public Line(String name, String color, int extraFare, Section section) {
        this(null, name, color, extraFare, List.of(section));
    }

    public Line(Long id, String name, String color, int extraFare) {
        this(id, name, color, extraFare, new LinkedList<>());
    }

    public Line(Long id, String name, String color, int extraFare, List<Section> sections) {
        this(id, name, color, extraFare, new Sections(sections));
    }

    public Line(Long id, String name, String color, int extraFare, Sections sections) {
        validateNotNull(name, "name");
        validateNotNull(color, "color");
        this.id = id;
        this.name = name;
        this.color = color;
        this.extraFare = extraFare;
        this.sections = sections;
    }

    private void validateNotNull(String input, String param) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException(String.format("%s은 필수 입력값입니다.", param));
        }
    }

    public void addSection(Section section) {
        sections.add(section);
    }

    public Section delete(Station station) {
        return sections.delete(station);
    }

    public boolean hasSameNameWith(Line otherLine) {
        return this.name.equals(otherLine.name);
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

    public Sections getSections() {
        return sections;
    }

    public List<Station> getStations() {
        return sections.getAllStations();
    }

    public int getExtraFare() {
        return extraFare;
    }
}
