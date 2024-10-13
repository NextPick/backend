package com.nextPick.statistics.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

public class StatisticsDto {@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public static class Response {
    // BE 서브카테고리
    private int Java;
    private int Spring;
    private int NodeJs;
    private int ExpressJs;
    private int Django;
    private int Flask;
    private int Ruby;
    private int PHP;
    private int GraphQL;
    private int MySQL;

    // CS 서브카테고리
    private int Networking;
    private int OS;
    private int DataStructure;
    private int Algorithms;
    private int SoftwareEngineering;
    private int DesignPatterns;
    private int ComputerArchitecture;
    private int Cybersecurity;
    private int ArtificialIntelligence;

    // FE 서브카테고리
    private int React;
    private int Vue;
    private int Angular;
    private int HTML5;
    private int CSS3;
    private int JavaScriptES6Plus;
    private int TypeScript;
    private int SassScss;
    private int Webpack;
    private int ResponsiveWebDesign;
}

}
