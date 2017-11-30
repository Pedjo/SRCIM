/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Transport.Libs;

import Simulation.Layout1;
import Transport.Conveyor;
import Transport.HardwareInterface;
import Transport.Item;
import Transport.TransportAgent;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import java.util.LinkedList;

/**
 *
 * @author André Dionisio Rocha
 */
public class HardwareSimulationLayout1 implements HardwareInterface {

    Layout1 myLayout;

    @Override
    public void initHardware(TransportAgent myTA) {
        myLayout = new Layout1();
        myLayout.setVisible(true);
        myTA.addBehaviour(new Simulate(myTA, 1000, myLayout));
    }

    @Override
    public void finishProduct(Item myItem) {
        myLayout.finishProduct(myItem);
    }

    @Override
    public void launchProduct(Item newItem) {
        myLayout.launchProduct(newItem);
    }

    private static class Simulate extends TickerBehaviour {

        TransportAgent myTA;
        Layout1 myLayout;

        public Simulate(Agent a, long period, Layout1 myLayout) {
            super(a, period);
            this.myTA = (TransportAgent) a;
            this.myLayout = myLayout;
        }

        @Override
        protected void onTick() {

            for (Conveyor conv : myTA.getMyConveyors().values()) {
                LinkedList<Item> inventory = conv.getInventory();
                for (int i = 0; i < inventory.size(); i++) {
                    if (inventory.get(i).getPosition() != 0 && inventory.get(i).getPosition() != i) {
                        inventory.get(i).decrementPosition();
                    }
                }

            }
            this.myLayout.updateState(myTA.getMyDiverters(), myTA.getMyConveyors());
        }
    }
}
