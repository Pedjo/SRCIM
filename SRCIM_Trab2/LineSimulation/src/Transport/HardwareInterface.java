/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Transport;

/**
 *
 * @author André Dionisio Rocha
 */
public interface HardwareInterface {
    
    void initHardware(TransportAgent myTA);

    void finishProduct(Item myItem);

    
    void launchProduct(Item newItem);
    
}
