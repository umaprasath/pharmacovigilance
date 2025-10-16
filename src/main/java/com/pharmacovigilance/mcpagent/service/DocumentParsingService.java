package com.pharmacovigilance.mcpagent.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.tika.Tika;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentParsingService {
    
    private final Tika tika = new Tika();
    
    /**
     * Parse PDF file and extract text content
     */
    public String parsePdfFile(byte[] pdfBytes) throws IOException {
        log.info("Parsing PDF file, size: {} bytes", pdfBytes.length);
        
        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            
            log.info("Successfully extracted {} characters from PDF", text.length());
            return text;
        } catch (Exception e) {
            log.error("Error parsing PDF file", e);
            throw new IOException("Failed to parse PDF: " + e.getMessage());
        }
    }
    
    /**
     * Parse PDF file from MultipartFile
     */
    public String parsePdfFile(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty or null");
        }
        
        String contentType = file.getContentType();
        if (contentType != null && !contentType.equals("application/pdf")) {
            log.warn("File content type is {}, expected application/pdf", contentType);
        }
        
        return parsePdfFile(file.getBytes());
    }
    
    /**
     * Parse PDF from Base64 encoded string
     */
    public String parsePdfFromBase64(String base64Content) throws IOException {
        log.info("Parsing PDF from Base64 string");
        
        try {
            byte[] pdfBytes = Base64.getDecoder().decode(base64Content);
            return parsePdfFile(pdfBytes);
        } catch (IllegalArgumentException e) {
            log.error("Invalid Base64 content", e);
            throw new IOException("Invalid Base64 content: " + e.getMessage());
        }
    }
    
    /**
     * Parse any document using Apache Tika
     */
    public String parseDocument(byte[] documentBytes) throws IOException {
        log.info("Parsing document using Apache Tika, size: {} bytes", documentBytes.length);
        
        try (InputStream stream = new ByteArrayInputStream(documentBytes)) {
            String text = tika.parseToString(stream);
            log.info("Successfully extracted {} characters from document", text.length());
            return text;
        } catch (Exception e) {
            log.error("Error parsing document with Tika", e);
            throw new IOException("Failed to parse document: " + e.getMessage());
        }
    }
    
    /**
     * Parse any document from MultipartFile
     */
    public String parseDocument(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty or null");
        }
        
        log.info("Parsing document: {} ({})", file.getOriginalFilename(), file.getContentType());
        return parseDocument(file.getBytes());
    }
    
    /**
     * Parse email content from raw email format (.eml)
     */
    public Map<String, String> parseEmail(byte[] emailBytes) throws IOException {
        log.info("Parsing email, size: {} bytes", emailBytes.length);
        
        try (InputStream stream = new ByteArrayInputStream(emailBytes)) {
            Session session = Session.getDefaultInstance(new Properties());
            MimeMessage message = new MimeMessage(session, stream);
            
            Map<String, String> emailData = new HashMap<>();
            emailData.put("subject", message.getSubject());
            emailData.put("from", message.getFrom()[0].toString());
            emailData.put("sentDate", message.getSentDate() != null ? message.getSentDate().toString() : null);
            emailData.put("body", extractEmailBody(message));
            
            log.info("Successfully parsed email with subject: {}", emailData.get("subject"));
            return emailData;
            
        } catch (Exception e) {
            log.error("Error parsing email", e);
            throw new IOException("Failed to parse email: " + e.getMessage());
        }
    }
    
    /**
     * Parse email from MultipartFile
     */
    public Map<String, String> parseEmail(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty or null");
        }
        
        return parseEmail(file.getBytes());
    }
    
    /**
     * Parse email from Base64 encoded string
     */
    public Map<String, String> parseEmailFromBase64(String base64Content) throws IOException {
        log.info("Parsing email from Base64 string");
        
        try {
            byte[] emailBytes = Base64.getDecoder().decode(base64Content);
            return parseEmail(emailBytes);
        } catch (IllegalArgumentException e) {
            log.error("Invalid Base64 content", e);
            throw new IOException("Invalid Base64 content: " + e.getMessage());
        }
    }
    
    /**
     * Extract body text from email message
     */
    private String extractEmailBody(Message message) throws Exception {
        Object content = message.getContent();
        
        if (content instanceof String) {
            return (String) content;
        } else if (content instanceof MimeMultipart) {
            return extractTextFromMultipart((MimeMultipart) content);
        }
        
        return "Unable to extract email body";
    }
    
    /**
     * Extract text from multipart email content
     */
    private String extractTextFromMultipart(MimeMultipart multipart) throws Exception {
        StringBuilder result = new StringBuilder();
        
        for (int i = 0; i < multipart.getCount(); i++) {
            BodyPart bodyPart = multipart.getBodyPart(i);
            
            if (bodyPart.isMimeType("text/plain")) {
                result.append(bodyPart.getContent().toString());
            } else if (bodyPart.isMimeType("text/html")) {
                // Strip HTML tags for simple text extraction
                String html = bodyPart.getContent().toString();
                result.append(stripHtmlTags(html));
            } else if (bodyPart.getContent() instanceof MimeMultipart) {
                result.append(extractTextFromMultipart((MimeMultipart) bodyPart.getContent()));
            }
        }
        
        return result.toString();
    }
    
    /**
     * Simple HTML tag stripper
     */
    private String stripHtmlTags(String html) {
        if (html == null) return "";
        return html.replaceAll("<[^>]*>", " ")
                   .replaceAll("\\s+", " ")
                   .trim();
    }
    
    /**
     * Detect document type from file
     */
    public String detectDocumentType(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IOException("File is empty or null");
        }
        
        try (InputStream stream = file.getInputStream()) {
            String mimeType = tika.detect(stream);
            log.info("Detected MIME type for {}: {}", file.getOriginalFilename(), mimeType);
            return mimeType;
        } catch (Exception e) {
            log.error("Error detecting document type", e);
            throw new IOException("Failed to detect document type: " + e.getMessage());
        }
    }
    
    /**
     * Validate if file is a supported document type
     */
    public boolean isSupportedDocumentType(String mimeType) {
        return mimeType != null && (
            mimeType.equals("application/pdf") ||
            mimeType.startsWith("text/") ||
            mimeType.equals("application/msword") ||
            mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") ||
            mimeType.equals("message/rfc822") // Email format
        );
    }
}

