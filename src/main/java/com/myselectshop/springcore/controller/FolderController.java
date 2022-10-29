package com.myselectshop.springcore.controller;

import com.myselectshop.springcore.dto.FolderRequestDto;
import com.myselectshop.springcore.model.Folder;
import com.myselectshop.springcore.model.Product;
import com.myselectshop.springcore.model.User;
import com.myselectshop.springcore.security.UserDetailsImpl;
import com.myselectshop.springcore.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FolderController {
    private final FolderService folderService;

    @Autowired
    public FolderController(FolderService folderService){
        this. folderService = folderService;
    }

    @PostMapping("/api/folders")
    public List<Folder> addFolders(
            /*
                RequestBody는 객체를
                RequestParam은 url 상의 데이터
            */
            @RequestBody FolderRequestDto folderRequestDto,
            @AuthenticationPrincipal UserDetailsImpl userDetails
    ){
            List<String> folderNames = folderRequestDto.getFolderNames();
            User user = userDetails.getUser();

            return folderService.addFolders(folderNames, user);
    }

    @GetMapping("/api/folders")
    public List<Folder> getFolders(@AuthenticationPrincipal UserDetailsImpl userDetails){
        return folderService.getFolders(userDetails.getUser());
    }

    @GetMapping("/api/folders/{folderId}/products")
    public Page<Product> getProductsInFolder(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @PathVariable Long folderId,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam String sortBy,
            @RequestParam boolean isAsc
    ){
        page = page - 1;
        return folderService.getProductsInFolder(
                folderId,
                page,
                size,
                sortBy,
                isAsc,
                userDetails.getUser());
    }
}
