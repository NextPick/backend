package com.nextPick.report.mapper;

import com.nextPick.report.dto.ReportDto;
import com.nextPick.report.entity.Report;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReportMapper {
    Report reportPostDtoToReport(ReportDto.Post post);
    ReportDto.Response reportToReportResponseDto(Report report);
    List<ReportDto.Responses> reportListToReportResponsesDto(List<Report> responseList);
}
