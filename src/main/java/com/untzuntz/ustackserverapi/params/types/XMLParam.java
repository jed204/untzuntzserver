package com.untzuntz.ustackserverapi.params.types;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.untzuntz.ustackserverapi.APIException;
import com.untzuntz.ustackserverapi.params.exceptions.ParamValueException;

public class XMLParam extends BaseParam implements ParameterDefinitionInt<Document>
{
	public XMLParam(String n, String d) {
		super(n, d);
	}
	
	public String getTypeDescription() {
		return "Properly formatted XML text (ex: <key><test></test></key>)";
	}

	@Override
	public void validate(String data) throws APIException {
		
		System.out.println("Field named [" + this.getName() + "] => Data [" + data + "]");
		
		Document ret = getDocument(data);
		if (ret != null)
			return;

		throw new ParamValueException(this, "Could not parse parameter");
	}
	
	@Override
	public Document getValue(String data) {

		if (data == null || data.length() == 0)
			return null;
		
		try {
			return getDocument(data);
		} catch (Exception jpe) {}
		
		return null;
				
	}

	private Document getDocument(String data) throws APIException
	{
		Document ret = null;
		try
		{
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			ret = db.parse(new InputSource(new StringReader(data)));
		}
		catch (ParserConfigurationException e) {
			throw new ParamValueException(this, "Internal error. Could not parse parameter");
		}
		catch (SAXException e) {
			throw new ParamValueException(this, "XML format issue. Could not parse parameter");
		}
		catch (IOException e) {
			throw new ParamValueException(this, "I/O Error. Could not parse parameter");
		}
		
		return ret;
	}
	
}
