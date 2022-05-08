package kr.goldenmine.flowerserver.file;

import kr.goldenmine.flowerserver.profile.Profile;
import kr.goldenmine.flowerserver.utils.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class StorageService {

    private String uploadPathStr;
    private Path uploadPath;

    @Autowired
    public StorageService(FileStorageProperties fileStorageProperties) {
        this.uploadPathStr = fileStorageProperties.getUploadDir();

        uploadPath = Paths.get(this.uploadPathStr);
        if (!Files.exists(uploadPath)) {
            try {
                Files.createDirectories(uploadPath);
                System.out.println("make dir : " + uploadPath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

//    private String getRandomStr(){
//        int leftLimit = 97; // letter 'a'
//        int rightLimit = 122; // letter 'z'
//        int targetStringLength = 10;
//        Random random = new Random();
//        String generatedString = random.ints(leftLimit, rightLimit + 1)
//                .limit(targetStringLength)
//                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
//                .toString();
//        System.out.println("random : " + generatedString);
//        return generatedString;
//    }

    public void saveProfileImage(Profile profile, MultipartFile file) throws IOException {
        String fileName = profile.getId() + ".jpg";

        writeFile(fileName, file);
    }

    public void saveImages(String type, int articleId, MultipartFile[] files) throws IOException {
        for (int i = 0; i < files.length; i++) {
            MultipartFile multipartFile = files[i];

            // ex) 0-0.jpg
//            String fileName = articleId + "-" + i + "." + StringUtil.getExtension(multipartFile.getOriginalFilename());
            String fileName = type + "-" + articleId + "-" + i + ".jpg";

            writeFile(fileName, multipartFile);
        }
    }

    public void writeFile(String fileName, MultipartFile file) throws IOException {
        Path filePath = uploadPath.resolve(fileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }

//    public List<String> saveFiles(MultipartFile[] files, String postName) throws IOException {
//        String randomStr = getRandomStr();
//        List<String> fileNames = new ArrayList<>();
//        for (MultipartFile file : files) {
//            fileNames.add(randomStr + StringUtils.cleanPath(file.getOriginalFilename()));
//        }
//        Path uploadPath = Paths.get(this.uploadPathStr + "/" + postName);
//        if (!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//            System.out.println("make dir : " + uploadPath.toString());
//        }
//        for (int i = 0; i < files.length; i++) {
//            try (InputStream inputStream = files[i].getInputStream()) {
//                Path filePath = uploadPath.resolve(fileNames.get(i));
//                Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
//            } catch (IOException ioe) {
//                throw new IOException("Could not save image file: " + fileNames.get(i), ioe);
//            }
//        }
//        return fileNames;
//    }

//    public String saveFile(MultipartFile file, String userName) throws IOException {
//
//        String randomStr = getRandomStr();
//        String fileName = randomStr + StringUtils.cleanPath(file.getOriginalFilename());
//
//        Path uploadPath = Paths.get(this.uploadPath+"/"+userName);
//        if(!Files.exists(uploadPath)) {
//            Files.createDirectories(uploadPath);
//        }
//
//        try (InputStream inputStream = file.getInputStream()) {
//            Path filePath = uploadPath.resolve(fileName);
//            Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
//            return fileName;
//        } catch (IOException ioe) {
//            throw new IOException("Could not save image file: " + fileName, ioe);
//        }
//    }

    public Resource loadFileAsResource(String fileName) throws FileNotFoundException {
        try {
            Path filePath = uploadPath.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new FileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new FileNotFoundException("File not found " + fileName + ", " + ex.getMessage());
        }
    }
}