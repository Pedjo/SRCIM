package ResourceAgent;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import jade.core.Agent;
import jade.domain.DFService;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.LinkedList;

// Imports (DF)
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAException;
import jade.proto.AchieveREResponder;
import java.util.logging.Level;
import java.util.logging.Logger;

//imports ContractNet
import jade.proto.ContractNetResponder;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class ResourceAgent extends Agent{
    
    protected LinkedList<String> mySkills;
    protected String myLocation;

    // Skill production times
    public HashMap<String, Integer> skill_times = new HashMap();
    // Skill states
    public HashMap<String, Boolean> skill_states = new HashMap();
    // Product waiting queue
    public Multimap<String,String> PwQueue = ArrayListMultimap.create();  // Product / Skill
    // Product arrived queue
    public Multimap<String, String> ParrQueue = ArrayListMultimap.create();   // Product / Skill or CLA / Skill

    @Override
    protected void setup() {
        //Argumets [0]resource location [1]skills that resource can perform
        Object[] args = getArguments();

        myLocation = (String) args[0];
        mySkills = (LinkedList<String>) args[1];

        /*
            DF Registry
            The PA/CLA will then search for TYPE_RA_SKILL or TYPE_RA_LOCAL 
        */
        try {
            UtilitiesTS.DFInteraction.RegisterInDF_RA(this, UtilitiesTS.Constants.TYPE_RA_SKILL, UtilitiesTS.Constants.TYPE_RA_LOCAL, mySkills, myLocation);
        } catch (FIPAException ex) {
            Logger.getLogger(ResourceAgent.class.getName()).log(Level.SEVERE, null, ex);
        }     
        
        // All the initial states are set to working
        for (String mySkill : mySkills) { 
            skill_states.put(mySkill, true);
        } 
        
        /*
            Retrieve times of operation for each skill from a .csv file.
        */
        String csvFile = "RA_presets/Skill_times.csv";
        String line = "";
        String cvsSplitBy = ";";
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            while ((line = br.readLine()) != null) {
                // use semi-comma as separator
                String[] skill_time = line.split(cvsSplitBy);
                skill_times.put(skill_time[0], Integer.parseInt(skill_time[1]));
            }
            
        } catch (IOException e) {
        }
        

        
        // Contract Net Participant (with PA)
        this.addBehaviour(new responder(this, MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.CFP), MessageTemplate.MatchOntology(UtilitiesTS.Constants.ONTOLOGY_PRODUCT_CONTRACT))));
        
        // Request Responder (with PA)
        this.addBehaviour(new responderREQ_execute_PA(this, MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchOntology(UtilitiesTS.Constants.ONTOLOGY_EXECUTE_PA))));
        
        // Request Responder (with CLA)
        this.addBehaviour(new responderREQ_execute_CLA(this, MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), MessageTemplate.MatchOntology(UtilitiesTS.Constants.ONTOLOGY_EXECUTE_CLA))));

    }
    
    
    
    /***************************************************************************
     * Malfunction
     ***************************************************************************

        This simulates that a skill might be malfunctioning (with 
        a probability of 5%).

        [true = working | false = malfunctioning]
     * @return 
    */
    
    protected boolean simulateResourceMalfunctioning(String skill)    {
        boolean state = skill_states.get(skill);
              
        // If skill is still working, then, there is a chance of malfunction,
        //  else, it continues with the malfunction
        if (state)
            state = (Math.random() < 0.8);//true; //Math.random() < 0.95;
        
        skill_states.put(skill, state);
        
        System.out.println("Working State: "+state);
        return state;
    }
    
    

    /***************************************************************************
     * ContractNet from PA to RA
     ***************************************************************************
     */

    /*
        The product agent requests Skill_A, and the RA returns all
        of the Skill_A' queues with the number of waiting products.
        After the PA chooses, the RA returns an aknowledge and the 
        localization of the chosen skill.

    PA(Initiator)                          RA(Participant)
                ------------ cfp --------->
                <----- refuse/proposal ----
                ------ reject/accept ----->
                <----- failure or ---------
                         inform done
                         inform result
    */


    private class responder extends ContractNetResponder{

        public responder(Agent a, MessageTemplate mt){
            super(a, mt);
        }

        /*
            To send the proposal with the Skill's queue.
        */        
        @Override
        protected ACLMessage handleCfp(ACLMessage cfp) throws RefuseException, FailureException, NotUnderstoodException{

                ACLMessage msg= cfp.createReply();
                
                if(simulateResourceMalfunctioning(cfp.getContent()))
                    msg.setPerformative(ACLMessage.PROPOSE);
                else
                    msg.setPerformative(ACLMessage.REFUSE);

                msg.setOntology(UtilitiesTS.Constants.ONTOLOGY_PRODUCT_CONTRACT);

                // Calculates the weight taking into consideration the waiting queue
                // and the time it takes to execute (a skill) - from a file
                int weight = PwQueue.size()+ParrQueue.size()+(skill_times.get(cfp.getContent())/100);
                msg.setContent(weight + ";" + myLocation);
                System.out.println(weight + ";" + myLocation); // The PA then selects the one with the minimum queue size (depending on the location)
                return msg;

        }   

        /*
            If the PA accepts the proposal, then the INFORM is sent.
        */  
        @Override 
        protected ACLMessage handleAcceptProposal (ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException{
                ACLMessage msg = cfp.createReply();

                msg.setPerformative(ACLMessage.INFORM);
                msg.setOntology(UtilitiesTS.Constants.ONTOLOGY_PRODUCT_CONTRACT);
                msg.setContent(myLocation);
                // added to queue
                PwQueue.put(cfp.getSender().getName(), cfp.getContent());
                return msg;
        }
    }




    /***************************************************************************
     * takeDown 
     ***************************************************************************
     */

    @Override
    protected void takeDown() {
         try {
            DFService.deregister(this);
        } catch (FIPAException ex) {
            Logger.getLogger(ResourceAgent.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }



    /***************************************************************************
     * Request from PA to RA
     ***************************************************************************
     */

    /*
        When a Product PA arrives at the station with the pre-sent 
        location, the PA notifies RA.
        The skill is ready for execution.
        After 10 seconds, the
        skill is considered done and the PA is notified.

    PA (Initiator)                    RA(Responder)
                ----------- request ------>
                <----- refuse/agree -------
                <----- failure or ---------
                         inform done
                         inform result
    */

    private class responderREQ_execute_PA extends AchieveREResponder{

        public responderREQ_execute_PA(Agent a, MessageTemplate mt){
            super(a, mt);
        }

        /*
            After receiving an "execute" from the PA when it
            reaches the Skill' location. The RA will reply
            with an AGREE and the pwQueue decreases and the Parr increases.
        */  
        @Override
        protected ACLMessage handleRequest(ACLMessage request) throws NotUnderstoodException, RefuseException {
            ACLMessage msg = request.createReply();
            
            if(simulateResourceMalfunctioning(request.getContent()))    {
                msg.setPerformative(ACLMessage.AGREE);
                System.out.println("WaitingQueue");
                ParrQueue.put(request.getSender().getName(), request.getContent());
            }
            else    
                msg.setPerformative(ACLMessage.REFUSE);

            msg.setOntology(UtilitiesTS.Constants.ONTOLOGY_EXECUTE_PA);

            // Remove from Waiting
            PwQueue.remove(request.getSender().getName(), request.getContent());
            return msg;
        }

        /*
            The Skill will be executed (10 seconds) and after that
            the PA will be notified. The skill is then removed 
            from the QarrQueue.
        */ 
         @Override
        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
            ACLMessage msg = request.createReply();
            
            msg.setPerformative(ACLMessage.INFORM);
            msg.setOntology(UtilitiesTS.Constants.ONTOLOGY_EXECUTE_PA);

            // To simulate a skill being executed (ms from file)
            block(skill_times.get(request.getContent()));

            System.out.println("Executed");
            ParrQueue.remove(request.getSender().getName(), request.getContent());
            return msg;
        }
    }
    
    /***************************************************************************
     * Request from CLA to RA
     ***************************************************************************
     */

    /*
        When a Product arrives at the station with the pre-sent 
        location, the CLA notifies RA.
        The CLA's skill is ready for execution.
        After 10 seconds, the
        skill is considered done and the CLA is notified.

    CLA(Initiator)                    RA(Responder)
                ----------- request ------>
                <----- refuse/agree -------
                <----- failure or ---------
                         inform done
                         inform result
    */

    private class responderREQ_execute_CLA extends AchieveREResponder{

        public responderREQ_execute_CLA(Agent a, MessageTemplate mt){
            super(a, mt);
        }

        /*
            After receiving an "execute" from the CLA when it
            reaches a specific Skill' location. The RA will reply
            with an AGREE and the Parr increases.
        */  
        @Override
        protected ACLMessage handleRequest(ACLMessage request) throws RefuseException {
            ACLMessage msg = request.createReply();

            msg.setOntology(UtilitiesTS.Constants.ONTOLOGY_EXECUTE_CLA);
            if(simulateResourceMalfunctioning(request.getContent()))    {
                msg.setPerformative(ACLMessage.AGREE);
                ParrQueue.put(request.getSender().getName(), request.getContent());
            }
            else    {
                msg.setPerformative(ACLMessage.REFUSE);
                msg.setContent(request.getContent());
            }

            return msg;
        }
        

        /*
            The Skill will be executed (10 seconds) and after that
            the PA will be notified. The skill is then removed 
            from the QarrQueue.
        */ 
         @Override
        protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
            ACLMessage msg = request.createReply();
            
            msg.setOntology(UtilitiesTS.Constants.ONTOLOGY_EXECUTE_CLA);
            msg.setPerformative(ACLMessage.INFORM);
            
            // To simulate a skill being executed (ms from file)
            block(skill_times.get(request.getContent()));

            ParrQueue.remove(request.getSender().getName(), request.getContent());
            return msg;
        }
    }
    
    /***************************************************************************
    * From CLA to RA - Request Skill
    **************************************************************************
    */

    /*
        The class below is not being used due to our system's architecture.

        CLA Requests specific atomic skill from RA. If the skill is 
        available, then an agree is sent and the skill is added to the waiting
        queue.

    CLA(Initiator)                         RA(Responder)
                ----------- request ------>
                <----- refuse/agree -------
                <----- failure or ---------
                         inform done
                         inform result

    */
//    private class responderREQ_CLA extends AchieveREResponder{
//
//        public responderREQ_CLA(Agent a, MessageTemplate mt){
//            super(a, mt);
//        }
//
//        /*
//            If the skill is available, put on the waiting queue and
//            reply with an AGREE
//        */ 
//        @Override
//        protected ACLMessage handleRequest(ACLMessage request) throws RefuseException {
//            ACLMessage msg = request.createReply();
//            if(request.getOntology().equals(UtilitiesTS.Constants.ONTOLOGY_SKILL_CLA))  {
//                PwQueue.put(request.getSender().getName(), request.getContent());
//                if(simulateResourceMalfunctioning())
//                    msg.setPerformative(ACLMessage.AGREE);
//                else
//                    msg.setPerformative(ACLMessage.REFUSE);
//                return msg;
//            }
//            return null;
//        }
//    }
}