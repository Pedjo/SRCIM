/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Transport;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.StaleProxyException;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author André
 */
public class LaunchProduct extends TickerBehaviour {

    int prodName;

    public LaunchProduct(Agent a, long period) {
        super(a, period);
        prodName = 0;
    }

    @Override
    protected void onTick() {
        if (!((TransportAgent) myAgent).prodQueue.isEmpty() && ((TransportAgent) myAgent).myDiverters.get("So").myItem == null) {
            try {
                ((TransportAgent) myAgent).launchNewProduct("Prod_" + prodName++, getProcessList(((TransportAgent) myAgent).prodQueue.removeFirst()));
            } catch (StaleProxyException ex) {
                Logger.getLogger(LaunchProduct.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    /*
     * Get tokens from the content
     */
    public static LinkedList<String> getProcessList(String myString) {
        LinkedList<String> myProcess = new LinkedList<>();
        StringTokenizer st = new StringTokenizer(myString, UtilitiesTS.Constants.TOKEN_PRODUCT_SKILLS);
        while (st.hasMoreTokens()) {
            myProcess.add(st.nextToken());
        }
        return myProcess;
    }
}
