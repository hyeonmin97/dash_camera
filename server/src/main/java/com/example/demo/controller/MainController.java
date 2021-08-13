package com.example.demo.controller;


import org.springframework.core.io.UrlResource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class MainController {
    //파일 리스트 목록 컨트롤러(예제)

    //1. 폴더든 파일이든 리스트만 보이게
    @GetMapping("/test1")
    public @ResponseBody File[] test(){
        File dir = new File("C:\\Users\\hyeonmin\\Desktop\\dash_camera\\webcam\\videos");
        File files[] = dir.listFiles();
        System.out.println("in Maincontroller");
        for (int i = 0; i < files.length; i++) {
            System.out.println("file: " + files[i]);
        }
        return files;
    }

    @GetMapping("/test2")
    //2. 폴더는 폴더안에 들어가서 파일목록 출력(재귀)
    public @ResponseBody List<String> list() {
        List<String> fileList = new ArrayList<String>();
        showFilesInDIr("C:\\Users\\hyeonmin\\Desktop\\dash_camera\\webcam\\videos", fileList);
        return fileList;
    }
    public static void showFilesInDIr(String dirPath, List<String> fileList) {
        File dir = new File(dirPath);
        File files[] = dir.listFiles();
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                showFilesInDIr(file.getPath(),fileList);

            } else {
                System.out.println("file: " + file);
                fileList.add(file.getAbsolutePath());
            }
        }
    }


    //스트리밍
    @GetMapping(value = "/video1/{directory}/{name}")
    public ResponseEntity<ResourceRegion> getVideo(@RequestHeader HttpHeaders headers, @PathVariable String directory, @PathVariable String name) throws IOException {

        UrlResource video = new UrlResource("file:C:\\Users\\hyeonmin\\Desktop\\dash_camera\\webcam\\videos\\"  + directory+ "\\" + name + ".mp4");
        ResourceRegion resourceRegion;

        final long chunkSize = 1000000L;
        long contentLength = video.contentLength();

        Optional<HttpRange> optional = headers.getRange().stream().findFirst();
        HttpRange httpRange;
        if (optional.isPresent()) {
            httpRange = optional.get();
            long start = httpRange.getRangeStart(contentLength);
            long end = httpRange.getRangeEnd(contentLength);
            long rangeLength = Long.min(chunkSize, end - start + 1);
            resourceRegion = new ResourceRegion(video, start, rangeLength);
        } else {
            long rangeLength = Long.min(chunkSize, contentLength);
            resourceRegion = new ResourceRegion(video, 0, rangeLength);
        }

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(MediaTypeFactory.getMediaType(video).orElse(MediaType.APPLICATION_OCTET_STREAM))
                .body(resourceRegion);
    }
}
