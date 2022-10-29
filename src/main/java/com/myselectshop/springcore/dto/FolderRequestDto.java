package com.myselectshop.springcore.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class FolderRequestDto {
    List<String> folderNames;
}
