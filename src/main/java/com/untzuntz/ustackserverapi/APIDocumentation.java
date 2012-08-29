package com.untzuntz.ustackserverapi;

import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.untzuntz.ustackserverapi.MethodDefinition.APICallParam;

public class APIDocumentation {

	public static void createPdf(String filename, String title, String version) throws IOException, DocumentException, SQLException {
		
        // step 1
		Document document = new Document();
        // step 2
        PdfWriter.getInstance(document, new FileOutputStream(filename));
        // step 3
        document.open();
        
        addTitle(document, title, version);
        
        addBasicInfo(document);
        
        
        // step 4
        List<MethodDefinition> methods = APICalls.getMethods();
        
        Collections.sort(methods, new MethodOrder());
        
        for (MethodDefinition method : methods) {
        	System.out.println("Method: " + method.getMethodName());
        }        
        
        for (MethodDefinition method : methods) {
        	
            Paragraph p = createMethodInformation(method);
            p.setAlignment(Element.ALIGN_LEFT);
            p.setIndentationLeft(18);
            p.setFirstLineIndent(-18);
            document.add(p);
            
            /*
             * Basic Info
             */
            PdfPTable table = new PdfPTable(2);
            table.setWidths(new int[] { 20, 80 });
            table.setSpacingBefore(10.0f);
            table.setSpacingAfter(5.0f);
            table.setWidthPercentage(100.0f);
            table.getDefaultCell().setPadding(4f);
            
            addURI(method, table);
            addHTTPMethods(method, table);
            addAuthRequirements(method, table);
            addHashEnforcement(method, table);
            addSinceVersion(method, table);
            
            document.add(table);

            /*
             * Parameters
             */
            table = new PdfPTable(4);
            table.setWidths(new int[] { 20, 55, 15, 10 });
            table.setSpacingBefore(10.0f);
            table.setSpacingAfter(20.0f);
            table.setWidthPercentage(100.0f);
            table.getDefaultCell().setPadding(4f);
            
            table.addCell(new Phrase("Parameter Name", BOLD));
            table.addCell(new Phrase("Description", BOLD));
            table.addCell(new Phrase("Required", BOLD));
            table.addCell(new Phrase("Since", BOLD));
            
            addParameters(method, table);
            
            document.add(table);

            
            
        }
        // step 5
        document.close();
    }
	
	public static void addTitle(Document document, String title, String version) throws DocumentException {
		
        Paragraph p = new Paragraph();
        p.setFont(NORMAL);
        p.setAlignment(Element.ALIGN_CENTER);
        p.add(new Phrase("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n" + title + "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n", LARGE));
        document.add(p);

        p = new Paragraph();
        p.setFont(NORMAL);
        p.setAlignment(Element.ALIGN_RIGHT);
        p.add(new Phrase("Version " + version));
        document.add(p);
        
        document.newPage();
	}
	
	public static void addBasicInfo(Document document) throws DocumentException {
		
		Paragraph p = new Paragraph();
		p.add(new Phrase("Basic Request Information", BOLD));
		document.add(p);
		
		p = new Paragraph();
		p.add(new Phrase("The API users HTTP GET, POST, DELETE, and PUT requests for message exchange. Implementations may require a SSL-encrypted connection. Please note that all parameters should be properly encoded for use in HTTP.\n\n\n"));
		document.add(p);
		
		p = new Paragraph();
		p.add(new Phrase("Request Format", BOLD));
		document.add(p);
		
        PdfPTable table = new PdfPTable(2);
        table.setWidths(new int[] { 20, 80 });
        table.setSpacingBefore(10.0f);
        table.setSpacingAfter(25.0f);
        table.setWidthPercentage(100.0f);
        table.getDefaultCell().setPadding(4f);

        table.addCell("GET");
        table.addCell("/api/path/method?param1=value1&param2=value2...");
        
        table.addCell("POST");
        table.addCell("/api/path/method\nRequest parameters posted per RFC 2616");
        
        document.add(table);

		p = new Paragraph();
		p.add(new Phrase("Request Signature", BOLD));
		document.add(p);
		
		p = new Paragraph();
		p.add(new Phrase("Certain API requests may require a request signature to be included as a parameter. The request signature should be calculated by sorting all request parameters alphabetically and performing a hmacSHA256 hash on the resulting URL encoded query string.\n\nExample Request: \n/api/test/hashtest?paramH=value1&paramA=value2 \n\nshould be calculated as the hash of:\n/api/test/hashtest?paramA=value2&paramH=value1\n\nNote: you can test your algorithm with the /api/test/hashtest call.\n\n\n"));
		document.add(p);
		
		p = new Paragraph();
		p.add(new Phrase("Authentication", BOLD));
		document.add(p);
		
		p = new Paragraph();
		p.add(new Phrase("If authentication is required the 'username' and 'accesscode' (user's password) parameters must be provided in each request.\n\nNote: If providing the hashed version of the user's password you must include the parameter s2=true\n\n\n"));
		document.add(p);


		p = new Paragraph();
		p.add(new Phrase("JSON", BOLD));
		document.add(p);
		
		p = new Paragraph();
		p.add(new Phrase("The API will respond with data in JSON format. See an example reponse below:"));
		document.add(p);

		p = new Paragraph();
		p.add(new Phrase("{\napiResult : { status : \"SUCCESS\" }\nmessage : \"Sample Message\"\nexampleObject : { a1 : 31, b1 : \"test\" }\n}", COURIER));
		document.add(p);
		
        document.newPage();

	}
	
	public static Paragraph createMethodInformation(MethodDefinition method) {
		
        Paragraph p = new Paragraph();
        p.setFont(NORMAL);
        
        p.add(getMethodHeader(method));
        
        p.add(getMethodDescription(method));

        return p;
    }
	
	private static Paragraph getMethodHeader(MethodDefinition method) {

        Paragraph p = new Paragraph();
        p.setFont(NORMAL);
        p.add(new Phrase("Method: "));
        p.add(new Phrase(method.getMethodName(), BOLD));
        return p;
	}

	private static Paragraph getMethodDescription(MethodDefinition method) {

        Paragraph p = new Paragraph();
        p.setFont(NORMAL);
        p.add(new Phrase(method.getDescription()));
        return p;
	}

	private static void addURI(MethodDefinition method, PdfPTable table) {

        table.addCell("URI");
        table.addCell("/api/" + method.getPath());
        
	}
	
	private static void addSinceVersion(MethodDefinition method, PdfPTable table) {

        table.addCell("Since Version");
        table.addCell(method.getSinceVersion());
        
	}
	
	private static void addAuthRequirements(MethodDefinition method, PdfPTable table) {

		table.addCell("Authentication");
		if (method.isAuthenticationRequired())
			table.addCell("Required");
		else
			table.addCell("No");

	}
	
	private static void addHashEnforcement(MethodDefinition method, PdfPTable table) {

		table.addCell("Request Sig");
		if (method.getHashEnforcement() == MethodDefinition.HASH_ENFORCEMENT_NONE)
			table.addCell("No");
		else if (method.getHashEnforcement() == MethodDefinition.HASH_ENFORCEMENT_WARN)
			table.addCell("Warn");
		else if (method.getHashEnforcement() == MethodDefinition.HASH_ENFORCEMENT_REJECT)
			table.addCell("Enforce");
	}
	
	private static void addParameters(MethodDefinition method, PdfPTable table) {

		
		List<APICallParam> params = method.getAPIParameters();
		for (APICallParam param : params)
		{
	        table.addCell(param.name);
	        table.addCell(param.description);
	        if (param.req)
	        	table.addCell("Yes");
	        else
	        	table.addCell("No");
	        
	        table.addCell(param.since);
		}

	}
	
	private static void addHTTPMethods(MethodDefinition method, PdfPTable table) {

        table.addCell("HTTP Method(s)");

        StringBuilder s = new StringBuilder();
        
        // we add the four remaining cells with addCell()
        if (method.isMethodDELETE())
        	s.append("DELETE ");
        
        if (method.isMethodGET())
        	s.append("GET ");
        
        if (method.isMethodPOST())
        	s.append("POST ");
        
        if (method.isMethodPUT())
        	s.append("PUT ");
		
        table.addCell(s.toString());
	}

	/** A font used in our PDF file */
    public static final Font LARGE = new Font(FontFamily.HELVETICA, 24, Font.BOLD);
	/** A font used in our PDF file */
    public static final Font COURIER = new Font(FontFamily.COURIER, 12, Font.NORMAL);
	/** A font used in our PDF file */
    public static final Font NORMAL = new Font(FontFamily.HELVETICA, 12, Font.NORMAL);
    /** A font used in our PDF file */
    public static final Font BOLD = new Font(FontFamily.HELVETICA, 12, Font.BOLD);
    /** A font used in our PDF file */
    public static final Font ITALIC = new Font(FontFamily.HELVETICA, 12, Font.ITALIC);
    /** A font used in our PDF file */
    public static final Font BOLDITALIC = new Font(FontFamily.HELVETICA, 12, Font.BOLDITALIC);
 
}
