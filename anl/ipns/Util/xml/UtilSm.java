package gov.anl.ipns.Util.xml;


import java.io.*;
import java.util.*;

import javax.xml.parsers.*;
import org.w3c.dom.*;
import org.xml.sax.*;
/**
 * This class contains utilities to find information in "general" xml files
 * with some known structure( names for tags, attributes, etc.).
 * 
 * All these methods use the DOM model for the xml parsing
 * 
 * @author ruth
 *
 */
public class UtilSm
{

   
   public static Document Open( String xml_fileName) throws IOException,
                                                            FactoryConfigurationError,
                                                            ParserConfigurationException,
                                                            SAXException
   {
      return DocumentBuilderFactory.newInstance( ).newDocumentBuilder( ).parse(  
            new FileInputStream(xml_fileName) );
   }
   
   /**
    * Returns the list of all children nodes of a given node satisfying the conditions.
    * If the conditions are absent( null) all matches will be returned
    * 
    * @param parentNode  The Node whose children are to be searched
    * @param nodeName    The xml name of the node. If null all nodes will be considered.
    * @param attributes  Vector of key and value pairs for match. If an element contains a third item
    *                    it will describe * type matching info.
    * @return the list of all children nodes of a given node satisfying the conditions.
    */
   public static  Node[] SearchChildrenNodes( Node parentNode, String nodeName, Vector<String[]> attributes )
   {
      Vector<Node> children = new Vector<Node>();
      
      for( Node N= NextChildNode( parentNode, null, nodeName, attributes);
               N != null;
               N= NextChildNode( parentNode, N, nodeName, attributes))
         
         children.add( N );
      
      return children.toArray( new Node[0]);
   }
   
   
   /**
    * Uses NextSibling and drills down thru all children 
    *  
    * @param parentNode  The Node whose children are to be searched
    * 
    * @param thisNode    null for first child, otherwise the last returned child of the parent.
    * 
    * @param nodeName    The xml name of the node. If null all nodes will be considered.
    * 
    * @param attributes  Vector of key and value pairs for match. If an element contains a third item
    *                    it will describe * type matching info. Regex expression matching
    *                    
    * @return      The next node matching the given criteria
    */
   public static  Node NextChildNodeRecursive( Node parentNode, Node thisNode,
                                   String nodeName, Vector<String[]> attributes )
   {
      Node SaveThisNode = thisNode;//in case need parent
      
      if( parentNode == null)
         return null;
      
      if( thisNode == null)
         
         thisNode = parentNode.getFirstChild();
      
      else if( thisNode.getFirstChild() == null)
         
         thisNode = thisNode.getNextSibling( );
      
      else
         
         thisNode = thisNode.getFirstChild();
      
      if( thisNode == null)//Thru at this level , go up a level
      {
         thisNode = SaveThisNode.getParentNode( );
         
         if( thisNode == parentNode || thisNode == null)
            return null;
         
         Node thisNode1 = null;
         
         for( thisNode1 = thisNode.getNextSibling( );thisNode1 == null;
                                      thisNode = thisNode.getParentNode())
            
            if( thisNode == null || thisNode== parentNode)
               
               return null;
         
            else
               thisNode1=thisNode.getNextSibling( );
         
         thisNode = thisNode1;
         
      }
      
      if( Match( thisNode, nodeName, attributes))
         
         return thisNode;
      
      else if( thisNode !=null)
         
         return NextChildNodeRecursive(parentNode, thisNode, nodeName,attributes )
;         
         
         
      return null; 
   }
   /**
    * Uses NextSibling 
    * @param parentNode  The Node whose children are to be searched
    * 
    * @param thisNode    null for first child, otherwise the last returned child of the parent.
    * 
    * @param nodeName    The xml name of the node. If null all nodes will be considered.
    * 
    * @param attributes  Vector of key and value pairs for match. If an element contains a third item
    *                    it will describe * type matching info. Regex expression matching
    *                    
    * @return      The next node matching the given criteria
    */
   public static  Node NextChildNode( Node parentNode, Node thisNode,
                                   String nodeName, Vector<String[]> attributes )
   {
      if( parentNode == null)
         return null;
      
      if( thisNode == null)
         thisNode = parentNode.getFirstChild();
      else
         thisNode = thisNode.getNextSibling( );
      
      if( thisNode == null)
         return thisNode;
      
      for( Node newNode = thisNode; newNode != null ;
                           newNode=newNode.getNextSibling( ))
      {
         if( Match( newNode, nodeName, attributes))
            return newNode;
      }
      
      return null;
      
      
   }
   
   
   /**
    * Currently checks node for exact matching only
    * 
    * @param node       The node to check for a match to the other conditions
    * 
    * @param nodeName   The nodeName to match( null means they all match)
    * 
    * @param attributes The attributes and corresponding values to match
    * 
    * @return           true if there is a match, otherwise false.
    */
   public static boolean Match( Node node, String nodeName, Vector<String[]>attributes)
   {
      if( node == null)
         return false;
      
      if( nodeName != null)
         if( !nodeName.equals( node.getNodeName()))
               return false;
      
      if( attributes == null || attributes.size() < 1)
         return true;
      
      NamedNodeMap attrs = node.getAttributes( );
      for( int i=0; i< attributes.size( ); i++)
      {
         String[] attr = attributes.elementAt(i);
         
         if ( attr != null && attr.length > 1 )
         {
            Node N = attrs.getNamedItem( attr[0] );
            if ( N == null )
               return false;

            String S = N.getNodeValue( );

            if( S == null && (attr.length <2 ||attr[1] == null))
            {
               
            }
            else if ( S == null && attr[1] != null )
               return false;

            else if ( attr[1] != null )
               if ( !attr[1].trim( ).equals( S.trim( ) ) )
                  return false;
         }
      }
      return true;
   }
   
   /**
    * Used to create the Vector of attribute keys and corresponding values in
    * one statement.
    * 
    * @param V    The current Vector or null( an empty vector will be created)
    * 
    * @param S1   The attribute name
    * 
    * @param S2   The corresponding attribute value(or null means the attribute
    *                must be absent)
    *                
    * @param S3   Used to describe * type matching. Not used yet.Can be null.
    * 
    * @return     The new vector with the new information appended to it.
    */
   public static Vector<String[]>  Add( Vector<String[]> V, String S1, String S2, 
                                  String S3)
   {
      if( V == null)
         V = new Vector<String[]> ();
         
     int n=1;
     
     if(S1 == null)
        return V;
     
     if( S2 != null)
        n++;
     
     if( S3 != null && S2 != null)
        n++;
     
     String[] S = new String[n];
     
     S[0]=S1;
     if( n > 1)
        S[1] = S2;
     
     if( n > 2)
        S[2] = S3;
     
     V.addElement(  S );
     
     return V;
   }
   
   /**
    * Will find the value using getNodeValue or check for one CDATA child
    * 
    * @param N  The Node
    * 
    * @return    The value of the Node.
    */
   public static String getNodeValue( Node N)
   {
      if( N == null)
         return null;
      
      if( N.getFirstChild() == null)
         return N.getNodeValue( );
      
      if( N.getNodeValue() != null)
         return N.getNodeValue( );
      
      Node[] Nds = SearchChildrenNodes( N,"#cdata-section", null); 
      
      if( Nds == null || Nds.length > 1)
         return null;
      
      return Nds[0].getNodeValue();
   }
   
   /**
    * @param args
    */
   public static void main1(String[] args)
   {
      String fileName = "C:/Users/ruth/SNS/EventData/PG3_539_cvinfo.xml";
      
     try
     {
        Document D = UtilSm.Open( fileName);
        Node N = D.getFirstChild( );
        Vector<String[] >attributes =UtilSm.Add(
                          UtilSm.Add( null , "name" , "Speed2" , null),
                          "device","ChopperSystem" ,null);  
        N = UtilSm.NextChildNode( N,null,"Process", null);
        Node[] Nds = UtilSm.SearchChildrenNodes(N, "cvlog" , attributes );
        if( Nds != null)
        {
           System.out.println("There are "+ Nds.length + " nodes");
           for( int i=0; i< Nds.length; i++)
              System.out.println( UtilSm.getNodeValue( Nds[i]));
        }
        else
           System.out.println("There are no Nodes");
        
     }catch( Exception ss)
     {
        System.out.println( "Error:"+ss);
        ss.printStackTrace( );
     }

   }
   
   /**
    * @param args
    */
   public static void main(String[] args)
   {
      String fileName = "C:/Users/ruth/SNS/EventData/PG3_539_cvinfo.xml";
      
     try
     {
        Document D = UtilSm.Open( fileName);
       
        Vector<String[] >attributes =UtilSm.Add(
                          UtilSm.Add( null , "name" , "Speed2" , null),
                          "device","ChopperSystem" ,null);
 
        Node N = UtilSm.NextChildNodeRecursive( D,null,"cvlog", attributes);
        
        if( N != null)
        {
              System.out.println("Node value is\n"+ UtilSm.getNodeValue( N));
        }
        else
           System.out.println("There are no Nodes");
        
     }catch( Exception ss)
     {
        System.out.println( "Error:"+ss);
        ss.printStackTrace( );
     }

   }

}
