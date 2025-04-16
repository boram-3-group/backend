package com.boram.look.service.region;

import com.boram.look.domain.region.GeoUtil;
import com.boram.look.domain.region.GridXY;
import com.boram.look.domain.region.RegionPolygon;
import com.boram.look.domain.region.SiGunGuRegion;
import com.boram.look.domain.region.entity.Region;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.io.geojson.GeoJsonReader;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GeoJsonRegionMapper {

    private final GeometryFactory geometryFactory = new GeometryFactory();
    private final RegionCacheService cacheService;

    public void buildRegionGeoJson(File geoJsonFile) throws Exception {
        String geoJsonText = Files.readString(geoJsonFile.toPath());
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(geoJsonText);

        JsonNode features = root.get("features");
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

        // SiGunGuRegion 객체 생성
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
                    .code(code)
                    .name(name)
                    .grid(grid)
                    .center(centroid.getCoordinate())
                    .polygons(polygons)
                    .build();
            cacheService.cache().put(code, siGunGuRegion);
        }
    }

    public List<Region> toRegionEntities() {
        return cacheService.cache().values().stream()
                .map(region -> {
                    String polygonWkt = region.polygons().getFirst().polygon().toText(); // 첫 폴리곤만 사용
                    return Region.builder()
                            .code(region.code())
                            .name(region.name())
                            .lat(region.center().y)
                            .lon(region.center().x)
                            .polygonText(polygonWkt)
                            .nx(region.grid().nx())
                            .ny(region.grid().ny())
                            .build();
                }).collect(Collectors.toList());
    }

    public Optional<String> findSiGunGuCode(double lat, double lon) {
        Point userPoint = geometryFactory.createPoint(new Coordinate(lon, lat));

        for (SiGunGuRegion region : cacheService.cache().values()) {
            for (RegionPolygon polygon : region.polygons()) {
                if (polygon.polygon().contains(userPoint)) {
                    return Optional.of(region.code());
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Coordinate> getSiGunGuCenter(String siGunGuCode) {
        return Optional.ofNullable(cacheService.cache().get(siGunGuCode)).map(SiGunGuRegion::center);
    }

    public Optional<SiGunGuRegion> getSiGunGuRegion(String siGunGuCode) {
        return Optional.ofNullable(cacheService.cache().get(siGunGuCode));
    }

}
