/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Transport;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

/**
 *
 * @author André Dionisio Rocha
 */
public class UpdateState extends TickerBehaviour {

    public UpdateState(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {

        //Verify Diverters
        for (Diverter nextDiv : ((TransportAgent) myAgent).myDiverters.values()) {
            //Sink
            if (nextDiv.myItem != null && nextDiv.ID.equals("Si") && nextDiv.myItem.position == 0) {
                ((TransportAgent) myAgent).send(nextDiv.myItem.msgToInform);
                ((TransportAgent) myAgent).myHW.finishProduct(nextDiv.myItem);
                nextDiv.myItem = null;
            }

            if (nextDiv.myItem != null && nextDiv.myItem.position == 1) {
                if (nextDiv.myItem.nextDestination != null && nextDiv.myItem.nextDestination.equals(nextDiv.ID) && !nextDiv.ID.equals("So")) {
                    ((TransportAgent) myAgent).send(nextDiv.myItem.msgToInform);
                }
                nextDiv.myItem.position--;
            } else {
                if (nextDiv.myItem != null && nextDiv.myItem.position == 0 && nextDiv.myItem.nextDestination != null && !nextDiv.myItem.nextDestination.equals(nextDiv.getID())) {
                    String nextHop = ((TransportAgent) myAgent).myRules.routeItem(nextDiv.ID, nextDiv.myItem.nextDestination);
                    if (nextHop != null && ((TransportAgent) myAgent).myConveyors.get(nextHop).inventory.size() < ((TransportAgent) myAgent).myConveyors.get(nextHop).dimension) {
                        nextDiv.myItem.position = ((TransportAgent) myAgent).myConveyors.get(nextHop).dimension;
                        ((TransportAgent) myAgent).myConveyors.get(nextHop).inventory.add(nextDiv.myItem);
                        nextDiv.myItem = null;
                    }
                }
            }
        }

        //Verify Conveyors
        for (Conveyor nextConv : ((TransportAgent) myAgent).myConveyors.values()) {
            if (!nextConv.inventory.isEmpty() && nextConv.inventory.getFirst().position == 0) {
                String nextHop = ((TransportAgent) myAgent).myRules.routeItem(nextConv.ID, nextConv.inventory.getFirst().nextDestination);
                if (nextHop != null && ((TransportAgent) myAgent).myDiverters.get(nextHop).myItem == null) {
                    ((TransportAgent) myAgent).myDiverters.get(nextHop).myItem = nextConv.inventory.getFirst();
                    ((TransportAgent) myAgent).myDiverters.get(nextHop).myItem.position = 1;
                    nextConv.inventory.removeFirst();
                }
            }

        }
    }
}
