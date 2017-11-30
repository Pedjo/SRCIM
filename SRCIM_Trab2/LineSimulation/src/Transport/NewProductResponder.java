/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Transport;

import UtilitiesTS.Constants;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

/**
 *
 * @author André Dionisio Rocha
 */
public class NewProductResponder extends TickerBehaviour {

    public NewProductResponder(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {
        ACLMessage msg = myAgent.receive(MessageTemplate.and(MessageTemplate.MatchOntology(Constants.ONTOLOGY_NEW_PRODUCT), MessageTemplate.MatchPerformative(ACLMessage.INFORM)));
        if (msg != null) {
            //Put Product in the queue to be launched
            ((TransportAgent) myAgent).prodQueue.add(msg.getContent());
        }
    }
}
