package com.tomy.tomy.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ClothesResponse {
    private Long id;
    private String name;
    private Integer price;
    private String category;
    private String imageUrl;
    private String description;
}
