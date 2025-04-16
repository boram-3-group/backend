package com.boram.look.service.region;

import com.boram.look.domain.region.GeoUtil;
import com.boram.look.domain.region.GridXY;
import com.boram.look.domain.region.RegionPolygon;
import com.boram.look.domain.region.SiGunGuRegion;
import com.boram.look.domain.region.entity.Region;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class GeoJsonRegionMapper {

    private final Map<String, SiGunGuRegion> siGunGuRegionMap = new HashMap<>();
    private final GeometryFactory geometryFactory = new GeometryFactory();

    public GeoJsonRegionMapper(File geoJsonFile) throws Exception {
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

            Geometry geom = reader.read(geometry.toString());
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
            GridXY grid = GeoUtil.toGrid(centroid.getCoordinate().x, centroid.getCoordinate().y);
            SiGunGuRegion siGunGuRegion = SiGunGuRegion.builder()
                    .code(code)
                    .name(name)
                    .grid(grid)
                    .center(centroid.getCoordinate())
                    .polygons(polygons)
                    .build();
            siGunGuRegionMap.put(code, siGunGuRegion);
        }
    }

    public List<Region> toRegionEntities() {
        return siGunGuRegionMap.values().stream()
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

        for (SiGunGuRegion region : siGunGuRegionMap.values()) {
            for (RegionPolygon polygon : region.polygons()) {
                if (polygon.polygon().contains(userPoint)) {
                    return Optional.of(region.code());
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Coordinate> getSiGunGuCenter(String siGunGuCode) {
        return Optional.ofNullable(siGunGuRegionMap.get(siGunGuCode)).map(SiGunGuRegion::center);
    }

    public Optional<SiGunGuRegion> getSiGunGuRegion(String siGunGuCode) {
        return Optional.ofNullable(siGunGuRegionMap.get(siGunGuCode));
    }


    public static void main(String[] args) throws Exception {
        File geoJson = new File("./HangJeongDong_ver20250401.geojson");
        GeoJsonRegionMapper mapper = new GeoJsonRegionMapper(geoJson);

        double lat = 37.4923;
        double lon = 127.0296;
        Optional<String> siGunGu = mapper.findSiGunGuCode(lat, lon);
        System.out.println("위치가 속한 시군구 코드: " + siGunGu.orElse("알 수 없음"));

        siGunGu.ifPresent(code -> {
            Optional<Coordinate> center = mapper.getSiGunGuCenter(code);
            center.ifPresent(c -> System.out.println("중심 좌표: lat=" + c.y + ", lon=" + c.x));
        });

        Coordinate center = mapper.getSiGunGuCenter("11740").orElseThrow();
        GridXY grid = GeoUtil.toGrid(center.y, center.x); // 위도(y), 경도(x)
        System.out.println("기상청 격자 좌표: " + grid);
    }
}
