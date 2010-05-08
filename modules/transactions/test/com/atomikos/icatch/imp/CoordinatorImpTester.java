//
//  CoordinatorImpTester.java
//  icatch
//
//  Created by guy on Tue Mar 27 2001.
//  
//

package com.atomikos.icatch.imp;
import java.util.Hashtable;
import java.util.Stack;

import com.atomikos.diagnostics.PrintStreamConsole;
import com.atomikos.icatch.HeurCommitException;
import com.atomikos.icatch.HeurHazardException;
import com.atomikos.icatch.HeurMixedException;
import com.atomikos.icatch.HeurRollbackException;
import com.atomikos.icatch.HeuristicMessage;
import com.atomikos.icatch.Participant;
import com.atomikos.icatch.RollbackException;
import com.atomikos.icatch.StringHeuristicMessage;
import com.atomikos.icatch.SysException;
import com.atomikos.icatch.TestSynchronization;
import com.atomikos.icatch.TxState;
import com.atomikos.persistence.LogStream;
import com.atomikos.persistence.StateRecoveryManager;
import com.atomikos.persistence.imp.FileLogStream;
import com.atomikos.persistence.imp.StateRecoveryManagerImp;
import com.atomikos.persistence.imp.StreamObjectLog;
import com.atomikos.util.UniqueIdMgr;

 /**
 *A tester class for testing CoordinatorImp.
 */

public class CoordinatorImpTester 
{
    static UniqueIdMgr tidmgr_ = new UniqueIdMgr ("TestTidMgr");
    
    /**
     *Get expected result from commit round.
     *@return int 2 is mixed, 3 is hazard, 6 is abort, 8 is OK.
     */
     
    static int checkCommitResult ( TestResultParticipant[] participants )
    {
         boolean heurMixed = false;
         boolean heurHazard = false;
         boolean heurAbort = false;
         int heurCount = 0;
         
         for ( int i = 0 ;  i < participants.length ;  i++ ) {
            heurAbort = heurAbort || participants[i].heurAbort();
            heurMixed = heurMixed || participants[i].heurMixed(false);
            heurHazard = heurHazard || participants[i].heurHazard(false);
            if ( participants[i].heurAbort() || 
                 participants[i].heurMixed ( false )  || 
                 participants[i].heurHazard ( false ) )
                heurCount++;
        }
        heurMixed = heurMixed ||
                            (  heurAbort && ( heurCount < participants.length ) );
         heurMixed = heurMixed || heurHazard && heurAbort;
        if (heurMixed)
            return 2;
        if (heurHazard)
            return 3;
        if (heurAbort)
            return 6;
        
        return 8;
        

    }
    
    /**
     *Check expected result of abort round.
     *@return int 8 is OK, 2 is mixed, 3 is hazard, 5 is commit.
     */
     
    static int checkAbortResult ( TestResultParticipant[] participants )
    {
         boolean heurMixed = false;
         boolean heurHazard = false;
         boolean heurCommit = false;
         int heurCount = 0;
         
         for ( int i = 0 ;  i < participants.length ;  i++ ) {
            heurCommit = heurCommit || participants[i].heurCommit(false);
            heurMixed = heurMixed || participants[i].heurMixed(false);
            heurHazard = heurHazard || participants[i].heurHazard(false);
            if ( participants[i].heurCommit ( false ) || 
                 participants[i].heurMixed ( false )  || 
                 participants[i].heurHazard ( false ) )
                heurCount++;
        }
        
        heurMixed = heurMixed || 
                           ( heurCommit && ( heurCount < participants.length ) );
         heurMixed = heurMixed || heurHazard && heurCommit;
        if (heurMixed)
            return 2;
        if (heurHazard)
            return 3;
        if (heurCommit)
            return 5;
        
        return 8;
        
    }
    
    /**
    *Gets expected prepare result for array of participants.
    *@return int 0 for pure no, 1 if all readonly, 2 if heuristic mixed,
    *3 if heuristic hazard, 4 if heuristic commit, and 10 if allOK.
    */
    
    static int checkPrepareResult ( TestResultParticipant[] participants )
    {
    //    boolean allOK = true;
//        boolean heuristic = false;
//        for ( int i = 0 ;  i < participants.length ;  i++ ) {
//               boolean readonly = participants[i].voteReadOnly();
//               allOK = ( allOK && ( readonly || participants[i].voteOK()));
//               heuristic = heuristic || participants[i].heurHazard ( true );
//        }
//        
//        if ( allOK ) 
//            return 10;
//        else if ( heuristic )
//            return 3;
//        else return 0;
//            
            
  
      
        boolean allOK = true;
        boolean allReadOnly = true;
        boolean heurMixed = false; //true asa one mixed case
        boolean heurHazard = false; //true asa ONE hazard case
        boolean heurCommit = false; //true asa ONE commit case
        boolean allCommit = true; //false asa one participant does not
        //return a commit exception
        
        for ( int i = 0 ;  i < participants.length ;  i++ ) {
            boolean readonly = (participants[i].voteReadOnly());
            boolean voteOK = ( readonly || participants[i].voteOK());
            allOK = allOK && voteOK;
            allReadOnly = allReadOnly && readonly;
            heurCommit = heurCommit || participants[i].heurCommit( false );
            heurMixed = heurMixed || participants[i].heurMixed(false);
            heurHazard = heurHazard || participants[i].heurHazard(false);
        }
        heurMixed = heurMixed || ( heurHazard && heurCommit);
        
        
        if (!allOK && heurMixed) 
            return 2;
        else if (!allOK && heurHazard)
            return 3;
        else if (allReadOnly) 
            return 1;
        else if (!allOK && heurCommit) 
            return 4;
        else if (allOK)
            return 10;
        else
            return 0;
        
    }
    
    /**
    *Test the given array of participants for 2PC.
    */
    
    static void testCombination ( TestResultParticipant[] participants , 
                                  boolean ots , boolean commitAfterPrepare ) 
        throws Exception
    {
        //first, determine expected prepare answer
        //System.out.println ("checking result first" );
        int prepare = checkPrepareResult( participants );
        //System.out.println ("done checking result" );
        CoordinatorImp coord = null;
        int vote = -1;
        int prepareresult = -1;        
        int commitresult = -1;
      
        try {
            //System.out.println ("Getting tid " );
            String tid = tidmgr_.get();
            //System.out.println ("Gotten tid" );
            coord = new CoordinatorImp(tid , true , null , !ots);
            //System.out.println ( "gotten coordinator" );
            Hashtable cascadelist = new Hashtable();
            Object state = coord.getState();
            //System.out.println ("Adding participants" );
            for ( int i = 0 ; i < participants.length ; i++ ) {
                coord.addParticipant ( participants[i] );
                coord.incLocalSiblingCount();
                if ( ! ots ) {
                    cascadelist.put ( participants[i] , new Integer (1) );
                    participants[i].setCascadeList ( cascadelist );
                    coord.setCascadeList ( cascadelist );
                    coord.setGlobalSiblingCount ( participants.length );
                }
            }
          
          	//System.out.println ( "setting state LOCALLY_DONE" );
            //coord.setState( TxState.LOCALLY_DONE);
            
            
            try {
                //System.out.println ( "preparing coord" );
                vote = coord.prepare();
                //System.out.println ("returned from prepare" );
                if ( vote == Participant.READ_ONLY ) 
                    prepareresult = 1;
                else 
                    prepareresult = 10;
            }
            //catch (HeurCommitException hc) {
//                prepareresult = 4;
//                if (!coord.getState().equals(TxState.HEUR_COMMITTED))
//                    throw new Exception ("prepare/heurcommit: wrong state found");
//                if ( prepare != 4 )
//                    throw new Exception ("anomaly on prepare: " + 
//                                         "result is heur.commit"+
//                                         "instead of " + prepare);
//            }
            //catch (HeurMixedException hm) {
//                prepareresult = 2;
//                if (!coord.getState().equals (TxState.HEUR_MIXED))
//                    throw new Exception ("prepare/heurmixed: wrong state found: " +
//                                          coord.getState());
//                if ( prepare != 2 )
//                    throw new Exception ("anomaly on prepare: " + 
//                                         "result should be heur.mixed instead of " + prepare);
//            }
            catch (HeurHazardException hh) {
                prepareresult = 3;
                if (!coord.getState().equals(TxState.HEUR_HAZARD))
                    throw new Exception ("prepare/hazard: wrong state found");
                if (prepare != 3)
                    throw new Exception ("anomaly on prepare: " + 
                                         "result should be heur.hazard");
            }
            catch (RollbackException rb) {
                prepareresult = 0;
                    if (!coord.getState().equals (TxState.TERMINATED))
                    throw new Exception ("prepare/rolledback: wrong state found");
                if (prepare != 0)
                    throw new Exception ("anomaly on prepare: " +
                                         "result is rolledback instead of "+prepare);
            }
            
            if ( ( vote == Participant.READ_ONLY ) && ( prepare != 1 ) ||
                 ( vote != Participant.READ_ONLY) && (prepare == 1) )
                throw new Exception ("anomaly on prepare: should be readonly");
                
            if ( prepare != prepareresult )
                throw new Exception ("anomaly on prepare: " + prepareresult +
                                     "instead of " + prepare );
            if ( prepare != 10 ) {
                coord.dispose();
                return;
            }
            //here we are for all votes OK and indoubt case
            if ( !coord.getState().equals(TxState.IN_DOUBT) )
                throw new Exception("coord state should be indoubt");
            
            int expected = -1;
            String stateName = "";
            try {
            
                if ( commitAfterPrepare ) {
                    expected = checkCommitResult ( participants );
                    coord.commit ( false );
                }
                else {
                    expected = checkAbortResult ( participants );
                    coord.rollback();
                }
                commitresult = 8;
                stateName = coord.getState().toString();
            }
            catch ( HeurMixedException hm ) {
                commitresult = 2;
                if ( !coord.getState().equals(TxState.HEUR_MIXED))
                    throw new Exception ("Wrong state after heurmix");
            }
            catch ( HeurHazardException hh ) {
                commitresult = 3;
                if ( !coord.getState().equals(TxState.HEUR_HAZARD))
                    throw new Exception ("Wrong state after heurhazard");
            }
            catch ( HeurRollbackException hr) {
                commitresult = 6;
                if (!coord.getState().equals(TxState.HEUR_ABORTED))
                    throw new Exception ("Wrong state after heurabort");
            }
            catch ( HeurCommitException hc) {
                commitresult = 5;
                if (!coord.getState().equals (TxState.HEUR_COMMITTED) )
                    throw new Exception("Wrong state after heurcommit");
            }
            
            if ( commitresult != expected )
                throw new Exception ("Anomaly on second 2pc round :" +
                                      commitresult + "instead of " + expected +
                                      " for coordinator state of " + stateName);
	  coord.dispose();
        }
        finally {
            if ( coord != null )
                coord.dispose();
        }
    
    }
    
    /**
    *Test if heuristic messages are added up properly.
    */
    
    static void testErrorMsgs() throws Exception 
    {
        CoordinatorImp coord = null;
        try {
            String tid = tidmgr_.get();
            coord = new CoordinatorImp(tid , true ,
                                       new PrintStreamConsole (Test.getOutput()) , false);
            HeuristicMessage msg =
                new StringHeuristicMessage ( "Heuristic message for test");
            HeuristicMessage[] msgs = new HeuristicMessage[1];
            msgs[0] = msg;
        
            //test heuristic utility functions
        
            //coord.addErrorMessages ( msgs );
            //msgs = new HeuristicMessage[1];
            //msgs[0] = msg;
            //coord.addErrorMessages ( msgs );
            //HeuristicMessage[] retmsgs = coord.getHeuristicMessages();
            //if ( ! retmsgs[0].equals(msg) || retmsgs.length != 2 )
              //  throw new Exception 
               // ("Heuristic messages not handled properly by utility functions");
            
            //test robustness for null items
            //coord.addErrorMessages ( null ); //should not have null exception
            //msgs = new HeuristicMessage[2];
            //msgs[0] = msg;
            //msgs[1] = null;
            //coord.addErrorMessages ( msgs ); //should not have null exception
            //retmsgs = coord.getHeuristicMessages();
            //if ( retmsgs.length != 4 ) 
             //   throw new Exception 
               // ("Heuristic msgs not handled OK by utility functions");
        }
        finally {
            if ( coord != null )
                coord.dispose();
        }

    }
    
    /**
    *Test simple get methods.
    */
    
    static void testGetters() throws Exception 
    {
        CoordinatorImp coord = null;
        try {
            String tid = tidmgr_.get();
            coord = new CoordinatorImp(tid , true , null , false);
            if (coord.getRecoveryCoordinator() != coord)
                throw new Exception ("getRecoveryCoordinator fails");
            if ( ! coord.getCoordinatorId().equals( tid )) 
                throw new Exception ("getRootTid fails");
//
//            coord.setRollbackOnly();
//            if ( ! coord.getState().equals( TxState.MARKED_ABORT)) 
//                throw new Exception ("setRollbackOnly fails");
//            coord.setGlobalSiblingCount(5);
//            if ( coord.getGlobalSiblingCount() != 5 )
//                throw new Exception ("getGlobalSiblingCount fails");
                
        }
        finally {
            if ( coord != null ) 
                coord.dispose();
        }	
    }
    
    static void testSynchronization() throws Exception
    {
         CoordinatorImp coord = null;
        try {
            String tid = tidmgr_.get();
            coord = new CoordinatorImp(tid , true , null , false);
            TestSynchronization ts = new TestSynchronization();
            coord.registerSynchronization ( ts );
            //coord.setState ( TxState.LOCALLY_DONE);
            coord.setState ( TxState.PREPARING );

            //FOLLOWING REMOVED: synchronization before completion is now in CT
//            if ( ts.getStatus() != TestSynchronization.BEFORE_COMPLETION)
//                throw new Exception ("Synchronization fails before completion");
            coord.setState( TxState.COMMITTING);
            if ( ts.getStatus() != TestSynchronization.COMMIT )
                throw new Exception ("Synchronization fails after commit");
            
            coord.dispose();
            
            coord = new CoordinatorImp(tid , true , null , false);
            ts = new TestSynchronization();
            coord.registerSynchronization ( ts );
            //coord.setState ( TxState.LOCALLY_DONE);
            coord.setState( TxState.ABORTING);
            if ( ts.getStatus() != TestSynchronization.ROLLBACK )
                throw new Exception ("Synchronization fails after commit");

            
        }
        finally {
            if ( coord != null ) 
                coord.dispose();
        }	

    }
    
    /***
    *Test 2PC logic.
    *@param ots If true, no cascadelist is set.
    *@param commit If true, a succesful prepare is followed by commit.
    */
    
    static void test2PC( boolean ots , boolean commit ) throws Exception
    {
         CoordinatorImp coord = null;
        try {
            String tid = tidmgr_.get();
            coord = new CoordinatorImp(tid , true , null , !ots);
            
            //first, test robustness for empty participant list
            //coord.setState( TxState.LOCALLY_DONE );
           
            int ret = coord.prepare();
            if ( ret != Participant.READ_ONLY )
                throw new Exception ("Readonly optimization does not work");
            //state should now be TERMINATED due to readonly optimization
            if ( ! coord.getState().equals( TxState.TERMINATED) )
                throw new Exception("After prepare, state not terminated but " + 
                                    coord.getState() );
            coord.dispose();
            
            coord = new CoordinatorImp( tid, true, null , !ots );
            TestResultParticipant part = new TestResultParticipant( 1 );
            Object rc = coord.addParticipant ( part );
            if ( !rc.equals(coord) ) 
                throw new Exception ("addParticipant does not return coordinator");
            //coord.setState( TxState.LOCALLY_DONE);
            Hashtable cascadelist = new Hashtable();
            cascadelist.put( part , new Integer (1) );
            if ( ! ots) 
                coord.setCascadeList ( cascadelist );
            ret = coord.prepare();
            if ( ret != Participant.READ_ONLY )
                throw new Exception ("Readonly optimization does not work");
            //state should now be TERMINATED due to readonly optimization
            if ( ! coord.getState().equals( TxState.TERMINATED) )
                throw new Exception("After prepare, state not terminated but " + 
                                    coord.getState() );
            coord.dispose();

            exhaustCombinations( 0 , 4, ots , commit );
            exhaustCombinations( 5 , 8, ots , commit );            
           

        }
        finally {
            if ( coord != null ) 
                coord.dispose();
        }	

    }
    
    /**
     *Test all combinations that make sense within a range of min and max.
     *
     *@param min The min status code for TestResultParticipant test range.
     *@param max The max status code for TestResultparticipant test range.
     *@param ots If true: no orphan checks.
     *@param commit If true: commit after successful prepare. Otherwise abort.
     */
     
    static void exhaustCombinations ( int min , int max , boolean ots , boolean commit )
        throws Exception
    {
        TestResultParticipant[] participants = null;
        for ( int i = min ; i < max+1 ; i++ ) {
            for ( int k = min ; k < max+1 ; k++ ) {
                Test.getOutput().print(".");
                for ( int j = min ; j < max+1 ; j++ ) {
                    for ( int l=min ; l < max+1 ; l++ ) {
                        try {
                            
                            //System.out.println ( " i= "+i +" j= " +j +
                            // " k = " +k+ " l = " + l );
                            int codes[] = {i,j,k,l};
                            participants = createCombination ( codes );
                            //System.out.println ( "calling testCombination" );
                            testCombination ( participants , ots ,commit );
                            //System.out.println ( "returned from testCombination" );
    
                        }
                        catch (Exception e) {
                            throw new Exception ("Error in combination: "+
                                                 i +","+j+","+k+","+l+" "+
                                                 e.getMessage()+e.getClass().getName());
                        }
                    }
                }
            }
        }
        Test.getOutput().println();
        
    }
    
    static TestResultParticipant[] createCombination(int[] codes) {
        TestResultParticipant[] parts = new TestResultParticipant[ codes.length ];
        
        for ( int i = 0 ; i < codes.length ; i++ ) {
            int code = codes[i];
            parts [i] = new TestResultParticipant(code);
        }
        return parts;
    }
    
    static void testReplay() throws Exception
    {
        String tid = tidmgr_.get();
        CoordinatorImp coord = new CoordinatorImp (tid , true , null , false);
        TestResultParticipant part1 = new TestResultParticipant ( 6 , tid);
        coord.addParticipant( part1 );
        //coord.setState(TxState.LOCALLY_DONE);
        coord.prepare();
	LogStream logstream = new FileLogStream ( "./","CoordinatorImpTester",
			null );
        StreamObjectLog log = new StreamObjectLog ( logstream , 10, null );
        StateRecoveryManager smgr = new StateRecoveryManagerImp ( log );
        smgr.init();
        smgr.register ( coord );
       
        try {
            coord.commit(false);
        }
        catch (HeurMixedException hm) {
        }
        
        
        coord.dispose();
        
        smgr.close();
        logstream = new FileLogStream ( "./","CoordinatorImpTester",
			null );
        log = new StreamObjectLog ( logstream , 10, null );
        smgr = new StateRecoveryManagerImp ( log );
        smgr.init();
    
        coord = ( CoordinatorImp ) smgr.recover( tid );
        coord.recover();
        if ( coord == null )
            throw new Exception ( "prepared coordinator could not be recovered!" );
        part1.setCode( 10 );
        coord.replayCompletion ( part1 );
        //coord.replayCompletion();
        Thread.currentThread().sleep ( 15000 );
        coord.dispose();
        if ( !coord.getState().equals ( TxState.TERMINATED ) )
            throw new Exception ("Error after replay: coord has status "+
                                  coord.getState());
        smgr.close();
            
    }
    
 
    
    static void testTerminate( boolean commit ) throws Exception
    {	CoordinatorImp  coord = null;
        try {
            String tid = tidmgr_.get();
            coord = new CoordinatorImp (tid , true , null , false);
            TestResultParticipant part1 = new TestResultParticipant ( 10 );
            coord.addParticipant( part1);
            //coord.setState(TxState.LOCALLY_DONE);
            coord.setGlobalSiblingCount(1);
            coord.terminate( commit );
            coord.dispose();
            
            
            coord = new CoordinatorImp (tid , true , null , false);
            part1 = new TestResultParticipant ( 10 );
            coord.addParticipant( part1  );
            TestResultParticipant part2 = new TestResultParticipant ( 10 );
            //coord.setState(TxState.LOCALLY_DONE);
            coord.setGlobalSiblingCount(2);
            coord.terminate( commit );
            coord.dispose();

            
            
            coord = new CoordinatorImp (tid , true , null , false);
            part1 = new TestResultParticipant ( 6 );
            coord.addParticipant( part1  );
            //coord.setState(TxState.LOCALLY_DONE);
            coord.setGlobalSiblingCount(1);
            try {
                coord.terminate( commit );
                if ( commit )
                    throw new Exception ("Error in terminate(): should be heurmixed");
                //note: on abort and 1PC, we don't care if heuristic or not.
                //there is no obligation to log in this case, since there can 
                //not be any indoubt participant due to  the lack of prepare
            }
            catch (HeurMixedException e) {
            }
            coord.dispose();

        
        }
        finally {
            if ( coord != null )
                coord.dispose();
        }
    }
       
    public static void test() throws Exception 
    {	
        testReplay();
        //testForget();
        Propagator.RETRY_INTERVAL = 10;
        //to make prepare / commit tests faster
        testSynchronization();
        test2PC ( true , true); //OTS: no orphan info
        test2PC ( true, false );
        test2PC ( false , true ); //icatch native with orphan detection
        test2PC ( false, false );
        testTerminate( true );
        testTerminate ( false );
        
        
        testErrorMsgs();
        testGetters(); 
    }
    
    public static void main(String args[]) 
    {
        Test.getOutput().println("Starting: CoordinatorImp Test");
        try {
            test();
        }
        catch (SysException se) {
            Test.getOutput().println("Error in test: " + se.getMessage());
            Stack errors = se.getErrors();
            while (! errors.empty()) {
                Exception ex = (Exception) errors.pop();
                Test.getOutput().println("Nested exception is " + 
                                          ex.getMessage() +
                                          ex.getClass().getName());
            }
            se.printStackTrace();

        }
        catch (Exception e) {
            Test.getOutput().println("Error in test: " + e.getMessage());
            e.printStackTrace();
        }
        finally {
            Test.getOutput().println("Done:     CoordinatorImp Test");
        }
    }
    
}
