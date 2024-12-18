package ru.practicum.ewm.dal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.ParamHitDto;
import ru.practicum.ewm.StatsDto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JdbcStatsRepository implements StatsRepository {
    private final NamedParameterJdbcOperations jdbc;

    private static final String INSERT_HIT_QUERY = """
            INSERT INTO endpointHits (app, uri, ip, timestamp)
            VALUES(:app, :uri, :ip, :timestamp)""";

    private static String getStatsQuery(boolean checkUri, boolean unique) {
        String count = unique ? "COUNT(DISTINCT ip)" : "COUNT(*)";
        String uris = checkUri ? " AND (uri IN (:uris))" : "";
        return String.format("""
                SELECT app, uri, %s hits
                FROM endpointHits
                WHERE (timestamp >= :start AND timestamp <= :end)%s
                GROUP BY app, uri
                ORDER BY hits DESC""", count, uris);
    }

    private static StatsDto mapRowTo(ResultSet rs, int rowNum) throws SQLException {
        StatsDto stats = new StatsDto();
        stats.setApp(rs.getString("app"));
        stats.setUri(rs.getString("uri"));
        stats.setHits(rs.getLong("hits"));
        return stats;
    }

    @Override
    public Long hit(@Valid ParamHitDto endpointHit) {
        GeneratedKeyHolder gkh = new GeneratedKeyHolder();
        jdbc.update(INSERT_HIT_QUERY,
                new MapSqlParameterSource("app", endpointHit.getApp())
                        .addValue("uri", endpointHit.getUri())
                        .addValue("ip", endpointHit.getIp())
                        .addValue("timestamp", endpointHit.getTimestamp()),
                gkh, new String[]{"id"});
        return gkh.getKeyAs(Long.class);
    }

    @Override
    public List<StatsDto> stats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        boolean haveUris = uris != null && !uris.isEmpty();
        String sql = getStatsQuery(haveUris, unique);
        return jdbc.query(sql,
                new MapSqlParameterSource("start", start)
                        .addValue("end", end)
                        .addValue("uris", haveUris ? uris : null),
                JdbcStatsRepository::mapRowTo);
    }
}
