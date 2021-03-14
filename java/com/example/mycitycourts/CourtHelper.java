package com.example.mycitycourts;

import java.io.Serializable;

public class CourtHelper implements Serializable {
    String name;
    Double latitude;
    Double longitude;
    String type;
    long courtId;
    Integer num;
    String fac;
    String img;
    String username;
    String description;
    Integer status;
    String Size;

    public CourtHelper(String name, Double latitude, Double longitude, String type, long courtId, Integer num, String fac, String img, String username, String description, Integer status, String size) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.type = type;
        this.courtId = courtId;
        this.num = num;
        this.fac = fac;
        this.img = img;
        this.username = username;
        this.description = description;
        this.status = status;
        Size = size;
    }


    public CourtHelper() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public long getCourtId() {
        return courtId;
    }

    public void setCourtId(long courtId) {
        this.courtId = courtId;
    }

    public Integer getNum() {
        return num;
    }

    public void setNum(Integer num) {
        this.num = num;
    }

    public String getFac() {
        return fac;
    }

    public void setFac(String fac) {
        this.fac = fac;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getSize() {
        return Size;
    }

    public void setSize(String size) {
        Size = size;
    }

}
