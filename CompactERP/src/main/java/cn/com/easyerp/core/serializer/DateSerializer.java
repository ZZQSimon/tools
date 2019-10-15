 package cn.com.easyerp.core.serializer;
 
 import java.io.IOException;
import java.sql.Timestamp;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
 
 
 
 
 
 
 
 
 
 public class DateSerializer
   extends JsonSerializer<Timestamp>
 {
   public void serialize(Timestamp value, JsonGenerator jgen, SerializerProvider provider) throws IOException { jgen.writeNumber(value.getTime()); }
 
 
 
 
   
   public Class<Timestamp> handledType() { return Timestamp.class; }
 }


