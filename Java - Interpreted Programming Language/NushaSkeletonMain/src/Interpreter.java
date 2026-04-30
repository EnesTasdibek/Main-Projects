import AST.*;

import java.util.*;
import java.util.stream.Collectors;

public class Interpreter {
    //to fill the definitions
    private HashMap<String, String[]> definition = new HashMap<>();
    //to fill the structInstance with variableNames
    private HashMap<String, structInstance[]> structMap = new HashMap<>();
    //I implemented this Hashmap in structInstance class
    //private HashMap<String, variableInstance[]> variableMap = new HashMap<String, variableInstance[]>();

    public void Interpret(Nusha tree) throws Exception {

        int definitionSize = tree.definitions.definition.size();
        String[] definitionInstance = new String[definitionSize];
        //this should be entry.size()

        for(Definition d : tree.definitions.definition) {

            if(d.choices.isPresent()){
                String[] definitions = d.choices.get().choice.toArray(new String[0]);
                definition.put(d.definitionName, definitions);
            }

        }
        structInstance[] structInstance = new structInstance[definitionSize];
        //Filled the hashmap of (name->structInstance)
        //now, each structInstance has a hashmap as (structInstance[i(g->variableInstance)])
        //String entry =tree.definitions.definition.get(definitionInstance.length - 1).nstruct.get().entry.get(0).name;
        int entrySize  = tree.definitions.definition.get(definitionInstance.length - 1).nstruct.get().entry.size();

        //there are entry.size times variableInstances
        variableInstance[] variableInstance = new variableInstance[tree.definitions.definition.get(definitionInstance.length - 1).nstruct.get().entry.size() * (definitionSize-1)];
        //variableInstance[] variableInstance = new variableInstance[entrySize];
        for (int i = 0; i< entrySize; i++) {
            //getting def by definitions array filled up

            structInstance[i] = new structInstance();
                //variableInstance[i] = new variableInstance(def, currentValue, unique);
                //and for every struct instance, there is entry.size times variable instances and entries
                for(int j =0; j<entrySize;j++){
                    //if(tree.definitions){
                        String[] def = definition.get(tree.definitions.definition.get(j).definitionName);
                        //this is definition's String[], need to access its index with integer
                        int currentValue = 0;
                        //definition.get(i);
                        boolean unique = tree.definitions.definition.get(definitionInstance.length - 1).nstruct.get().entry.get(j).unique;
                        String entry = tree.definitions.definition.get(definitionInstance.length - 1).nstruct.get().entry.get(j).name;
                        //it points to same adresss in memory, but should not,
                        // need to produce new instances of variale instance every time we call
                        //may need to move inside struct population
                    //add one more loop to add
                        variableInstance[j] = new variableInstance(def, currentValue, unique);
                        structInstance[i].structmap.put(entry, variableInstance[j]);
                    //}
                }
        }

        System.out.println(variableInstance.length);
        boolean notSeenYet = false;
        LinkedList<String> uniques = new LinkedList<>();
        for(int i=0;i<entrySize;i++){
            Object key2 = structInstance[i].structmap.keySet().toArray()[i];

            for(int j=0;j<entrySize;j++) {
                if (variableInstance[i].unique == true) {
                    //then that value must not be repeated in any other instance
                    //store values like this in a linkedlist
                    //this means the list must not have any repeated values
                    uniques.add(structInstance[i].structmap.get(key2).getDefinition());

                }
            }
        }
        HashSet<Object> duplicates = new HashSet<Object>();

        for (int x = 0; x < uniques.size(); x++) {
            for (int y = x + 1; y < uniques.size(); y++) {
                if (x == y) break;
                if (uniques.get(x).equals(uniques.get(y))) {
                    duplicates.add(uniques.get(x));
                    break;
                }
            }
        }
        if(duplicates.isEmpty()){
            System.out.println("No duplicates found for unique values");
        }
        else{
            System.out.println("DUPLICATE UNIQUES " + duplicates);
        }

        //now fill structMap
        String variableName = tree.variables.variable.get(0).variableName;
        structMap.put(variableName, structInstance);

        //this is variable size set on struct
        int varCount = Integer.parseInt(tree.variables.variable.get(0).size.get());
        //if we have the struct mapped
        if(structMap.containsValue(structInstance)) {
            System.out.println("SUCCESS:");
        }
        for(int instanceNumber =0;instanceNumber < varCount;instanceNumber++){

            for (int i = 0; i < entrySize; i++) {

                Object key1 = structInstance[i].structmap.keySet().toArray()[i];
                //in each sInstance, there is one key, such as "p" => entryName
                //in each structMap, there is one key, such as "Fleet" => variableName
                System.out.println(structMap.keySet().toArray()[0] + "[" + instanceNumber + "]" + "."
                        + structInstance[i].structmap.keySet().toArray()[i] + " = " +
                        structInstance[i].structmap.get(key1).getDefinition());
            }//variableInstance[i].getDefinition() +
            System.out.println();
        }
        //if strings match, print out with function, if in the right structure, success, print
        //Interpreter1 should have the logic for building the necessary structures
        // and printing out all structure instances with "default" values
        //Interpreter2 should be an extension of the above, which handles the
        // logic for interpreting the rules to solve the logic puzzle in the given Nusha code
    }



}
