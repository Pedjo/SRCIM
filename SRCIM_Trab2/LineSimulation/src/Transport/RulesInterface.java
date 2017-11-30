/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Transport;

/**
 *
 * @author André Dionisio Rocha
 */
public interface RulesInterface {
    
    String routeItem(String dispatchingEntity, String nextDestination);
    
    void initRules(TransportAgent myTA);
    
}
