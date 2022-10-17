package com.petry.global.jwt.service;

import org.springframework.web.multipart.MultipartFile;

public interface ImgService {
    String save(MultipartFile multipartFile) throws Exception;

    void delete(String imgId);
}
