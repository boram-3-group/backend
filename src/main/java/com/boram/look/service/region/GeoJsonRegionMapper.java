package com.boram.look.service.region;

import com.boram.look.domain.region.GeoUtil;
import com.boram.look.domain.region.GridXY;
import com.boram.look.domain.region.RegionPolygon;
import com.boram.look.domain.region.SiGunGuRegion;
import com.boram.look.domain.region.entity.Region;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.geojson.GeoJsonReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GeoJsonRegionMapper {

    public List<SiGunGuRegion> buildRegionGeoJson(File geoJsonFile) throws Exception {
        try (InputStream is = new FileInputStream(geoJsonFile)) {
            JsonFactory factory = new JsonFactory();
            JsonParser parser = factory.createParser(is);
            ObjectMapper mapper = new ObjectMapper();

            JsonNode root = mapper.readTree(parser);
            JsonNode features = root.get("features");
            if (features == null || !features.isArray()) {
                throw new JsonMappingException(is, "Invalid GeoJSON format: 'features' field is missing or not an array");
            }

            GeoJsonReader reader = new GeoJsonReader();

            Map<String, List<RegionPolygon>> tempMap = new HashMap<>();

            for (JsonNode feature : features) {
                String fullAdmCd = feature.get("properties").get("adm_cd").asText();
                String siGunGuCode = fullAdmCd.substring(0, 5); // 시군구 코드
                String admNm = feature.get("properties").get("adm_nm").asText();
                JsonNode geometry = feature.get("geometry");

                Geometry geom = reader.read(mapper.writeValueAsString(geometry));
                RegionPolygon region = new RegionPolygon(siGunGuCode, admNm, geom);

                tempMap.computeIfAbsent(siGunGuCode, k -> new ArrayList<>()).add(region);
            }

            List<SiGunGuRegion> siGunGuRegions = new ArrayList<>();
            for (Map.Entry<String, List<RegionPolygon>> entry : tempMap.entrySet()) {
                String code = entry.getKey();
                List<RegionPolygon> polygons = entry.getValue();
                String name = polygons.getFirst().admNm().split(" ")[1]; // ex: "서울특별시 강남구 역삼동" → "강남구"

                Geometry union = polygons.getFirst().polygon();
                for (int i = 1; i < polygons.size(); i++) {
                    union = union.union(polygons.get(i).polygon());
                }
                Point centroid = union.getCentroid();
                GridXY grid = GeoUtil.toGrid(centroid.getCoordinate().y, centroid.getCoordinate().x);
                SiGunGuRegion siGunGuRegion = SiGunGuRegion.builder()
                        .sgg(code)
                        .sggnm(name)
                        .sido(code)
                        .sidonm(code)
                        .grid(grid)
                        .center(centroid.getCoordinate())
                        .polygon(union)
                        .build();
                siGunGuRegions.add(siGunGuRegion);
            }

            return siGunGuRegions;
        }
    }

    public List<Region> toRegionEntities(List<SiGunGuRegion> regions) {
        return regions.stream()
                .map(siGunGu -> {
                    String polygonWkt = siGunGu.polygon().toText(); // 첫 폴리곤만 사용
                    return Region.builder()
                            .sgg(siGunGu.sgg())
                            .sggnm(siGunGu.sggnm())
                            .sido(siGunGu.sido())
                            .sidonm(siGunGu.sidonm())
                            .lat(siGunGu.center().y)
                            .lon(siGunGu.center().x)
                            .polygonText(polygonWkt)
                            .nx(siGunGu.grid().nx())
                            .ny(siGunGu.grid().ny())
                            .build();
                }).collect(Collectors.toList());
    }

}
