/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Transport;

import ProductAgent.ProductAgent;
import UtilitiesTS.Constants;
import UtilitiesTS.DFInteraction;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author André Dionisio Rocha
 */
public class TransportAgent extends Agent {
    /*
     * Arguments -  [0] RulesLib Name
     *              [1] HardwareLib Name
     */

    protected HashMap<String, Conveyor> myConveyors;
    protected HashMap<String, Diverter> myDiverters;
    protected RulesInterface myRules;
    protected HardwareInterface myHW;
    protected LinkedList<String> prodQueue;
    protected UpdateState updateState;

    @Override
    protected void setup() {
        
        try {
            //Initialize lists
            myConveyors = new HashMap<>();
            myDiverters =  new HashMap<>();
            prodQueue = new LinkedList<>();
            
            //Register in DF
            DFInteraction.RegisterInDF(this, this.getLocalName(), Constants.DFSERVICE_TRANSPORT);
            
            //Get Arguments
            Object[] arguments = this.getArguments();
            
            //Load Rules Lib
            try {
                String className = "Transport.Libs." + (String)arguments[0];
                Class cls = Class.forName(className);
                Object instance;
                instance = cls.newInstance();
                myRules = (RulesInterface) instance;
                System.out.println(instance);
            } catch (    ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(TransportAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
            myRules.initRules(this);
            
            //Load Hardware Lib
            try {
                String className = "Transport.Libs." + (String)arguments[1];
                Class cls = Class.forName(className);
                Object instance;
                instance = cls.newInstance();
                myHW = (HardwareInterface) instance;
                System.out.println(instance);
            } catch (    ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                Logger.getLogger(TransportAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
            myHW.initHardware(this);
            
            updateState = new UpdateState(this, 500);
            this.addBehaviour(updateState);
            
            this.addBehaviour(new ReceiveTransportRequest(this, MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchOntology(Constants.ONTOLOGY_TRANSPORT))));
            
            this.addBehaviour(new NewProductResponder(this, UtilitiesTS.Constants.TIMER_CHECK_NEW_PROD_MSG));
            
            this.addBehaviour(new LaunchProduct(this, Constants.TIMER_LAUNCH_PROD));
            
        } catch (FIPAException ex) {
            Logger.getLogger(TransportAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException ex) {
            Logger.getLogger(TransportAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    //Add a new conveyor to the myConveyors list
    public void addNewConveyor(Conveyor conv){
        this.myConveyors.put(conv.ID, conv);
    }
    
    //Add a new diverter to the myDiverters list
    public void addNewDiverter(Diverter div){
        this.myDiverters.put(div.ID, div);
    }
    
    
    public HashMap<String, Conveyor> getMyConveyors() {
        return myConveyors;
    }

    public HashMap<String, Diverter> getMyDiverters() {
        return myDiverters;
    }

    boolean setNewDestiny(String prodID, String newDestiny, ACLMessage informToSend) {
        for (Diverter nextDiv : this.myDiverters.values()) {
            if(nextDiv.myItem != null && nextDiv.myItem.ID.equals(prodID)){
                nextDiv.myItem.nextDestination = newDestiny;
                nextDiv.myItem.msgToInform = informToSend;
                return true;
            }
        }
        return false;
    }

    void launchNewProduct(String prodName, LinkedList<String> process) throws StaleProxyException {
        //Add Carrier
        Item newItem = new Item(prodName, 1);
        this.getMyDiverters().get("So").setMyItem(newItem);
        //Launch ProductAgent
        ProductAgent newProd = new ProductAgent();
        newProd.setArguments(new Object[]{process});
        AgentController agent = this.getContainerController().acceptNewAgent(prodName, newProd);
        agent.start();
        //UpdateHW
        this.myHW.launchProduct(newItem);
    }
    
}
