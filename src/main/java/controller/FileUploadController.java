package controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.FileJob;
import model.bo.FileJobBO;
import model.bo.UserBO;
import worker.WorkerLauncher;

@WebServlet("/upload")
public class FileUploadController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        request.getRequestDispatcher("/jsp/upload.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        String userId = null;
        int numberOfFiles = 0;
        UserBO userBO = new UserBO();
        boolean convertsDeducted = false;

        try {
            // Check session
            HttpSession session = request.getSession(false);
            if (session == null) {
                response.getWriter().write("{\"success\":false,\"message\":\"Session not found\"}");
                return;
            }

            userId = (String) session.getAttribute("userId");
            if (userId == null || userId.isEmpty()) {
                response.getWriter().write("{\"success\":false,\"message\":\"User not logged in\"}");
                return;
            }
            
            System.out.println("=== Conversion Request ===");
            System.out.println("User ID: " + userId);
            
            // Read JSON request body
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = request.getReader();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            System.out.println("Request body: " + sb.toString());

            // Parse JSON
            JsonNode jsonNode = mapper.readTree(sb.toString());
            JsonNode filesArray = jsonNode.get("files");

            if (filesArray == null || filesArray.size() == 0) {
                response.getWriter().write("{\"success\":false,\"message\":\"No files provided\"}");
                return;
            }
            
            numberOfFiles = filesArray.size();
            System.out.println("Number of files: " + numberOfFiles);

            for (JsonNode fileNode : filesArray) {
                String targetFormat = fileNode.get("targetFormat").asText();
                if (targetFormat == null || targetFormat.isEmpty()) {
                    response.getWriter().write("{\"success\":false,\"message\":\"All files must have target format\"}");
                    return;
                }
            }

            // Deduct remaining converts
            boolean decremented = userBO.decrementRemainingConverts(userId, (long) numberOfFiles);
            if (!decremented) {
                System.out.println("❌ User does not have enough remaining converts");
                response.getWriter().write("{\"success\":false,\"message\":\"Not enough remaining converts. Please add more.\"}");
                return;
            }
            convertsDeducted = true;
            System.out.println("✅ Deducted " + numberOfFiles + " remaining converts");
            
            List<FileJob> jobs = new ArrayList<>();
            List<String> jobIds = new ArrayList<>();
            FileJobBO fileJobBO = new FileJobBO();

            for (JsonNode fileNode : filesArray) {
                String fileName = fileNode.get("fileName").asText();
                String fileUrl = fileNode.get("fileUrl").asText();
                String targetFormat = fileNode.get("targetFormat").asText(); 

                System.out.println("📄 Processing file: " + fileName);
                System.out.println("   File URL: " + fileUrl);
                System.out.println("   Target Format: " + targetFormat);

                if (!fileUrl.contains("FlexFile/SourceFile")) {
                    System.out.println("⚠️  Warning: File not in SourceFile folder");
                }

                // Create fileJob
                String jobId = UUID.randomUUID().toString();
                FileJob fileJob = new FileJob();
                fileJob.setId(jobId);
                fileJob.setUserId(userId);
                fileJob.setFileName(fileName);
                fileJob.setFileUrl(fileUrl);
                fileJob.setTargetFormat(targetFormat); 
                fileJob.setStatus("PENDING");
                fileJob.setCreatedAt(new Date());
                fileJob.setUpdatedAt(new Date());
                
                System.out.println("Creating FileJob with ID: " + jobId);
                
                boolean created = fileJobBO.createFileJob(fileJob);
                if (created) {
                    jobs.add(fileJob);
                    jobIds.add(jobId);
                    System.out.println("✅ FileJob created successfully");
                } else {
                    System.out.println("❌ Failed to create FileJob");
                    
                    // Rollback
                    System.out.println("⚠️  Rolling back " + numberOfFiles + " converts...");
                    userBO.incrementRemainingConverts(userId, (long) numberOfFiles);
                    convertsDeducted = false;
                    
                    response.getWriter().write("{\"success\":false,\"message\":\"Failed to create job for: " + fileName + "\"}");
                    return; 
                }
            }

            // Push jobs to RabbitMQ
            if (!jobs.isEmpty()) {
                try {
                    System.out.println("📤 Pushing " + jobs.size() + " jobs to RabbitMQ...");
                    WorkerLauncher.launchWorker(jobs);
                    System.out.println("✅ Jobs pushed to RabbitMQ successfully");

                    String jsonResponse = String.format(
                        "{\"success\":true,\"message\":\"Jobs created successfully\",\"jobIds\":%s}",
                        mapper.writeValueAsString(jobIds)
                    );
                    response.getWriter().write(jsonResponse);
                    
                } catch (Exception e) {
                    System.out.println("❌ Failed to push to RabbitMQ: " + e.getMessage());
                    e.printStackTrace();
                    
                    // Mark all jobs as failed
                    for (FileJob job : jobs) {
                        fileJobBO.markFailed(job);
                    }
                    
                    // Rollback
                    System.out.println("⚠️  Rolling back " + numberOfFiles + " converts...");
                    userBO.incrementRemainingConverts(userId, (long) numberOfFiles);
                    convertsDeducted = false;
                    
                    response.getWriter().write("{\"success\":false,\"message\":\"Failed to launch worker: " + e.getMessage() + "\"}");
                }
            } else {
                response.getWriter().write("{\"success\":false,\"message\":\"No files to convert\"}");
            }
            
        } catch (Exception e) {
            System.out.println("❌ Server error: " + e.getMessage());
            e.printStackTrace();
            
            // Rollback
            if (convertsDeducted && userId != null && numberOfFiles > 0) {
                try {
                    System.out.println("⚠️  Rolling back " + numberOfFiles + " converts due to error...");
                    userBO.incrementRemainingConverts(userId, (long) numberOfFiles);
                } catch (Exception rollbackError) {
                    System.out.println("❌ Rollback failed: " + rollbackError.getMessage());
                }
            }
            
            response.getWriter().write("{\"success\":false,\"message\":\"Server error: " + e.getMessage() + "\"}");
        }
    }
}