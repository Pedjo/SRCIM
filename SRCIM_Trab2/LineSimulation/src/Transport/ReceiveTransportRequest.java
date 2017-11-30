/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Transport;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREResponder;

/**
 *
 * @author André Dionisio Rocha
 */
public class ReceiveTransportRequest extends AchieveREResponder{

    public ReceiveTransportRequest(Agent a, MessageTemplate mt) {
        super(a, mt);
    }

    @Override
    protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
        
        System.out.println("Transport Request");
        
        ACLMessage informToSend = request.createReply();
        informToSend.setPerformative(ACLMessage.INFORM);
        
        for (Diverter nextDiv : ((TransportAgent) myAgent).myDiverters.values()) {
            if(nextDiv.myItem != null && nextDiv.myItem.ID.equals(request.getSender().getLocalName()) && nextDiv.ID.matches(request.getContent())){
                return informToSend;
            }
        }
        
        if(((TransportAgent)myAgent).setNewDestiny(request.getSender().getLocalName(), request.getContent(), informToSend)){
            //registerPrepareResultNotification(((TransportAgent) myAgent).updateState);
            ACLMessage reply = request.createReply();
            reply.setPerformative(ACLMessage.AGREE);
            return reply;
        }else{
            ACLMessage reply = request.createReply();
            reply.setPerformative(ACLMessage.REFUSE);
            return reply;
        }
    }
    
}
