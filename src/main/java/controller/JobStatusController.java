package controller;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.bean.FileJob;
import model.dao.FileJobDAO;

@WebServlet("/api/job-status")
public class JobStatusController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final FileJobDAO fileJobDAO = new FileJobDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Get userId from session
        HttpSession session = request.getSession(false);
        if (session == null) {
            response.getWriter().write("{\"error\":\"Session not found\"}");
            return;
        }

        String userId = (String) session.getAttribute("userId");
        if (userId == null || userId.isEmpty()) {
            response.getWriter().write("{\"error\":\"User not logged in\"}");
            return;
        }

        String jobId = request.getParameter("jobId");
        
        if (jobId == null || jobId.isEmpty()) {
            response.getWriter().write("{\"error\":\"Job ID is required\"}");
            return;
        }

        try {
            FileJob job = fileJobDAO.getFileJob(userId, jobId);
            
            if (job == null) {
                response.getWriter().write("{\"error\":\"Job not found\"}");
                return;
            }

            String json = mapper.writeValueAsString(job);
            response.getWriter().write(json);
            
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}