package utils;

import org.htmlparser.tags.CompositeTag;  

public class StrongTag extends CompositeTag {  
 
   private static final long serialVersionUID = 1L;  
   private static final String[] mIds = new String[] { "STRONG" };  
 
   public String[] getIds() {  
      return mIds;  
   }  
 
   public String[] getEnders() {  
      return mIds;  
   }  
}  
