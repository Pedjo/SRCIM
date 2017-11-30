package ProductAgent;

import UtilitiesTS.Constants;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAException;
import java.util.logging.Logger;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import java.util.LinkedList;
import java.util.Vector;
import java.util.logging.Level;
import jade.proto.AchieveREInitiator;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;

/*
* To change this template, choose Tools | Templates
* and open the template in the editor.
*/

/**
 *
 * @author  Antonio Borges + Ricardo Martins + Ricardo Matos
 */
public class ProductAgent extends Agent {
    
    protected LinkedList<String> mySkills;              ////Lista de Skills associadas ao produto
    protected String productType;                       ////Tipo do produto
    protected AID nextExecutor;                         ////AID do proximo CLA/RA a executar uma skill no produto
    protected String Location = "Source";               ////Localização do produto
    protected String nextLocation;                      ////Proximo destino
    protected DFAgentDescription[] Lista;               ////Lista de RA/CLA que executam uma skill pretendida
    protected DFAgentDescription[] transport_AIDs;      ////Lista de Transportes
    
    /////Sequential Behaviours
    protected SequentialBehaviour sb;            ///Plano de tarefas a executar em condicoes normais
    protected SequentialBehaviour sb1;           ///Plano de tarefas a executar em condicoes anormais
    
    ////Mensagens
    protected ACLMessage msg_ct;                    ///Para o ContractNet
    protected ACLMessage msg_t;                     ///Para o transporte
    protected ACLMessage msg_ts;                    ///Para o transporte mas para o SI
    protected ACLMessage msg_re;                    ///Para o Request
    protected ACLMessage auxM;                      ///Auxiliar
    ///Lista de mensagens
    protected LinkedList<ACLMessage> msg_ct_Vector = new LinkedList<ACLMessage>();                 ///Para o ContractNet
    protected LinkedList<ACLMessage> msg_t_Vector = new LinkedList<ACLMessage>();                  ///Para o transporte
    protected LinkedList<ACLMessage> msg_re_Vector = new LinkedList<ACLMessage>();                 ///Para o Request
    
    protected int L_pos;        /// Iterador de skills
    ///Iteradores de listas de mensagens a enviar
    protected int R_pos;        /// Iterador de lista de mensagens referentes ao request de execucao
    protected int I_pos;        /// Iterador de lista de mensagens referentes ao request de transporte
    
    
    @Override
    protected void setup() {
        try {
            Object[] args = getArguments();
            //Skills associadas ao produto
            mySkills = (LinkedList<String>) args[0];
            transport_AIDs = UtilitiesTS.DFInteraction.SearchInDFByType(UtilitiesTS.Constants.DFSERVICE_TRANSPORT, this);
            
            //Registo do produto na DF
            UtilitiesTS.DFInteraction.RegisterInDF(this, this.getLocalName(), UtilitiesTS.Constants.TYPE_PRODUCT);
            
            ///////////// Produto sem skills associadas
            ///////////// Enviado para SI
            if (mySkills.size() == 0) {
                
                ////////// Preparação da mensagem para FIPArequest transporte para o SI
                ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
                msg.addReceiver(transport_AIDs[0].getName());
                msg.setOntology(Constants.ONTOLOGY_TRANSPORT);
                msg.setContent("Si");
                this.addBehaviour(new RequestTransport(this, msg));
                
            } else {
                
                //Inicializacao da sequencia de behaviours para a execução do produto
                sb = new SequentialBehaviour();
                ///Inicialização dos iteradores
                I_pos = R_pos = 0;
                
                for (L_pos = 0; L_pos < mySkills.size(); L_pos++) {
                    
                    //Procura de RA/CLA dentro da DF que executam a skill pretendida
                    Lista = UtilitiesTS.DFInteraction.SearchInDFByName(mySkills.get(L_pos), this);
                    
                    //Verificacao do caso de nao haver RA/CLA para executar certa skill
                    if(Lista.length==0){
                        System.out.println("********************************************");
                        System.out.println("*   There aren't any RA/CLA with: " + mySkills.get(L_pos)+ "     *");
                        System.out.println("********************************************");
                        sb = new SequentialBehaviour();
                        break;
                    }
                    
                    ////////// Preparacao da mensagem para Contract Net
                    msg_ct = new ACLMessage(ACLMessage.CFP);
                    msg_ct.setContent(mySkills.get(L_pos));
                    msg_ct.setOntology(UtilitiesTS.Constants.ONTOLOGY_PRODUCT_CONTRACT);
                    msg_ct_Vector.add(L_pos, msg_ct);
                    
                    //Percorrer a lista para enviar contracto os varios RA/CLA encontrados na df
                    for (int i = 0; i < Lista.length; i++) {
                        msg_ct.addReceiver(Lista[i].getName());
                    }
                    
                    ///Adicionar o contract ao Sequencial behaviour
                    sb.addSubBehaviour(new ContNetInitiator(this, msg_ct_Vector.get(L_pos)));
                    
                    ////////// Preparação da mensagem para FIPArequest transporte
                    msg_t = new ACLMessage(ACLMessage.REQUEST);
                    msg_t.addReceiver(transport_AIDs[0].getName());
                    msg_t.setOntology(Constants.ONTOLOGY_TRANSPORT);
                    msg_t_Vector.add(L_pos, msg_t);
                    ///Adicionar o request ao Sequencial behaviour
                    sb.addSubBehaviour(new RequestTransport(this, msg_t_Vector.get(L_pos)));
                    
                    ////////// Preparação da mensagem para FIPArequest para executar a skill
                    msg_re = new ACLMessage(ACLMessage.REQUEST);
                    msg_re.setOntology(UtilitiesTS.Constants.ONTOLOGY_EXECUTE_PA);
                    msg_re.setContent(mySkills.get(L_pos));
                    msg_re_Vector.add(L_pos, msg_re);
                    ///Adicionar ao Sequencial behaviour
                    sb.addSubBehaviour(new FIPAInitiator(this, msg_re_Vector.get(L_pos)));
                    
                }
                
                
                //////////////////////Produto finalizado
                ////////// Preparação da mensagem para FIPArequest transporte para o SI
                msg_ts = new ACLMessage(ACLMessage.REQUEST);
                msg_ts.addReceiver(transport_AIDs[0].getName());
                msg_ts.setOntology(Constants.ONTOLOGY_TRANSPORT);
                msg_ts.setContent("Si");
                ///Adicionar ao Sequencial behaviour
                sb.addSubBehaviour(new RequestTransport(this, msg_ts));
                ///Retirar o produto da DF
                sb.addSubBehaviour(new Kill());
                this.addBehaviour(sb);
                
            }
        } catch (FIPAException ex) {
            Logger.getLogger(ProductAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /////////////////////////////////FIPArequest
    private class FIPAInitiator extends AchieveREInitiator {
        
        public FIPAInitiator(Agent a, ACLMessage msg) {
            super(a, msg);
            
        }
        
        @Override
        protected void handleAgree(ACLMessage agree) {
            System.out.println(myAgent.getLocalName() + ": AGREE message received");
            
        }
        
        @Override
        protected void handleInform(ACLMessage agree) {
            System.out.println(myAgent.getLocalName() + ": INFORM message received");
            
        }
        
        @Override
        protected void handleRefuse(ACLMessage refuse) {
            System.out.println(myAgent.getLocalName() + ": REFUSE message received");
            //Forcar o sequential behaviour a done
            sb.skipNext();
            //Construcao do sequential behaviour por forma a retirar a peca do sistema
            sb1 = new SequentialBehaviour();
            sb1.addSubBehaviour(new RequestTransport(myAgent, msg_ts));
            ///Produto é retirado da DF
            sb1.addSubBehaviour(new Kill());
            myAgent.addBehaviour(sb1);
            
        }
        
    }
    /////////////////////////////////Contract Net
    private class ContNetInitiator extends ContractNetInitiator {
        
        public ContNetInitiator(Agent a, ACLMessage msg) {
            super(a, msg);
            System.out.println(myAgent.getLocalName() + " sent CFP of " + mySkills.get(L_pos));
        }
        
        @Override
        protected void handleInform(ACLMessage Inform) {
            System.out.println(myAgent.getLocalName() + ": INFORM message received of " + mySkills.get(I_pos));
            ////Actualizar mensagem de request transport
            auxM = msg_t_Vector.get((I_pos));
            nextLocation = Inform.getContent();
            //Actualizar o destino da mensagem
            auxM.setContent(Inform.getContent());
            msg_t_Vector.set((I_pos), auxM);
            I_pos++;
        }
        
        @Override
        protected void handleAllResponses(Vector responses, Vector acceptances) {
            System.out.println(myAgent.getLocalName() + ": ALL PROPOSALS received of " + mySkills.get(R_pos));
            ACLMessage auxMsg;
            ACLMessage auxMsg2;
            
            int i = 1;
            int Maior = 0;
            int peso1 = 999;
            int peso2 = 999;
            String[] auxsms;
            String[] auxsms2;
            
            //Primeira proposta de contrato
            auxMsg = (ACLMessage) responses.get(Maior);
            
            if(auxMsg.getPerformative()==ACLMessage.REFUSE){
                Maior = -1;
            }
            
            ///Ciclo para percorrer todas as propostas
            for (; i < responses.size(); i++) {
                if(Maior != -1)
                    auxMsg = (ACLMessage) responses.get(Maior);
                
                auxMsg2 = (ACLMessage) responses.get(i);
                
                //Verificacao de rejeicao de propostas
                if(auxMsg.getPerformative()==ACLMessage.REFUSE && auxMsg2.getPerformative()==ACLMessage.REFUSE){
                    Maior = -1;
                    break;
                }
                else if(auxMsg.getPerformative()==ACLMessage.REFUSE && auxMsg2.getPerformative()==ACLMessage.PROPOSE){
                    Maior = i;
                    break;
                }
                else if(auxMsg.getPerformative()==ACLMessage.PROPOSE && auxMsg2.getPerformative()==ACLMessage.REFUSE){
                    break;
                }
                else if(auxMsg.getPerformative()==ACLMessage.PROPOSE && auxMsg2.getPerformative()==ACLMessage.PROPOSE){
                    auxsms = auxMsg.getContent().split(";");
                    auxsms2 = auxMsg2.getContent().split(";");
                    
                    peso1 -= Integer.parseInt(auxsms[0]);
                    peso2 -= Integer.parseInt(auxsms2[0]);
                    
                    /////Se a localizacao do RA/CLA corresponder a do produto
                    ////Prioridade maxima
                    if (auxsms[1].equals(Location)) {
                        peso1 = 1000;
                    }
                    
                    if (auxsms2[1].equals(Location)) {
                        peso2 = 1000;
                    }
                    
                    if (peso1 < peso2) {
                        Maior = i;
                    }
                }
            }
            //Caso todos os RA/CLA's tenham recusado o contrato
            if(Maior == -1 ){
                System.out.println("*********************** Contract Refused ***********************");
                //Forcar o sequential behaviour a done
                sb.skipNext();
                //Construcao de
                sb1 = new SequentialBehaviour();
                sb1.addSubBehaviour(new RequestTransport(myAgent, msg_ts));
                //Construcao do sequential behaviour por forma a retirar a peca do sistema
                sb1.addSubBehaviour(new Kill());
                myAgent.addBehaviour(sb1);
            }
            else{
                ACLMessage reply;
                ///Responder a todos os RA/CLA com refuse excepto o RA/CLA que ira executar a skill
                for (i = 0; i < responses.size(); i++) {
                    auxMsg = (ACLMessage) responses.get(i);
                    reply = auxMsg.createReply();
                    
                    if (i == Maior) {
                        ///Aceitar proposta
                        reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                        auxMsg = (ACLMessage) responses.get(Maior);
                        ///actualização do proximo CLA/RA que vai executar a skill no produto
                        nextExecutor = auxMsg.getSender();
                        auxM = msg_re_Vector.get((R_pos));
                        //Actualizar do recetor das mensagens de request execute
                        auxM.addReceiver(auxMsg.getSender());
                        msg_re_Vector.set((R_pos), auxM);
                        
                    } else {
                        ///Rejeitar proposta
                        reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
                    }
                    ///Envio das respotas
                    acceptances.add(reply);
                }
                R_pos++;
            }
        }
    }
    
    /////////////////////////////////FIPArequest Transporte
    public class RequestTransport extends AchieveREInitiator {
        
        public RequestTransport(Agent a, ACLMessage msg) {
            super(a, msg);
        }
        
        @Override
        protected void handleAgree(ACLMessage agree) {
            System.out.println("Transporting");
        }
        
        @Override
        protected void handleRefuse(ACLMessage refuse) {
            System.out.println("Transport refused");
        }
        
        @Override
        protected void handleInform(ACLMessage inform) {
            System.out.println("Transport finished: Arrived " + inform.getSender().getLocalName());
            ///actualização da localização do produto
            Location = nextLocation;
        }
    }
    
/////////////////////////////////Kill Product
    public class Kill extends OneShotBehaviour{
        
        @Override
        public void action(){
            takeDown();
        }
    }
    
    @Override
    protected void takeDown() {
        System.out.println(this.getLocalName() + " killed");
        
        try {
            DFService.deregister(this);
        } catch (FIPAException ex) {
            Logger.getLogger(ProductAgent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
