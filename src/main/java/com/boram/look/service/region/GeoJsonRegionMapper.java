package com.boram.look.service.region;

import com.boram.look.domain.region.GeoUtil;
import com.boram.look.domain.region.GridXY;
import com.boram.look.domain.region.RegionPolygon;
import com.boram.look.domain.region.cache.SiGunGuRegion;
import com.boram.look.domain.region.entity.Region;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.ParseException;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.io.geojson.GeoJsonReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.StreamSupport;

public class GeoJsonRegionMapper {

    public List<SiGunGuRegion> buildRegionGeoJson(File geoJsonFile) throws Exception {
        List<JsonNode> features = parseFeaturesFromGeoJson(geoJsonFile);
        Map<String, List<RegionPolygon>> polygonsGroupedBySgg = groupPolygonsBySgg(features);

        List<SiGunGuRegion> result = new ArrayList<>();
        for (Map.Entry<String, List<RegionPolygon>> entry : polygonsGroupedBySgg.entrySet()) {
            result.add(mergePolygonsAndCreateRegion(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    private List<JsonNode> parseFeaturesFromGeoJson(File file) throws IOException {
        try (InputStream is = new FileInputStream(file)) {
            ObjectMapper mapper = new ObjectMapper(new JsonFactory());
            JsonNode root = mapper.readTree(is);
            JsonNode features = root.get("features");

            if (features == null || !features.isArray()) {
                throw new JsonMappingException(is, "'features' field is missing or not an array");
            }

            return StreamSupport.stream(features.spliterator(), false).toList();
        }
    }

    private Map<String, List<RegionPolygon>> groupPolygonsBySgg(List<JsonNode> features) throws ParseException, JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        GeoJsonReader reader = new GeoJsonReader();
        Map<String, List<RegionPolygon>> grouped = new HashMap<>();

        for (JsonNode feature : features) {
            JsonNode properties = feature.get("properties");
            String admCd = properties.get("adm_cd").asText();
            String siGunGuCode = properties.get("sgg").asText();
            String sidoCode = siGunGuCode.substring(0, 2);
            String admNm = properties.get("adm_nm").asText();
            String sidoNm = properties.get("sidonm").asText();
            String sggNm = properties.get("sggnm").asText();
            Geometry geom = reader.read(mapper.writeValueAsString(feature.get("geometry")));
            RegionPolygon polygon = RegionPolygon.builder()
                    .siGunGuNm(sggNm)
                    .sidoNm(sidoNm)
                    .admCd(admCd)
                    .admNm(admNm)
                    .polygon(geom)
                    .sidoCode(sidoCode)
                    .siGunGuCode(siGunGuCode)
                    .build();

            grouped.computeIfAbsent(siGunGuCode, k -> new ArrayList<>()).add(polygon);
        }

        return grouped;
    }

    private SiGunGuRegion mergePolygonsAndCreateRegion(String sggCode, List<RegionPolygon> polygons) {
        RegionPolygon represntativePolygon = polygons.getFirst();
        Geometry union = polygons.stream().map(RegionPolygon::polygon).reduce(Geometry::union).orElseThrow();
        Point centroid = union.getCentroid();
        GridXY grid = GeoUtil.toGrid(centroid.getY(), centroid.getX());

        return SiGunGuRegion.builder()
                .sgg(sggCode)
                .sggnm(represntativePolygon.siGunGuNm())
                .sido(represntativePolygon.sidoCode())
                .sidonm(represntativePolygon.sidoNm())
                .grid(grid)
                .center(centroid.getCoordinate())
                .polygon(union)
                .build();
    }


    public Map<String, Geometry> mergeSidoPolygons(List<Region> rows) {
        Map<String, Geometry> sidoToMergedGeometry = new HashMap<>();
        WKTReader reader = new WKTReader();

        for (Region row : rows) {
            Geometry polygon;
            try {
                polygon = reader.read(row.getPolygonText());
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            String sido = row.getSido();

            if (!sidoToMergedGeometry.containsKey(sido)) {
                sidoToMergedGeometry.put(sido, polygon);
            } else {
                sidoToMergedGeometry.computeIfPresent(sido, (k, existing) -> existing.union(polygon));
            }
        }

        return sidoToMergedGeometry;
    }

}
