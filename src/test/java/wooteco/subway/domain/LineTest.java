package wooteco.subway.domain;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import wooteco.subway.domain.line.Line;

class LineTest {

    @Test
    @DisplayName("이름이 같은지 확인한다.")
    public void hasSameNameWith() {
        // given
        Station upTermination = new Station(1L, "상행종점역");
        Station downTermination = new Station(2L, "하행종점역");
        Section section = new Section(upTermination, downTermination, 10);
        Line line = new Line("신분당선", "bg-red-600", 900, section);
        // when
        boolean hasSameName = line.hasSameNameWith(new Line("신분당선", "bg-red-600", 900, section));
        // then
        assertThat(hasSameName).isTrue();
    }
}
