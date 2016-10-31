/**
 * 
 */
package edu.stanford.nlp.pipeline;

import java.util.ArrayDeque;
import java.util.Queue;

import edu.stanford.nlp.ling.CoreAnnotations;

import java.util.HashMap;

/**
 * @author Luke Teacy
 *
 */
public class AnnotationCache {
	
	/**
	 * Default cache size.
	 */
	public final static int DEFAULT_SIZE = 50;
	
	/**
	 * Maximum number of entries to hold in this cache.
	 */
	private int cache_size;
	
	/**
	 * Queue to keep track of oldest annotations to get rid of next.
	 */
	private Queue<String> cache_order;
	
	/**
	 * Map holding cached annotations.
	 */
	private HashMap<String, Annotation> cache;
	
	/**
	 * Construct a new cache with default size.
	 */
	public AnnotationCache() {
		this(DEFAULT_SIZE);
	}
	
	/**
	 * Construct a new cache with specific size.
	 * @param size maximum number of annotations to hold in cache.
	 */
	public AnnotationCache(int size) {
		this.cache_order = new ArrayDeque<String>(size);
		this.cache = new HashMap<String, Annotation>();
		this.cache_size = size;
	}
	
	/**
	 * Retrieve Annotation for specified text from cache.
	 * If annotation for this text is not cached, then a new one is created,
	 * possibly resulting in the deletion of older annotations if capacity is full.
	 * @param text text to annotate.
	 * @return new annotation object for text.
	 */
	synchronized public Annotation get(String text) {
		
		// if possible, make a copy of an annotation from cache
		if(this.cache.containsKey(text)) {
			return new Annotation(this.cache.get(text));
		}
		
		// otherwise return a new annotation
		return new Annotation(text);
	}
	
	synchronized public void add(Annotation doc) {
	  
	  // get the text associated with this annotation
	  String text = doc.get(CoreAnnotations.TextAnnotation.class);
		
		// make room in cache if none available
		if(cache.size() >= this.cache_size) {
			String oldest_key = this.cache_order.poll();
			this.cache.remove(oldest_key);
		}
		
		// then put new a copy of the new annotation in cache
		Annotation new_annotation = new Annotation(doc);
		this.cache_order.add(text);
		this.cache.put(text, new_annotation);
	}
}
