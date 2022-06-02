package wooteco.subway.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Component;

import wooteco.subway.domain.Section;
import wooteco.subway.domain.Station;
import wooteco.subway.domain.line.Line;

@Component
public class LineDao {
    private final SimpleJdbcInsert jdbcInsert;
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public LineDao(DataSource dataSource) {
        this.jdbcInsert = new SimpleJdbcInsert(dataSource)
                .withTableName("line")
                .usingGeneratedKeyColumns("id");
        this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
    }

    public Line save(Line line) {
        SqlParameterSource param = new BeanPropertySqlParameterSource(line);
        Long id = jdbcInsert.executeAndReturnKey(param).longValue();
        return new Line(id, line);
    }

    private Line mapToLine(ResultSet resultSet) throws SQLException {
        return new Line(
                resultSet.getLong("id"),
                resultSet.getString("name"),
                resultSet.getString("color"),
                resultSet.getInt("extra_fare"),
                findSectionsById(resultSet.getLong("id")));
    }

    public List<Line> findAll() {
        String sql = "SELECT * FROM line";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> mapToLine(resultSet));
    }

    public List<Line> findByIds(List<Long> ids) {
        String sql = "SELECT * FROM line WHERE id IN (:ids)";
        SqlParameterSource parameterSource = new MapSqlParameterSource("ids", ids);
        return jdbcTemplate.query(sql, parameterSource, (resultSet, rowNum) -> mapToLine(resultSet));
    }

    public Optional<Line> findById(Long id) {
        String sql = "SELECT * FROM line WHERE id = :id";
        SqlParameterSource paramSource = new MapSqlParameterSource("id", id);
        try {
            return Optional.ofNullable(
                    jdbcTemplate.queryForObject(sql, paramSource, (resultSet, rowNum) -> mapToLine(resultSet)));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    private List<Section> findSectionsById(Long id) {
        String sql = "SELECT "
                + "sec.id, sec.distance, "
                + "sec.up_station_id, us.name up_station_name,"
                + "sec.down_station_id, ds.name down_station_name "
                + "FROM section AS sec "
                + "JOIN station AS us ON sec.up_station_id = us.id "
                + "JOIN station AS ds ON sec.down_station_id = ds.id "
                + "WHERE line_id = :id ORDER BY index_num";

        SqlParameterSource paramSource = new MapSqlParameterSource("id", id);
        return jdbcTemplate.query(sql, paramSource, (resultSet, rowNum) -> mapToSection(resultSet));
    }

    private Section mapToSection(ResultSet resultSet) throws SQLException {
        Long upStationId = resultSet.getLong("up_station_id");
        String upStationName = resultSet.getString("up_station_name");

        Long downStationId = resultSet.getLong("down_station_id");
        String downStationName = resultSet.getString("down_station_name");

        return new Section(
                resultSet.getLong("id"),
                new Station(upStationId, upStationName),
                new Station(downStationId, downStationName),
                resultSet.getInt("distance")
        );
    }

    public int update(Line line) {
        String sql = "UPDATE line SET name = :name, color = :color WHERE id = :id";

        SqlParameterSource paramSource = new MapSqlParameterSource("name", line.getName())
                .addValue("color", line.getColor())
                .addValue("id", line.getId());

        int updatedCount = jdbcTemplate.update(sql, paramSource);
        validateUpdated(updatedCount);
        return updatedCount;
    }

    private void validateUpdated(int updatedCount) {
        if (updatedCount == 0) {
            throw new IllegalStateException("수정하고자 하는 노선이 존재하지 않습니다.");
        }
    }

    public int delete(Long id) {
        String sql = "DELETE FROM line WHERE id = :id";

        SqlParameterSource paramSource = new MapSqlParameterSource("id", id);
        int deletedCount = jdbcTemplate.update(sql, paramSource);
        validateDeleted(deletedCount);
        return deletedCount;
    }

    private void validateDeleted(int deletedCount) {
        if (deletedCount == 0) {
            throw new IllegalStateException("삭제하고자 하는 노선이 존재하지 않습니다.");
        }
    }
}
