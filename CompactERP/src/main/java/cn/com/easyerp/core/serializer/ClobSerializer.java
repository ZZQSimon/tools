 package cn.com.easyerp.core.serializer;
 
 import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.Clob;
import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import cn.com.easyerp.core.exception.ApplicationException;
 
 
 
 
 
 
 
 
 public class ClobSerializer
   extends JsonSerializer<Clob>
 {
   public static String convert(Clob data) { return convert(data, null); }
 
 
   
   public static String convert(Clob data, String nullValue) {
     if (data == null)
       return nullValue; 
     StringBuilder sb = new StringBuilder();
     try {
       Reader reader = data.getCharacterStream();
       BufferedReader br = new BufferedReader(reader);
       
       String line;
       while (null != (line = br.readLine())) {
         sb.append(line);
       }
       br.close();
     } catch (SQLException|IOException e) {
       throw new ApplicationException(e);
     } 
     return sb.toString();
   }
 
 
 
   
   public void serialize(Clob value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException { jgen.writeString(convert(value)); }
 
 
 
 
   
   public Class<Clob> handledType() { return Clob.class; }
 }


