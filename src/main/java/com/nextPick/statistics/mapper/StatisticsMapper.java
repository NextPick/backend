package com.nextPick.statistics.mapper;

import com.nextPick.statistics.dto.StatisticsDto;
import com.nextPick.statistics.entity.Statistics;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StatisticsMapper {
    default StatisticsDto.Response statisticsToStatisticsResponseDto(List<Statistics> statisticsList) {
        StatisticsDto.Response response = new StatisticsDto.Response();
        for (Statistics statistics : statisticsList) {
            switch (statistics.getDescription()) {
                // BE 서브카테고리
                case "Java":
                    response.setJava(statistics.getCount());
                    break;
                case "Spring":
                    response.setSpring(statistics.getCount());
                    break;
                case "NodeJs":
                    response.setNodeJs(statistics.getCount());
                    break;
                case "ExpressJs":
                    response.setExpressJs(statistics.getCount());
                    break;
                case "Django":
                    response.setDjango(statistics.getCount());
                    break;
                case "Flask":
                    response.setFlask(statistics.getCount());
                    break;
                case "Ruby":
                    response.setRuby(statistics.getCount());
                    break;
                case "PHP":
                    response.setPHP(statistics.getCount());
                    break;
                case "GraphQL":
                    response.setGraphQL(statistics.getCount());
                    break;
                case "MySQL":
                    response.setMySQL(statistics.getCount());
                    break;
                // CS 서브카테고리
                case "Networking":
                    response.setNetworking(statistics.getCount());
                    break;
                case "OS":
                    response.setOS(statistics.getCount());
                    break;
                case "DataStructure":
                    response.setDataStructure(statistics.getCount());
                    break;
                case "Algorithms":
                    response.setAlgorithms(statistics.getCount());
                    break;
                case "SoftwareEngineering":
                    response.setSoftwareEngineering(statistics.getCount());
                    break;
                case "DesignPatterns":
                    response.setDesignPatterns(statistics.getCount());
                    break;
                case "ComputerArchitecture":
                    response.setComputerArchitecture(statistics.getCount());
                    break;
                case "Cybersecurity":
                    response.setCybersecurity(statistics.getCount());
                    break;
                case "ArtificialIntelligence":
                    response.setArtificialIntelligence(statistics.getCount());
                    break;
                // FE 서브카테고리
                case "React":
                    response.setReact(statistics.getCount());
                    break;
                case "Vue":
                    response.setVue(statistics.getCount());
                    break;
                case "Angular":
                    response.setAngular(statistics.getCount());
                    break;
                case "HTML5":
                    response.setHTML5(statistics.getCount());
                    break;
                case "CSS3":
                    response.setCSS3(statistics.getCount());
                    break;
                case "JavaScriptES6Plus":
                    response.setJavaScriptES6Plus(statistics.getCount());
                    break;
                case "TypeScript":
                    response.setTypeScript(statistics.getCount());
                    break;
                case "SassScss":
                    response.setSassScss(statistics.getCount());
                    break;
                case "Webpack":
                    response.setWebpack(statistics.getCount());
                    break;
                case "ResponsiveWebDesign":
                    response.setResponsiveWebDesign(statistics.getCount());
                    break;
                default:
                    // 처리하지 않는 경우에 대한 로직을 추가할 수 있습니다.
                    break;
            }
        }
        return response;
    }
}
