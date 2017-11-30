/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
 */
package CoalitionLeaderAgent;

import UtilitiesTS.Constants;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.AchieveREInitiator;
import jade.proto.AchieveREResponder;
import jade.proto.ContractNetResponder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Duarte Bragadesto
 * @author José Pinto
 * @author Pedro Rodrigues
 */
public class CoalitionLeaderAgent extends Agent {

    protected String myLocation;
    protected DFAgentDescription[] RAs;
    protected LinkedList<String> services = new LinkedList<String>();

    //protected String[] services;
    protected String services_string;
    protected String[] mySkills = new String[10];
    protected String[][] RAUseSkills = new String[10][6];
    // Product waiting queue
    public Multimap<String, String> PwQueue = ArrayListMultimap.create();  // Product / Skill
    // Product arrived queue
    public Multimap<String, String> ParrQueue = ArrayListMultimap.create();   // Product / Skill or CLA / Skill

    protected SequentialBehaviour execSkiBeh;
    protected  ACLMessage msg2;
    
    protected int refuse_aux;

    @Override
    protected void setup() {
        //Argumets [0]CLA location
        Object[] args = getArguments();
        //Get my location
        myLocation = (String) args[0];

        //Launch Behaviours here
        SequentialBehaviour seq = new SequentialBehaviour();
        seq.addSubBehaviour(new ReadDFOneShotBehaviour()); //Search in DF here
        seq.addSubBehaviour(new CLARulesOneShotBehaviour()); //Register in DF here
        addBehaviour(seq);
        this.addBehaviour(new CNETResponderToPA(this, MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CFP), MessageTemplate.MatchOntology(UtilitiesTS.Constants.ONTOLOGY_PRODUCT_CONTRACT))));

    }

    //Search in DF
    class ReadDFOneShotBehaviour extends OneShotBehaviour {

        @Override
        public void action() {

            try {
                RAs = UtilitiesTS.DFInteraction.SearchInDF(UtilitiesTS.Constants.TYPE_RA_LOCAL, myLocation, myAgent);
            } catch (FIPAException ex) {
                Logger.getLogger(CoalitionLeaderAgent.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    //Register in DF
    class CLARulesOneShotBehaviour extends OneShotBehaviour {

        @Override
        public void action() {

            for (int i = 0; i < RAs.length; i++) {
                Iterator services_it = RAs[i].getAllServices();

                while (services_it.hasNext()) {
                    ServiceDescription a = (ServiceDescription) services_it.next();
                    services_string = a.getName();
                    services.add(services_string);

                    System.out.println("s: " + services_string);
                }
            }
            int j = 0;
            if (services.contains("Sk_A") && services.contains("Sk_B") && services.contains("Sk_C") && services.contains("Sk_D")) {
                try {
                    UtilitiesTS.DFInteraction.RegisterInDF(myAgent, "Sk_Z", UtilitiesTS.Constants.TYPE_CLA_SKILL);
                    mySkills[j] = "Sk_Z";
                    RAUseSkills[j][0] = "Sk_A";
                    RAUseSkills[j][1] = "Sk_B";
                    RAUseSkills[j][2] = "Sk_C";
                    RAUseSkills[j][3] = "Sk_D";
                    j++;
                } catch (FIPAException ex) {
                    Logger.getLogger(CoalitionLeaderAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (services.contains("Sk_A") && services.contains("Sk_B")) {
                try {
                    UtilitiesTS.DFInteraction.RegisterInDF(myAgent, "Sk_Y", UtilitiesTS.Constants.TYPE_CLA_SKILL);
                    mySkills[j] = "Sk_Y";
                    RAUseSkills[j][0] = "Sk_A";
                    RAUseSkills[j][1] = "Sk_B";
                    j++;
                } catch (FIPAException ex) {
                    Logger.getLogger(CoalitionLeaderAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            if (services.contains("Sk_C") && services.contains("Sk_D")) {
                try {
                    UtilitiesTS.DFInteraction.RegisterInDF(myAgent, "Sk_X", UtilitiesTS.Constants.TYPE_CLA_SKILL);
                    mySkills[j] = "Sk_X";
                    RAUseSkills[j][0] = "Sk_C";
                    RAUseSkills[j][1] = "Sk_D";
                    j++;
                } catch (FIPAException ex) {
                    Logger.getLogger(CoalitionLeaderAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (services.contains("Sk_E") && services.contains("Sk_F")) {
                try {
                    UtilitiesTS.DFInteraction.RegisterInDF(myAgent, "Sk_W", UtilitiesTS.Constants.TYPE_CLA_SKILL);
                    mySkills[j] = "Sk_W";
                    RAUseSkills[j][0] = "Sk_E";
                    RAUseSkills[j][1] = "Sk_F";
                    j++;
                } catch (FIPAException ex) {
                    Logger.getLogger(CoalitionLeaderAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (services.contains("Sk_G") && services.contains("Sk_H") && services.contains("Sk_C3")) {
                try {
                    UtilitiesTS.DFInteraction.RegisterInDF(myAgent, "Sk_V", UtilitiesTS.Constants.TYPE_CLA_SKILL);
                    mySkills[j] = "Sk_V";
                    RAUseSkills[j][0] = "Sk_G";
                    RAUseSkills[j][1] = "Sk_H";
                    j++;
                } catch (FIPAException ex) {
                    Logger.getLogger(CoalitionLeaderAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (services.contains("Sk_D") && services.contains("Sk_A")) {
                try {
                    UtilitiesTS.DFInteraction.RegisterInDF(myAgent, "Sk_U", UtilitiesTS.Constants.TYPE_CLA_SKILL);
                    mySkills[j] = "Sk_U";
                    RAUseSkills[j][0] = "Sk_D";
                    RAUseSkills[j][1] = "Sk_A";
                    j++;
                } catch (FIPAException ex) {
                    Logger.getLogger(CoalitionLeaderAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            if (services.contains("Sk_H") && services.contains("Sk_B")) {
                try {
                    UtilitiesTS.DFInteraction.RegisterInDF(myAgent, "Sk_K", UtilitiesTS.Constants.TYPE_CLA_SKILL);
                    mySkills[j] = "Sk_K";
                    RAUseSkills[j][0] = "Sk_H";
                    RAUseSkills[j][1] = "Sk_B";
                    j++;
                } catch (FIPAException ex) {
                    Logger.getLogger(CoalitionLeaderAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    class InformPA extends SimpleBehaviour {

        private boolean finished = false;
        private ACLMessage msg;

        public InformPA(Agent a, ACLMessage _msg) {
            super(a);
            this.msg = _msg;
        }

        @Override
        public void action() {
            //send mensage
            send(msg);
            finished = true;
        }

        @Override
        public boolean done() {
            return finished;
        }
    }

    // Contratnet with Product
    public class CNETResponderToPA extends ContractNetResponder {

        public CNETResponderToPA(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        @Override
        protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException {

            if (cfp.getOntology().equals(UtilitiesTS.Constants.ONTOLOGY_PRODUCT_CONTRACT)) {
                ACLMessage msg = cfp.createReply();
                msg.setPerformative(ACLMessage.PROPOSE);
                msg.setOntology(UtilitiesTS.Constants.ONTOLOGY_PRODUCT_CONTRACT);
                msg.setContent("" + PwQueue.size() + ";" + myLocation); // The PA then selects the one with the minimum queue size.
                System.out.println("CLA aceitou o contrato");
                return msg;
            }
            return null;
        }

        /*
        If the PA accepts the proposal, then the INFORM is sent.
         */
        @Override
        protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
            if (accept.getOntology().equals(UtilitiesTS.Constants.ONTOLOGY_PRODUCT_CONTRACT)) {
                ACLMessage msg = cfp.createReply();
                msg.setPerformative(ACLMessage.INFORM);
                msg.setOntology(UtilitiesTS.Constants.ONTOLOGY_PRODUCT_CONTRACT);
                msg.setContent(myLocation);
                // added to queue waiting line here
                PwQueue.put(cfp.getSender().getName(), cfp.getContent());
                myAgent.addBehaviour(new responderPA(myAgent, MessageTemplate.MatchPerformative(ACLMessage.REQUEST)));
                return msg;
            }
            return null;
        }

        /*
        If the PA refuses, then the Skill is removed from the waiting queue.
         */
        protected void handleRefuse(ACLMessage refuse) {
        }

    }

    // Responder ao executar do PA
    private class responderPA extends AchieveREResponder {

        private int i, CLA_Skill_ID = 0, RA_Skill_ID, RAs_id = 999;
        private String skill_ra;

        public responderPA(Agent a, MessageTemplate mt) {
            super(a, mt);
        }

        @Override
        protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
            ACLMessage msg_1 = request.createReply();

            if (request.getOntology().equals(UtilitiesTS.Constants.ONTOLOGY_EXECUTE_PA)) {
                System.out.println("CLA vai executar a Tarefa");
                msg_1.setPerformative(ACLMessage.AGREE);
                msg_1.setOntology(UtilitiesTS.Constants.ONTOLOGY_EXECUTE_PA);

                // Remove from Waiting
                PwQueue.remove(request.getSender().getName(), request.getContent());

                //skill que o CLA ira executar
                String skill = request.getContent();

                execSkiBeh = new SequentialBehaviour(); //Sequencial Behaviour to send information               
                //Obtain the skill ID off the CLA Agent
                for (i = 0; i < mySkills.length; i++) {
                    if (mySkills[i].equals(skill)) {
                        CLA_Skill_ID = i;
                        break;
                    }
                }

                for (RA_Skill_ID = 0; RA_Skill_ID < RAUseSkills[CLA_Skill_ID].length; RA_Skill_ID++) {   // percorre o RAUseSkills com todas as skills do RA para a dada skill do CLA
                    skill_ra = RAUseSkills[CLA_Skill_ID][RA_Skill_ID];
                    RAs_id = 999;
                    if (skill_ra == null) {
                        break;
                    }

                    for (i = 0; i < RAs.length; i++) {
                        Iterator services_it = RAs[i].getAllServices(); // ALl Services proveid by this Agent

                        while (services_it.hasNext()) {
                            ServiceDescription a = (ServiceDescription) services_it.next();
                            services_string = a.getName();
                            //Find the correct skill
                            if (services_string.equals(skill_ra)) {                                
                                RAs_id = i; // value off id in the DFAgentDescription vector
                                break;
                            }
                        }
                        if (RAs_id == i) {
                            break;
                        }
                    }
                    ACLMessage msg_to_RA = new ACLMessage(ACLMessage.REQUEST);
                    msg_to_RA.setOntology(UtilitiesTS.Constants.ONTOLOGY_EXECUTE_CLA);
                    msg_to_RA.addReceiver(RAs[RAs_id].getName());
                    msg_to_RA.setContent(RAUseSkills[CLA_Skill_ID][RA_Skill_ID]);
                    //behaviour to send request to RA for execution
                    execSkiBeh.addSubBehaviour(new request_Execut_RA(myAgent, msg_to_RA));
                }

                // behaviour to send menssage with the inform to PA
                msg2 = request.createReply();
                msg2.setPerformative(ACLMessage.INFORM);
                msg2.setOntology(UtilitiesTS.Constants.ONTOLOGY_EXECUTE_PA);
                execSkiBeh.addSubBehaviour(new InformPA(myAgent, msg2));
                // build the sequencial behaviour to execute\
                registerPrepareResultNotification(execSkiBeh);

            } else {
                msg_1.setPerformative(ACLMessage.REFUSE);
            }
            return msg_1;
        }
    }

    // Request skill to RA
    private class request_Execut_RA extends AchieveREInitiator {

        public request_Execut_RA(Agent a, ACLMessage msg) {
            super(a, msg);
        }

        @Override
        protected void handleAgree(ACLMessage agree) {
            System.out.println(myAgent.getLocalName() + ": AGREE message received");
        }

        @Override
        protected void handleInform(ACLMessage inform) {
            System.out.println(myAgent.getLocalName() + ": Inform message received");
        }

        @Override
        protected void handleRefuse(ACLMessage refuse) {
            System.out.println(myAgent.getLocalName() + ": Refuse message received");
            System.out.println("*********************** Product not executed ***********************");
            execSkiBeh.skipNext();
            SequentialBehaviour execSkiBeh2 = new SequentialBehaviour();
            execSkiBeh2.addSubBehaviour(new InformPA(myAgent, msg2));
            myAgent.addBehaviour(execSkiBeh2);
        }

        @Override
        protected void handleFailure(ACLMessage failure) {
            System.out.println(myAgent.getLocalName() + ": Failure message received");
        }
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (FIPAException ex) {
            Logger.getLogger(CoalitionLeaderAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
