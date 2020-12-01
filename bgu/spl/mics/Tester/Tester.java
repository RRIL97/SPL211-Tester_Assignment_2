package bgu.spl.mics.Tester;

import bgu.spl.mics.application.Main;
import bgu.spl.mics.application.passiveObjects.Attack;
import bgu.spl.mics.application.passiveObjects.Diary;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Tester {

    //Coded By Ron Rachev
    private final  int numOfTestsToGenerate  = 20; //Number of tests that are generated each time
    private final  int numEwoksUpperLimit    = 7;  //Upper limit for number of ewoks to use
    private final  int numEwoksLowerLimit    = 4;  //Lower limit for number of ewoks to use
    private final int  numAttacksUpperLimit  = 5;  //Upper limit for number of attacks to generate
    private final int  numAttacksLowerLimit  = 3;  //Lower limit for number of attacks to generate
    private long       startingTimeOfTest       ;  //saves time offset for starting of the tests
    private Gson       testBuilderJson;


    /*
    Parses json info of a given test in a specified filepath
    */
    private static Test[] getTestsFromJson(String filePath) throws IOException {
        Gson gson = new Gson();
        try (Reader reader = new FileReader(filePath)) {
            return gson.fromJson(reader, Test[].class);
        }
    }
    /*
    Converts a given object to Json and writes it into a specific file path
     */
    private void saveOutputToJson(String filePath,Object objectToConvert)  {
        testBuilderJson     = new GsonBuilder().create();
        try{
            FileWriter fileWriter = new FileWriter(filePath);
            testBuilderJson.toJson(objectToConvert,fileWriter);
            fileWriter.flush();
            fileWriter.close();
        }catch(Exception fileWriteException){

        }
    }
    /*
    Responsible for generation of tests
    id - id of test
    */
    private Test generateTest(int id){
         Test generatedTest       = new Test();
         Attack    []       generatedAttacks;
         ArrayList<Integer> serialsUsedInAttack;

         int numEwoks     = (int) ( Math.random() * (numEwoksUpperLimit)+numEwoksLowerLimit);
         int numAttacks   = (int) ( Math.random() * (numAttacksUpperLimit)+numAttacksLowerLimit);
         generatedAttacks = new Attack[numAttacks];

         //Generate Random Attacks
         for(int i = 0 ; i < numAttacks ; i++) {
             serialsUsedInAttack = new ArrayList<>();
             for(int r = 1 ; r <= numEwoks ; r++)
             {
                 //Coin Toss For Each Serial
                 if((int) (Math.random()*(2)+1) == 1)
                      serialsUsedInAttack.add(r);
             }
             if(serialsUsedInAttack.isEmpty()) serialsUsedInAttack.add(1);
             generatedAttacks[i] = new Attack(serialsUsedInAttack,(int)(Math.random()*(3000)+1000));
         }
         generatedTest.setAttacks(generatedAttacks);
         generatedTest.setEwoks(numEwoks);
         generatedTest.setLando((int)(Math.random()*(2000)+1000));
         generatedTest.setR2D2 ((int)(Math.random()*(2000)+1000));
         generatedTest.setTestId(id);
         generatedTest.setNumOfAttacks(generatedAttacks.length);
         //Set generatedTest vals so we can save it easily via the class reference

         System.out.println("\r\nDone Generating Test --> " + id);
         System.out.println("----------------------------------------\r\n");

         return generatedTest;
    }

    //Generates numOfTestsToGenerate Tests
    public void generateTests(){
        Test [] randTests = new Test[numOfTestsToGenerate];
        for(int i = 0; i < numOfTestsToGenerate ; i++)
            randTests[i] = generateTest(i);
        saveOutputToJson("Tests.json",randTests);
    }
    long FindMin(long a, long b, long c, long d){
        long min1 = (a>b)?b:a;
        long min2 = (c>d)?d:c;
        return (min1>min2)?min2:min1;
    }
    //Run a specific test via a specified json.
    //If testNumber=X then we search for "TestX.json" in the current directory to run the test
    public void runTests(){
        Test currentTests [] ;
        try {
                currentTests = getTestsFromJson("Tests.json");
                for(int i = 0; i < currentTests.length; i ++) {
                    saveOutputToJson("input.json", currentTests[i]);
                    //An output file shall be only created whenever the program finishes it's logic.
                    File outputFile = new File("Output.json");
                    if (outputFile.exists()) outputFile.delete();
                    Main.main(null);
                    do {
                        Thread.sleep(10);
                    } while (!outputFile.exists());
                    System.out.println("\r\n\r\nFinished Test --- > " + i);

                    AtomicInteger numOfAttacksInTest = Diary.getInstance().getNumberOfAttacks();

                    long startingTimeOfTest = Math.max(Diary.getInstance().getC3POFinish(), Diary.getInstance().getHanSoloFinish());
                    long shieldDeactivationTestValue = (Diary.getInstance().getR2D2Deactivate() - startingTimeOfTest);
                    System.out.println("\r\n-----------------------------------");

                    boolean passedFirstTest  = false; //Checking Deactivation Shield Logic
                    boolean passedSecondTest = false; //Checking Num Of Attacks Logic
                    boolean passedThirdTest  = true; //Checking Graceful Termination (Should be at the same mili second~)
                    System.out.println("Your Deactivation Shield Finished Time --> " + shieldDeactivationTestValue + "  Test Value Should Of Been -> " + currentTests[i].getR2D2Sleep());
                    if (Math.round(shieldDeactivationTestValue/1000)*1000 ==  (Math.round(currentTests[i].getR2D2Sleep())/1000)*1000)
                        passedFirstTest = true;
                    if(numOfAttacksInTest.get() == (currentTests[i].getNumberOfAttacks().get()))
                        passedSecondTest = true;


                    long soloTerminate  = (Diary.getInstance().getHanSoloTerminate());
                    long C3POTerminate  = (Diary.getInstance().getC3POTerminate());
                    long LandoTermiante = (Diary.getInstance().getLandoTerminate());
                    long R2D2Terminate = (Diary.getInstance().getR2D2Terminate());

                    long minTerminate = FindMin(soloTerminate,C3POTerminate,LandoTermiante,R2D2Terminate);
                    System.out.println("Minimum Termination Time --> " + minTerminate);
                    if(soloTerminate - minTerminate > 0000000000010L)
                        passedThirdTest = false;
                    if(!passedThirdTest && C3POTerminate - minTerminate > 0000000000010L)
                        passedThirdTest = false;
                    if(!passedThirdTest && LandoTermiante - minTerminate > 0000000000010L)
                        passedThirdTest = false;
                    if(!passedThirdTest && R2D2Terminate - minTerminate > 0000000000010L)
                        passedThirdTest = false;

                    if(passedFirstTest && passedSecondTest && passedThirdTest) {
                        System.out.println("Passed Test --> " + i);
                    }else
                        System.out.println("Failed Test --> " + i);

                }
        }catch(Exception runTestException){
            runTestException.printStackTrace();
        }
    }
}
