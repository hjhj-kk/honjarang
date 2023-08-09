package com.example.honjarang.domain.user.dto;

import com.example.honjarang.domain.user.entity.User;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
public class UserInfoDto {
    private String nickname;
    private String email;
    private String profileImage;
    private Integer point;
    private String address;
    private Double latitude;
    private Double longitude;


    public UserInfoDto(User user) {
        this.nickname = user.getNickname();
        this.email = user.getEmail();
        this.profileImage = "https://honjarang-bucket.s3.ap-northeast-2.amazonaws.com/profileImage/" + user.getProfileImage();
        this.point = user.getPoint();
        this.address = user.getAddress();
        this.latitude = user.getLatitude();
        this.longitude = user.getLongitude();
    }
}