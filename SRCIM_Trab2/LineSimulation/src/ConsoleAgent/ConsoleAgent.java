package ConsoleAgent;

import CoalitionLeaderAgent.CoalitionLeaderAgent;
import ResourceAgent.ResourceAgent;
import Transport.TransportAgent;
import jade.core.Agent;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author André Dionisio Rocha
 */
public class ConsoleAgent extends Agent implements frameToAgentCom {

    ConsoleFrame myFrame;

    @Override
    protected void setup() {
        myFrame = new ConsoleFrame(this);
        myFrame.setVisible(true);
    }

    @Override
    public boolean launchNewSimulationEnvironment(String RulesLib, String HardwareLib) {
        try {
            TransportAgent newTA = new TransportAgent();
            newTA.setArguments(new Object[]{RulesLib, HardwareLib});
            AgentController agent = this.getContainerController().acceptNewAgent("TransportAgent", newTA);
            agent.start();
            return false;
        } catch (StaleProxyException ex) {
            Logger.getLogger(ConsoleAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    public void launchNewProduct(String process) {
        try {
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setContent(process);
            msg.addReceiver(Utilities.DFInteraction.SearchInDF(Utilities.Constants.DFSERVICE_TRANSPORT, this)[0].getName());
            msg.setOntology(Utilities.Constants.ONTOLOGY_NEW_PRODUCT);
            this.send(msg);
        } catch (FIPAException ex) {
            Logger.getLogger(ConsoleAgent.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("Transport Agent NOT present");
        }
    }

    @Override
    public void launchNewResource(String ResourceID, String ResourceLocation, String ResourceSkills) {
        try {
            LinkedList<String> mySkills = new LinkedList<>();
            StringTokenizer st = new StringTokenizer(ResourceSkills, UtilitiesTS.Constants.TOKEN_PRODUCT_SKILLS);
            while (st.hasMoreTokens()) {
                mySkills.add(st.nextToken());
            }
            //Launch ResourceAgent
            ResourceAgent newResource = new ResourceAgent();
            newResource.setArguments(new Object[]{ResourceLocation, mySkills});
            AgentController agent = this.getContainerController().acceptNewAgent(ResourceID, newResource);
            agent.start();
        } catch (StaleProxyException ex) {
            Logger.getLogger(ConsoleAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void launchNewCLA(String CLA_ID, String CLAlocation) {
        try {
            //Launch CoalitionLeaderAgent
            CoalitionLeaderAgent newCLA = new CoalitionLeaderAgent();
            newCLA.setArguments(new Object[]{CLAlocation});
            AgentController agent = this.getContainerController().acceptNewAgent(CLA_ID, newCLA);
            agent.start();
        } catch (StaleProxyException ex) {
            Logger.getLogger(ConsoleAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }


}
