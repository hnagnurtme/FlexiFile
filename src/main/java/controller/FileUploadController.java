package controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import model.bean.FileJob;
import model.bo.FileJobBO;
import util.CloudinaryUtil;

@WebServlet("/upload")
public class FileUploadController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.getRequestDispatcher("/jsp/upload.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        try {
            Collection<Part> fileParts = request.getParts();
            FileJobBO fileJobBO = new FileJobBO();
            String userId = (String) request.getSession().getAttribute("userId");

            for (Part filePart : fileParts) {
                if (filePart.getName().equals("files") && filePart.getSize() > 0) {
                    String fileName = filePart.getSubmittedFileName();
                    InputStream inputStream = filePart.getInputStream();

                    // Upload directly to Cloudinary
                    String fileUrl = CloudinaryUtil.uploadSourceStream(inputStream, fileName);
                    inputStream.close();

                    // Create and save FileJob
                    FileJob fileJob = new FileJob();
                    fileJob.setUserId(userId);
                    fileJob.setFileName(fileName);
                    fileJob.setFileUrl(fileUrl);

                    fileJobBO.createFileJob(fileJob);
                }
            }

            // Redirect back to upload page with success message
            response.sendRedirect(request.getContextPath() + "/upload?success=1");

        } catch (Exception e) {
            e.printStackTrace();
            // Optionally redirect with error message
            response.sendRedirect(request.getContextPath() + "/upload?error=1");
        }
    }
}
