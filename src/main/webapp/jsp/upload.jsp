<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="vi">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FlexiFile - File Converter</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/style/upload.css">
</head>
<body>
    <div class="container">
        <!-- Header -->
        <div class="header">
            <a href="#" class="logo">
                <div class="logo-icon">📁</div>
                <span>FlexiFile</span>
            </a>
            <div class="auth-buttons">
                <%
                    String userId = (String) session.getAttribute("userId");
                    if (userId != null) {
                %>
                    <a href="<%= request.getContextPath() %>/account" class="btn-login">My Account</a>
                    <a href="<%= request.getContextPath() %>/logout" class="btn-signup">Logout</a>
                <% } else { %>
                    <a href="<%= request.getContextPath() %>/login" class="btn-login">Login</a>
                    <a href="<%= request.getContextPath() %>/register" class="btn-signup">Sign Up</a>
                <% } %>
            </div>
        </div>

        <!-- Main Card -->
        <div class="main-card">
            <div class="title-section">
                <h1>🚀 File Converter</h1>
                <p>Convert your files quickly and easily. Support PDF, DOCX, TXT, Images and more.</p>
            </div>

            <form id="conversionForm">
                <!-- Upload Area -->
                <div class="upload-area" id="uploadArea">
                    <div class="upload-icon">📤</div>
                    <h3>Select Files</h3>
                    <p>Choose one or multiple files from your computer</p>
                    <button type="button" class="btn-select-file" id="btnSelectFile">
                        <span>📁</span>
                        Choose Files
                    </button>
                    <input type="file" id="fileInput" multiple style="display: none;">
                </div>

                <!-- File List -->
                <div class="file-list" id="fileList"></div>

                <!-- Convert Button -->
                <button type="submit" class="btn-convert" id="btnConvert" style="display: none;">
                    🚀 Start Conversion
                </button>
            </form>
        </div>

        <!-- Footer -->
        <div class="footer">
            <p>&copy; 2025 FlexiFile - Made with ❤️ in Vietnam</p>
            <div class="stats">
                <p>Fast • Secure • Easy to use</p>
            </div>
        </div>
    </div>

        <script>
        // Cloudinary config
        const CLOUDINARY_CLOUD_NAME = 'dababrmdw';
        const CLOUDINARY_UPLOAD_PRESET = 'flexifile_unsigned';
        const CLOUDINARY_UPLOAD_FOLDER = 'FlexFile/SourceFile';

        // Supported formats mapping
        const SUPPORTED_FORMATS = {
            'pdf': ['docx', 'txt', 'log', 'csv', 'html', 'png', 'jpg', 'jpeg', 'gif', 'webp'],
            'docx': ['pdf', 'txt', 'log', 'csv', 'html'],
            'txt': ['pdf', 'docx', 'log', 'csv', 'html'],
            'log': ['pdf', 'docx', 'txt', 'csv', 'html'],
            'csv': ['pdf', 'docx', 'txt', 'log', 'html'],
            'html': ['pdf', 'docx', 'txt', 'log', 'csv'],
            'png': ['jpg', 'jpeg', 'gif', 'webp', 'pdf'],
            'jpg': ['png', 'jpeg', 'gif', 'webp', 'pdf'],
            'jpeg': ['png', 'jpg', 'gif', 'webp', 'pdf'],
            'gif': ['png', 'jpg', 'jpeg', 'webp', 'pdf'],
            'webp': ['png', 'jpg', 'jpeg', 'gif', 'pdf']
        };

        let selectedFiles = [];
        let jobsTracking = {};

        // Check if user is logged in
        const isLoggedIn = <%= (session.getAttribute("userId") != null) ? "true" : "false" %>;

        // Wait for DOM to be ready
        document.addEventListener('DOMContentLoaded', function() {
            // Elements
            const fileInput = document.getElementById('fileInput');
            const fileList = document.getElementById('fileList');
            const btnConvert = document.getElementById('btnConvert');
            const btnSelectFile = document.getElementById('btnSelectFile');
            const uploadArea = document.getElementById('uploadArea');

            if (!fileInput || !btnSelectFile) {
                console.error('Required elements not found!');
                return;
            }

            // Button select file
            btnSelectFile.addEventListener('click', function(e) {
                e.preventDefault();
                e.stopPropagation();
                
                console.log('Button clicked, isLoggedIn:', isLoggedIn);
                
                if (!isLoggedIn) {
                    alert('Please login first!');
                    window.location.href = '<%= request.getContextPath() %>/login';
                    return;
                }
                fileInput.click();
            });

            // Upload area click
            if (uploadArea) {
                uploadArea.addEventListener('click', function(e) {
                    if (e.target.closest('.btn-select-file')) {
                        return;
                    }
                    if (e.target === uploadArea || e.target.closest('.upload-icon, h3, p')) {
                        btnSelectFile.click();
                    }
                });
            }

            // File input change
            fileInput.addEventListener('change', function() {
                const files = Array.from(this.files);
                if (files.length === 0) return;

                files.forEach(file => {
                    const extension = getFileExtension(file.name);
                    selectedFiles.push({
                        file: file,
                        sourceFormat: extension,
                        targetFormat: ''
                    });
                });
                
                displaySelectedFiles();
                fileInput.value = '';
            });

            // Form submit
            document.getElementById('conversionForm').addEventListener('submit', async function(e) {
                e.preventDefault();
                
                if (!isLoggedIn) {
                    alert('Please login first!');
                    window.location.href = '<%= request.getContextPath() %>/login';
                    return;
                }
                
                const invalidFiles = selectedFiles.filter(f => !f.targetFormat);
                if (invalidFiles.length > 0) {
                    alert('Please select target format for all files!');
                    return;
                }
                
                if (selectedFiles.length === 0) {
                    alert('Please select at least one file!');
                    return;
                }

                btnConvert.disabled = true;
                btnSelectFile.disabled = true;
                btnConvert.innerHTML = 'Uploading files...';

                const uploadedFiles = [];
                
                for (let i = 0; i < selectedFiles.length; i++) {
                    const fileData = selectedFiles[i];
                    const fileItem = document.getElementById('file-' + i);
                    
                    try {
                        updateFileStatus(fileItem, 'uploading', 'Uploading... (' + (i + 1) + '/' + selectedFiles.length + ')');
                        btnConvert.innerHTML = 'Uploading ' + (i + 1) + '/' + selectedFiles.length + '...';
                        
                        const fileUrl = await uploadToCloudinary(fileData.file);
                        
                        uploadedFiles.push({
                            fileName: fileData.file.name,
                            fileUrl: fileUrl,
                            targetFormat: fileData.targetFormat
                        });
                        
                        updateFileStatus(fileItem, 'done', 'Uploaded');
                        
                    } catch (error) {
                        console.error('Upload failed:', fileData.file.name, error);
                        updateFileStatus(fileItem, 'failed', 'Upload failed: ' + error.message);
                        
                        btnConvert.disabled = false;
                        btnSelectFile.disabled = false;
                        btnConvert.innerHTML = 'Start Conversion';
                        return;
                    }
                }

                btnConvert.innerHTML = 'Creating conversion jobs...';

                try {
                    const response = await fetch('<%= request.getContextPath() %>/upload', {
                        method: 'POST',
                        headers: {
                            'Content-Type': 'application/json'
                        },
                        body: JSON.stringify({
                            files: uploadedFiles
                        })
                    });

                    const result = await response.json();
                    
                    if (result.success) {
                        console.log('Jobs created:', result.jobIds);
                        
                        for (let i = 0; i < selectedFiles.length; i++) {
                            const fileItem = document.getElementById('file-' + i);
                            const jobId = result.jobIds[i];
                            
                            jobsTracking[jobId] = {
                                fileItem: fileItem,
                                fileName: selectedFiles[i].file.name,
                                index: i
                            };
                            
                            updateFileStatus(fileItem, 'pending', 'Waiting in queue...');
                        }
                        
                        btnConvert.innerHTML = 'Converting... (checking status every 3s)';
                        startPollingJobStatus(result.jobIds);
                        
                    } else {
                        alert('Error: ' + result.message);
                        btnConvert.disabled = false;
                        btnSelectFile.disabled = false;
                        btnConvert.innerHTML = 'Start Conversion';
                    }
                } catch (error) {
                    console.error('Error:', error);
                    alert('Request failed! Please try again.');
                    btnConvert.disabled = false;
                    btnSelectFile.disabled = false;
                    btnConvert.innerHTML = 'Start Conversion';
                }
            });

            // Functions
            function getFileExtension(filename) {
                const ext = filename.split('.').pop().toLowerCase();
                return ext === 'jpeg' ? 'jpg' : ext;
            }

            function getAvailableFormats(sourceFormat) {
                return SUPPORTED_FORMATS[sourceFormat] || [];
            }

            function displaySelectedFiles() {
                fileList.innerHTML = '';
                
                if (selectedFiles.length > 0) {
                    selectedFiles.forEach((fileData, index) => {
                        const availableFormats = getAvailableFormats(fileData.sourceFormat);
                        
                        const fileItem = document.createElement('div');
                        fileItem.className = 'file-item';
                        fileItem.id = 'file-' + index;
                        
                        let formatOptions = '<option value="">Select format...</option>';
                        availableFormats.forEach(format => {
                            formatOptions += '<option value="' + format + '">' + format.toUpperCase() + '</option>';
                        });
                        
                        let formatWarning = '';
                        if (availableFormats.length === 0) {
                            formatWarning = '<span style="color: #ef4444; font-size: 13px;">Format not supported</span>';
                        }
                        
                        fileItem.innerHTML = 
                            '<div class="file-header">' +
                                '<div class="file-icon">📄</div>' +
                                '<div class="file-info">' +
                                    '<div class="file-name">' + escapeHtml(fileData.file.name) + '</div>' +
                                    '<div class="file-meta">' +
                                        '<span class="file-size">📦 ' + formatFileSize(fileData.file.size) + '</span>' +
                                        '<span class="source-format">📝 ' + fileData.sourceFormat.toUpperCase() + '</span>' +
                                    '</div>' +
                                '</div>' +
                                '<button type="button" class="btn-remove-file" onclick="removeFile(' + index + ')">×</button>' +
                            '</div>' +
                            '<div class="format-conversion">' +
                                '<span class="format-label">Convert to:</span>' +
                                '<select class="format-select" data-index="' + index + '" onchange="updateTargetFormat(' + index + ', this.value)">' +
                                    formatOptions +
                                '</select>' +
                                formatWarning +
                            '</div>' +
                            '<div class="file-status">' +
                                '<span class="status-badge status-ready">✓ Ready</span>' +
                            '</div>';
                        
                        fileList.appendChild(fileItem);
                    });
                    
                    btnConvert.style.display = 'block';
                } else {
                    btnConvert.style.display = 'none';
                }
            }

            function updateFileStatus(fileItem, status, message) {
                const statusContainer = fileItem.querySelector('.file-status');
                let statusClass = 'status-ready';
                let statusText = message;
                
                if (status === 'uploading') {
                    statusClass = 'status-uploading';
                } else if (status === 'pending') {
                    statusClass = 'status-pending';
                } else if (status === 'processing' || status === 'converting') {
                    statusClass = 'status-converting';
                    statusContainer.innerHTML = 
                        '<span class="status-badge ' + statusClass + '">' + statusText + '</span>' +
                        '<div class="progress-bar">' +
                            '<div class="progress-fill" style="width: 60%"></div>' +
                        '</div>';
                    return;
                } else if (status === 'done') {
                    statusClass = 'status-done';
                } else if (status === 'failed') {
                    statusClass = 'status-failed';
                }
                
                statusContainer.innerHTML = '<span class="status-badge ' + statusClass + '">' + statusText + '</span>';
            }

            async function startPollingJobStatus(jobIds) {
                let pollCount = 0;
                const maxPolls = 100;
                
                const checkInterval = setInterval(async () => {
                    pollCount++;
                    let allDone = true;
                    
                    console.log('Polling status... (attempt ' + pollCount + '/' + maxPolls + ')');
                    
                    for (const jobId of jobIds) {
                        try {
                            const response = await fetch('<%= request.getContextPath() %>/api/job-status?jobId=' + jobId);
                            
                            if (!response.ok) {
                                console.error('Failed to fetch job status:', response.status);
                                continue;
                            }
                            
                            const job = await response.json();
                            
                            if (job.error) {
                                console.error('Job error:', job.error);
                                continue;
                            }
                            
                            const tracking = jobsTracking[jobId];
                            if (!tracking) continue;
                            
                            console.log('Job ' + jobId + ': ' + job.status);
                            
                            if (job.status === 'DONE') {
                                updateFileStatus(tracking.fileItem, 'done', 'Completed');
                                
                                const statusContainer = tracking.fileItem.querySelector('.file-status');
                                if (job.resultUrl) {
                                    statusContainer.innerHTML = 
                                        '<span class="status-badge status-done">✓ Completed</span>' +
                                        '<a href="' + job.resultUrl + '" class="btn-download" download target="_blank">' +
                                            '⬇️ Download' +
                                        '</a>';
                                }
                                
                            } else if (job.status === 'FAILED') {
                                updateFileStatus(tracking.fileItem, 'failed', 'Conversion failed');
                                
                            } else if (job.status === 'PROCESSING') {
                                updateFileStatus(tracking.fileItem, 'processing', 'Converting...');
                                allDone = false;
                                
                            } else if (job.status === 'PENDING') {
                                updateFileStatus(tracking.fileItem, 'pending', 'Waiting...');
                                allDone = false;
                            } else {
                                allDone = false;
                            }
                            
                        } catch (error) {
                            console.error('Failed to check job status:', jobId, error);
                            allDone = false;
                        }
                    }
                    
                    if (allDone || pollCount >= maxPolls) {
                    clearInterval(checkInterval);
                    
                    if (allDone) {
                        btnConvert.innerHTML = '✅ All conversions completed!';
                        console.log('All jobs completed!');
                    } else {
                        btnConvert.innerHTML = '⚠️ Polling stopped';
                    }
                    
                    btnConvert.disabled = false;
                    btnSelectFile.disabled = false;
                    
                    btnConvert.style.display = 'none';
                }
                    
                }, 3000);
            }

            async function uploadToCloudinary(file) {
                const formData = new FormData();
                formData.append('file', file);
                formData.append('upload_preset', CLOUDINARY_UPLOAD_PRESET);
                formData.append('folder', CLOUDINARY_UPLOAD_FOLDER);
                
                const url = 'https://api.cloudinary.com/v1_1/' + CLOUDINARY_CLOUD_NAME + '/auto/upload';
                
                const response = await fetch(url, {
                    method: 'POST',
                    body: formData
                });
                
                if (!response.ok) {
                    const errorText = await response.text();
                    let error;
                    try {
                        error = JSON.parse(errorText);
                    } catch {
                        throw new Error('Upload failed with status: ' + response.status);
                    }
                    throw new Error(error.error?.message || 'Upload failed');
                }

                const result = await response.json();
                return result.secure_url;
            }

            function formatFileSize(bytes) {
                if (bytes === 0) return '0 Bytes';
                const k = 1024;
                const sizes = ['Bytes', 'KB', 'MB', 'GB'];
                const i = Math.floor(Math.log(bytes) / Math.log(k));
                return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
            }

            function escapeHtml(text) {
                const div = document.createElement('div');
                div.textContent = text;
                return div.innerHTML;
            }

            // Global functions for onclick handlers
            window.removeFile = function(index) {
                selectedFiles.splice(index, 1);
                displaySelectedFiles();
            };

            window.updateTargetFormat = function(index, format) {
                selectedFiles[index].targetFormat = format;
                console.log('File ' + index + ': ' + selectedFiles[index].sourceFormat + ' to ' + format);
            };
        });
    </script>
</body>
</html>