package wooteco.subway.domain.path;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedMultigraph;

import wooteco.subway.domain.Station;
import wooteco.subway.domain.line.Line;
import wooteco.subway.domain.line.Lines;

public class DijkstraPathCalculator implements PathCalculator {
    private final DijkstraShortestPath<Station, ShortestPathEdge> dijkstraShortestPath;

    private DijkstraPathCalculator(
            DijkstraShortestPath<Station, ShortestPathEdge> dijkstraShortestPath) {
        this.dijkstraShortestPath = dijkstraShortestPath;
    }

    public static DijkstraPathCalculator from(List<Line> inputLines) {
        Lines lines = new Lines(inputLines);
        Set<Station> stations = lines.extractStations();
        WeightedMultigraph<Station, ShortestPathEdge> graph = getMultiGraph(stations, lines);
        return new DijkstraPathCalculator(new DijkstraShortestPath<>(graph));
    }

    private static WeightedMultigraph<Station, ShortestPathEdge> getMultiGraph(Set<Station> stations, Lines lines) {
        WeightedMultigraph<Station, ShortestPathEdge> graph = new WeightedMultigraph<>(ShortestPathEdge.class);
        addVertex(stations, graph);
        lines.addEdge(graph);
        return graph;
    }

    private static void addVertex(Set<Station> stations, WeightedMultigraph<Station, ShortestPathEdge> graph) {
        for (Station station : stations) {
            graph.addVertex(station);
        }
    }

    @Override
    public List<Station> calculateShortestPath(Station source, Station target) {
        return dijkstraShortestPath.getPath(source, target).getVertexList();
    }

    @Override
    public List<Long> calculateShortestPathLines(Station source, Station target) {
        List<ShortestPathEdge> edges = dijkstraShortestPath.getPath(source, target).getEdgeList();
        return edges.stream()
                .map(ShortestPathEdge::getLineId)
                .collect(Collectors.toList());
    }

    @Override
    public double calculateShortestDistance(Station source, Station target) {
        return dijkstraShortestPath.getPathWeight(source, target);
    }
}
