package com.lumengaming.chat.asyncnetworkold.utility;

import com.google.common.base.Strings;
import java.util.ArrayList;

/**
 *
 * @author Taylor
 */
public class TagTree {
    private TagNode root;
    private TagNode current;
    public TagTree(String rootTag){
        this.root = new TagNode(rootTag);
        this.current = root;
    }
            
    /**
     * Appends to current tree node, then goes IN.
     * @param tag
     * @return 
     */
    public TagTree in(String tag){
        TagNode n = new TagNode(tag,current);
        current.children.add(n);
        current = n;
        return this;
    }    
    
    /**
     * Appends to current tree node.
     * @param tag
     * @return 
     */
    public TagTree append(String tag){
        TagNode n = new TagNode(tag,current);
        current.children.add(n);
        return this;
    }
    /**
     * Returns depth of current pointer in tree
     * @return 
     */
    public int getCurrentDepth(){
        int d = 1;
        TagNode tmp = current;
        while(tmp.parent != null){
            tmp = tmp.parent;d++;
        }
        return d;
    }   
    
    /**
     * Returns depth of current pointer in tree
     * @return 
     */
    public String getCurrentTabs(){
        return Strings.padStart("", getCurrentDepth(), '\t');
    }
    /**
     * goes OUT of current tree node and goes to parent.
     * @param tag
     * @return 
     */
    public TagTree out(String tag){
        if (current.parent == null) { throw new ArrayIndexOutOfBoundsException("Cannot go above root node.");}
        current = current.parent;
        return this;
    }
    
    @Override
    public String toString(){
        return this.root.toString();
    }
    
    private class TagNode{
        
        String tag;
        TagNode parent = null;
        ArrayList<TagNode> children;
        
        TagNode(String tag){
            this.tag = tag;
            this.children = new ArrayList<>();
        }        
        TagNode(String tag, TagNode parent){
            this.tag = tag;
            this.children = new ArrayList<>();
            this.parent = parent;
        }
        
        @Override
        public String toString(){
            return this.toString("");
        }
        
        private String toString(String tabs){
            String output = tabs;
            if (this.getId().equals(current.getId())){
                output += "*";
            }
            if (!this.children.isEmpty()){
                output += "<"+tag+">\r\n";
                for(TagNode child : children){
                    output += child.toString(tabs+"\t")+"\r\n";
                }
                output += tabs+"</"+tag+">";
            }else{
                output += "<"+tag+"/>";
            }
            return output;
        }
        
        String getId(){
            if (this.parent == null){
                return this.tag;
            }else{
                return this.parent.getId()+"."+this.tag;
            }
        }
    }
}
