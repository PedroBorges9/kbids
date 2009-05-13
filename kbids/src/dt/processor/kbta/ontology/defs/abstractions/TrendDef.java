/**
 * 
 */
package dt.processor.kbta.ontology.defs.abstractions;

import java.util.ArrayList;


/**
 * @author 
 *
 */
public final class TrendDef extends AbstractionDef {

	public TrendDef(String name, String abstractedFrom,
		ArrayList<String> necessaryContexts /*TODO add mapping function */){
		super(name, necessaryContexts);
	}
}
