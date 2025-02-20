import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;

public class VolcanoAnalyzer {
    private List<Volcano> volcanos;

    public void loadVolcanoes(Optional<String> pathOpt) throws IOException, URISyntaxException {
        try {
            String path = pathOpt.orElse("volcano.json");
            URL url = this.getClass().getClassLoader().getResource(path);
            String jsonString = new String(Files.readAllBytes(Paths.get(url.toURI())));
            ObjectMapper objectMapper = new ObjectMapper();
            TypeFactory typeFactory = objectMapper.getTypeFactory();
            volcanos = objectMapper.readValue(jsonString,
                    typeFactory.constructCollectionType(List.class, Volcano.class));
        } catch (Exception e) {
            throw (e);
        }
    }

    public Integer numbVolcanoes() {
        return volcanos.size();
    }

    // add methods here to meet the requirements in README.md
    public List<Volcano> eruptedInEighties() {
        return volcanos.stream().filter(i -> i.getYear() >= 1980 && i.getYear() < 1990).collect(Collectors.toList());
    }

    public String[] highVEI() {
        return volcanos.stream().filter(i -> i.getVEI() >= 6).map(Volcano::getName).collect(Collectors.toList())
                .toArray(new String[0]);
    }

    public Volcano mostDeadly() {
        return volcanos.stream()
                .max(Comparator.comparingInt(d -> Integer.parseInt((d.getDEATHS().isEmpty() ? "0" : d.getDEATHS()))))
                .orElse(null);
    }

    public double causedTsunami() {
        return volcanos.stream().filter(i -> i.getTsu().equals("tsu")).count() * 100 / volcanos.size();
    }

    public String mostCommonType() {
        return volcanos.stream()
                .collect(Collectors.collectingAndThen(Collectors.groupingBy(Volcano::getType, Collectors.counting()),
                        i -> i.entrySet().stream()
                                .max(Map.Entry.comparingByValue()).get().getKey()));
    }

    public int eruptionsByCountry(String country) {
        return volcanos.stream().filter(i -> i.getCountry().equals(country)).collect(Collectors.toList()).size();
    }

    public double averageElevation() {
        return volcanos.stream().mapToDouble(Volcano::getElevation).sum() / volcanos.size();
    }

    public String[] volcanoTypes() {
        return volcanos.stream().map(Volcano::getType).distinct().collect(Collectors.toList()).toArray(new String[0]);
    }

    public double percentNorth() {
        return volcanos.stream().filter(i -> i.getLatitude() > 0).count() * 100d / volcanos.size();
    }

    public String[] manyFilters() {
        return volcanos.stream()
                .filter(i -> i.getYear() > 1800 && i.getTsu().equals("") && i.getLatitude() < 0 && i.getVEI() == 5)
                .map(Volcano::getName).collect(Collectors.toList()).toArray(new String[0]);
    }

    public String[] elevatedVolcanoes(int elevation) {
        return volcanos.stream().filter(i -> i.getElevation() >= elevation).map(Volcano::getName)
                .collect(Collectors.toList()).toArray(new String[0]);
    }

    public String[] topAgentsOfDeath() {
        return volcanos.stream()
                .sorted((i, j) -> Integer.parseInt(j.getDEATHS().isEmpty() ? "0" : j.getDEATHS())
                        - Integer.parseInt(i.getDEATHS().isEmpty() ? "0" : i.getDEATHS()))
                .limit(10).map(v -> Arrays.asList(v.getAgent().isEmpty() ? new String[0] : v.getAgent().split(",")))
                .flatMap(List::stream).distinct().collect(Collectors.toList()).toArray(new String[0]);
    }
}
