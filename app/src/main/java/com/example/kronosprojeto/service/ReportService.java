package com.example.kronosprojeto.service;
import com.example.kronosprojeto.dto.ReportRequestDto;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ReportService {

    @POST("/api/report/adicionar")
    Call<ReportRequestDto> insertReport(@Header("Authorization") String token,@Body ReportRequestDto reportRequestDto);
}
