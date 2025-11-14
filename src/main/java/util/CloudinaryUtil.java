package util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryUtil {

    private static final Cloudinary cloudinary;

    static {
        cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", CloudinaryConfig.CLOUD_NAME,
                "api_key",    CloudinaryConfig.API_KEY,
                "api_secret", CloudinaryConfig.API_SECRET,
                "secure", true
        ));
    }

    // =============================
    // 1. Upload to SOURCE folder
    // =============================
    public static String uploadSourceFile(File file) throws Exception {
        Map upload = cloudinary.uploader().upload(file, ObjectUtils.asMap(
                "folder", CloudinaryConfig.SOURCE_FOLDER,
                "resource_type", "auto"
        ));
        return upload.get("secure_url").toString();
    }

    // =============================
    // 2. Upload to CONVERTED folder
    // =============================
    public static String uploadConvertedFile(File file) throws Exception {
        Map upload = cloudinary.uploader().upload(file, ObjectUtils.asMap(
                "folder", CloudinaryConfig.CONVERTED_FOLDER,
                "resource_type", "auto"
        ));
        return upload.get("secure_url").toString();
    }

    // =============================
    // 3. Download file
    // =============================
    public static byte[] downloadFile(String fileUrl) throws Exception {
        try (InputStream in = new URL(fileUrl).openStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {

            int nRead;
            byte[] data = new byte[1024];  // read 1 KB at a time
            while ((nRead = in.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        }
    }


    public static File downloadToFile(String fileUrl, String outputPath) throws Exception {
        byte[] bytes = downloadFile(fileUrl);
        File output = new File(outputPath);
        Files.write(output.toPath(), bytes);
        return output;
    }


    // =============================
    // 4. Delete file by publicId
    // =============================
    public static Map deleteFile(String publicId) throws Exception {
        return cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
    }

    // =============================
    // 5. Extract publicId from URL
    // =============================
    public static String getPublicIdFromUrl(String url) {
        // remove ? parameters
        String clean = url.split("\\?")[0];
        String[] parts = clean.split("/");

        String filename = parts[parts.length - 1];        // file.ext
        String folder   = parts[parts.length - 2];        // SourceFile or ConvertedFile
        String parent   = parts[parts.length - 3];        // FlexFile

        String nameWithoutExt = filename.substring(0, filename.lastIndexOf('.'));

        return parent + "/" + folder + "/" + nameWithoutExt;
    }
}
