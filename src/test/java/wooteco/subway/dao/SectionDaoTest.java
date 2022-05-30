package wooteco.subway.dao;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.line.Line;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@JdbcTest
@Import({SectionDao.class, StationDao.class, LineDao.class})
public class SectionDaoTest {
    @Autowired
    private SectionDao sectionDao;
    @Autowired
    private StationDao stationDao;
    @Autowired
    private LineDao lineDao;

    private final Station upTermination = new Station(1L, "상행종점역");
    private final Station downTermination = new Station(2L, "하행종점역");
    private final Station station = new Station(3L, "추가역");
    private Line line;

    @BeforeEach
    void setUp() {
        stationDao.save(upTermination);
        stationDao.save(downTermination);
        stationDao.save(station);

        Section section = new Section(upTermination, downTermination, 10);
        line = new Line("신분당선", "bg-red-600", 900, section);
        line = lineDao.save(line);
    }

    @DisplayName("기존 노선에 구간을 추가할 수 있다")
    @Test
    void save_sections() {
        Section section = new Section(downTermination, station, 5);
        line.addSection(section);
        sectionDao.saveAll(line.getSections(), line.getId());
    }

    @DisplayName("특정 구간을 삭제할 수 있다")
    @Test
    void delete() {
        Section section = new Section(downTermination, station, 5);
        line.addSection(section);
        sectionDao.saveAll(line.getSections(), line.getId());

        Line updatedLine = lineDao.findById(line.getId()).get();
        Section deletedSection = updatedLine.delete(station);
        assertThat(sectionDao.deleteById(deletedSection)).isEqualTo(1);
    }

    @DisplayName("특정 노선의 구간을 모두 삭제할 수 있다")
    @Test
    void deleteByLine() {
        Section section = new Section(downTermination, station, 5);
        line.addSection(section);
        sectionDao.saveAll(line.getSections(), line.getId());
        assertThat(sectionDao.deleteByLine(line.getId())).isEqualTo(2);
    }

    @DisplayName("삭제할 구간이 없을 경우 예외가 발생한다")
    @Test
    void delete_no_data() {
        Section section = new Section(upTermination, downTermination, 10);
        assertThatExceptionOfType(IllegalStateException.class)
                .isThrownBy(() -> sectionDao.deleteById(section))
                .withMessageContaining("존재하지 않습니다");
    }

    @DisplayName("저장한 모든 구간 목록을 불러온다")
    @Test
    void findAll() {
        Section section = new Section(downTermination, station, 10);

        line.addSection(section);

        sectionDao.saveAll(line.getSections(), line.getId());
        assertThat(sectionDao.findAll()).hasSize(2);
    }
}
