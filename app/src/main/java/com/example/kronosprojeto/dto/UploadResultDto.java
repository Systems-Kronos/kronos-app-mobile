package com.example.kronosprojeto.dto;

public class UploadResultDto {


    public int width;
    public int height;
    public String url;
    public String secure_url;

    public UploadResultDto(int width, int height, String url, String secure_url) {
        this.width = width;
        this.height = height;
        this.url = url;
        this.secure_url = secure_url;
    }

    public UploadResultDto() {
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSecure_url() {
        return secure_url;
    }

    public void setSecure_url(String secure_url) {
        this.secure_url = secure_url;
    }
}
